package jen.example.hibernate.repository;

import jen.example.hibernate.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Set<Group> getAllByIdIn(Collection<Long> id);
}
