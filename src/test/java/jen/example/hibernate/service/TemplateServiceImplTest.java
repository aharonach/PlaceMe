package jen.example.hibernate.service;

import jen.example.hibernate.entity.Attribute;
import jen.example.hibernate.entity.Template;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TemplateServiceImplTest {

    @Autowired
    TemplateService service;

    @Test
    void save() {
        List<Attribute> attributeList = new ArrayList<>();

        Template template = new Template();
        template.setName("Template 1");
        template.setDescription("Desc");
        template.setAttributes(attributeList);

        //attributeList.add(new RangeAttribute());

        assertEquals(0, service.fetchAll().size());

        Template saved = service.save(template);
        System.out.println(saved);

        assertEquals(1, service.fetchAll().size());

        assertEquals("Template 1", service.fetchAll().get(0).getName());

        //todo: handle lazy load
        //System.out.println(service.fetch(1L));;
    }
}