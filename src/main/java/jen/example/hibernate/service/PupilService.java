package jen.example.hibernate.service;

import jen.example.hibernate.entity.Group;
import jen.example.hibernate.entity.Pupil;
import jen.example.hibernate.exception.BadRequest;
import jen.example.hibernate.exception.NotFound;
import jen.example.hibernate.repository.PupilRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PupilService implements EntityService<Pupil>{

    private static final Logger logger = LoggerFactory.getLogger(PupilService.class);

    private final PupilRepository repository;
    private final GroupService groupService;

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

        validateGivenId(pupil.getGivenId());
        pupil.setGivenId(newPupil.getGivenId());
        pupil.setFirstName(newPupil.getFirstName());
        pupil.setLastName(newPupil.getLastName());
        pupil.setGender(newPupil.getGender());
        pupil.setBirthDate(newPupil.getBirthDate());
        pupil.setAttributeValues(newPupil.getAttributeValues());
        return repository.save(pupil);
    }

    @Override
    public void deleteById(Long id) {
        Pupil pupil = getOr404(id);
        repository.delete(pupil);
    }

    public void addAttributeValues(Pupil pupil, Group group, Map<Long, Double> attributeValues) {
        try {
            for (Map.Entry<Long, Double> attributeValue : attributeValues.entrySet()) {
                pupil.addAttributeValue(group, attributeValue.getKey(), attributeValue.getValue());
            }

            // not saving the attribute values :(
            repository.save(pupil);
            groupService.getRepository().save(group);
        } catch(Exception exception) {
            throw new BadRequest(exception.getMessage());
        }
    }

    public void removeAttributeValues(Pupil pupil, Group group, Set<Long> attributeIds) {
        try {
            for (Long attributeId : attributeIds) {
                pupil.removeAttributeValue(group, attributeId);
            }
            repository.save(pupil);
        } catch(Exception exception) {
            throw new BadRequest(exception.getMessage());
        }
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