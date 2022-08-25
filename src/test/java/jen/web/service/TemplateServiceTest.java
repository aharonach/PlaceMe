package jen.web.service;

import jen.web.entity.Attribute;
import jen.web.entity.RangeAttribute;
import jen.web.entity.Template;
import jen.web.exception.NotFound;
import jen.web.util.PagesAndSortHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TemplateServiceTest {

    public static final String TEMPLATE_1 = "Template 1";
    public static final String TEMPLATE_DESCRIPTION_1 = "Template1 description";
    public static final String TEMPLATE_2 = "Template 2";
    public static final String TEMPLATE_DESCRIPTION_2 = "Template2 description";


    @Autowired TemplateService templateService;
    @Autowired RepositoryTestUtils repositoryTestUtils;

    @BeforeEach
    @AfterEach
    void verifyDbIsEmpty() {
        repositoryTestUtils.verifyAllTablesAreEmpty();
    }

    @Test
    @Transactional
    void shouldCreateAndRemoveTemplateWhenAddingTemplateAndDeletingIt() throws PagesAndSortHandler.FieldNotSortableException {

        Template template1 = new Template(TEMPLATE_1, TEMPLATE_DESCRIPTION_1);
        templateService.add(template1);

        Template template2 = new Template(TEMPLATE_2, TEMPLATE_DESCRIPTION_2);
        templateService.add(template2);

        assertEquals(2, getTemplatesFromService().size());
        Template receivedTemplate1 = getTemplatesFromService().get(0);
        Template receivedTemplate2 = getTemplatesFromService().get(1);

        assertEquals(receivedTemplate1, templateService.getOr404(receivedTemplate1.getId()));
        assertNotEquals(receivedTemplate1, templateService.getOr404(receivedTemplate2.getId()));

        assertNotEquals(receivedTemplate1.getId(), receivedTemplate2.getId());
        assertNotEquals(templateService.getOr404(receivedTemplate1.getId()), templateService.getOr404(receivedTemplate2.getId()));

        assertEquals(TEMPLATE_1, receivedTemplate1.getName());
        assertEquals(TEMPLATE_DESCRIPTION_1, receivedTemplate1.getDescription());
        assertEquals(0, receivedTemplate1.getAttributes().size());

        templateService.deleteById(receivedTemplate1.getId());
        templateService.deleteById(receivedTemplate2.getId());
    }

    @Test
    @Transactional
    void shouldCreateTemplateWithAttributesWhenAddTemplate() throws PagesAndSortHandler.FieldNotSortableException {
        Set<Attribute> attributes = Set.of(new RangeAttribute("name", "attr desc", 10));
        Template template = new Template(TEMPLATE_1, TEMPLATE_DESCRIPTION_1, attributes);
        templateService.add(template);

        Template receivedTemplate = getTemplatesFromService().get(0);
        Set<Attribute> receivedAttributes = receivedTemplate.getAttributes();
        Attribute receivedAttribute = receivedAttributes.stream().findFirst().get();

        assertEquals(1, receivedAttributes.size());
        assertEquals("name", receivedAttribute.getName());
        assertEquals("attr desc", receivedAttribute.getDescription());
        assertEquals(10, receivedAttribute.getPriority());
        templateService.deleteById(receivedTemplate.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionOnGetTemplateWhenTemplateNotExist() {
        assertThrows(NotFound.class, () -> templateService.getOr404(100L));
    }

    private List<Template> getTemplatesFromService() throws PagesAndSortHandler.FieldNotSortableException {
        return templateService.all(repositoryTestUtils.getFirstPageRequest()).getContent();
    }
}