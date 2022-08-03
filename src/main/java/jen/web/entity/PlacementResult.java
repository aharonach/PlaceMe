package jen.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "placement_results")
public class PlacementResult extends BaseEntity {
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "placement_id")
    private Placement placement;

    @ToString.Exclude
    @JsonIgnore
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private transient List<PlacementClassroom> classesForAlgorithm = new ArrayList<>(); // list is needed for the algorithm

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "placementResult")
    private Set<PlacementClassroom> classes; // Set is better for hibernate

    public PlacementResult(List<PlacementClassroom> classesForAlgorithm){
        this.classesForAlgorithm = classesForAlgorithm;
        this.classes = new HashSet<>(this.classesForAlgorithm);
    }

    public Set<PlacementClassroom> getClasses() {
        return Collections.unmodifiableSet(classes);
    }

    // score of 0 to 100, the target is to get the lowest score (A lower score is better)
    public double getPlacementScore(){
        // the target is to get the lowest score (A lower score is better)
        return getSumOfDeltasBetweenNumOfPupils() * 10
                + getSumOfDeltasBetweenPupilsScores() * 15
                + getSumOfDeltasBetweenClassScores() * 10;

//        return getPercentageOfPupilsNumber() * 0.15
//                + getPercentageOfClassScores() * 0.25
//                + getPercentageOfPupilsScores() * 0.6;
    }

    private double getSumOfDeltasBetweenNumOfPupils(){
        double avgNumOfStudents = classesForAlgorithm.stream().mapToDouble(PlacementClassroom::getNumOfPupils).sum() / classesForAlgorithm.size();

        return classesForAlgorithm.stream()
                .map(classInfo -> Math.abs(classInfo.getNumOfPupils() - avgNumOfStudents))
                .reduce(0d, Double::sum);
    }

    private double getSumOfDeltasBetweenClassScores(){
        double scoreOfAllClasses = classesForAlgorithm.stream().mapToDouble(PlacementClassroom::getClassScore).sum();
        double avgScore = scoreOfAllClasses / classesForAlgorithm.size();

        return classesForAlgorithm.stream()
                .map(classInfo -> Math.abs(classInfo.getClassScore() - avgScore))
                .reduce(0d, Double::sum);
    }

    private double getSumOfDeltasBetweenPupilsScores(){
        double scoreOfAllPupils = classesForAlgorithm.stream().mapToDouble(PlacementClassroom::getSumScoreOfPupils).sum();
        double avgScore = scoreOfAllPupils / classesForAlgorithm.size();

        return classesForAlgorithm.stream()
                .map(classInfo -> Math.abs(classInfo.getSumScoreOfPupils() - avgScore))
                .reduce(0d, Double::sum);
    }
}