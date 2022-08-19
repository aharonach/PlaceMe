package jen.web.service;

import jen.web.entity.*;
import jen.web.exception.EntityAlreadyExists;
import jen.web.exception.NotFound;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class PupilServiceTest {

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
    void shouldCreateAndRemovePupilWhenAddingPupilAndDeletingIt() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        Pupil pupil1 = new Pupil("123456789", "Pupil1", "Last1", Pupil.Gender.MALE, LocalDate.of(1990, 1, 1));
        Pupil receivedPupil1 = pupilService.add(pupil1);

        Pupil pupil2 = new Pupil("987654321", "Pupil2", "Last2", Pupil.Gender.FEMALE, LocalDate.of(1992, 2, 2));
        Pupil receivedPupil2 = pupilService.add(pupil2);

        assertEquals(2, pupilService.all().size());

        assertEquals("Pupil1", receivedPupil1.getFirstName());
        assertEquals("Last1", receivedPupil1.getLastName());
        assertEquals("123456789", receivedPupil1.getGivenId());
        assertEquals(Pupil.Gender.MALE, receivedPupil1.getGender());
        assertEquals(1990, receivedPupil1.getBirthDate().getYear());

        assertEquals("Pupil2", receivedPupil2.getFirstName());
        assertEquals("Last2", receivedPupil2.getLastName());
        assertEquals("987654321", receivedPupil2.getGivenId());
        assertEquals(Pupil.Gender.FEMALE, receivedPupil2.getGender());
        assertEquals(1992, receivedPupil2.getBirthDate().getYear());

        pupilService.deleteById(receivedPupil1.getId());
        pupilService.deleteById(receivedPupil2.getId());

    }

    @Test
    void shouldGetSamePupilWhenGettingPupilByIdAndByGivenId() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        Pupil pupil = new Pupil("123456789", "Pupil1", "Last1", Pupil.Gender.MALE, LocalDate.of(1990, 1, 1));
        assertFalse(pupilService.isPupilExists("123456789"));

        Pupil receivedPupil = pupilService.add(pupil);

        assertTrue(pupilService.isPupilExists("123456789"));
        assertEquals(1, pupilService.all().size());

        Pupil pupilByGivenId = pupilService.getByGivenIdOr404("123456789");
        assertEquals(receivedPupil, pupilByGivenId);

        pupilService.deleteById(receivedPupil.getId());
    }

    @Test
    void shouldThrowExceptionWhenAddingPupilWithSameOrInvalidGivenId() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        Pupil pupil = new Pupil("123456789", "Pupil1", "Last1", Pupil.Gender.MALE, LocalDate.of(1990, 1, 1));
        Pupil receivedPupil = pupilService.add(pupil);

        assertEquals(1, pupilService.all().size());

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
    void shouldAddPupilToGroupWhenCreatingPupilAndAddHimToAGroup() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        Group receivedGroup1 = groupService.add(new Group("group 1", "group 1 desc", null));
        Group receivedGroup2 = groupService.add(new Group("group 2", "group 2 desc", null));
        assertEquals(2, groupService.all().size());

        Pupil pupil = new Pupil("123456789", "Pupil1", "Last1", Pupil.Gender.MALE, LocalDate.of(1990, 1, 1));
        pupil.addToGroup(receivedGroup1);
        Pupil receivedPupil = pupilService.add(pupil);

        // not require save or update
        receivedPupil.addToGroup(receivedGroup2);

        assertEquals(1, pupilService.all().size());
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
        Pupil pupil = new Pupil("123456789", "Pupil1", "Last1", Pupil.Gender.MALE, LocalDate.of(1990, 1, 1));
        Pupil receivedPupil = pupilService.add(pupil);

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

    // test update

    @Test
    void shouldThrowNotFoundExceptionOnGetPupilWhenPupilNotExist() {
        assertThrows(NotFound.class, () -> pupilService.getOr404(100L));
    }

    @Test
    void shouldThrowNotFoundExceptionOnGetPupilByGivenIdWhenPupilNotExist() {
        assertThrows(NotFound.class, () -> pupilService.getByGivenIdOr404("1234"));
    }
}