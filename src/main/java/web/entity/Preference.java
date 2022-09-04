package web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "preferences")
@NoArgsConstructor
public class Preference implements Comparable{
    @EmbeddedId
    private SelectorSelectedId selectorSelectedId = new SelectorSelectedId();

    private Boolean isSelectorWantToBeWithSelected;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @ToString.Exclude
    @JsonIgnore
    private Group group;

    public Preference(Pupil selector, Pupil selected, boolean isSelectorWantToBeWithSelected) throws SamePupilException {
        if(selector.getId().equals(selected.getId())){
            throw new SamePupilException();
        }

        this.selectorSelectedId.setSelectorId(selector.getId());
        this.selectorSelectedId.setSelectedId(selected.getId());
        this.isSelectorWantToBeWithSelected = isSelectorWantToBeWithSelected;
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

    @Override
    public int compareTo(Object o) {
        Preference other = (Preference)o;
        int compareBySelector = (int) (this.getSelectorSelectedId().getSelectorId() - other.getSelectorSelectedId().getSelectorId());
        int compareBySelected= (int) (this.getSelectorSelectedId().getSelectedId() - other.getSelectorSelectedId().getSelectedId());

        if(compareBySelector != 0){
            return compareBySelector;
        } else {
            return compareBySelected;
        }
    }

    public static class SamePupilException extends Exception {
        public SamePupilException(){
            super("Selector pupil cannot be equal to selected pupil");
        }
    }
}