package jen.web.repository;

import jen.web.entity.AttributeValue;
import jen.web.entity.PupilAttributeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, PupilAttributeId> {
    void deleteAllByPupilAttributeId_PupilId(Long pupilId);
}
