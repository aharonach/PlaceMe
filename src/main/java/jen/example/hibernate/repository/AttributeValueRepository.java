package jen.example.hibernate.repository;

import jen.example.hibernate.entity.AttributeValue;
import jen.example.hibernate.entity.PupilAttributeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, PupilAttributeId> {
}
