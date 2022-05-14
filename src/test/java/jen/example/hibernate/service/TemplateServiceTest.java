package jen.example.hibernate.service;

import jen.example.hibernate.entity.Attribute;
import jen.example.hibernate.entity.Template;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TemplateServiceTest {

    @Autowired
    TemplateService service;

    @Test
    @Transactional
    void save() {
        Template template = new Template();
        template.setName("Template 1");
        template.setDescription("Desc");

        assertEquals(0, service.getAll().size());

        Template saved = service.add(template);
        System.out.println(saved);

        assertEquals(1, service.getAll().size());

        assertEquals("Template 1", service.getAll().get(0).getName());
        assertEquals("Template 1", service.getOr404(1L).getName());
        assertEquals(0, service.getOr404(1L).getAttributes().size());
    }

    @Test
    void shouldThrowExceptionWhenTemplateNotExist() {
        assertThrows(TemplateService.NotFound.class, () -> {
            service.getOr404(100L);
        });
    }
}