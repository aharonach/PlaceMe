package jen.example.hibernate.repository;

import jen.example.hibernate.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    List<Attribute> getAllByIdIn(Collection<Long> ids);
}
