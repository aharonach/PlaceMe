package web.entity;

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
        // score is double: from 0.0 to 5.0
        // priority is int: from 1 to 10
        return score * priority;
    }

    @Override
    double minValue() {
        return 0;
    }

    @Override
    double maxValue() {
        return 5;
    }
}
