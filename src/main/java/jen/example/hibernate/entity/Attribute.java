package jen.example.hibernate.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = RangeAttribute.class, name = "range")})
@Table(name = "attributes")
public abstract class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected LocalDateTime createdTime = LocalDateTime.now();

    protected String name;
    protected String description;
    protected int priority;

    protected Attribute(String name, String description, int priority){
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    abstract double calculate(double score);

    abstract double getMaxValue();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Attribute attribute = (Attribute) o;
        return id != null && Objects.equals(id, attribute.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
