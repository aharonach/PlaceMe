package jen.web.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Map;
import java.util.Objects;

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
    @OneToMany
    @ToString.Exclude
    //@JsonIgnore
    @MapKey(name = "id")
    private Map<Long, PlacementResult> results;

    public Placement(String name, int numberOfClasses, Group group){
        this.name = name;
        this.numberOfClasses = numberOfClasses;
        this.group = group;
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
}
