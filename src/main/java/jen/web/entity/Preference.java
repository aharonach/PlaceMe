package jen.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "preferences")
public class Preference {
    @EmbeddedId
    @JsonIgnore
    private SelectorSelectedId selectorSelectedId = new SelectorSelectedId();

    private Boolean isSelectorWantToBeWithSelected;

    @ManyToOne
    private Placement placement;

    public Preference(Pupil selector, Pupil selected, boolean isSelectorWantToBeWithSelected, Placement placement){
        this.selectorSelectedId.setSelectorId(selector.getId());
        this.selectorSelectedId.setSelectedId(selected.getId());
        this.isSelectorWantToBeWithSelected = isSelectorWantToBeWithSelected;
        this.placement = placement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Preference that = (Preference) o;
        return selectorSelectedId != null && Objects.equals(selectorSelectedId, that.selectorSelectedId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectorSelectedId);
    }
}