package jen.web.repository;

import jen.web.entity.PlacementClassroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacementClassroomRepository extends JpaRepository<PlacementClassroom, Long> {
}
