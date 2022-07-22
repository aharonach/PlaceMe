package jen.web.repository;

import jen.web.entity.Preference;
import jen.web.entity.SelectorSelectedId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, SelectorSelectedId>{
}
