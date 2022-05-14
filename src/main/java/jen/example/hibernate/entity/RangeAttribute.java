package jen.example.hibernate.entity;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class RangeAttribute extends Attribute {

    public RangeAttribute(String name, String description, int priority){
        super(name, description, priority);
    }

    @Override
    double calculate(double score) {
        return score * priority;
    }

    @Override
    double getMaxValue() {
        return 5;
    }
}
