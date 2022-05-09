package jen.example.hibernate.entity;

import javax.persistence.Entity;

@Entity
public class RangeAttribute extends Attribute{
    @Override
    double calculate(double score) {
        return score * priority;
    }

    @Override
    double getMaxValue() {
        return 5;
    }
}
