package jen.example.hibernate.service;

import jen.example.hibernate.entity.Pupil;

import java.util.List;

public interface PupilService {
    Pupil save(Pupil pupil);

    Pupil fetch(Long id);

    List<Pupil> fetchAll();
}
