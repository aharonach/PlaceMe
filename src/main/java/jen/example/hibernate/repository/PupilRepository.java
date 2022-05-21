package jen.example.hibernate.repository;

import jen.example.hibernate.entity.Pupil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PupilRepository extends JpaRepository<Pupil, Long> {
    boolean existsByGivenId(String givenId);
}
