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

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PlacementServiceTest {

    @Autowired PlacementService placementService;
    @Autowired GroupService groupService;
    @Autowired TemplateService templateService;
    @Autowired PupilService pupilService;
    @Autowired RepositoryTestUtils repositoryTestUtils;

    @BeforeEach
    void setUp() {
        repositoryTestUtils.clearAllData();
        PlaceEngineConfig placeEngineConfig = new PlaceEngineConfig();
        placementService.updateGlobalConfig(placeEngineConfig);
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
    @Transactional
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

    @Test
    void shouldGenerateResultSuccessfullyWhenAllTheRequirementsAreExist() throws PlacementService.PlacementResultsInProgressException, PlacementService.PlacementWithoutGroupException, PlacementService.PlacementWithoutPupilsInGroupException, Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException, InterruptedException, ExecutionException, Placement.ResultNotExistsException, Template.AttributeAlreadyExistException {
        // replace Executor with Mock
        ExecutorMock executorMock = new ExecutorMock();
        placementService.setExecutor(executorMock);

        Pupil receivedPupil1 = pupilService.add(repositoryTestUtils.createPupil1());
        Pupil receivedPupil2 = pupilService.add(repositoryTestUtils.createPupil2());
        Template receivedTemplate = templateService.add(repositoryTestUtils.createTemplate2());
        Group receivedGroup = groupService.add(new Group("group 1", "group 1 desc", receivedTemplate));
        groupService.linkPupilToGroup(receivedGroup, receivedPupil1);
        groupService.linkPupilToGroup(receivedGroup, receivedPupil2);
        Placement receivedPlacement = placementService.add(new Placement("placement 1", 4, receivedGroup));

        PlacementResult placementResult = placementService.generatePlacementResult(receivedPlacement, "name", "description");
        assertEquals(PlacementResult.Status.IN_PROGRESS, placementResult.getStatus());
        assertNotNull(placementResult.getId());

        // verify that exceptions are throws when performing action on placement with in_progress result
        assertThrows(PlacementService.PlacementResultsInProgressException.class, () -> placementService.deleteById(receivedPlacement.getId()));
        assertThrows(PlacementService.PlacementResultsInProgressException.class, () -> placementService.updateById(receivedPlacement.getId(), receivedPlacement));
        assertThrows(PlacementService.PlacementResultsInProgressException.class, () -> placementService.deleteAllPlacementResults(receivedPlacement));
        assertThrows(PlacementService.PlacementResultsInProgressException.class, () -> placementService.deletePlacementResultById(receivedPlacement, placementResult.getId()));

        // start the alg and update status
        Future<?> future = executorMock.submitFirst();
        future.get();
        assertTrue(future.isDone());

        PlacementResult receivedPlacementResult = placementService.getResultById(receivedPlacement, placementResult.getId());
        assertEquals(PlacementResult.Status.COMPLETED, receivedPlacementResult.getStatus());

        groupService.deleteById(receivedGroup.getId());
        placementService.deleteById(receivedPlacement.getId());
        templateService.deleteById(receivedTemplate.getId());
        pupilService.deleteById(receivedPupil1.getId());
        pupilService.deleteById(receivedPupil2.getId());
    }

    @Test
    void shouldThrowExceptionsOnGenerateResultWhenThereIsNoGroup() throws PlacementService.PlacementResultsInProgressException {
        Placement receivedPlacement = placementService.add(new Placement("placement 1", 4, null));
        assertThrows(PlacementService.PlacementWithoutGroupException.class, () -> placementService.generatePlacementResult(receivedPlacement, "name", "description"));
        placementService.deleteById(receivedPlacement.getId());
    }

    @Test
    void shouldThrowExceptionsOnGenerateResultWhenThereAreNoPupilsGroup() throws PlacementService.PlacementResultsInProgressException {
        Group receivedGroup = groupService.add(new Group("group 1", "group 1 desc", null));
        Placement receivedPlacement = placementService.add(new Placement("placement 1", 4, receivedGroup));
        assertThrows(PlacementService.PlacementWithoutPupilsInGroupException.class, () -> placementService.generatePlacementResult(receivedPlacement,"name", "description"));
        placementService.deleteById(receivedPlacement.getId());
        groupService.deleteById(receivedGroup.getId());
    }

    @Test
    void shouldGenerateFewResultsSuccessfullyAndDeleteThem() throws PlacementService.PlacementResultsInProgressException, PlacementService.PlacementWithoutGroupException, PlacementService.PlacementWithoutPupilsInGroupException, Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException, InterruptedException, ExecutionException, Placement.ResultNotExistsException, Template.AttributeAlreadyExistException {
        // replace Executor with Mock
        ExecutorMock executorMock = new ExecutorMock();
        placementService.setExecutor(executorMock);

        Pupil receivedPupil1 = pupilService.add(repositoryTestUtils.createPupil1());
        Pupil receivedPupil2 = pupilService.add(repositoryTestUtils.createPupil2());
        Template receivedTemplate = templateService.add(repositoryTestUtils.createTemplate2());
        Group receivedGroup = groupService.add(new Group("group 1", "group 1 desc", receivedTemplate));
        groupService.linkPupilToGroup(receivedGroup, receivedPupil1);
        groupService.linkPupilToGroup(receivedGroup, receivedPupil2);
        Placement receivedPlacement = placementService.add(new Placement("placement 1", 4, receivedGroup));

        List<PlacementResult> results = new ArrayList<>(3);
        for(int i=0; i<3; i++){
            PlacementResult placementResult = placementService.generatePlacementResult(placementService.getOr404(receivedPlacement.getId()), "name", "description");
            results.add(placementResult);
            assertEquals(PlacementResult.Status.IN_PROGRESS, placementResult.getStatus());
            assertNotNull(placementResult.getId());
        }
        assertEquals(3, placementService.getOr404(receivedPlacement.getId()).getResults().size());

        assertThrows(PlacementService.PlacementResultsInProgressException.class, () -> placementService.deletePlacementResultById(receivedPlacement, results.get(0).getId()));
        PlacementResult r = (PlacementResult) executorMock.submitFirst().get();
        assertEquals(PlacementResult.Status.COMPLETED, placementService.getResultById(receivedPlacement, results.get(0).getId()).getStatus());
        placementService.deletePlacementResultById(receivedPlacement, results.get(0).getId());
        assertEquals(2, placementService.getOr404(receivedPlacement.getId()).getResults().size());

        assertThrows(PlacementService.PlacementResultsInProgressException.class, () -> placementService.deleteAllPlacementResults(placementService.getOr404(receivedPlacement.getId())));
        executorMock.submitFirst().get();
        executorMock.submitFirst().get();
        assertEquals(PlacementResult.Status.COMPLETED, placementService.getResultById(receivedPlacement, results.get(1).getId()).getStatus());
        assertEquals(PlacementResult.Status.COMPLETED, placementService.getResultById(receivedPlacement, results.get(2).getId()).getStatus());
        placementService.deleteAllPlacementResults(receivedPlacement);
        assertEquals(0, placementService.getOr404(receivedPlacement.getId()).getResults().size());

        // generate one more result
        placementService.generatePlacementResult(placementService.getOr404(receivedPlacement.getId()), "name", "description");
        executorMock.submitFirst().get();
        assertEquals(1, placementService.getOr404(receivedPlacement.getId()).getResults().size());

        groupService.deleteById(receivedGroup.getId());
        placementService.deleteById(receivedPlacement.getId());
        templateService.deleteById(receivedTemplate.getId());
        pupilService.deleteById(receivedPupil1.getId());
        pupilService.deleteById(receivedPupil2.getId());
    }

    @Test
    void shouldUpdatePlacementResultOnEditProperties() throws PlacementService.PlacementWithoutGroupException, PlacementService.PlacementWithoutPupilsInGroupException, Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException, ExecutionException, InterruptedException, Placement.ResultNotExistsException, PlacementService.PlacementResultsInProgressException, Template.AttributeAlreadyExistException {
        // replace Executor with Mock
        ExecutorMock executorMock = new ExecutorMock();
        placementService.setExecutor(executorMock);

        Pupil receivedPupil1 = pupilService.add(repositoryTestUtils.createPupil1());
        Pupil receivedPupil2 = pupilService.add(repositoryTestUtils.createPupil2());
        Template receivedTemplate = templateService.add(repositoryTestUtils.createTemplate2());
        Group receivedGroup = groupService.add(new Group("group 1", "group 1 desc", receivedTemplate));
        groupService.linkPupilToGroup(receivedGroup, receivedPupil1);
        groupService.linkPupilToGroup(receivedGroup, receivedPupil2);
        Placement receivedPlacement = placementService.add(new Placement("placement 1", 4, receivedGroup));

        PlacementResult placementResult = placementService.generatePlacementResult(receivedPlacement, "name", "description");
        PlacementResult updatedResult = new PlacementResult();

        updatedResult.setName("name updated");
        updatedResult.setDescription("desc updated");

        // validate the result after generation
        assertEquals("name", placementResult.getName() );
        assertEquals("description", placementResult.getDescription() );

        executorMock.submitFirst().get();
        PlacementResult receivedResult = placementService.getResultById(receivedPlacement, placementResult.getId());

        updatedResult = placementService.updatePlacementResult(receivedPlacement, receivedResult.getId(), updatedResult);

        assertEquals("name updated", updatedResult.getName());
        assertEquals("desc updated", updatedResult.getDescription());

        assertEquals(1, placementService.getOr404(receivedPlacement.getId()).getResults().size());

        groupService.deleteById(receivedGroup.getId());
        placementService.deleteById(receivedPlacement.getId());
        templateService.deleteById(receivedTemplate.getId());
        pupilService.deleteById(receivedPupil1.getId());
        pupilService.deleteById(receivedPupil2.getId());
    }

    // @Todo: add import export tests
//    @Test
//    @Transactional
//    void testImport(){
//
//    }

    // @Todo: Test update method

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

    private class ExecutorMock extends ThreadPoolExecutor {
        public Queue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        public ExecutorMock() {
            super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        }
        @Override
        public Future<?> submit(Runnable task){
            this.taskQueue.add(task);
            return new FutureTask<>(() -> null);
        }

        public Future<?> submitFirst(){
            Runnable task = this.taskQueue.poll();
            if(task == null){
                throw new RuntimeException("There are no task in Q");
            }
            return super.submit(task);
        }
    }

}