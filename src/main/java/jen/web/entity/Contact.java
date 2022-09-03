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
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static jen.web.entity.Pupil.DIGITS_REGEX;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "contact")
public class Contact extends BaseEntity{

    @NaturalId(mutable=true)
    private String givenId;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Pupil.Gender gender;
    private String phoneNumber;
    @ManyToOne
    private Pupil pupil;

}
