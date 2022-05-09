package jen.example.placePupils;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public abstract class Attribute {
    protected final String name;
    protected final String description;
    protected final int priority;

    abstract double calculate(double score);

    abstract double getMaxValue();
}
