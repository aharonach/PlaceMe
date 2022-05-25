package jen.example.hibernate.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlacementClassroom extends BaseEntity {
    private String name;
    @ManyToOne
    private PlacementResult placementResult;
    @ManyToMany
    private List<Pupil> pupils;
}
