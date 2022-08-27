package jen.web.service;

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
    void shouldCreateAndRemovePlacementWhenAddingPlacementAndDeletingIt() throws PagesAndSortHandler.FieldNotSortableException, PlacementService.PlacementResultsInProgressException {
        Placement receivedPlacement1 = placementService.add(new Placement("placement 1", 4, null));
        Placement receivedPlacement2 = placementService.add(new Placement("placement 2", 3, null));
        assertEquals(2, getPlacementsFromService().size());
        assertNotEquals(receivedPlacement1.getId(), receivedPlacement2.getId());
        assertNotEquals(placementService.getOr404(receivedPlacement1.getId()), placementService.getOr404(receivedPlacement2.getId()));

        assertEquals("placement 1", receivedPlacement1.getName());
        assertEquals(4, receivedPlacement1.getNumberOfClasses());
        placementService.deleteById(receivedPlacement1.getId());

        assertEquals("placement 2", receivedPlacement2.getName());
        assertEquals(3, receivedPlacement2.getNumberOfClasses());
        placementService.deleteById(receivedPlacement2.getId());
    }

    // Tests

    @Test
    void shouldThrowNotFoundExceptionOnGetPlacementWhenPlacementNotExist() {
        assertThrows(NotFound.class, () -> placementService.getOr404(100L));
    }

    private List<Placement> getPlacementsFromService() throws PagesAndSortHandler.FieldNotSortableException {
        return placementService.all(repositoryTestUtils.getFirstPageRequest()).getContent();
    }
}