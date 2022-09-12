package web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import web.dto.PupilsConnectionsDto;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
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

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "classrooms")
    private Set<Pupil> pupils = new LinkedHashSet<>();

    @JsonIgnore
    @ToString.Exclude
    private transient PupilsConnectionsDto connectionsToInclude = new PupilsConnectionsDto(new HashMap<>());
    @JsonIgnore
    @ToString.Exclude
    private transient PupilsConnectionsDto connectionsToExclude = new PupilsConnectionsDto(new HashMap<>());

    @JsonIgnore
    int totalNumberOfMales;
    @JsonIgnore
    int totalNumberOfFemales;

    public PlacementClassroom(List<Pupil> pupilsForAlgorithm, PupilsConnectionsDto connectionsToInclude,
                              PupilsConnectionsDto connectionsToExclude, int totalNumberOfMales, int totalNumberOfFemales){
        this.pupilsForAlgorithm = pupilsForAlgorithm;
        this.pupils = new HashSet<>(this.pupilsForAlgorithm);

        this.connectionsToInclude = connectionsToInclude;
        this.connectionsToExclude = connectionsToExclude;

        this.totalNumberOfMales = totalNumberOfMales;
        this.totalNumberOfFemales = totalNumberOfFemales;
    }

    // score of 0 to 100, the target is to get the lowest score (A lower score is better)
    public double getClassScore(){
        double percentageOfWrongConnectionsToInclude = percentageRelativeToPupilsNumber(getNumberOfWrongConnectionsToInclude());
        double percentageOfWrongConnectionsToExclude = percentageRelativeToPupilsNumber(getNumberOfWrongConnectionsToExclude());
        double percentageOfMales = percentageRelativeToPupilsNumber(getDeltaBetweenMales());
        double percentageOfFemales = percentageRelativeToPupilsNumber(getDeltaBetweenFemales());
        double percentageOfMalesAndFemales = percentageRelativeToPupilsNumber(getDeltaBetweenMalesAndFemales());

        return percentageOfWrongConnectionsToInclude * 0.18
                + percentageOfWrongConnectionsToExclude * 0.13
                + percentageOfMales * 0.23
                + percentageOfFemales * 0.23
                + percentageOfMalesAndFemales * 0.23;
    }

    private double percentageRelativeToPupilsNumber(double value){
        return (value / (totalNumberOfFemales + totalNumberOfMales)) * 100;
    }

    public long getNumberOfMales(){
        return getNumOfPupilsByGender(Pupil.Gender.MALE);
    }

    public long getNumberOfFemales(){
        return getNumOfPupilsByGender(Pupil.Gender.FEMALE);
    }

    public long getDeltaBetweenMalesAndFemales(){
        long numOfMales = getNumberOfMales();
        long numOfFemales = getNumberOfFemales();

        return Math.abs(numOfMales - numOfFemales);
    }

    @JsonIgnore
    public long getDeltaBetweenMales(){
        long numOfMalesInClass = getNumberOfMales();

        return Math.abs(numOfMalesInClass - totalNumberOfMales);
    }

    @JsonIgnore
    public long getDeltaBetweenFemales(){
        long numOfFemalesInClass = getNumberOfFemales();

        return Math.abs(numOfFemalesInClass - totalNumberOfFemales);
    }

    @JsonIgnore
    public double getSumScoreOfPupils(){
        return pupils.stream().mapToDouble(Pupil::getPupilScore).sum();
    }

    @JsonIgnore
    public double getSumMaxScoreOfPupils(){
        return pupils.stream().mapToDouble(Pupil::getPupilMaxScore).sum();
    }

    public double getRelativeScoreOfPupils(){
        return (getSumScoreOfPupils() / getSumMaxScoreOfPupils()) * 100;
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

    public List<Pupil> getPupils() {
        return pupils.stream()
                .sorted((o1, o2) -> (int)(o1.getId() - o2.getId()))
                .collect(Collectors.toList());
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

    public void addPupilToClass(Pupil pupil){
        if(!pupils.contains(pupil)){
            pupils.add(pupil);
            pupil.addToClassrooms(this);
        }
    }

    public void removePupilFromClass(Pupil pupil){
        pupils.remove(pupil);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PlacementClassroom that = (PlacementClassroom) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}