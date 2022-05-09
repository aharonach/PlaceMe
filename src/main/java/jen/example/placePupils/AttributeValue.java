package jen.example.placePupils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AttributeValue {
    @Getter
    private final Attribute attribute;
    @Getter
    private final double value;

    public double getScore(){
        return attribute.calculate(value);
    }

    public double getMaxScore(){
        return attribute.calculate(attribute.getMaxValue());
    }
}
