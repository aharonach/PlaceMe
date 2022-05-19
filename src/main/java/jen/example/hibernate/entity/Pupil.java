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
@NoArgsConstructor
@Table(name = "pupils")
public class Pupil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String givenId;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthDate;

    @OneToMany(mappedBy = "pupil") // Aharon: Added mappedBy because without it, a new redundant table is created.
    @ToString.Exclude
    private List<AttributeValue> attributeValues;

    public Pupil(String givenId, String firstName, String lastName, Gender gender, LocalDate birthDate){
        this.givenId = givenId; // todo: validate that givenId contains only digits
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
    }

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
