package jen.example.hibernate.entity;

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
    private PupilAttributeId PupilAttributeId;

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
        this.pupil = pupil;
        this.attribute = attribute;
        this.value = value;
    }

    public double getScore(){
        return attribute.calculate(value);
    }

    public double getMaxScore(){
        return attribute.calculate(attribute.getMaxValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AttributeValue that = (AttributeValue) o;
        return PupilAttributeId != null && Objects.equals(PupilAttributeId, that.PupilAttributeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(PupilAttributeId);
    }
}
