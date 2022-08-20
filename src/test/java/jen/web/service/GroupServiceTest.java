package jen.web.service;

import jen.web.entity.Group;
import jen.web.entity.Pupil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class GroupServiceTest {

    @Autowired
    PupilService pupilService;
    @Autowired
    GroupService groupService;
    @Autowired
    TemplateService templateService;

    @Autowired
    RepositoryTestUtils repositoryTestUtils;

    @BeforeEach
    @AfterEach
    void verifyDbIsEmpty() {
        repositoryTestUtils.verifyAllTablesAreEmpty();
    }


    @Test
    void shouldCreateAndRemoveGroupWhenAddingGroupAndDeletingIt() {
        Group receivedGroup1 = groupService.add(new Group("group 1", "group 1 desc", null));
        Group receivedGroup2 = groupService.add(new Group("group 2", "group 2 desc", null));
        assertEquals(2, groupService.all().size());
        assertNotEquals(groupService.getOr404(groupService.all().get(0).getId()), groupService.getOr404(groupService.all().get(1).getId()));


        assertEquals("group 1", receivedGroup1.getName());
        assertEquals("group 1 desc", receivedGroup1.getDescription());
        assertEquals(null, receivedGroup1.getTemplate());
        assertEquals(0, receivedGroup1.getPupils().size());
        assertEquals(0, receivedGroup1.getPreferences().size());
        assertEquals(0, receivedGroup1.getPlacements().size());
        groupService.deleteById(receivedGroup1.getId());

        assertEquals("group 2", receivedGroup2.getName());
        assertEquals("group 2 desc", receivedGroup2.getDescription());
        assertEquals(null, receivedGroup2.getTemplate());
        assertEquals(0, receivedGroup2.getPupils().size());
        assertEquals(0, receivedGroup2.getPreferences().size());
        assertEquals(0, receivedGroup2.getPlacements().size());
        groupService.deleteById(receivedGroup2.getId());
    }

    @Test
    @Transactional
    void shouldAddPupilToGroupWhenAddingFromService() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        Pupil receivedPupil1 = pupilService.add(
                new Pupil("123456789", "Pupil1", "Last1", Pupil.Gender.MALE, LocalDate.of(1990, 1, 1))
        );
        Pupil receivedPupil2 = pupilService.add(
                new Pupil("987654321", "Pupil2", "Last2", Pupil.Gender.FEMALE, LocalDate.of(1992, 2, 2))
        );
        Group receivedGroup1 = groupService.add(new Group("group 1", "group 1 desc", null));
        Group receivedGroup2 = groupService.add(new Group("group 2", "group 2 desc", null));
        assertEquals(2, groupService.all().size());

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

        groupService.unlinkPupilToGroup(receivedGroup1, receivedPupil1);
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
        Pupil receivedPupil1 = pupilService.add(
                new Pupil("123456789", "Pupil1", "Last1", Pupil.Gender.MALE, LocalDate.of(1990, 1, 1))
        );
        Pupil receivedPupil2 = pupilService.add(
                new Pupil("987654321", "Pupil2", "Last2", Pupil.Gender.FEMALE, LocalDate.of(1992, 2, 2))
        );
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

}