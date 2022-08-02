package jen.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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
    @ToString.Exclude
    @ManyToOne
    private Template template;
    @ToString.Exclude
    @JsonIgnore
    @ManyToMany(mappedBy = "groups", cascade = {CascadeType.ALL})
    @Fetch(FetchMode.JOIN)
    private Set<Pupil> pupils = new LinkedHashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "group")
    @JsonIgnore
    private Set<Preference> preferences = new LinkedHashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Placement> placements = new LinkedHashSet<>();


    public Group(String name, String description, Template template){
        this.name = name;
        this.description = description;
        this.template = template;
    }

    public Long getTemplateId(){
        if(template != null){
            return template.getId();
        }
        return null;
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

    public Pupil getPupilById(Long pupilId) throws PupilNotBelongException {
        Optional<Pupil> pupil = pupils.stream()
                .filter(p -> p.getId().equals(pupilId))
                .findFirst();

        if(pupil.isEmpty()){
            throw new PupilNotBelongException(pupilId, this);
        }

        return pupil.get();
    }

    public void addPreference(Pupil selector, Pupil selected, boolean wantToBeTogether) throws Preference.SamePupilException {
        preferences.add(new Preference(selector, selected, wantToBeTogether, this));
    }

    public Set<Preference> getPreferencesForPupils(Long selectorId, Long selectedId){
        return preferences.stream()
                .filter(preference -> preference.getSelectorSelectedId().getSelectorId().equals(selectorId))
                .filter(preference -> preference.getSelectorSelectedId().getSelectedId().equals(selectedId))
                .collect(Collectors.toSet());
    }

    public Set<Preference> getAllPreferencesForPupil(Long pupilId){
        return preferences.stream()
                .filter(preference -> preference.getSelectorSelectedId().getSelectorId().equals(pupilId)
                        || preference.getSelectorSelectedId().getSelectedId().equals(pupilId))
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

    public static class PupilNotBelongException extends Exception {
        public PupilNotBelongException(Long pupilId, Group group){
            super("Pupil Id '" + pupilId + "' is not in '" + group.getName() + "' group.");
        }
    }
}