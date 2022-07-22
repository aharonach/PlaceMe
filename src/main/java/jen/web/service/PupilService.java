package jen.web.service;

import jen.web.entity.Group;
import jen.web.entity.Placement;
import jen.web.entity.Pupil;
import jen.web.entity.PupilAttributeId;
import jen.web.exception.BadRequest;
import jen.web.exception.NotFound;
import jen.web.repository.AttributeValueRepository;
import jen.web.repository.PreferenceRepository;
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

    private final PupilRepository repository;
    private final AttributeValueRepository attributeValueRepository;
    private final PreferenceRepository preferenceRepository;


    @Override
    @Transactional
    public Pupil add(Pupil pupil) {
        validateGivenId(pupil.getGivenId());
        return repository.save(pupil);
    }

    @Override
    public Pupil getOr404(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFound("Could not find pupil " + id));
    }

    @Override
    public List<Pupil> all() {
        return repository.findAll();
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

        return repository.save(pupil);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // todo: bug - it works only after sending the request twice.
        Pupil pupil = getOr404(id);
        attributeValueRepository.deleteAllByPupilAttributeId_PupilId(id);
        pupil.setGroups(new HashSet<>());
        repository.delete(pupil);
    }

    public void addPreference(Pupil selector, Pupil selected, boolean wantToBeTogether, Placement placement){
        selector.addPreference(selected, wantToBeTogether, placement);
        preferenceRepository.saveAllAndFlush(selector.getPreferences());
    }

    public void deletePreference(Pupil selector, Pupil selected, Placement placement){
        preferenceRepository.deleteAllById(selector.deletePreference(selected, placement));
    }

    public void addAttributeValues(Pupil pupil, Group group, Map<Long, Double> attributeValues) {

        try {
            for (Map.Entry<Long, Double> attributeValue : attributeValues.entrySet()) {
                pupil.addAttributeValue(group, attributeValue.getKey(), attributeValue.getValue());
            }
        } catch(Pupil.NotBelongToGroupException e) {
            throw new BadRequest(e.getMessage());
        }

        attributeValueRepository.saveAllAndFlush(pupil.getAttributeValues());
    }

    public void removeAttributeValues(Pupil pupil, Group group, Set<Long> attributeIds) {
        List<PupilAttributeId> toRemove = new ArrayList<>();

        try {
            for (Long attributeId : attributeIds) {
                toRemove.add(pupil.removeAttributeValue(group, attributeId));
            }
        } catch(Pupil.NotBelongToGroupException e) {
            throw new BadRequest(e.getMessage());
        }

        attributeValueRepository.deleteAllById(toRemove);
    }

    public boolean pupilExists(String givenId) {
        return givenId != null && repository.existsByGivenId(givenId);
    }

    private void validateGivenId(String givenId){
        if(pupilExists(givenId)){
            throw new BadRequest("pupil with given ID " + givenId + " already exists");
        }
    }
}