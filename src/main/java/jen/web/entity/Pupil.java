package jen.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jen.web.util.ImportField;
import jen.web.util.ImportConstructor;
import jen.web.util.IsraeliIdValidator;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NaturalId;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static jen.web.util.IsraeliIdValidator.ID_LENGTH;

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

    @Getter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "pupils_classrooms",
            joinColumns = @JoinColumn(name = "pupil_id", referencedColumnName = "id"))
    private Set<PlacementClassroom> classrooms = new LinkedHashSet<>();

    @ImportConstructor
    public Pupil(@ImportField(title = "Given ID", fieldName = "givenId") String givenId,
                 @ImportField(title = "First Name", fieldName = "firstName") String firstName,
                 @ImportField(title = "Last Name", fieldName = "lastName") String lastName,
                 @ImportField(title = "Gender", fieldName = "gender") Gender gender,
                 @ImportField(title = "Birth Date", fieldName = "birthDate") LocalDate birthDate)
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

        validateGivenId(givenId);

        this.givenId = givenId;
    }

    public static void validateGivenId(String givenId) throws GivenIdContainsProhibitedCharsException, GivenIdIsNotValidException {
        if(!givenId.matches(DIGITS_REGEX)){
            throw new GivenIdContainsProhibitedCharsException(givenId);
        }
        if(givenId.length() != ID_LENGTH){
            throw new GivenIdIsNotValidException("Given id must contain " + ID_LENGTH + " digits: '" + givenId + "'.");
        }
        if(!IsraeliIdValidator.isValid(givenId)){
            throw new GivenIdIsNotValidException("Given id is not valid: '" + givenId + "'.");
        }
    }

    public void addAttributeValue(Group group, Long attributeId, Double value)
            throws Group.PupilNotBelongException, Template.AttributeNotBelongException, AttributeValue.ValueOutOfRangeException {

        verifyPupilInGroup(group);
        group.getTemplate().verifyAttributeBelongsToTemplate(attributeId);
        Attribute attribute = group.getTemplate().getAttribute(attributeId).get();

        // Find if the attribute is already has a value for pupil
        AttributeValue attributeValue = getAttributeValueOfUserByAttribute(attribute);

        // If it does, update the value, otherwise create a new AttributeValue.
        if(attributeValue != null){
            attributeValue.setValue(value);
        } else {
            attributeValues.add(new AttributeValue(this, attribute, value));
        }

    }

    private AttributeValue getAttributeValueOfUserByAttribute(Attribute attribute) {
        Optional<AttributeValue> optionalAttributeValue = attributeValues.stream()
                .filter(attributeValue -> attributeValue.getAttribute().equals(attribute))
                .findFirst();
        return optionalAttributeValue.orElse(null);
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

    public Map<Long, Double> getAttributeValueMap(){
        Map<Long, Double> result = new HashMap<>(attributeValues.size());
        for(AttributeValue attributeValue: attributeValues){
            result.put(attributeValue.getAttribute().getId(), attributeValue.getValue());
        }
        return result;
    }

    public Integer getAge(){
        return Math.abs(Period.between(LocalDate.now(), this.birthDate).getYears());
    }

    @JsonProperty
    public void setGroups(Set<Group> newGroups){
        groups = newGroups;
        for (Group group: newGroups){
            group.addPupil(this);
        }
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
        if(!groups.contains(group)){
            groups.add(group);
            group.addPupil(this);
        }
    }

    public void removeFromGroup(Group group) {
        if(groups.contains(group)){
            groups.remove(group);
            group.removePupil(this);
        }
    }

    public Set<Long> getClassroomIds(){
        return classrooms.stream().map(BaseEntity::getId).collect(Collectors.toSet());
    }

    public void addToClassrooms(PlacementClassroom placementClassroom){
        if(!classrooms.contains(placementClassroom)){
            classrooms.add(placementClassroom);
            placementClassroom.addPupilToClass(this);
        }
    }

    public void removeFromClassrooms(PlacementClassroom placementClassroom) {
        classrooms.remove(placementClassroom);
    }

    @JsonIgnore
    public double getPupilScore() {
        double totalScore = 0;
        for(AttributeValue attributeValue : attributeValues){
            totalScore += attributeValue.getScore();
        }
        return totalScore;
    }

    public double getPupilMaxScore() {
        double totalScore = 0;
        for(AttributeValue attributeValue : attributeValues){
            totalScore += attributeValue.getMaxScore();
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
        public GivenIdContainsProhibitedCharsException(String givenId){
            super("Given id must contain only digits: '" + givenId + "'.");
        }
    }

    public static class GivenIdIsNotValidException extends Exception {
        public GivenIdIsNotValidException(String message){
            super(message);
        }
    }
}