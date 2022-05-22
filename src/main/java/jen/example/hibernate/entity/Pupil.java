package jen.example.hibernate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @OneToMany(mappedBy = "pupil")
    @ToString.Exclude
    private List<AttributeValue> attributeValues;

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
        if (israeliId.length() != 9)
            return false;

        int sum = 0;

        for (int i = 0; i < israeliId.length(); i++) {
            int digit = israeliId.charAt(i) - '0';
            sum += i % 2 != 0 ?
                    switch (digit) {
                        case 1 -> 2;
                        case 2 -> 4;
                        case 3 -> 6;
                        case 4 -> 8;
                        case 5 -> 1;
                        case 6 -> 3;
                        case 7 -> 5;
                        case 8 -> 7;
                        case 9 -> 9;
                        default -> 0;
                    } : digit;
        }

        return sum % 10 == 0;
    }

    public void addAttributeValue(Group group, Long attributeId, Double value) throws Exception {
        if (!isInGroup(group)) {
            throw new Exception("Pupil " + this.getGivenId() + " is not in " + group.getName() + " group.");
        }

        // First find the attribute object inside the group's template.
        // Then find if the attribute is already has a value for pupil,
        // If it does, update the value, otherwise create a new AttributeValue.
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

    public PupilAttributeId removeAttributeValue(Group group, Long attributeId) throws Exception {
        if (!isInGroup(group)) {
            throw new Exception("Pupil " + this.getGivenId() + " is not in " + group.getName() + " group.");
        }

        AtomicReference<PupilAttributeId> removed = new AtomicReference<>();

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
