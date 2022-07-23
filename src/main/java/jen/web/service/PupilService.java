package jen.web.service;

import jen.web.entity.*;
import jen.web.exception.BadRequest;
import jen.web.exception.EntityAlreadyExists;
import jen.web.exception.NotFound;
import jen.web.repository.AttributeValueRepository;
import jen.web.repository.PupilRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

        validateGivenId(pupil.getGivenId());
        return pupilRepository.save(pupil);
    }

    @Override
    public Pupil getOr404(Long id) {
        return pupilRepository.findById(id).orElseThrow(() -> new NotFound("Could not find pupil " + id));
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
            validateGivenId(pupil.getGivenId());
            pupil.setGivenId(newPupil.getGivenId());
        }

        pupil.setFirstName(newPupil.getFirstName());
        pupil.setLastName(newPupil.getLastName());
        pupil.setGender(newPupil.getGender());
        pupil.setBirthDate(newPupil.getBirthDate());

        if ( !newPupil.getAttributeValues().isEmpty() ) {
            pupil.setAttributeValues(newPupil.getAttributeValues());
        }

        return pupilRepository.save(pupil);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Pupil pupil = getOr404(id);

        attributeValueRepository.deleteAll(pupil.getAttributeValues());
        for(Group group : pupil.getGroups()){
            group.removePupil(pupil);
            pupil.removeFromGroup(group);
            groupService.deletePupilPreferences(pupil, group);
        }

        pupilRepository.delete(pupil);
    }

    public void addAttributeValues(Pupil pupil, Group group, Map<Long, Double> attributeValues) {

        try {
            for (Map.Entry<Long, Double> attributeValue : attributeValues.entrySet()) {
                pupil.addAttributeValue(group, attributeValue.getKey(), attributeValue.getValue());
            }
        } catch (Group.NotBelongToGroupException | Template.NotExistInTemplateException e) {
            throw new BadRequest(e.getMessage());
        }

        attributeValueRepository.saveAllAndFlush(pupil.getAttributeValues());
    }

    public void removeAttributeValues(Pupil pupil, Group group, Set<Long> attributeIds) {

        try {
            Set<AttributeValue> attributeValues = pupil.getAttributeValues(group, attributeIds);
            attributeValueRepository.deleteAll(attributeValues);

        }  catch (Group.NotBelongToGroupException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    public Set<AttributeValue> getAttributeValues(Pupil pupil, Group group) {

        try {
            Set<AttributeValue> attributeValues = pupil.getAttributeValues(group);
            return attributeValues;

        }  catch (Group.NotBelongToGroupException e) {
            throw new BadRequest(e.getMessage());
        }
    }


    public boolean pupilExists(String givenId) {
        return givenId != null && pupilRepository.existsByGivenId(givenId);
    }

    private void validateGivenId(String givenId){
        if(pupilExists(givenId)){
            throw new BadRequest("pupil with given ID " + givenId + " already exists");
        }
    }
}