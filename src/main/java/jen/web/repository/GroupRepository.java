package jen.web.repository;

import jen.web.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> getAllByIdInOrderById(Collection<Long> id);
    Page<Group> getAllByIdIn(Collection<Long> id, Pageable pageable);
}
