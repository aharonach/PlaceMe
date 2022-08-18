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

    @OneToMany(fetch = FetchType.EAGER, mappedBy="placement")
    @JsonIgnore
    @ToString.Exclude
    private Set<PlacementResult> results = new LinkedHashSet<>();

    public Placement(String name, int numberOfClasses, Group group){
        this.name = name;
        this.numberOfClasses = numberOfClasses;
        this.group = group;
        group.addPlacement(this); //@todo: check
    }

    public Long getGroupId(){
        if(group != null){
            return group.getId();
        }
        return null;
    }

    public Integer getNumberOfResults(){
        return results.size();
    }

    public Set<PlacementResult> getResults() {
        return Collections.unmodifiableSet(results);
    }

    public void addResult(PlacementResult placementResult){
        results.add(placementResult);
    }

    public void removeResult(PlacementResult placementResult){
        results.remove(placementResult);
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
        return results.stream()
                .filter(r -> r.getId().equals(resultId))
                .findFirst()
                .orElseThrow(() -> new ResultNotExistsException(resultId));
    }

    public void removePupilFromAllResults(Pupil pupil){
        getResults().forEach(placementResult -> {
            placementResult.getClasses().forEach(placementClassroom -> {
                placementClassroom.removePupilFromClass(pupil);
            });
        });
    }

    public PlacementResult getSelectedResult() {
        return getResults().stream()
                .filter(PlacementResult::isSelected)
                .findFirst()
                .orElse(null);
    }

    public void setSelectedResult(PlacementResult result) throws PlacementResult.NotCompletedException {
        if (getResults().contains(result)) {
            if(!result.isCompleted()) {
                throw new PlacementResult.NotCompletedException();
            }

            getResults().forEach(existsResult -> existsResult.setSelected(false));
            result.setSelected(true);
        }
    }

    public static class ResultNotExistsException extends Exception {
        public ResultNotExistsException(Long resultId){
            super("Placement does not have result with Id '" + resultId + "'.");
        }
    }

    public static class NoSelectedResultException extends Exception {
        public NoSelectedResultException(){
            super("Placement does not have a selected result.");
        }
    }
}
