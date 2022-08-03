package jen.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jen.web.util.IsraeliIdValidator;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "pupils")
public class Pupil extends BaseEntity {
    public static final String DIGITS_REGEX = "\\d+";

    @NaturalId(mutable=true)
    private String givenId;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthDate;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pupil")
    @JsonIgnore
    private Set<AttributeValue> attributeValues = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "pupils_groups",
            joinColumns = @JoinColumn(name = "pupils_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "groups_id", referencedColumnName = "id"))
    @Fetch(FetchMode.JOIN)
    private Set<Group> groups = new LinkedHashSet<>();

    public Pupil(String givenId, String firstName, String lastName, Gender gender, LocalDate birthDate)
            throws GivenIdContainsProhibitedCharsException, GivenIdIsNotValidException {
        setGivenId(givenId);
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public Set<AttributeValue> getAttributeValues() {
        return Collections.unmodifiableSet(attributeValues);
    }

    public void setGivenId(String givenId) throws GivenIdContainsProhibitedCharsException, GivenIdIsNotValidException {

        if(!givenId.matches(DIGITS_REGEX)){
            throw new GivenIdContainsProhibitedCharsException();
        }
        if(!IsraeliIdValidator.isValid(givenId)){
            throw new GivenIdIsNotValidException();
        }

        this.givenId = givenId;
    }

    public void addAttributeValue(Group group, Long attributeId, Double value) throws Group.PupilNotBelongException, Template.AttributeNotBelongException {

        verifyPupilInGroup(group);
        Attribute attribute = group.getTemplate().getAttribute(attributeId);

        // Then find if the attribute is already has a value for pupil,
        // If it does, update the value, otherwise create a new AttributeValue.
        attributeValues.stream()
                .filter(attributeValue -> attributeValue.getAttribute().equals(attribute))
                .findFirst()
                .ifPresentOrElse(attributeValue -> attributeValue.setValue(value),
                        () -> attributeValues.add(new AttributeValue(this, attribute, value)));
    }

    public Set<AttributeValue> getAttributeValues(Group group, Set<Long> attributeIds) throws Group.PupilNotBelongException {

        verifyPupilInGroup(group);
        Template template = group.getTemplate();

        // get all AttributeValues by attribute ids for specific group
        return getAttributeValues().stream()
                .filter(attributeValue ->  template.getAttributes().contains(attributeValue.getAttribute()))
                .filter(attributeValue ->  attributeIds.contains(attributeValue.getAttribute().getId()))
                .collect(Collectors.toSet());
    }

    public Set<AttributeValue> getAttributeValues(Group group) throws Group.PupilNotBelongException {

        verifyPupilInGroup(group);
        Template template = group.getTemplate();

        // get all AttributeValues by attribute ids for specific group
        return getAttributeValues().stream()
                .filter(attributeValue ->  template.getAttributes().contains(attributeValue.getAttribute()))
                .collect(Collectors.toSet());
    }

    @JsonProperty
    public void setGroups(Set<Group> newGroups){
        groups = newGroups;
    }

    @JsonIgnore
    public Set<Group> getGroups(){
        return Collections.unmodifiableSet(groups);
    }

    public List<Long> getGroupIds(){
        return groups.stream().map(BaseEntity::getId).collect(Collectors.toList());
    }

    public boolean isInGroup(Group group){
        return groups.contains(group);
    }

    public void addToGroup(Group group){
        groups.add(group);
    }

    public void removeFromGroup(Group group) {
        groups.remove(group);
    }

    @JsonIgnore
    public double getPupilScore() {
        double totalScore = 0;
        for(AttributeValue attributeValue : attributeValues){
            totalScore += attributeValue.getScore();
        }
        return totalScore;
    }

    private void verifyPupilInGroup(Group group) throws Group.PupilNotBelongException {
        if(!isInGroup(group)){
            throw new Group.PupilNotBelongException(getId(), group);
        }
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

    public enum Gender {
        MALE, FEMALE
    }

    public static class GivenIdContainsProhibitedCharsException extends Exception {
        public GivenIdContainsProhibitedCharsException(){
            super("Given id must contain only digits.");
        }
    }

    public static class GivenIdIsNotValidException extends Exception {
        public GivenIdIsNotValidException(){
            super("Given id is not valid.");
        }
    }
}