package jen.web.service;

import jen.web.entity.Group;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TemplateServiceTest {

    @Autowired TemplateService templateService;
    @Autowired GroupService groupService;
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
        assertEquals(1, receivedTemplate2.getAttributes().stream()
                .filter(attribute -> attribute.getName().equals("attr 1") && attribute.getDescription().equals("attr 1 for template 2"))
                .count());
        assertEquals(1, receivedTemplate2.getAttributes().stream()
                .filter(attribute -> attribute.getName().equals("attr 2") && attribute.getDescription().equals("attr 2 for template 2"))
                .count());
        templateService.deleteById(receivedTemplate2.getId());
    }

    @Test
    @Transactional
        void shouldDeleteTemplateWhenGroupUseTheTemplate() {
        Template receivedTemplate = templateService.add(repositoryTestUtils.createTemplate1());
        Group receivedGroup1 = groupService.add(new Group("group 1", "group 1 desc", receivedTemplate));
        Group receivedGroup2 = groupService.add(new Group("group 2", "group 2 desc", receivedTemplate));
        assertEquals(2, receivedTemplate.getGroups().size());

        templateService.deleteById(receivedTemplate.getId());
        groupService.deleteById(receivedGroup1.getId());
        groupService.deleteById(receivedGroup2.getId());
    }

    // check: update, deleteAttributeForTemplateById, updateAttributeForTemplateById, addAttributeForTemplateById

    @Test
    void shouldThrowNotFoundExceptionOnGetTemplateWhenTemplateNotExist() {
        assertThrows(NotFound.class, () -> templateService.getOr404(100L));
    }

    @Test
    void shouldThrowNotFoundExceptionOnGetAttributeWhenAttributeNotExist() {
        assertThrows(NotFound.class, () -> templateService.getAttributeOr404(100L));
    }

    private List<Template> getTemplatesFromService() throws PagesAndSortHandler.FieldNotSortableException {
        return templateService.all(repositoryTestUtils.getFirstPageRequest()).getContent();
    }
}