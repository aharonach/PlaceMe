package jen.example.hibernate.service;

import jen.example.hibernate.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PersonServiceImplTest {

    @Autowired
    PersonService service;

    @Test
    void shouldAddToDbWhenSavePerson() {
        Person p = new Person();
        p.setName("Gal");

        assertEquals(0, service.fetchList().size());

        service.save(p);

        assertEquals(1, service.fetchList().size());
        assertEquals("Gal", service.fetchList().get(0).getName());
    }
}