package jen.example.hibernate.service;

import jen.example.hibernate.entity.Pupil;
import jen.example.hibernate.repository.PupilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PupilServiceImpl implements PupilService{

    private final PupilRepository repository;

    @Override
    public Pupil save(Pupil pupil) {
        // verify
        return repository.save(pupil);
    }

    @Override
    public Pupil fetch(Long id) {
        return repository.getById(id);
    }

    @Override
    public List<Pupil> fetchAll() {
        return repository.findAll();
    }
}
