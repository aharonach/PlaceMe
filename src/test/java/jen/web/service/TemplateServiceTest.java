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

    @Autowired TemplateService templateService;
    @Autowired RepositoryTestUtils repositoryTestUtils;

    @BeforeEach
    void setUp() {
        repositoryTestUtils.clearAllData();
    }

    @AfterEach
    void tearDown() {
        repositoryTestUtils.verifyAllTablesAreEmpty();
    }

    @Test
    @Transactional
    void shouldCreateAndRemoveTemplateWhenAddingTemplateAndDeletingIt() throws PagesAndSortHandler.FieldNotSortableException {
        Template receivedTemplate1 = templateService.add(repositoryTestUtils.createTemplateWithoutAttributes());
        Template receivedTemplate2 = templateService.add(repositoryTestUtils.createTemplate2());
        assertEquals(2, getTemplatesFromService().size());
        assertNotEquals(receivedTemplate1.getId(), receivedTemplate2.getId());
        assertNotEquals(templateService.getOr404(receivedTemplate1.getId()), templateService.getOr404(receivedTemplate2.getId()));

        assertEquals("template 3", receivedTemplate1.getName());
        assertEquals("template 3 desc", receivedTemplate1.getDescription());
        assertEquals(0, receivedTemplate1.getAttributes().size());
        templateService.deleteById(receivedTemplate1.getId());

        assertEquals("template 2", receivedTemplate2.getName());
        assertEquals("template 2 desc", receivedTemplate2.getDescription());
        assertEquals(2, receivedTemplate2.getAttributes().size());
        // check attr values
        templateService.deleteById(receivedTemplate2.getId());
    }

    @Test
    @Transactional
    void shouldCreateTemplateWithAttributesWhenAddTemplate() throws PagesAndSortHandler.FieldNotSortableException {
        Set<Attribute> attributes = Set.of(new RangeAttribute("name", "attr desc", 10));
        Template template = new Template("Template 1", "Template1 description", attributes);
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