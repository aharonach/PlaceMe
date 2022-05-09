package jen.example.placePupils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class Placement {

    @Getter
    private final List<ClassInfo> classes;

//    public double getPlacementScore(){
//        //System.out.println(getSumOfDeltasBetweenNumOfPupils() + "_" + getSumOfDeltasBetweenPupilsScores() + "_" + getSumOfDeltasBetweenClassScores());
//        // max value:  num of pupils ////   ////
//
//        // the target is to get the lowest score (A lower score is better)
//        return getSumOfDeltasBetweenNumOfPupils() * 10
//                + getSumOfDeltasBetweenPupilsScores() * 15
//                + getSumOfDeltasBetweenClassScores() * 10;
//    }
//
//    private double getSumOfDeltasBetweenNumOfPupils(){
//        double avgNumOfStudents = classes.stream().mapToDouble(ClassInfo::getNumOfPupils).sum() / classes.size();
//
//        return classes.stream()
//                .map(classInfo -> Math.abs(classInfo.getNumOfPupils() - avgNumOfStudents))
//                .reduce(0d, Double::sum);
//    }
//
//    private double getSumOfDeltasBetweenClassScores(){
//        double scoreOfAllClasses = classes.stream().mapToDouble(ClassInfo::getClassScore).sum();
//        double avgScore = scoreOfAllClasses / classes.size();
//
//        return classes.stream()
//                .map(classInfo -> Math.abs(classInfo.getClassScore() - avgScore))
//                .reduce(0d, Double::sum);
//    }
//
//    private double getSumOfDeltasBetweenPupilsScores(){
//        double scoreOfAllPupils = classes.stream().mapToDouble(ClassInfo::getSumScoreOfPupils).sum();
//        double avgScore = scoreOfAllPupils / classes.size();
//
//        return classes.stream()
//                .map(classInfo -> Math.abs(classInfo.getSumScoreOfPupils() - avgScore))
//                .reduce(0d, Double::sum);
//    }


    // score of 0 to 100, the target is to get the lowest score (A lower score is better)
    public double getPlacementScore(){
        return getPercentageOfPupilsNumber() * 0.15
                + getPercentageOfClassScores() * 0.25
                + getPercentageOfPupilsScores() * 0.6;
    }

    private double getPercentageOfPupilsNumber(){
        double numOfStudents = classes.stream().mapToDouble(ClassInfo::getNumOfPupils).sum();
        double avgNumOfStudents = numOfStudents / classes.size();

        double deltaBetweenNumOfStudents = classes.stream()
                .map(classInfo -> Math.abs(classInfo.getNumOfPupils() - avgNumOfStudents))
                .reduce(0d, Double::sum);

        return (deltaBetweenNumOfStudents / numOfStudents) * 100;
    }

    private double getPercentageOfClassScores(){
        double scoreOfAllClasses = classes.stream().mapToDouble(ClassInfo::getClassScore).sum();
        double avgScore = scoreOfAllClasses / classes.size();

        double deltaBetweenClassScores = classes.stream()
                .map(classInfo -> Math.abs(classInfo.getClassScore() - avgScore))
                .reduce(0d, Double::sum);

        return (deltaBetweenClassScores / scoreOfAllClasses) * 100;
    }

    private double getPercentageOfPupilsScores(){
        double scoreOfAllPupils = classes.stream().mapToDouble(ClassInfo::getSumScoreOfPupils).sum();
        double maxScoreOfAllPupils = classes.stream().mapToDouble(ClassInfo::getSumMaxScoreOfPupils).sum();

        return (scoreOfAllPupils / maxScoreOfAllPupils) * 100;
    }

    @Override
    public String toString() {
        return "Placement{" +
                "classes=" + classes +
                '}';
    }
}