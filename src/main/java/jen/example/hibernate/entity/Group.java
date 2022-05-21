package jen.example.hibernate.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Setter
@ToString
@NoArgsConstructor
@Getter
@Table(name = "groups")
public class Group  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdTime = LocalDateTime.now();
    private String name;
    private String description;
    @ManyToOne
    private Template template;

    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    @ManyToMany(mappedBy = "groups", cascade = CascadeType.ALL)
    private Set<Pupil> pupils = new LinkedHashSet<>();


    public Group(String name, String description, Template template){
        this.name = name;
        this.description = description;
        this.template = template;
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