package web.repository;

import web.entity.Placement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface PlacementRepository extends JpaRepository<Placement, Long> {
    Set<Placement> getAllByIdIn(Collection<Long> id);
}
