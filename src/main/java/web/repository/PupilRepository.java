package web.repository;

import web.entity.Pupil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PupilRepository extends JpaRepository<Pupil, Long> {
    boolean existsByGivenId(String givenId);
    Optional<Pupil> getPupilByGivenId(String givenId);
    Page<Pupil> getAllByIdIn(Collection<Long> id, Pageable pageable);
    List<Pupil> getAllByIdIn(Collection<Long> id);
}
