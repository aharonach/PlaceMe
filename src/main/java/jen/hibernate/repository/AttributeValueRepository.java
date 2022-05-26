package jen.hibernate.repository;

import jen.hibernate.entity.AttributeValue;
import jen.hibernate.entity.PupilAttributeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, PupilAttributeId> {
}
