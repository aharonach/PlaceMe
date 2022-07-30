package jen.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jen.web.dto.PupilsConnectionsDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @ManyToMany
    private List<Pupil> pupils;
    @JsonIgnore
    @ToString.Exclude
    private transient PupilsConnectionsDto connectionsToInclude = new PupilsConnectionsDto(new HashMap<>());
    @JsonIgnore
    @ToString.Exclude
    private transient PupilsConnectionsDto connectionsToExclude = new PupilsConnectionsDto(new HashMap<>());
    @JsonIgnore
    @ToString.Exclude
    private transient List<Long> pupilIds;

    public PlacementClassroom(List<Pupil> pupils, PupilsConnectionsDto connectionsToInclude, PupilsConnectionsDto connectionsToExclude){
        this.pupils = pupils;
        this.connectionsToInclude = connectionsToInclude;
        this.connectionsToExclude = connectionsToExclude;
        pupilIds = pupils.stream().map(Pupil::getId).toList();
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

//    public double getSumMaxScoreOfPupils(){
//        return pupils.stream().mapToDouble(Pupil::getPupilMaxScore).sum();
//    }

    public long getNumOfPupils(){
        return pupils.size();
    }

    public long getNumOfPupilsByGender(Pupil.Gender gender){
        return pupils.stream().filter(p -> p.getGender()==gender).count();
    }

    public int getNumberOfWrongConnectionsToInclude(){
        int wrongConnections = 0;
        Map<Long, Set<Long>> connectionsMap = connectionsToInclude.getValues();

        for(Pupil pupil : pupils){
            if(connectionsMap.containsKey(pupil.getId())){
                long numOfIncludedPupil = connectionsMap.get(pupil.getId()).stream().filter(pupilIds::contains).count();
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
                wrongConnections += connectionsMap.get(pupil.getId()).stream().filter(pupilIds::contains).count();
            }
        }
        return wrongConnections;
    }
}