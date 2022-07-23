package jen.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jen.web.util.IsraeliIdValidator;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "pupils")
public class Pupil extends BaseEntity {
    @NaturalId
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
    @JsonIgnore
    private Set<Group> groups = new LinkedHashSet<>();

    public Pupil(String givenId, String firstName, String lastName, Gender gender, LocalDate birthDate){
        this.givenId = givenId; // todo: validate that givenId contains only digits
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    /**
     * Validate an Israeli ID Number.
     *
     * @param israeliId the id to validate
     * @return bool
     */
    public static boolean isGivenIdValid(String israeliId) {
        return IsraeliIdValidator.isValid(israeliId);
    }

    public void addAttributeValue(Group group, Long attributeId, Double value) throws Template.NotExistInTemplateException, Group.NotBelongToGroupException {

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

    public Set<AttributeValue> getAttributeValues(Group group, Set<Long> attributeIds) throws Group.NotBelongToGroupException {

        verifyPupilInGroup(group);
        Template template = group.getTemplate();

        // get all AttributeValues by attribute ids for specific group
        return getAttributeValues().stream()
                .filter(attributeValue ->  template.getAttributes().contains(attributeValue.getAttribute()))
                .filter(attributeValue ->  attributeIds.contains(attributeValue.getAttribute().getId()))
                .collect(Collectors.toSet());
    }

    public Set<AttributeValue> getAttributeValues(Group group) throws Group.NotBelongToGroupException {

        verifyPupilInGroup(group);
        Template template = group.getTemplate();

        // get all AttributeValues by attribute ids for specific group
        return getAttributeValues().stream()
                .filter(attributeValue ->  template.getAttributes().contains(attributeValue.getAttribute()))
                .collect(Collectors.toSet());
    }

    public void setGroups(Set<Group> newGroups){
        groups = newGroups;
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

    public Set<Group> getGroups(){
        return Collections.unmodifiableSet(groups);
    }

    public double getPupilScore() {
        double totalScore = 0;
        for(AttributeValue attributeValue : attributeValues){
            totalScore += attributeValue.getScore();
        }
        return totalScore;
    }

    private void verifyPupilInGroup(Group group) throws Group.NotBelongToGroupException {
        if(!isInGroup(group)){
            throw new Group.NotBelongToGroupException("Pupil '" + this.getFirstName() + "' is not in '" + group.getName() + "' group.");
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
}