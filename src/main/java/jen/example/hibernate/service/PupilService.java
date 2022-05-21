package jen.example.hibernate.service;

import jen.example.hibernate.entity.Pupil;
import jen.example.hibernate.repository.PupilRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PupilService implements EntityService<Pupil>{

    private static final Logger logger = LoggerFactory.getLogger(PupilService.class);

    private final PupilRepository repository;

    @Override
    @Transactional
    public Pupil add(Pupil pupil) {
        // todo: validate that id dont exists
        return repository.save(pupil);
    }

    @Override
    public Pupil getOr404(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFound(id));
    }

    @Override
    public List<Pupil> all() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Pupil updateById(Long id, Pupil newPupil) {
        Pupil pupil = getOr404(id);

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

//    public void addAttributeValues(Long pupilId, Map<Long, Double> attributeValues) {
//        Pupil pupil = getOr404(pupilId);
//        List<Attribute> attributes = attributeRepository.getAllByIdIn(attributeValues.keySet());
//        attributes.forEach(attribute -> pupil.addAttributeValue(attribute, attributeValues.get(attribute.getId())));
//        repository.save(pupil);
//    }
//
//    public void addAttributeValue(Long pupilId, Long attributeId, Double value) {
//        Pupil pupil = getOr404(pupilId);
//        Attribute attribute = attributeRepository.getById(attributeId);
//        pupil.addAttributeValue(attribute, value);
//        repository.save(pupil);
//    }
//
//    public void removeAttributeValue(Long pupilId, Long attributeId) {
//        Pupil pupil = getOr404(pupilId);
//        Attribute attribute = attributeRepository.getById(attributeId);
//        pupil.removeAttributeValue(attribute);
//        repository.save(pupil);
//    }

    public boolean pupilExists(String givenId) {
        return givenId != null && repository.existsByGivenId(givenId);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class NotFound extends RuntimeException{
        public NotFound(Long id){
            super("Could not find pupil " + id);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class GivenIdAlreadyExists extends RuntimeException {
        public GivenIdAlreadyExists(String givenId){
            super("pupil with given ID " + givenId + " already exists");
        }
    }
}