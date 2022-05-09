package jen.example.placePupils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public final class Pupil implements Comparable<Pupil> {

    private final String name;
    @Getter
    private final Gender gender;
    private final List<AttributeValue> attributeValues;



    public double getPupilScore() {
        double totalScore = 0;
        for(AttributeValue attributeValue : attributeValues){
            totalScore += attributeValue.getScore();
        }
        return totalScore;
    }

    public double getPupilMaxScore() {
        double totalScore = 0;
        for(AttributeValue attributeValue : attributeValues){
            totalScore += attributeValue.getMaxScore();
        }
        return totalScore;
    }

    @Override
    public String toString() {
        return "Pupil{" +
                "name=" + name +
                ", gender=" + gender +
                ", score=" + getPupilScore() +
                '}';
    }

    @Override
    public int compareTo(Pupil o) {
        return (int) (getPupilScore() - o.getPupilScore());
    }

    public enum Gender {
        MALE, FEMALE
    }
}