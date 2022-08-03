package jen.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "placements")
public class Placement extends BaseEntity {
    private String name;
    private int numberOfClasses;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @Fetch(FetchMode.JOIN)
    private Group group;

    @OneToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    private Set<PlacementResult> results = new LinkedHashSet<>();

    public Placement(String name, int numberOfClasses, Group group){
        this.name = name;
        this.numberOfClasses = numberOfClasses;
        this.group = group;
        group.getPlacements().add(this);
    }

    public Integer getNumberOfResults(){
        return results.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Placement placement = (Placement) o;
        return id != null && Objects.equals(id, placement.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public PlacementResult getResultById(Long resultId) throws ResultNotExistsException {
        Optional<PlacementResult> result = results.stream()
                .filter(r -> r.getId().equals(resultId))
                .findFirst();

        if(result.isEmpty()){
            throw new ResultNotExistsException(resultId);
        }

        return result.get();
    }

    public void removePupilFromAllResults(Pupil pupil){
        getResults().forEach(placementResult -> {
            placementResult.getClasses().forEach(placementClassroom -> {
                placementClassroom.removePupilFromClass(pupil);
            });
        });
    }

    public static class ResultNotExistsException extends Exception {
        public ResultNotExistsException(Long resutlId){
            super("Placement does not have result with Id '" + resutlId + "'.");
        }
    }
}
