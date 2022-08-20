package jen.web.repository;

import jen.web.entity.Preference;
import jen.web.entity.SelectorSelectedId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, SelectorSelectedId>{
    void deleteAllBySelectorSelectedIdInAndGroupId(Set<SelectorSelectedId> ids, Long groupId);
}
