package jen.example.hibernate.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "pupils_attributes_values")
public class AttributeValue {
    @EmbeddedId
    private PupilAttributeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pupilId")
    private Pupil pupil;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("attributeId")
    private Attribute attribute;

    @Column(name = "attribute_value")
    private double value;

    public AttributeValue(Pupil pupil, Attribute attribute, double value){
        this.pupil = pupil;
        this.attribute = attribute;
        this.value = value;
        this.id = new PupilAttributeId(pupil.getId(), attribute.getId());
    }

    public double getScore(){
        return attribute.calculate(value);
    }

    public double getMaxScore(){
        return attribute.calculate(attribute.getMaxValue());
    }
}
