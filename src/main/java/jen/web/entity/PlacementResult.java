package jen.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "placement_results")
public class PlacementResult extends BaseEntity {
    private String name;
    private String description;

    private boolean selected;

    @ManyToOne
    @JoinColumn(name = "placement_id")
    @JsonIgnore
    @ToString.Exclude
    private Placement placement;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ToString.Exclude
    @JsonIgnore
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private transient List<PlacementClassroom> classesForAlgorithm = new ArrayList<>(); // list is needed for the algorithm

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "placementResult")
    private Set<PlacementClassroom> classes = new LinkedHashSet<>(); // Set is better for hibernate

    public PlacementResult(List<PlacementClassroom> classesForAlgorithm){
        this.classesForAlgorithm = classesForAlgorithm;
        this.classes = new HashSet<>(this.classesForAlgorithm);
        this.status = Status.IN_PROGRESS;
    }

    public PlacementResult(String name, String description){
        this.name = name;
        this.description = description;
        this.status = Status.IN_PROGRESS;
    }

    public PlacementResult(){
        this.name = "Name";
        this.description = "Description";
        this.status = Status.IN_PROGRESS;
    }

    public Set<PlacementClassroom> getClasses() {
        return Collections.unmodifiableSet(classes);
    }

    // score of 0 to 100, the target is to get the lowest score (A lower score is better)
    public double getPlacementScore() {

        return getPercentageOfPupilsNumber() * 0.2
                + getPercentageOfClassScores() * 0.4
                + getPercentageOfPupilsScores() * 0.4;
    }

    private double getPercentageOfPupilsNumber(){
        double numOfStudents = classes.stream().mapToDouble(PlacementClassroom::getNumOfPupils).sum();
        double avgNumOfStudents = numOfStudents / classes.size();

        double deltaBetweenNumOfStudents = classes.stream()
                .map(classInfo -> Math.abs(classInfo.getNumOfPupils() - avgNumOfStudents))
                .reduce(0d, Double::sum);

        return (deltaBetweenNumOfStudents / numOfStudents) * 100;
    }

    private double getPercentageOfClassScores(){
        double scoreOfAllClasses = classes.stream().mapToDouble(PlacementClassroom::getClassScore).sum();
        double avgScore = scoreOfAllClasses / classes.size();

        double deltaBetweenClassScores = classes.stream()
                .map(classInfo -> Math.abs(classInfo.getClassScore() - avgScore))
                .reduce(0d, Double::sum);

        return (deltaBetweenClassScores / scoreOfAllClasses) * 100;
    }

    private double getPercentageOfPupilsScores(){
        double scoreOfAllPupils = classes.stream().mapToDouble(PlacementClassroom::getSumScoreOfPupils).sum();
        double maxScoreOfAllPupils = classes.stream().mapToDouble(PlacementClassroom::getSumMaxScoreOfPupils).sum();

        return (scoreOfAllPupils / maxScoreOfAllPupils) * 100;
    }

    private int getNumOfPupils(){
        return (int) classesForAlgorithm.stream().mapToLong(PlacementClassroom::getNumOfPupils).sum();
    }

    public boolean isCompleted() {
        return Status.COMPLETED.equals(getStatus());
    }

    public enum Status {
        IN_PROGRESS, COMPLETED, FAILED
    }

    public static class NotCompletedException extends Exception {
        public NotCompletedException() {
            super("Placement result is not completed.");
        }
    }
}