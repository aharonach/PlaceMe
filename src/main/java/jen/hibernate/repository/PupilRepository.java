package jen.hibernate.repository;

import jen.hibernate.entity.Pupil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PupilRepository extends JpaRepository<Pupil, Long> {
    boolean existsByGivenId(String givenId);
}
