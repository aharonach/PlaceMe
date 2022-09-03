package jen.web.repository;

import jen.web.entity.Contact;
import jen.web.entity.Pupil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    boolean existsByGivenId(String givenId);

}
