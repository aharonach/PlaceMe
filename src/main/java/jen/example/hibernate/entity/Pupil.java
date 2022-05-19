package jen.example.hibernate.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "pupils")
public class Pupil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String givenId;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthDate;

    @OneToMany(mappedBy = "pupil")
    @ToString.Exclude
    private List<AttributeValue> attributeValues;

    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "pupils_groups",
            joinColumns = @JoinColumn(name = "pupils_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "groups_id", referencedColumnName = "id"))
    private Set<Group> groups = new LinkedHashSet<>();

    public Pupil(String givenId, String firstName, String lastName, Gender gender, LocalDate birthDate){
        this.givenId = givenId; // todo: validate that givenId contains only digits
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
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

    /**
     * Validate an Israeli ID Number.
     *
     * @param israeliId the id to validate
     * @return bool
     */
    public static boolean isGivenIdValid(String israeliId ) {
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

    public void addGroup(Group group) {
        this.groups.add(group);
        group.getPupils().add(this);
    }

    public void removeGroup(long groupId) {
        Group tag = this.groups.stream().filter(t -> t.getId() == groupId).findFirst().orElse(null);
        if (tag != null) {
            this.groups.remove(tag);
            tag.getPupils().remove(this);
        }
    }

    public enum Gender {
        MALE, FEMALE
    }
}
