package jen.example.hibernate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pupils")
public class Pupil {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;

    @OneToMany(mappedBy = "pupil") // Aharon: Added mappedBy because without it, a new redundant table is created.
    private List<AttributeValue> attributeValues;

    public enum Gender {
        MALE, FEMALE
    }
}
