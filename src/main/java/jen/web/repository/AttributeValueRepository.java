package jen.web.repository;

import jen.web.entity.AttributeValue;
import jen.web.entity.PupilAttributeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, PupilAttributeId> {
    void deleteAttributeValuesByAttributeId(Long attributeId);
    List<AttributeValue> getAllByPupilAttributeIdInOrderByPupilAttributeId(Set<PupilAttributeId> pupilAttributeId);
}
