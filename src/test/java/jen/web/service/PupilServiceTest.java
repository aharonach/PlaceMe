package jen.web.service;

import jen.web.entity.*;
import jen.web.exception.EntityAlreadyExists;
import jen.web.exception.NotFound;
import jen.web.util.PagesAndSortHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PupilServiceTest {

    @Autowired PupilService pupilService;
    @Autowired GroupService groupService;
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
    void shouldCreateAndRemovePupilWhenAddingPupilAndDeletingIt() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException, PagesAndSortHandler.FieldNotSortableException {
        Pupil receivedPupil1 = pupilService.add(repositoryTestUtils.createPupil1());
        Pupil receivedPupil2 = pupilService.add(repositoryTestUtils.createPupil2());

        assertEquals(2, getPupilsFromService().size());
        assertNotEquals(receivedPupil1.getId(), receivedPupil2.getId());
        assertNotEquals(pupilService.getOr404(receivedPupil1.getId()), pupilService.getOr404(receivedPupil2.getId()));

        assertEquals("Pupil1", receivedPupil1.getFirstName());
        assertEquals("Last1", receivedPupil1.getLastName());
        assertEquals("123456789", receivedPupil1.getGivenId());
        assertEquals(Pupil.Gender.MALE, receivedPupil1.getGender());
        assertEquals(1990, receivedPupil1.getBirthDate().getYear());
        pupilService.deleteById(receivedPupil1.getId());

        assertEquals("Pupil2", receivedPupil2.getFirstName());
        assertEquals("Last2", receivedPupil2.getLastName());
        assertEquals("987654321", receivedPupil2.getGivenId());
        assertEquals(Pupil.Gender.FEMALE, receivedPupil2.getGender());
        assertEquals(1992, receivedPupil2.getBirthDate().getYear());
        pupilService.deleteById(receivedPupil2.getId());
    }

    @Test
    void shouldGetSamePupilWhenGettingPupilByIdAndByGivenId() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException, PagesAndSortHandler.FieldNotSortableException {
        Pupil pupil = repositoryTestUtils.createPupil1();
        assertFalse(pupilService.isPupilExists("123456789"));

        Pupil receivedPupil = pupilService.add(pupil);

        assertTrue(pupilService.isPupilExists("123456789"));
        assertEquals(1, getPupilsFromService().size());

        Pupil pupilByGivenId = pupilService.getByGivenIdOr404("123456789");
        assertEquals(receivedPupil, pupilByGivenId);

        pupilService.deleteById(receivedPupil.getId());
    }

    @Test
    void shouldThrowExceptionWhenAddingPupilWithSameOrInvalidGivenId() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException, PagesAndSortHandler.FieldNotSortableException {
        Pupil pupil = repositoryTestUtils.createPupil1();
        Pupil receivedPupil = pupilService.add(pupil);

        assertEquals(1, getPupilsFromService().size());

        // adding again the same pupil
        assertThrows(EntityAlreadyExists.class, () -> pupilService.add(pupil));

        assertThrows(Pupil.GivenIdContainsProhibitedCharsException.class, () -> {
            new Pupil("sdfsdf", "Pupil1", "Last1", Pupil.Gender.MALE, LocalDate.of(1990, 1, 1));
        });

        // @todo: add validate for GivenIdIsNotValidException when we enabling it

        pupilService.deleteById(receivedPupil.getId());
    }

    @Test
    @Transactional
    void shouldAddPupilToGroupWhenCreatingPupilAndAddHimToAGroup() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException, PagesAndSortHandler.FieldNotSortableException {
        Group receivedGroup1 = groupService.add(new Group("group 1", "group 1 desc", null));
        Group receivedGroup2 = groupService.add(new Group("group 2", "group 2 desc", null));

        Pupil pupil = repositoryTestUtils.createPupil1();
        pupil.addToGroup(receivedGroup1);
        Pupil receivedPupil = pupilService.add(pupil);

        // not require save or update
        receivedPupil.addToGroup(receivedGroup2);

        assertEquals(1, getPupilsFromService().size());
        assertEquals(2, receivedPupil.getGroups().size());
        assertTrue(pupilService.getOr404(receivedPupil.getId()).getGroups().contains(receivedGroup1));
        assertTrue(pupilService.getOr404(receivedPupil.getId()).getGroups().contains(receivedGroup2));
        assertTrue(groupService.getOr404(receivedGroup1.getId()).getPupils().contains(receivedPupil));
        assertTrue(groupService.getOr404(receivedGroup2.getId()).getPupils().contains(receivedPupil));

        pupilService.deleteById(receivedPupil.getId());
        assertEquals(0, receivedGroup1.getPupils().size());
        assertEquals(0, receivedGroup2.getPupils().size());

        groupService.deleteById(receivedGroup1.getId());
        groupService.deleteById(receivedGroup2.getId());
    }

    @Test
    void shouldUpdatePupilGeneralInfoWhenUpdatingGeneralInfo() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        Pupil receivedPupil = pupilService.add(repositoryTestUtils.createPupil1());

        assertEquals("Pupil1", receivedPupil.getFirstName());
        assertEquals("Last1", receivedPupil.getLastName());
        assertEquals("123456789", receivedPupil.getGivenId());
        assertEquals(Pupil.Gender.MALE, receivedPupil.getGender());
        assertEquals(1990, receivedPupil.getBirthDate().getYear());

        receivedPupil.setFirstName("new_Pupil1");
        receivedPupil.setLastName("new_Last1");
        receivedPupil.setGivenId("987654321");
        receivedPupil.setGender(Pupil.Gender.FEMALE);
        receivedPupil.setBirthDate(LocalDate.of(2000, 2, 2));

        Pupil updatedPupil = pupilService.updateById(receivedPupil.getId(), receivedPupil);

        assertEquals("new_Pupil1", updatedPupil.getFirstName());
        assertEquals("new_Last1", updatedPupil.getLastName());
        assertEquals("987654321", updatedPupil.getGivenId());
        assertEquals(Pupil.Gender.FEMALE, updatedPupil.getGender());
        assertEquals(2000, updatedPupil.getBirthDate().getYear());

        pupilService.deleteById(receivedPupil.getId());
    }

    @Test
    @Transactional
    void shouldUpdatePupilGroupsWhenUpdatingAddingAndDeletingGroupsForPupil() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        Group receivedGroup1 = groupService.add(new Group("group 1", "group 1 desc", null));
        Group receivedGroup2 = groupService.add(new Group("group 2", "group 2 desc", null));
        Group receivedGroup3 = groupService.add(new Group("group 3", "group 3 desc", null));

        Pupil pupil1 = repositoryTestUtils.createPupil1();
        pupil1.addToGroup(receivedGroup1);

        Pupil receivedPupil1 = pupilService.add(pupil1);
        Pupil receivedPupil2 = pupilService.add(repositoryTestUtils.createPupil2());

        assertTrue(receivedPupil1.getGroups().contains(receivedGroup1));
        assertEquals(1, receivedPupil1.getGroups().size());
        assertEquals(0, receivedPupil2.getGroups().size());

        pupilService.linkPupilToGroup(receivedPupil1, receivedGroup2);
        pupilService.unlinkPupilFromGroup(receivedPupil1, receivedGroup1);
        pupilService.setPupilGroups(receivedPupil2, Set.of(receivedGroup1, receivedGroup2, receivedGroup3));

        receivedGroup1 = groupService.getOr404(receivedGroup1.getId());
        receivedGroup2 = groupService.getOr404(receivedGroup2.getId());
        receivedGroup3 = groupService.getOr404(receivedGroup3.getId());

        // expected results: pupil1-> group2, pupil2-> group1, group2, group3
        assertEquals(1, receivedPupil1.getGroups().size());
        assertTrue(receivedPupil1.getGroups().contains(receivedGroup2));
        assertEquals(3, receivedPupil2.getGroups().size());
        assertEquals(1, receivedGroup1.getPupils().size());
        assertEquals(2, receivedGroup2.getPupils().size());
        assertEquals(1, receivedGroup3.getPupils().size());
        assertTrue(receivedGroup1.isContains(receivedPupil2));
        assertTrue(receivedGroup2.isContains(receivedPupil1));
        assertTrue(receivedGroup2.isContains(receivedPupil2));
        assertTrue(receivedGroup3.isContains(receivedPupil2));

        pupilService.deleteById(receivedPupil1.getId());
        pupilService.deleteById(receivedPupil2.getId());

        groupService.deleteById(receivedGroup1.getId());
        groupService.deleteById(receivedGroup2.getId());
        groupService.deleteById(receivedGroup3.getId());
    }

    @Test
    void shouldAddOrUpdateAttributeValues() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException, Group.PupilNotBelongException, AttributeValue.ValueOutOfRangeException, Template.AttributeNotBelongException {
        Template receivedTemplate = templateService.add(repositoryTestUtils.createTemplate2());
        Group receivedGroup1 = groupService.add(new Group("group 1", "group 1 desc", receivedTemplate));
        Group receivedGroup2 = groupService.add(new Group("group 2", "group 2 desc", null));

        Pupil pupil1 = repositoryTestUtils.createPupil1();
        pupil1.addToGroup(receivedGroup1);
        Pupil receivedPupil = pupilService.add(pupil1);

        assertEquals(0, receivedPupil.getAttributeValues(receivedGroup1).size());
        assertEquals(0, pupilService.getAttributeValues(receivedPupil, receivedGroup1).size());
        assertThrows(Group.PupilNotBelongException.class, () -> receivedPupil.getAttributeValues(receivedGroup2));
        assertEquals(0, receivedPupil.getAttributeValues().size());

        Map<Long, Double> valuesMap = new HashMap<>(2);
        valuesMap.put(1L, 2D);
        valuesMap.put(2L, 3D);
        pupilService.addOrUpdateAttributeValuesFromIdValueMap(receivedPupil, receivedGroup1, valuesMap);
        assertEquals(2, pupilService.getAttributeValues(receivedPupil, receivedGroup1).size());

        AttributeValue attributeValue1 = receivedPupil.getAttributeValues(receivedGroup1, Set.of(1L)).stream().findFirst().get();
        assertEquals(2D, attributeValue1.getValue());
        AttributeValue attributeValue2 = receivedPupil.getAttributeValues(receivedGroup1, Set.of(2L)).stream().findFirst().get();
        assertEquals(3D, attributeValue2.getValue());

        pupilService.deleteById(receivedPupil.getId());
        groupService.deleteById(receivedGroup1.getId());
        groupService.deleteById(receivedGroup2.getId());
        templateService.deleteById(receivedTemplate.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionOnGetPupilWhenPupilNotExist() {
        assertThrows(NotFound.class, () -> pupilService.getOr404(100L));
    }

    @Test
    void shouldThrowNotFoundExceptionOnGetPupilByGivenIdWhenPupilNotExist() {
        assertThrows(NotFound.class, () -> pupilService.getByGivenIdOr404("1234"));
    }

    private List<Pupil> getPupilsFromService() throws PagesAndSortHandler.FieldNotSortableException {
        return pupilService.all(repositoryTestUtils.getFirstPageRequest()).getContent();
    }
}