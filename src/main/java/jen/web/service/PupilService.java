package jen.web.service;

import jen.web.entity.*;
import jen.web.exception.BadRequest;
import jen.web.exception.EntityAlreadyExists;
import jen.web.exception.NotFound;
import jen.web.repository.AttributeValueRepository;
import jen.web.repository.GroupRepository;
import jen.web.repository.PupilRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PupilService implements EntityService<Pupil>{

    private static final Logger logger = LoggerFactory.getLogger(PupilService.class);

    private final PupilRepository pupilRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final GroupRepository groupRepository;
    private final GroupService groupService;


    @Override
    @Transactional
    public Pupil add(Pupil pupil) {
        Long id = pupil.getId();
        if (id != null && pupilRepository.existsById(id)) {
            throw new EntityAlreadyExists("Pupil with Id '" + id + "' already exists.");
        }

        validateGivenIdNotExists(pupil.getGivenId());
        pupil.getGroups().stream().map(BaseEntity::getId).forEach(groupId -> {
            Group group = groupService.getOr404(groupId);
            pupil.addToGroup(group);
        });

        return pupilRepository.save(pupil);
    }

    @Override
    public Pupil getOr404(Long id) {
        return pupilRepository.findById(id).orElseThrow(() -> new NotFound("Could not find pupil " + id));
    }

    public Pupil getByGivenIdOr404(String givenId) {
        return pupilRepository.getPupilByGivenId(givenId).orElseThrow(() -> new NotFound("Could not find pupil with given ID " + givenId));
    }

    @Override
    public List<Pupil> all() {
        return pupilRepository.findAll();
    }

    @Override
    @Transactional
    public Pupil updateById(Long id, Pupil newPupil) {
        Pupil pupil = getOr404(id);

        if (!(newPupil.getGivenId() == null || pupil.getGivenId().equals(newPupil.getGivenId()))) {
            validateGivenIdNotExists(newPupil.getGivenId());
            try {
                pupil.setGivenId(newPupil.getGivenId());
            } catch (Pupil.GivenIdContainsProhibitedCharsException | Pupil.GivenIdIsNotValidException e) {
                throw new RuntimeException(e);
            }
        }

        Set<Long> newGroupIds = newPupil.getGroups().stream().map(BaseEntity::getId).collect(Collectors.toSet());
        if(!newGroupIds.isEmpty()){
            pupil.setGroups(groupService.getByIds(newGroupIds));
        }

        pupil.setFirstName(newPupil.getFirstName());
        pupil.setLastName(newPupil.getLastName());
        pupil.setGender(newPupil.getGender());
        pupil.setBirthDate(newPupil.getBirthDate());

        if ( !newPupil.getAttributeValues().isEmpty() ) {
            pupil.setAttributeValues(new HashSet<>(newPupil.getAttributeValues()));
        }

        return pupilRepository.save(pupil);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Pupil pupil = getOr404(id);

        attributeValueRepository.deleteAll(pupil.getAttributeValues());
        Set<Group> groups = groupRepository.getAllByIdIn(pupil.getGroupIds());
        for(Group group : groups){
            group.getPlacements().forEach(placement -> placement.removePupilFromAllResults(pupil));
            group.removePupil(pupil);
            pupil.removeFromGroup(group);
            groupService.deletePupilPreferences(group, pupil);
        }

        pupilRepository.delete(pupil);
    }

    public void addAttributeValues(Pupil pupil, Group group, Map<Long, Double> attributeValues)
            throws Group.PupilNotBelongException, Template.AttributeNotBelongException, AttributeValue.ValueOutOfRangeException {

        for (Map.Entry<Long, Double> attributeValue : attributeValues.entrySet()) {
            pupil.addAttributeValue(group, attributeValue.getKey(), attributeValue.getValue());
        }

        attributeValueRepository.saveAllAndFlush(pupil.getAttributeValues());
    }

    public void removeAttributeValues(Pupil pupil, Group group, Set<Long> attributeIds) throws Group.PupilNotBelongException {

        Set<AttributeValue> attributeValues = pupil.getAttributeValues(group, attributeIds);
        attributeValues.forEach(attributeValue -> pupil.removeAttributeValue(attributeValue));
        attributeValueRepository.deleteAll(attributeValues);

    }

    public Set<AttributeValue> getAttributeValues(Pupil pupil, Group group) throws Group.PupilNotBelongException {

        Set<AttributeValue> attributeValues = getOr404(pupil.getId()).getAttributeValues(group);
        return attributeValues;

    }

    public boolean isPupilExists(String givenId) {
        return givenId != null && pupilRepository.existsByGivenId(givenId);
    }

    private void validateGivenIdNotExists(String givenId){
        if(givenId == null){
            throw new BadRequest("given ID cannot be null");
        }

        if(isPupilExists(givenId)){
            throw new BadRequest("pupil with given ID " + givenId + " already exists");
        }
    }
}