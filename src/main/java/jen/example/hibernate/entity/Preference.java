package jen.example.hibernate.entity;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "preference")
public class Preference {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private Pupil selector;

    @OneToOne
    private Pupil selected;

    private Boolean preference; // True = Selector wants to be with Selected

    @ManyToOne
    private Placement placement;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Preference preference = (Preference) o;
        return id != null && Objects.equals(id, preference.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
