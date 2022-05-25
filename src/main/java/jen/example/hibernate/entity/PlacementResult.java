package jen.example.hibernate.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "placement_results")
public class PlacementResult extends BaseEntity {
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "placement_id")
    private Placement placement;

    @OneToMany(mappedBy = "placementResult")
    private List<PlacementClassroom> classes;
}
