package jen.web.repository;

import jen.web.entity.PlacementResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface PlacementResultRepository extends JpaRepository<PlacementResult, Long> {
    Page<PlacementResult> getAllByIdIn(Collection<Long> id, Pageable pageable);
}
