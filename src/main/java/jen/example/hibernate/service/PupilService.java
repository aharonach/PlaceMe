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
    public Pupil add(Pupil item) {
        validate(item);
        return repository.save(item);
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
    public Pupil updateById(Long id, Pupil item) {
        Pupil pupil = getOr404(id);

        validate(item);

        pupil.setGivenId(item.getGivenId());
        pupil.setFirstName(item.getFirstName());
        pupil.setLastName(item.getLastName());
        pupil.setGender(item.getGender());
        pupil.setBirthDate(item.getBirthDate());
        pupil.setAttributeValues(item.getAttributeValues());
        return repository.save(pupil);
    }

    @Override
    public void deleteById(Long id) {
        Pupil pupil = getOr404(id);
        repository.delete(pupil);
    }

    public boolean pupilExists(String givenId) {
        return givenId != null && repository.existsByGivenId(givenId);
    }

    public void addAttributeValue() {

    }

    public void validate(Pupil pupil) {
        if (pupilExists(pupil.getGivenId())) {
            throw new GivenIdAlreadyExists(pupil.getGivenId());
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class NotFound extends RuntimeException{
        public NotFound(Long id){
            super("Could not find pupil " + id);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public class GivenIdAlreadyExists extends RuntimeException {
        public GivenIdAlreadyExists(String givenId){
            super("pupil with given ID " + givenId + " already exists");
        }
    }
}
