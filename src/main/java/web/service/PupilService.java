package web.service;

import web.entity.*;
import web.exception.BadRequest;
import web.exception.EntityAlreadyExists;
import web.exception.NotFound;
import web.repository.AttributeValueRepository;
import web.repository.PupilRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static web.util.IsraeliIdValidator.padWithZerosAndTrim;

@Service
@RequiredArgsConstructor
public class PupilService implements EntityService<Pupil>{

    private static final Logger logger = LoggerFactory.getLogger(PupilService.class);

    private final PupilRepository pupilRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final GroupService groupService;

    @Override
    @Transactional
    public Pupil add(Pupil pupil) {
        Long id = pupil.getId();
        if (id != null && pupilRepository.existsById(id)) {
            throw new EntityAlreadyExists("Pupil with Id '" + id + "' already exists.");
        }

        validateThatGivenIdNotExists(pupil.getGivenId());
        if(!pupil.getGroups().isEmpty()){
            setPupilGroups(pupil, pupil.getGroups());
        }

        return pupilRepository.save(pupil);
    }

    @Override
    public Pupil getOr404(Long id) {
        return pupilRepository.findById(id).orElseThrow(() -> new NotFound("Could not find pupil " + id));
    }

    @Override
    public List<Pupil> allWithoutPages() {
        return pupilRepository.findAll();
    }

    public Pupil getByGivenIdOr404(String givenId) {
        String paddedGivenId = padWithZerosAndTrim(givenId);
        return pupilRepository.getPupilByGivenId(paddedGivenId).orElseThrow(() -> new NotFound("Could not find pupil with given ID " + givenId));
    }

    public Pupil updateOrCreatePupilByGivenId(Pupil newPupil) throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        Optional<Pupil> currentPupil = pupilRepository.getPupilByGivenId(newPupil.getGivenId());
        if(currentPupil.isPresent()){
            return updateById(currentPupil.get().getId(), newPupil);
        } else {
            return add(newPupil);
        }
    }

    @Override
    public Page<Pupil> all(PageRequest pageRequest) {
        return pupilRepository.findAll(pageRequest);
    }

    @Override
    @Transactional
    public Pupil updateById(Long id, Pupil newPupil) throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        Pupil pupil = getOr404(id);

        // edit general information
        if (newPupil.getGivenId() != null && !pupil.getGivenId().equals(newPupil.getGivenId())) {
            validateThatGivenIdNotExists(newPupil.getGivenId());
            pupil.setGivenId(newPupil.getGivenId());
        }
        pupil.setFirstName(newPupil.getFirstName());
        pupil.setLastName(newPupil.getLastName());
        pupil.setGender(newPupil.getGender());
        pupil.setBirthDate(newPupil.getBirthDate());

        // edit groups if new pupil contain groups
        if(!newPupil.getGroups().isEmpty()){
            setPupilGroups(pupil, newPupil.getGroups());
        }

        return pupilRepository.save(pupil);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Pupil pupil = getOr404(id);

        attributeValueRepository.deleteAll(pupil.getAttributeValues());
        List<Group> groups = groupService.getByIdsWithoutPages(new HashSet<>(pupil.getGroupIds()));

        for(Group group : groups){
            group.getPlacements().forEach(placement -> placement.removePupilFromAllResults(pupil));
            group.removePupil(pupil);
            pupil.removeFromGroup(group);
            groupService.deletePupilPreferences(group, pupil);
        }

        pupilRepository.delete(pupil);
    }

    public Page<Group> getPupilGroups(Pupil pupil, PageRequest pageRequest) {
        return groupService.getByIds(new HashSet<>(pupil.getGroupIds()), pageRequest);
    }

    public void addOrUpdateAttributeValuesFromIdValueMap(Pupil pupil, Group group, Map<Long, Double> attributeIdValueMap)
            throws Group.PupilNotBelongException, Template.AttributeNotBelongException, AttributeValue.ValueOutOfRangeException {

        for (Map.Entry<Long, Double> attributeValue : attributeIdValueMap.entrySet()) {
            pupil.addAttributeValue(group, attributeValue.getKey(), attributeValue.getValue());
        }

        attributeValueRepository.saveAllAndFlush(pupil.getAttributeValues());
    }

    public List<AttributeValue> getAttributeValues(Pupil pupil, Group group) throws Group.PupilNotBelongException {
        Set<AttributeValue> attributeValues = pupil.getAttributeValues(group);

        return attributeValueRepository.getAllByPupilAttributeIdInOrderByPupilAttributeId(attributeValues.stream()
                .map(attributeValue -> attributeValue.getPupilAttributeId())
                .collect(Collectors.toSet()));
    }

    @Transactional
    public List<Group> setPupilGroups(Pupil pupil, Collection<Group> newGroups){
        Set<Long> newGroupIds = newGroups.stream().map(BaseEntity::getId).collect(Collectors.toSet());
        pupil.setGroups(new HashSet<>(groupService.getByIdsWithoutPages(newGroupIds)));
        pupilRepository.save(pupil);

        return groupService.getByIdsWithoutPages(new HashSet<>(pupil.getGroupIds()));
    }

    @Transactional
    public List<Group> linkPupilToGroup(Pupil pupil, Group newGroup){
        Set<Group> pupilGroups = new HashSet<>(pupil.getGroups());
        pupilGroups.add(newGroup);

        return setPupilGroups(pupil, pupilGroups);
    }

    @Transactional
    public void unlinkPupilFromGroup(Pupil pupil, Group groupToRemove){
        pupil.removeFromGroup(groupToRemove);
        pupilRepository.save(pupil);
    }

    public boolean isPupilExists(String givenId) {
        return givenId != null && pupilRepository.existsByGivenId(givenId);
    }

    private void validateThatGivenIdNotExists(String givenId){
        if(givenId == null){
            throw new BadRequest("given ID cannot be null");
        }

        if(isPupilExists(givenId)){
            throw new BadRequest("pupil with given ID " + givenId + " already exists");
        }
    }
}