package jen.web.service;

import jen.web.entity.Group;
import jen.web.entity.Placement;
import jen.web.exception.NotFound;
import jen.web.util.PagesAndSortHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PlacementServiceTest {

    @Autowired PlacementService placementService;
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
    void shouldCreateAndRemovePlacementWhenAddingPlacementWithoutGroupAndDeletingIt() throws PagesAndSortHandler.FieldNotSortableException, PlacementService.PlacementResultsInProgressException {
        Placement receivedPlacement1 = placementService.add(new Placement("placement 1", 4, null));
        Placement receivedPlacement2 = placementService.add(new Placement("placement 2", 3, null));
        assertEquals(2, getPlacementsFromService().size());
        assertNotEquals(receivedPlacement1.getId(), receivedPlacement2.getId());
        assertNotEquals(placementService.getOr404(receivedPlacement1.getId()), placementService.getOr404(receivedPlacement2.getId()));

        assertEquals("placement 1", receivedPlacement1.getName());
        assertEquals(4, receivedPlacement1.getNumberOfClasses());
        assertNull(receivedPlacement1.getGroup());
        assertNull(receivedPlacement1.getGroupId());
        placementService.deleteById(receivedPlacement1.getId());

        assertEquals("placement 2", receivedPlacement2.getName());
        assertEquals(3, receivedPlacement2.getNumberOfClasses());
        placementService.deleteById(receivedPlacement2.getId());
    }

    @Test
    void shouldCreateAndRemovePlacementWhenAddingPlacementWithGroupAndDeletingIt() throws PagesAndSortHandler.FieldNotSortableException, PlacementService.PlacementResultsInProgressException {
        Group receivedGroup1 = groupService.add(new Group("group 1", "group 1 desc", null));
        Placement receivedPlacement1 = placementService.add(new Placement("placement 1", 4, receivedGroup1));

        assertEquals("placement 1", receivedPlacement1.getName());
        assertEquals(4, receivedPlacement1.getNumberOfClasses());
        assertEquals("group 1", receivedPlacement1.getGroup().getName());
        assertEquals("group 1 desc", receivedPlacement1.getGroup().getDescription());
        assertEquals(receivedGroup1.getId(), receivedPlacement1.getGroupId());
        placementService.deleteById(receivedPlacement1.getId());

        groupService.deleteById(receivedGroup1.getId());
    }




    // Tests

    @Test
    void shouldThrowNotFoundExceptionOnGetPlacementWhenPlacementNotExist() {
        assertThrows(NotFound.class, () -> placementService.getOr404(100L));
    }

    @Test
    void shouldThrowResultNotExistsExceptionOnGetPlacementResultWhenPlacementDontHaveTheResult() throws PlacementService.PlacementResultsInProgressException {
        Placement receivedPlacement = placementService.add(new Placement("placement 1", 4, null));
        assertThrows(Placement.ResultNotExistsException.class, () -> placementService.getResultById(receivedPlacement, 100L));
        placementService.deleteById(receivedPlacement.getId());
    }

    private List<Placement> getPlacementsFromService() throws PagesAndSortHandler.FieldNotSortableException {
        return placementService.all(repositoryTestUtils.getFirstPageRequest()).getContent();
    }
}