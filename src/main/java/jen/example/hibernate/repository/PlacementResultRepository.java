package jen.example.hibernate.repository;

import jen.example.hibernate.entity.PlacementResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacementResultRepository extends JpaRepository<PlacementResult, Long> {
}
