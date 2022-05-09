package jen.example.placePupils;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ClassInfoTest {

    @Test
    public void shouldReturnZeroWhenFriendIsInClass() {
        List<Pupil> pupils = List.of(
                new Pupil("", Pupil.Gender.MALE, null),
                new Pupil("", Pupil.Gender.FEMALE, null)
        );
        PupilsConnections connectionsToInclude = new PupilsConnections(Map.of(
                pupils.get(0), List.of(pupils.get(1))
        ));
        PupilsConnections connectionsToExclude = new PupilsConnections(Map.of(
        ));
        ClassInfo classInfo = new ClassInfo(pupils, connectionsToInclude, connectionsToExclude);

        assertEquals(0, classInfo.getNumberOfWrongConnectionsToInclude());
        assertEquals(0, classInfo.getNumberOfWrongConnectionsToExclude());
    }

    @Test
    public void shouldReturnOneWhenFriendIsNotInClass() {
        List<Pupil> pupils = List.of(
                new Pupil("", Pupil.Gender.MALE, null),
                new Pupil("", Pupil.Gender.FEMALE, null)
        );
        PupilsConnections connectionsToInclude = new PupilsConnections(Map.of(
                pupils.get(0), List.of(new Pupil("", Pupil.Gender.MALE, null))
        ));
        PupilsConnections connectionsToExclude = new PupilsConnections(Map.of(
                pupils.get(0), List.of(pupils.get(1))
        ));
        ClassInfo classInfo = new ClassInfo(pupils, connectionsToInclude, connectionsToExclude);

        assertEquals(1, classInfo.getNumberOfWrongConnectionsToInclude());
        assertEquals(1, classInfo.getNumberOfWrongConnectionsToExclude());
    }

    @Test
    public void getNumberOfWrongConnectionsToExclude() {
    }


    @Test
    public void getNumOfPupilsByGender() {
    }
}