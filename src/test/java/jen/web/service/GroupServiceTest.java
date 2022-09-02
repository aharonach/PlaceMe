package jen.web.service;

import jen.web.entity.*;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class GroupServiceTest {

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
    void shouldCreateAndRemoveGroupWhenAddingGroupAndDeletingIt() throws PagesAndSortHandler.FieldNotSortableException {
        Group receivedGroup1 = groupService.add(new Group("group 1", "group 1 desc", null));
        Group receivedGroup2 = groupService.add(new Group("group 2", "group 2 desc", null));
        assertEquals(2, getGroupsFromService().size());
        assertNotEquals(receivedGroup1.getId(), receivedGroup2.getId());
        assertNotEquals(groupService.getOr404(receivedGroup1.getId()), groupService.getOr404(receivedGroup2.getId()));


        assertEquals("group 1", receivedGroup1.getName());
        assertEquals("group 1 desc", receivedGroup1.getDescription());
        assertNull(receivedGroup1.getTemplate());
        assertEquals(0, receivedGroup1.getPupils().size());
        assertEquals(0, receivedGroup1.getPreferences().size());
        assertEquals(0, receivedGroup1.getPlacements().size());
        groupService.deleteById(receivedGroup1.getId());

        assertEquals("group 2", receivedGroup2.getName());
        assertEquals("group 2 desc", receivedGroup2.getDescription());
        assertNull(receivedGroup2.getTemplate());
        assertEquals(0, receivedGroup2.getPupils().size());
        assertEquals(0, receivedGroup2.getPreferences().size());
        assertEquals(0, receivedGroup2.getPlacements().size());
        groupService.deleteById(receivedGroup2.getId());
    }

    @Test
    @Transactional
    void shouldCreateAndRemoveGroupWithTemplateWhenAddingGroupAndDeletingIt() throws Template.AttributeAlreadyExistException {
        Template receivedTemplate = templateService.add(repositoryTestUtils.createTemplate2());
        Group receivedGroup = groupService.add(new Group("group 1", "group 1 desc", receivedTemplate));
        receivedTemplate = templateService.getOr404(receivedTemplate.getId());

        assertEquals("template 2", receivedGroup.getTemplate().getName());
        assertEquals(1, receivedGroup.getTemplate().getGroups().size());
        assertEquals(1, receivedTemplate.getGroups().size());

        templateService.deleteById(receivedTemplate.getId());
        groupService.deleteById(receivedGroup.getId());
    }

    @Test
    @Transactional
    void shouldAddPupilToGroupWhenAddingFromService() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException, PagesAndSortHandler.FieldNotSortableException {
        Pupil receivedPupil1 = pupilService.add(repositoryTestUtils.createPupil1());
        Pupil receivedPupil2 = pupilService.add(repositoryTestUtils.createPupil2());
        Group receivedGroup1 = groupService.add(new Group("group 1", "group 1 desc", null));
        Group receivedGroup2 = groupService.add(new Group("group 2", "group 2 desc", null));
        assertEquals(2, getGroupsFromService().size());

        groupService.linkPupilToGroup(receivedGroup1, receivedPupil1);
        groupService.linkPupilToGroup(receivedGroup2, receivedPupil1);
        groupService.linkPupilToGroup(receivedGroup2, receivedPupil2);

        receivedGroup1 = groupService.getOr404(receivedGroup1.getId());
        receivedGroup2 = groupService.getOr404(receivedGroup2.getId());
        receivedPupil1 = pupilService.getOr404(receivedPupil1.getId());
        receivedPupil2 = pupilService.getOr404(receivedPupil2.getId());

        assertEquals(1, receivedGroup1.getPupils().size());
        assertTrue(receivedGroup1.getPupils().contains(receivedPupil1));
        assertEquals(2, receivedGroup2.getPupils().size());
        assertTrue(receivedGroup2.getPupils().contains(receivedPupil1));
        assertTrue(receivedGroup2.getPupils().contains(receivedPupil2));
        assertTrue(receivedPupil1.isInGroup(receivedGroup1));
        assertTrue(receivedPupil1.isInGroup(receivedGroup2));
        assertFalse(receivedPupil2.isInGroup(receivedGroup1));
        assertTrue(receivedPupil2.isInGroup(receivedGroup2));

        groupService.unlinkPupilFromGroup(receivedGroup1, receivedPupil1);
        receivedGroup1 = groupService.getOr404(receivedGroup1.getId());
        receivedPupil1 = pupilService.getOr404(receivedPupil1.getId());
        assertEquals(0, receivedGroup1.getPupils().size());
        assertFalse(receivedPupil1.isInGroup(receivedGroup1));

        groupService.deleteById(receivedGroup1.getId());
        groupService.deleteById(receivedGroup2.getId());
        pupilService.deleteById(receivedPupil1.getId());
        pupilService.deleteById(receivedPupil2.getId());
    }

    @Test
    @Transactional
    void shouldClearPupilListWhenRemovingAllPupilsFromGroup() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        Pupil receivedPupil1 = pupilService.add(repositoryTestUtils.createPupil1());
        Pupil receivedPupil2 = pupilService.add(repositoryTestUtils.createPupil2());
        Group receivedGroup = groupService.add(new Group("group 1", "group 1 desc", null));

        groupService.linkPupilToGroup(receivedGroup, receivedPupil1);
        groupService.linkPupilToGroup(receivedGroup, receivedPupil2);

        assertEquals(2, receivedGroup.getPupils().size());
        assertTrue(receivedPupil1.isInGroup(receivedGroup));
        assertTrue(receivedPupil2.isInGroup(receivedGroup));

        groupService.unlinkAllPupilsFromGroup(receivedGroup);
        receivedGroup = groupService.getOr404(receivedGroup.getId());
        receivedPupil1 = pupilService.getOr404(receivedPupil1.getId());
        receivedPupil2 = pupilService.getOr404(receivedPupil2.getId());

        assertEquals(0, receivedGroup.getPupils().size());
        assertFalse(receivedPupil1.isInGroup(receivedGroup));
        assertFalse(receivedPupil2.isInGroup(receivedGroup));

        groupService.deleteById(receivedGroup.getId());
        pupilService.deleteById(receivedPupil1.getId());
        pupilService.deleteById(receivedPupil2.getId());
    }

    @Test
    @Transactional
    void testCreatingPupilPreferences() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException, Preference.SamePupilException, Group.PupilNotBelongException {
        Pupil receivedPupil1 = pupilService.add(repositoryTestUtils.createPupil1());
        Pupil receivedPupil2 = pupilService.add(repositoryTestUtils.createPupil2());
        Pupil receivedPupil3 = pupilService.add(repositoryTestUtils.createPupil3());
        Group receivedGroup1 = groupService.add(new Group("group 1", "group 1 desc", null));
        Group receivedGroup2 = groupService.add(new Group("group 2", "group 2 desc", null));
        Group receivedGroup3 = groupService.add(new Group("group 3", "group 3 desc", null));
        groupService.linkPupilToGroup(receivedGroup1, receivedPupil1);
        groupService.linkPupilToGroup(receivedGroup1, receivedPupil2);
        groupService.linkPupilToGroup(receivedGroup1, receivedPupil3);

        groupService.addPupilPreference(receivedGroup1, new Preference(receivedPupil1, receivedPupil2, true));
        groupService.addPupilPreference(receivedGroup1, new Preference(receivedPupil2, receivedPupil3, true));
        groupService.addPupilPreference(receivedGroup1, new Preference(receivedPupil1, receivedPupil3, false));

        groupService.linkPupilToGroup(receivedGroup2, receivedPupil1);
        groupService.linkPupilToGroup(receivedGroup2, receivedPupil2);

        groupService.addPupilPreference(receivedGroup2, new Preference(receivedPupil1, receivedPupil2, true));

        assertEquals(2, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil1).size());
        assertEquals(2, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil2).size());
        assertEquals(2, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil3).size());

        // preference with same pupil
        assertThrows(Preference.SamePupilException.class, () -> groupService.addPupilPreference(receivedGroup1, new Preference(receivedPupil1, receivedPupil1, true)));

        // pupil not in group
        assertThrows(Group.PupilNotBelongException.class, () -> groupService.addPupilPreference(receivedGroup3, new Preference(receivedPupil1, receivedPupil2, true)));

        groupService.deleteById(receivedGroup1.getId());
        groupService.deleteById(receivedGroup2.getId());
        groupService.deleteById(receivedGroup3.getId());
        pupilService.deleteById(receivedPupil1.getId());
        pupilService.deleteById(receivedPupil2.getId());
        pupilService.deleteById(receivedPupil3.getId());
    }

    @Test
    @Transactional
    void testDeletingPupilPreferences() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException, Preference.SamePupilException, Group.PupilNotBelongException {
        Pupil receivedPupil1 = pupilService.add(repositoryTestUtils.createPupil1());
        Pupil receivedPupil2 = pupilService.add(repositoryTestUtils.createPupil2());
        Pupil receivedPupil3 = pupilService.add(repositoryTestUtils.createPupil3());

        Group receivedGroup1 = groupService.add(new Group("group 1", "group 1 desc", null));
        groupService.linkPupilToGroup(receivedGroup1, receivedPupil1);
        groupService.linkPupilToGroup(receivedGroup1, receivedPupil2);
        groupService.linkPupilToGroup(receivedGroup1, receivedPupil3);

        Group receivedGroup2 = groupService.add(new Group("group 2", "group 2 desc", null));
        groupService.linkPupilToGroup(receivedGroup2, receivedPupil1);
        groupService.linkPupilToGroup(receivedGroup2, receivedPupil2);
        groupService.linkPupilToGroup(receivedGroup2, receivedPupil3);

        groupService.addPupilPreference(receivedGroup1, new Preference(receivedPupil1, receivedPupil2, true));
        groupService.addPupilPreference(receivedGroup1, new Preference(receivedPupil2, receivedPupil3, true));
        groupService.addPupilPreference(receivedGroup1, new Preference(receivedPupil1, receivedPupil3, false));

        groupService.addPupilPreference(receivedGroup2, new Preference(receivedPupil1, receivedPupil2, true));
        groupService.addPupilPreference(receivedGroup2, new Preference(receivedPupil2, receivedPupil3, true));

        assertEquals(2, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil1).size());
        assertEquals(2, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil2).size());
        assertEquals(2, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil3).size());
        assertEquals(1, groupService.getAllPreferencesForPupil(receivedGroup2, receivedPupil1).size());
        assertEquals(2, groupService.getAllPreferencesForPupil(receivedGroup2, receivedPupil2).size());
        assertEquals(1, groupService.getAllPreferencesForPupil(receivedGroup2, receivedPupil3).size());

        groupService.deletePupilPreferences(receivedGroup1, receivedPupil1);
        assertEquals(0, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil1).size());
        assertEquals(1, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil2).size());
        assertEquals(1, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil3).size());

        groupService.deletePupilPreferences(receivedGroup1, receivedPupil2.getId(), receivedPupil3.getId());
        assertEquals(0, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil1).size());
        assertEquals(0, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil2).size());
        assertEquals(0, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil3).size());

        groupService.deleteAllPreferencesFromGroup(receivedGroup2);
        assertEquals(0, receivedGroup2.getPreferences().size());
        assertEquals(0, groupService.getAllPreferencesForPupil(receivedGroup2, receivedPupil1).size());
        assertEquals(0, groupService.getAllPreferencesForPupil(receivedGroup2, receivedPupil2).size());
        assertEquals(0, groupService.getAllPreferencesForPupil(receivedGroup2, receivedPupil3).size());

        groupService.deleteById(receivedGroup1.getId());
        groupService.deleteById(receivedGroup2.getId());
        pupilService.deleteById(receivedPupil1.getId());
        pupilService.deleteById(receivedPupil2.getId());
        pupilService.deleteById(receivedPupil3.getId());
    }

    @Test
    @Transactional
    void shouldSetNewTemplateWhenUpdatingGroup() throws Template.AttributeAlreadyExistException {
        Template receivedTemplate1 = templateService.add(repositoryTestUtils.createTemplate1());
        Template receivedTemplate2 = templateService.add(repositoryTestUtils.createTemplate2());
        Group receivedGroup = groupService.add(new Group("group 1", "group 1 desc", receivedTemplate1));

        assertEquals("template 1", receivedGroup.getTemplate().getName());
        assertEquals(1, receivedGroup.getTemplate().getGroups().size());
        assertEquals(receivedGroup.getTemplate().getId(), receivedTemplate1.getId());

        Group newGroup = new Group(receivedGroup.getName(), receivedGroup.getDescription(), receivedTemplate2);
        Group updatedGroup = groupService.updateById(receivedGroup.getId(), newGroup);
        receivedTemplate1 = templateService.getOr404(receivedTemplate1.getId());
        receivedTemplate2 = templateService.getOr404(receivedTemplate2.getId());

        assertEquals("template 2", updatedGroup.getTemplate().getName());
        assertEquals(updatedGroup.getTemplate().getId(), receivedTemplate2.getId());
        assertEquals(1, updatedGroup.getTemplate().getGroups().size());
        assertEquals(0, receivedTemplate1.getGroups().size());
        assertEquals(1, receivedTemplate2.getGroups().size());

        templateService.deleteById(receivedTemplate1.getId());
        templateService.deleteById(receivedTemplate2.getId());
        groupService.deleteById(receivedGroup.getId());
    }

    @Test
    @Transactional
    void shouldDeletePupilPreferencesWhenUnlinkPupilFromGroup() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException, Preference.SamePupilException, Group.PupilNotBelongException {
        Pupil receivedPupil1 = pupilService.add(repositoryTestUtils.createPupil1());
        Pupil receivedPupil2 = pupilService.add(repositoryTestUtils.createPupil2());
        Pupil receivedPupil3 = pupilService.add(repositoryTestUtils.createPupil3());

        Group receivedGroup1 = groupService.add(new Group("group 1", "group 1 desc", null));
        groupService.linkPupilToGroup(receivedGroup1, receivedPupil1);
        groupService.linkPupilToGroup(receivedGroup1, receivedPupil2);
        groupService.linkPupilToGroup(receivedGroup1, receivedPupil3);

        groupService.addPupilPreference(receivedGroup1, new Preference(receivedPupil1, receivedPupil2, true));
        groupService.addPupilPreference(receivedGroup1, new Preference(receivedPupil2, receivedPupil3, true));
        groupService.addPupilPreference(receivedGroup1, new Preference(receivedPupil1, receivedPupil3, false));

        assertEquals(2, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil1).size());
        assertEquals(2, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil2).size());
        assertEquals(2, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil3).size());

        groupService.unlinkPupilFromGroup(receivedGroup1, receivedPupil1);

        assertEquals(0, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil1).size());
        assertEquals(1, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil2).size());
        assertEquals(1, groupService.getAllPreferencesForPupil(receivedGroup1, receivedPupil3).size());

        groupService.deleteById(receivedGroup1.getId());
        pupilService.deleteById(receivedPupil1.getId());
        pupilService.deleteById(receivedPupil2.getId());
        pupilService.deleteById(receivedPupil3.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionOnGetGroupWhenGroupNotExist() {
        assertThrows(NotFound.class, () -> groupService.getOr404(100L));
    }

    private List<Group> getGroupsFromService() throws PagesAndSortHandler.FieldNotSortableException {
        return groupService.all(repositoryTestUtils.getFirstPageRequest()).getContent();
    }
}