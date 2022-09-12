package web.repository;

import web.entity.PlacementClassroom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface PlacementClassroomRepository extends JpaRepository<PlacementClassroom, Long> {
    List<PlacementClassroom> getAllByIdIn(Collection<Long> id);
    Page<PlacementClassroom> getAllByIdIn(Collection<Long> id, Pageable pageable);
}
