package jen.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jen.web.dto.PupilsConnectionsDto;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PlacementClassroom extends BaseEntity {
    private String name;
    @ManyToOne
    @JoinColumn
    @JsonIgnore
    @ToString.Exclude
    private PlacementResult placementResult;

    @ToString.Exclude
    @JsonIgnore
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private transient List<Pupil> pupilsForAlgorithm;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Pupil> pupils;

    @JsonIgnore
    @ToString.Exclude
    private transient PupilsConnectionsDto connectionsToInclude = new PupilsConnectionsDto(new HashMap<>());
    @JsonIgnore
    @ToString.Exclude
    private transient PupilsConnectionsDto connectionsToExclude = new PupilsConnectionsDto(new HashMap<>());

    public PlacementClassroom(List<Pupil> pupilsForAlgorithm, PupilsConnectionsDto connectionsToInclude, PupilsConnectionsDto connectionsToExclude){
        this.pupilsForAlgorithm = pupilsForAlgorithm;
        this.pupils = new HashSet<>(this.pupilsForAlgorithm);

        this.connectionsToInclude = connectionsToInclude;
        this.connectionsToExclude = connectionsToExclude;
    }

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

    public long getNumOfPupils(){
        return pupils.size();
    }

    public long getNumOfPupilsByGender(Pupil.Gender gender){
        return pupils.stream().filter(p -> p.getGender()==gender).count();
    }

    @JsonIgnore
    private List<Long> getPupilIds(){
        return pupils.stream().map(Pupil::getId).toList();
    }

    public int getNumberOfWrongConnectionsToInclude(){
        int wrongConnections = 0;
        Map<Long, Set<Long>> connectionsMap = connectionsToInclude.getValues();

        for(Pupil pupil : pupils){
            if(connectionsMap.containsKey(pupil.getId())){
                long numOfIncludedPupil = connectionsMap.get(pupil.getId()).stream().filter(getPupilIds()::contains).count();
                if(numOfIncludedPupil < 1){
                    wrongConnections++;
                }
            }
        }
        return wrongConnections;
    }

    public int getNumberOfWrongConnectionsToExclude(){
        int wrongConnections = 0;
        Map<Long, Set<Long>> connectionsMap = connectionsToExclude.getValues();

        for(Pupil pupil : pupils){
            if(connectionsMap.containsKey(pupil.getId())){
                wrongConnections += connectionsMap.get(pupil.getId()).stream().filter(getPupilIds()::contains).count();
            }
        }
        return wrongConnections;
    }

    public void removePupilFromClass(Pupil pupil){
        pupils.remove(pupil);
    }
}