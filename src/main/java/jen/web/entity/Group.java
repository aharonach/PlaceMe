package jen.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @ToString.Exclude
    @ManyToOne
    private Template template;
    @ToString.Exclude
    @JsonIgnore
    @ManyToMany(mappedBy = "groups")
    @Fetch(FetchMode.JOIN)
    private Set<Pupil> pupils = new LinkedHashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "group")
    @JsonIgnore
    private Set<Preference> preferences = new LinkedHashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "group")
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
    @JsonIgnore
    public Template getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(Template template) {
        if(this.template != null){
            this.template.removeGroup(this);
        }
        this.template = template;
        if(template != null){
            template.addGroup(this);
        }
    }

    public Integer getNumberOfPupils(){
        return pupils.size();
    }

    public Set<Long> getPlacementIds(){
        return placements.stream().map(BaseEntity::getId).collect(Collectors.toSet());
    }

    public void addPlacement(Placement placement){
        placements.add(placement);
    }

    public void removePlacement(Placement placement){
        placements.remove(placement);
    }

    public void setPupils(Set<Pupil> pupils) {
        this.pupils.forEach(this::removePupil);
        pupils.forEach(this::addPupil);
    }

    public boolean isContains(Pupil pupil){
        return pupils.contains(pupil);
    }

    public void addPupil(Pupil pupil){
        if(!pupils.contains(pupil)){
            pupils.add(pupil);
            pupil.addToGroup(this);
        }
    }

    public void removePupil(Pupil pupil){
        if(pupils.contains(pupil)){
            pupils.remove(pupil);
            pupil.removeFromGroup(this);
        }
    }

    public void clearPupils(){
        pupils.clear();
    }

    public Set<Pupil> getPupils(){
        return Collections.unmodifiableSet(pupils);
    }

    public void clearPreferences(){
        preferences.clear();
    }

    public Set<Preference> getPreferences() {
        return Collections.unmodifiableSet(preferences);
    }

    public Set<Placement> getPlacements() {
        return Collections.unmodifiableSet(placements);
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

    public void addOrUpdatePreference(Pupil selector, Pupil selected, boolean wantToBeTogether) throws Preference.SamePupilException {
        Optional<Preference> optionalPreference = getPreferenceForPupils(selector.getId(), selected.getId());
        Preference preference;
        if(optionalPreference.isPresent()){
            preference = optionalPreference.get();
            preference.setIsSelectorWantToBeWithSelected(wantToBeTogether);
        } else {
            preference = new Preference(selector, selected, wantToBeTogether);
            preferences.add(preference);
            preference.setGroup(this);
        }
    }

    public Optional<Preference> getPreferenceForPupils(Long selectorId, Long selectedId){
        return preferences.stream()
                .filter(preference -> preference.getSelectorSelectedId().getSelectorId().equals(selectorId))
                .filter(preference -> preference.getSelectorSelectedId().getSelectedId().equals(selectedId))
                .findFirst();
    }

    public Set<Preference> getAllPreferencesForPupil(Long pupilId){
        return preferences.stream()
                .filter(preference -> preference.getSelectorSelectedId().getSelectorId().equals(pupilId)
                        || preference.getSelectorSelectedId().getSelectedId().equals(pupilId))
                .collect(Collectors.toSet());
    }

    public void deletePreference(Preference preference){
        preferences.remove(preference);
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