package web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "placementResult")
    private Set<PlacementClassroom> classes = new LinkedHashSet<>(); // Set is better for hibernate

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "group_id")
    private Group group;

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

    public Set<Long> getClassesIds(){
        return this.getClasses().stream().map(BaseEntity::getId).collect(Collectors.toSet());
    }

    public int getTotalNumberOfMales(){
        if(classes.stream().findFirst().isPresent()){
            return classes.stream().findFirst().get().totalNumberOfMales;
        }
        return 0;
    }

    public int getTotalNumberOfFemales(){
        if(classes.stream().findFirst().isPresent()){
            return classes.stream().findFirst().get().totalNumberOfFemales;
        }
        return 0;
    }

    // score of 0 to 100, the target is to get the lowest score (A lower score is better)
    public double getPlacementScore() {

        return getPercentageOfPupilsNumber() * 0.2
                + getPercentageOfClassScores() * 0.45
                + getPercentageOfPupilsScores() * 0.35;
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
        double sumRelativeScoreOfAllPupils = classes.stream().mapToDouble(PlacementClassroom::getRelativeScoreOfPupils).sum();
        return sumRelativeScoreOfAllPupils / classes.size();
    }

    private int getNumOfPupils(){
        return (int) classesForAlgorithm.stream().mapToLong(PlacementClassroom::getNumOfPupils).sum();
    }

    public boolean isCompleted() {
        return Status.COMPLETED.equals(getStatus());
    }

    public Long getGroupId() { return placement.getGroupId(); }

    public int getNumberOfClasses(){
        return classes.size();
    }

    public enum Status {
        IN_PROGRESS, COMPLETED, FAILED
    }

    public static class NotCompletedException extends Exception {
        public NotCompletedException() {
            super("Placement result is not completed.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PlacementResult that = (PlacementResult) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}