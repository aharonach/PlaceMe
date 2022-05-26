package jen.web.dto;

import jen.web.entity.AttributeValue;
import jen.web.entity.Group;
import jen.web.entity.Pupil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class PupilDto {
    private Long id;
    private LocalDateTime createdTime;
    private String givenId;
    private String firstName;
    private String lastName;
    private Pupil.Gender gender;
    private LocalDate birthDate;
    private List<AttributeValue> attributeValues;
    private Set<Long> groups ;

    public PupilDto(Pupil pupil){
        this.id = pupil.getId();
        this.createdTime = pupil.getCreatedTime();
        this.givenId = pupil.getGivenId();
        this.firstName = pupil.getFirstName();
        this.lastName = pupil.getLastName();
        this.gender = pupil.getGender();
        this.birthDate = pupil.getBirthDate();
        this.attributeValues = new ArrayList<>(pupil.getAttributeValues());
        this.groups = pupil.getGroups().stream().map(Group::getId).collect(Collectors.toSet());
    }
}
