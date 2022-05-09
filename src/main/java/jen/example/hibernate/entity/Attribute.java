package jen.example.hibernate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attributes")
public abstract class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    protected String name;
    protected String description;
    protected int priority;

//    @OneToMany(mappedBy = "attribute")
//    protected List<Pupil> pupils;

    abstract double calculate(double score);

    abstract double getMaxValue();
}
