package jen.web.service;

import jen.web.entity.Attribute;
import jen.web.entity.RangeAttribute;
import jen.web.entity.Template;
import jen.web.exception.NotFound;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TemplateServiceTest {

    public static final String TEMPLATE_1 = "Template 1";
    public static final String TEMPLATE_DESCRIPTION_1 = "Template1 description";
    public static final String TEMPLATE_2 = "Template 2";
    public static final String TEMPLATE_DESCRIPTION_2 = "Template2 description";


    @Autowired
    TemplateService service;

    @Test
    @Transactional
    void shouldCreateAndRemoveTemplateWhenAddTemplateAndDeleteIt() {
        assertEquals(0, service.all().size());

        Template template1 = new Template(TEMPLATE_1, TEMPLATE_DESCRIPTION_1);
        service.add(template1);

        Template template2 = new Template(TEMPLATE_2, TEMPLATE_DESCRIPTION_2);
        service.add(template2);

        assertEquals(2, service.all().size());
        Template receivedTemplate1 = service.all().get(0);
        Template receivedTemplate2 = service.all().get(1);

        assertEquals(receivedTemplate1, service.getOr404(receivedTemplate1.getId()));
        assertNotEquals(receivedTemplate1, service.getOr404(receivedTemplate2.getId()));

        assertEquals(TEMPLATE_1, receivedTemplate1.getName());
        assertEquals(TEMPLATE_DESCRIPTION_1, receivedTemplate1.getDescription());
        assertEquals(0, receivedTemplate1.getAttributes().size());

        service.deleteById(receivedTemplate1.getId());
        service.deleteById(receivedTemplate2.getId());
        assertEquals(0, service.all().size());
    }

    @Test
    @Transactional
    void shouldCreateTemplateWithAttributesWhenAddTemplate() {
        List<Attribute> attributes = List.of(new RangeAttribute("name", "attr desc", 10));
        Template template = new Template(TEMPLATE_1, TEMPLATE_DESCRIPTION_1, attributes);
        service.add(template);

        Template receivedTemplate = service.all().get(0);
        List<Attribute> receivedAttributes = receivedTemplate.getAttributes();
        assertEquals(1, receivedAttributes.size());
        assertEquals("name", receivedAttributes.get(0).getName());
        assertEquals("attr desc", receivedAttributes.get(0).getDescription());
        assertEquals(10, receivedAttributes.get(0).getPriority());
        service.deleteById(receivedTemplate.getId());
    }

    @Test
    void shouldAddNewAttributesAndUpdateTemplateFieldsWhenUpdateTemplate() {
        List<Attribute> attributes = Arrays.asList(
                new RangeAttribute("name1", "attr desc1", 10),
                new RangeAttribute("name2", "attr desc2", 20)
        );
        service.add(new Template(TEMPLATE_1, TEMPLATE_DESCRIPTION_1, attributes));

        Template receivedTemplate = service.all().get(0);
        assertEquals(2, receivedTemplate.getAttributes().size());

        List<Attribute> newAttributes = Arrays.asList(
                attributes.get(0),
                new RangeAttribute("name3", "attr desc3", 30),
                new RangeAttribute("name4", "attr desc4", 40)
        );
        Template newTemplate = new Template("new name", "new description", newAttributes);
        Template updatedReceivedTemplate = service.updateById(receivedTemplate.getId(), newTemplate);

        assertEquals(receivedTemplate.getId(), updatedReceivedTemplate.getId());
        assertEquals("new name", updatedReceivedTemplate.getName());
        assertEquals("new description", updatedReceivedTemplate.getDescription());

        assertEquals(3, updatedReceivedTemplate.getAttributes().size());
        assertEquals("name1", updatedReceivedTemplate.getAttributes().get(0).getName());
        assertEquals("name3", updatedReceivedTemplate.getAttributes().get(1).getName());

        service.deleteById(receivedTemplate.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionOnGetTemplateWhenTemplateNotExist() {
        assertThrows(NotFound.class, () -> service.getOr404(100L));
    }
}