package jen.hibernate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jen.hibernate.util.IsraeliIdValidator;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
    private Set<AttributeValue> attributeValues = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.ALL})
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

    public void addAttributeValue(Group group, Long attributeId, Double value) throws NotBelongToGroupException {
        verifyPupilInGroup(group);

        // First find the attribute object inside the group's template.
        // Then find if the attribute is already has a value for pupil,
        // If it does, update the value, otherwise create a new AttributeValue.
        // todo: add to the Group class method for getting attribute by ID
        group.getTemplate()
                .getAttributes().stream()
                .filter(attribute -> attribute.getId().equals(attributeId))
                .findFirst()
                .ifPresent(attribute -> this.getAttributeValues().stream()
                        .filter(attributeValue -> attributeValue.getAttribute().equals(attribute))
                        .findFirst()
                        .ifPresentOrElse(attributeValue -> attributeValue.setValue(value),
                                () -> this.getAttributeValues().add(new AttributeValue(this, attribute, value))));
    }

    public PupilAttributeId removeAttributeValue(Group group, Long attributeId) throws NotBelongToGroupException {
        verifyPupilInGroup(group);

        AtomicReference<PupilAttributeId> removed = new AtomicReference<>();

        // todo: add to the Group class method for getting attribute by ID
        group.getTemplate()
                .getAttributes().stream()
                .filter(attribute -> attribute.getId().equals(attributeId))
                .findFirst().flatMap(attribute -> this.getAttributeValues().stream()
                        .filter(attributeValue -> attributeValue.getAttribute().equals(attribute))
                        .findFirst()).ifPresent(attributeValue -> {
                            this.getAttributeValues().remove(attributeValue);
                            removed.set(attributeValue.getPupilAttributeId());
                        });

        return removed.get();
    }

    public void setGroups(Set<Group> groups){
        getGroups().forEach(this::removeFromGroup);
        groups.forEach(this::addToGroup);
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

    private void verifyPupilInGroup(Group group) throws NotBelongToGroupException {
        if (!isInGroup(group)) {
            throw new NotBelongToGroupException("Pupil '" + this.getFirstName() + "' is not in '" + group.getName() + "' group.");
        }
    }

    public double getPupilScore() {
        double totalScore = 0;
        for(AttributeValue attributeValue : attributeValues){
            totalScore += attributeValue.getScore();
        }
        return totalScore;
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

    public static class NotBelongToGroupException extends Exception{
        public NotBelongToGroupException(String message){
            super(message);
        }
    }

    public enum Gender {
        MALE, FEMALE
    }
}