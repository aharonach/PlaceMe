package jen.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mysql.cj.exceptions.NumberOutOfRange;
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
    @JsonIgnore
    private PupilAttributeId pupilAttributeId = new PupilAttributeId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pupilId")
    @ToString.Exclude
    @JsonIgnore
    private Pupil pupil;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("attributeId")
    @ToString.Exclude
    private Attribute attribute;

    @Column(name = "attribute_value")
    private double value;

    public AttributeValue(Pupil pupil, Attribute attribute, double value) throws ValueOutOfRangeException {
        this.pupilAttributeId.setPupilId(pupil.getId());
        this.pupilAttributeId.setAttributeId(attribute.getId());
        this.pupil = pupil;
        this.attribute = attribute;
        setValue(value);
    }

    public double getScore(){
        return attribute.calculate(value);
    }

    public void setValue(double value) throws ValueOutOfRangeException {
        if(value > this.attribute.maxValue() || value < this.attribute.minValue()){
            throw new ValueOutOfRangeException(this.attribute);
        }

        this.value = value;
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

    public static class ValueOutOfRangeException extends Exception {
        public ValueOutOfRangeException(Attribute attribute){
            super("Value out of range. Valid range is from '" + attribute.minValue() + "' to '" + attribute.maxValue() + "'.");
        }
    }
}
