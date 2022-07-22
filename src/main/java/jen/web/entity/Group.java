package jen.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Fetch(FetchMode.JOIN)
    private Set<Pupil> pupils = new LinkedHashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "group")
    @JsonIgnore
    private Set<Preference> preferences = new LinkedHashSet<>();


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


    public void addPreference(Pupil selector, Pupil selected, boolean wantToBeTogether) throws Preference.SamePupilException {
        preferences.add(new Preference(selector, selected, wantToBeTogether, this));
    }

    public Set<SelectorSelectedId> getPreferencesIdForPupils(Pupil selector, Pupil selected){
        return preferences.stream().map(Preference::getSelectorSelectedId)
                .filter(selectorSelectedId -> selectorSelectedId.getSelectorId().equals(selector.getId()))
                .filter(selectorSelectedId -> selectorSelectedId.getSelectedId().equals(selected.getId()))
                .collect(Collectors.toSet());
    }

    public Set<SelectorSelectedId> getAllPreferencesIdForPupil(Pupil pupil){
        return preferences.stream().map(Preference::getSelectorSelectedId)
                .filter(selectorSelectedId -> selectorSelectedId.getSelectorId().equals(pupil.getId()) || selectorSelectedId.getSelectedId().equals(pupil.getId()))
                .collect(Collectors.toSet());
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