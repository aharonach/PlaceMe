package web.service;

import web.entity.Attribute;
import web.entity.Group;
import web.entity.RangeAttribute;
import web.entity.Template;
import web.exception.NotFound;
import web.util.PagesAndSortHandler;
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
    void shouldCreateAndRemoveTemplateWhenAddingTemplateAndDeletingIt() throws PagesAndSortHandler.FieldNotSortableException, Template.AttributeAlreadyExistException {
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
        void shouldDeleteTemplateWhenGroupUseTheTemplate() throws Template.AttributeAlreadyExistException {
        Template receivedTemplate = templateService.add(repositoryTestUtils.createTemplate1());
        Group receivedGroup1 = groupService.add(new Group("group 1", "group 1 desc", receivedTemplate));
        Group receivedGroup2 = groupService.add(new Group("group 2", "group 2 desc", receivedTemplate));
        assertEquals(2, receivedTemplate.getGroups().size());

        templateService.deleteById(receivedTemplate.getId());
        groupService.deleteById(receivedGroup1.getId());
        groupService.deleteById(receivedGroup2.getId());
    }

    @Test
    @Transactional
    void shouldAddUpdateAndDeleteAttributesFromTemplate() throws Template.AttributeNotBelongException, Template.AttributeAlreadyExistException {
        Attribute attr1 = new RangeAttribute("attr 1", "attr 1 desc", 10);
        Attribute attr2 = new RangeAttribute("attr 2", "attr 2 desc", 20);
        Attribute newAttr = new RangeAttribute("new name", "new desc", 40);
        Template receivedTemplate = templateService.add(new Template("template", "desc", Set.of(attr1)));
        assertEquals(1, receivedTemplate.getAttributes().size());

        receivedTemplate = templateService.addAttributeForTemplate(receivedTemplate, attr2);
        assertEquals(2, receivedTemplate.getAttributes().size());

        Attribute receivedAttr1 = receivedTemplate.getAttributes().stream()
                .filter(attribute -> attribute.getName().equals("attr 1"))
                .findFirst().get();

        templateService.deleteAttributeForTemplate(receivedTemplate, receivedAttr1);
        assertEquals(1, receivedTemplate.getAttributes().size());

        Attribute receivedAttr2 = receivedTemplate.getAttributes().stream()
                .filter(attribute -> attribute.getName().equals("attr 2"))
                .findFirst().get();

        receivedTemplate = templateService.updateAttributeForTemplate(receivedTemplate, receivedAttr2, newAttr);

        Attribute receivedUpdatedAttr = templateService.getAttributeOr404(receivedAttr2.getId());
        assertEquals("new name", receivedUpdatedAttr.getName());
        assertEquals("new desc", receivedUpdatedAttr.getDescription());
        assertEquals(40, receivedUpdatedAttr.getPriority());

        templateService.deleteById(receivedTemplate.getId());
    }

    @Test
    @Transactional
    void shouldThrowAnExceptionForTwoAttributesWithSameNameOnSameTemplate() throws Template.AttributeAlreadyExistException {
        Attribute attr1 = new RangeAttribute("attr 1", "attr 1 desc", 10);
        Attribute attr2 = new RangeAttribute("attr 2", "attr 2 desc", 20);
        Attribute attrSameName = new RangeAttribute("attr 2", "attr 2 desc new", 30);

        assertThrows(Template.AttributeAlreadyExistException.class, () -> templateService.add(new Template("template", "desc", Set.of(attr1, attr2, attrSameName))));

        Template receivedTemplate1 = templateService.add(new Template("template1", "desc", Set.of(attr1, attr2)));
        Template receivedTemplate2 = templateService.add(new Template("template2", "desc", Set.of(attr1, attrSameName)));
        assertEquals(2, receivedTemplate1.getAttributes().size());
        assertEquals(2, receivedTemplate2.getAttributes().size());

        templateService.deleteById(receivedTemplate1.getId());
        templateService.deleteById(receivedTemplate2.getId());
    }

    @Test
    void shouldThrowNotBelongExceptionWhenPerformingOperationOnAttributeOfAnotherTemplate() throws Template.AttributeAlreadyExistException {
        Template receivedTemplate1 = templateService.add(repositoryTestUtils.createTemplate1());
        Template receivedTemplate2 = templateService.add(repositoryTestUtils.createTemplate2());
        Attribute attrOfTemplate2 = receivedTemplate2.getAttributes().stream().findFirst().get();

        assertThrows(Template.AttributeNotBelongException.class, () -> templateService.deleteAttributeForTemplate(receivedTemplate1, attrOfTemplate2));
        assertThrows(Template.AttributeNotBelongException.class, () -> templateService.updateAttributeForTemplate(receivedTemplate1, attrOfTemplate2, new RangeAttribute()));

        templateService.deleteById(receivedTemplate1.getId());
        templateService.deleteById(receivedTemplate2.getId());
    }

    @Test
    void shouldUpdateGeneralInfoAndAttributesWhenUpdatingTemplate() throws Template.AttributeAlreadyExistException {
        Attribute attr1 = new RangeAttribute("attr 1", "attr 1 desc", 10);
        Attribute attr2 = new RangeAttribute("attr 2", "attr 2 desc", 20);
        Attribute attr3 = new RangeAttribute("attr 3", "attr 3 desc", 30);

        Template receivedTemplate = templateService.add(new Template("template", "desc", Set.of(attr1, attr2)));
        assertEquals(2, receivedTemplate.getAttributes().size());

        assertEquals(1, receivedTemplate.getAttributes().stream()
                .filter(attribute -> attribute.getName().equals("attr 1")).count());
        assertEquals(1, receivedTemplate.getAttributes().stream()
                .filter(attribute -> attribute.getName().equals("attr 2")).count());
        assertEquals(0, receivedTemplate.getAttributes().stream()
                .filter(attribute -> attribute.getName().equals("attr 3")).count());

        attr2.setName("new name");
        Template newTemplate = new Template("template new", "desc new", Set.of(attr2, attr3));
        templateService.updateById(receivedTemplate.getId(), newTemplate);
        Template updatedTemplate = templateService.getOr404(receivedTemplate.getId());

        assertEquals(2, updatedTemplate.getAttributes().size());
        assertEquals("template new", updatedTemplate.getName());
        assertEquals("desc new", updatedTemplate.getDescription());

        assertEquals(0, updatedTemplate.getAttributes().stream()
                .filter(attribute -> attribute.getName().equals("attr 1")).count());
        assertEquals(0, updatedTemplate.getAttributes().stream()
                .filter(attribute -> attribute.getName().equals("attr 2")).count());
        assertEquals(1, updatedTemplate.getAttributes().stream()
                .filter(attribute -> attribute.getName().equals("new name")).count());
        assertEquals(1, updatedTemplate.getAttributes().stream()
                .filter(attribute -> attribute.getName().equals("attr 3")).count());

        templateService.deleteById(receivedTemplate.getId());
    }

    @Test
    void shouldUpdateGeneralInfoWhenUpdatingTemplateWithoutAttributesInfo() throws Template.AttributeAlreadyExistException {
        Template receivedTemplate = templateService.add(repositoryTestUtils.createTemplate1());
        assertEquals(2, receivedTemplate.getAttributes().size());

        Template newTemplateInfo = new Template("new name", "new desc");
        newTemplateInfo.setAttributes(null);

        Template updatedTemplate = templateService.updateById(receivedTemplate.getId(), newTemplateInfo);

        assertEquals(2, updatedTemplate.getAttributes().size());
        templateService.deleteById(receivedTemplate.getId());
    }

    @Test
    void shouldUpdateGeneralInfoAndEraseAttributesWhenUpdatingTemplateWithEmptyAttributesList() throws Template.AttributeAlreadyExistException {
        Template receivedTemplate = templateService.add(repositoryTestUtils.createTemplate1());
        assertEquals(2, receivedTemplate.getAttributes().size());

        Template newTemplateInfo = new Template("new name", "new desc");
        Template updatedTemplate = templateService.updateById(receivedTemplate.getId(), newTemplateInfo);

        assertEquals(0, updatedTemplate.getAttributes().size());
        templateService.deleteById(receivedTemplate.getId());
    }

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