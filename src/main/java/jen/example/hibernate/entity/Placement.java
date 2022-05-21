package jen.example.hibernate.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "placements")
public class Placement {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Setter(AccessLevel.NONE)
    private LocalDateTime createdTime = LocalDateTime.now();
    private String name;
    private int numberOfClasses;
    @ToString.Exclude
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Group group;


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
