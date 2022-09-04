package web.entity;

import web.dto.PupilsConnectionsDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PlacementClassroomTest {

    @Test
    void testScoreWhenThereAreNoPupils(){
        List<Pupil> pupils = new ArrayList<>();
        PupilsConnectionsDto connectionsToInclude = PupilsConnectionsDto.fromSelectorSelectedSet(Set.of(new SelectorSelectedId()));
        PupilsConnectionsDto connectionsToExclude = PupilsConnectionsDto.fromSelectorSelectedSet(Set.of(new SelectorSelectedId()));
        int totalNumberOfMales = 5;
        int totalNumberOfFemales = 5;

        PlacementClassroom placementClassroom = new PlacementClassroom(pupils, connectionsToInclude, connectionsToExclude, totalNumberOfMales, totalNumberOfFemales);
        double score = placementClassroom.getClassScore();
        System.out.println(score);

        assertTrue(score >= 0 && score <= 100);
    }

    @Test
    void testScoreWhenDeltaOfNumberOfPupilsIsLessThanTotalNumber() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        List<Pupil> pupils = createPupilList();
        PupilsConnectionsDto connectionsToInclude = PupilsConnectionsDto.fromSelectorSelectedSet(Set.of(new SelectorSelectedId()));
        PupilsConnectionsDto connectionsToExclude = PupilsConnectionsDto.fromSelectorSelectedSet(Set.of(new SelectorSelectedId()));
        int totalNumberOfMales = 5;
        int totalNumberOfFemales = 5;

        PlacementClassroom placementClassroom = new PlacementClassroom(pupils, connectionsToInclude, connectionsToExclude, totalNumberOfMales, totalNumberOfFemales);
        double score = placementClassroom.getClassScore();
        System.out.println(score);

        assertTrue(score >= 0 && score <= 100);
    }

    @Test
    void testScore() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        List<Pupil> pupils = createPupilList();
        PupilsConnectionsDto connectionsToInclude = PupilsConnectionsDto.fromSelectorSelectedSet(Set.of(new SelectorSelectedId()));
        PupilsConnectionsDto connectionsToExclude = PupilsConnectionsDto.fromSelectorSelectedSet(Set.of(new SelectorSelectedId()));
        int totalNumberOfMales = 2;
        int totalNumberOfFemales = 0;

        PlacementClassroom placementClassroom = new PlacementClassroom(pupils, connectionsToInclude, connectionsToExclude, totalNumberOfMales, totalNumberOfFemales);
        double score = placementClassroom.getClassScore();
        System.out.println(score);

        assertTrue(score >= 0 && score <= 100);
    }

    private List<Pupil> createPupilList() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        List<Pupil> pupils = List.of(
                new Pupil("123456789", "name1", "lastName1", Pupil.Gender.MALE, LocalDate.of(1992, 2, 2)),
                new Pupil("234567890", "name2", "lastName2", Pupil.Gender.MALE, LocalDate.of(1992, 2, 2))
        );
        return pupils;
    }
}