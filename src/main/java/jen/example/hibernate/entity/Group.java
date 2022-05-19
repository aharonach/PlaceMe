package jen.example.hibernate.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Setter
@ToString
@NoArgsConstructor
@Getter
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @ManyToOne
    private Template template;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "PUPILS_GROUPS",
            joinColumns = @JoinColumn(name = "groups_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "pupils_id", referencedColumnName = "id"))
    private List<Pupil> pupils = new ArrayList<>();  // todo: maybe it is better to use set instead of list


    public Group(String name, String description, Template template){
        this.name = name;
        this.description = description;
        this.template = template;
    }

    public List<Pupil> getPupils() {
        return Collections.unmodifiableList(pupils);
    }

    public void addPupil(Pupil pupil){
        pupils.add(pupil);
    }

    public void removePupil(Pupil pupil){
        if(pupils.contains(pupil)){
            pupils.remove(pupil);
        }
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