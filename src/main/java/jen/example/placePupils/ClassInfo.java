package jen.example.placePupils;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public final class ClassInfo{

    private final List<Pupil> pupils;
    private final PupilsConnections connectionsToInclude;
    private final PupilsConnections connectionsToExclude;

    // score of 0 to 100, the target is to get the lowest score (A lower score is better)
    public double getClassScore(){
        double percentageOfWrongConnectionsToInclude = percentageRelativeToPupilsNumber(getNumberOfWrongConnectionsToInclude());
        double percentageOfWrongConnectionsToExclude = percentageRelativeToPupilsNumber(getNumberOfWrongConnectionsToExclude());
        double percentageOfMalesAndFemales = percentageRelativeToPupilsNumber(getDeltaBetweenMalesAndFemales());

        //System.out.println(percentageOfWrongConnectionsToInclude + " ~ " + percentageOfWrongConnectionsToExclude + " ~ " + percentageOfMalesAndFemales);

        return percentageOfWrongConnectionsToInclude * 0.25
                + percentageOfWrongConnectionsToExclude * 0.2
                + percentageOfMalesAndFemales * 0.55;
    }

    private double percentageRelativeToPupilsNumber(double value){
        return (value / pupils.size()) * 100;
    }

    public long getDeltaBetweenMalesAndFemales(){
        long numOfMales = getNumOfPupilsByGender(Pupil.Gender.MALE);
        long numOfFemales = getNumOfPupilsByGender(Pupil.Gender.FEMALE);

        return Math.abs(numOfMales - numOfFemales);
    }

    public double getSumScoreOfPupils(){
        return pupils.stream().mapToDouble(Pupil::getPupilScore).sum();
    }

    public double getSumMaxScoreOfPupils(){
        return pupils.stream().mapToDouble(Pupil::getPupilMaxScore).sum();
    }

    public long getNumOfPupils(){
        return pupils.size();
    }

    public long getNumOfPupilsByGender(Pupil.Gender gender){
        return pupils.stream().filter(p -> p.getGender()==gender).count();
    }

    public int getNumberOfWrongConnectionsToInclude(){
        int wrongConnections = 0;
        Map<Pupil, List<Pupil>> connectionsMap = connectionsToInclude.getValues();

        for(Pupil pupil : pupils){
            if(connectionsMap.containsKey(pupil)){
                long numOfIncludedPupil = connectionsMap.get(pupil).stream().filter(pupils::contains).count();
                if(numOfIncludedPupil < 1){
                    wrongConnections++;
                }
            }
        }
        return wrongConnections;
    }

    public int getNumberOfWrongConnectionsToExclude(){
        int wrongConnections = 0;
        Map<Pupil, List<Pupil>> connectionsMap = connectionsToExclude.getValues();

        for(Pupil pupil : pupils){
            if(connectionsMap.containsKey(pupil)){
                wrongConnections += connectionsMap.get(pupil).stream().filter(pupils::contains).count();
            }
        }
        return wrongConnections;
    }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "pupils=" + pupils +
                '}';
    }
}