package jen.web.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "pupils_attributes_values")
public class AttributeValue {
    @EmbeddedId
    private PupilAttributeId pupilAttributeId = new PupilAttributeId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pupilId")
    @ToString.Exclude
    private Pupil pupil;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("attributeId")
    @ToString.Exclude
    private Attribute attribute;

    @Column(name = "attribute_value")
    private double value;

    public AttributeValue(Pupil pupil, Attribute attribute, double value){
        this.pupilAttributeId.setPupilId(pupil.getId());
        this.pupilAttributeId.setAttributeId(attribute.getId());
        this.pupil = pupil;
        this.attribute = attribute;
        this.value = value;
    }

    public double getScore(){
        return attribute.calculate(value);
    }

    public double getMaxScore(){
        return attribute.calculate(attribute.maxValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AttributeValue that = (AttributeValue) o;
        return pupilAttributeId != null && Objects.equals(pupilAttributeId, that.pupilAttributeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pupilAttributeId);
    }
}
