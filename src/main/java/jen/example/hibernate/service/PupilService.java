package jen.example.hibernate.service;

import jen.example.hibernate.entity.Pupil;
import jen.example.hibernate.repository.PupilRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PupilService implements EntityService<Pupil>{

    private static final Logger logger = LoggerFactory.getLogger(PupilService.class);

    private final PupilRepository repository;

    @Override
    public Pupil add(Pupil item) {
        return null;
    }

    @Override
    public Pupil getOr404(Long id) {
        return null;
    }

    @Override
    public List<Pupil> getAll() {
        return null;
    }

    @Override
    public Pupil updateById(Long id, Pupil item) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
