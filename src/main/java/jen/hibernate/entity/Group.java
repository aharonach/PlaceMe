package jen.hibernate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;

@Entity
@Setter
@ToString
@NoArgsConstructor
@Getter
@Table(name = "groups")
public class Group extends BaseEntity {
    private String name;
    private String description;
    @JsonIgnore
    @ManyToOne
    private Template template;
    @ToString.Exclude
    @JsonIgnore
    @ManyToMany(mappedBy = "groups", cascade = CascadeType.ALL)
    private Set<Pupil> pupils = new LinkedHashSet<>();

//    @OneToMany
//    @ToString.Exclude
//    private Set<Placement> placements = new LinkedHashSet<>();


    public Group(String name, String description, Template template){
        this.name = name;
        this.description = description;
        this.template = template;
    }

    public void setPupils(Set<Pupil> pupils) {
        getPupils().forEach(this::removePupil);
        pupils.forEach(this::addPupil);
    }

    public boolean isContains(Pupil pupil){
        return pupils.contains(pupil);
    }

    public void addPupil(Pupil pupil){
        pupils.add(pupil);
        pupil.addToGroup(this);
    }

    public void removePupil(Pupil pupil){
        pupils.remove(pupil);
        pupil.removeFromGroup(this);
    }

    public Set<Pupil> getPupils(){
        return Collections.unmodifiableSet(pupils);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Group group = (Group) o;
        return id != null && Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}