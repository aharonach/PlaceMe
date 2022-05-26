package jen.hibernate.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "placement_results")
public class PlacementResult extends BaseEntity {
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "placement_id")
    private Placement placement;

    @OneToMany(mappedBy = "placementResult")
    private List<PlacementClassroom> classes;

    public PlacementResult(List<PlacementClassroom> classes){
        this.classes = classes;
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
        double avgNumOfStudents = classes.stream().mapToDouble(PlacementClassroom::getNumOfPupils).sum() / classes.size();

        return classes.stream()
                .map(classInfo -> Math.abs(classInfo.getNumOfPupils() - avgNumOfStudents))
                .reduce(0d, Double::sum);
    }

    private double getSumOfDeltasBetweenClassScores(){
        double scoreOfAllClasses = classes.stream().mapToDouble(PlacementClassroom::getClassScore).sum();
        double avgScore = scoreOfAllClasses / classes.size();

        return classes.stream()
                .map(classInfo -> Math.abs(classInfo.getClassScore() - avgScore))
                .reduce(0d, Double::sum);
    }

    private double getSumOfDeltasBetweenPupilsScores(){
        double scoreOfAllPupils = classes.stream().mapToDouble(PlacementClassroom::getSumScoreOfPupils).sum();
        double avgScore = scoreOfAllPupils / classes.size();

        return classes.stream()
                .map(classInfo -> Math.abs(classInfo.getSumScoreOfPupils() - avgScore))
                .reduce(0d, Double::sum);
    }

//    private double getPercentageOfPupilsNumber(){
//        double numOfStudents = classes.stream().mapToDouble(PlacementClassroom::getNumOfPupils).sum();
//        double avgNumOfStudents = numOfStudents / classes.size();
//
//        double deltaBetweenNumOfStudents = classes.stream()
//                .map(placementClassroom -> Math.abs(placementClassroom.getNumOfPupils() - avgNumOfStudents))
//                .reduce(0d, Double::sum);
//
//        return (deltaBetweenNumOfStudents / numOfStudents) * 100;
//    }
//
//    private double getPercentageOfClassScores(){
//        double scoreOfAllClasses = classes.stream().mapToDouble(PlacementClassroom::getClassScore).sum();
//        double avgScore = scoreOfAllClasses / classes.size();
//
//        double deltaBetweenClassScores = classes.stream()
//                .map(placementClassroom -> Math.abs(placementClassroom.getClassScore() - avgScore))
//                .reduce(0d, Double::sum);
//
//        return (deltaBetweenClassScores / scoreOfAllClasses) * 100;
//    }
//
//    private double getPercentageOfPupilsScores(){
//        double scoreOfAllPupils = classes.stream().mapToDouble(PlacementClassroom::getSumScoreOfPupils).sum();
//        double maxScoreOfAllPupils = classes.stream().mapToDouble(PlacementClassroom::getSumMaxScoreOfPupils).sum();
//
//        return (scoreOfAllPupils / maxScoreOfAllPupils) * 100;
//    }


    public void printClassInfo(){
        getClasses().forEach(classInfo -> {
            System.out.print("[Pupils: " + classInfo.getNumOfPupils() + " (Males: " + classInfo.getNumOfPupilsByGender(Pupil.Gender.MALE) + " ,Females: " + classInfo.getNumOfPupilsByGender(Pupil.Gender.FEMALE) + " ,Delta: " + classInfo.getDeltaBetweenMalesAndFemales() + ") ");
            System.out.print("Pupils Score: " + classInfo.getSumScoreOfPupils() + " Class Score: " + classInfo.getClassScore() + " ");
            System.out.print("Include is OK: " + (classInfo.getNumberOfWrongConnectionsToInclude() == 0) + ", ");
            System.out.print("Exclude is OK: " + (classInfo.getNumberOfWrongConnectionsToExclude() == 0));
            System.out.print("] | ");
            System.out.println(classInfo.getPupils());
        });
    }
    @Override
    public String toString() {
        return "PlacementResult{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", placement=" + placement +
                ", classes=" + classes +
                '}';
    }
}