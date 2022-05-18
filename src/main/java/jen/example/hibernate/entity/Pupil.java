package jen.example.hibernate.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "pupils")
public class Pupil {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique=true)
    private String givenId;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthDate;

    @OneToMany(mappedBy = "pupil")
    @ToString.Exclude // Aharon: Added mappedBy because without it, a new redundant table is created.
    private List<AttributeValue> attributeValues;

    public enum Gender {
        MALE, FEMALE
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Pupil pupil = (Pupil) o;
        return id != null && Objects.equals(id, pupil.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
