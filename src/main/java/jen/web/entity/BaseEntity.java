package jen.web.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@ToString
@NoArgsConstructor
@Getter
@MappedSuperclass
public abstract class BaseEntity {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    protected Long id;

    @Setter(AccessLevel.NONE)
    protected LocalDateTime createdTime = LocalDateTime.now();
}
