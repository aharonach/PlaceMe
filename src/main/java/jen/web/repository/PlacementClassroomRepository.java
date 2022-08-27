package jen.web.repository;

import jen.web.entity.PlacementClassroom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface PlacementClassroomRepository extends JpaRepository<PlacementClassroom, Long> {
    Set<PlacementClassroom> getAllByIdIn(Collection<Long> id);
    Page<PlacementClassroom> getAllByIdIn(Collection<Long> id, Pageable pageable);
}
