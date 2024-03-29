package web.service;

import web.engine.PlaceEngine;
import web.entity.*;
import web.exception.BadRequest;
import web.exception.EntityAlreadyExists;
import web.exception.NotFound;
import web.repository.*;
import web.util.CsvUtils;
import web.util.ImportExportUtils;
import web.util.OperationInfo;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
@RequiredArgsConstructor
public class PlacementService implements EntityService<Placement> {

    private static final Logger logger = LoggerFactory.getLogger(PlacementService.class);

    private final PlacementRepository placementRepository;
    private final PlacementResultRepository placementResultRepository;
    private final PlacementClassroomRepository placementClassroomRepository;
    private final PupilRepository pupilRepository;
    private final GroupService groupService;
    private final PlaceEngineConfigRepository engineConfigRepository;
    private final ImportExportUtils importExportUtils;

    @Setter
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    @Transactional
    public Placement add(Placement placement) {
        Long id = placement.getId();
        if (id != null && placementRepository.existsById(id)) {
            throw new EntityAlreadyExists("Placement with Id '" + id + "' already exists.");
        }

        if(placement.getGroup() != null && placement.getGroup().getId() != null){
            Group group = groupService.getOr404(placement.getGroup().getId());
            group.addPlacement(placement);
            placement.setGroup(group);
        } else {
            placement.setGroup(null);
        }

        return placementRepository.save(placement);
    }

    @Override
    public Placement getOr404(Long id) {
        return placementRepository.findById(id).orElseThrow(() -> new NotFound("Could not find placement " + id));
    }

    @Override
    public List<Placement> allWithoutPages() {
        return placementRepository.findAll();
    }

    @Override
    public Page<Placement> all(PageRequest pageRequest) {
        return placementRepository.findAll(pageRequest);
    }

    @Override
    @Transactional
    public Placement updateById(Long id, Placement newPlacement) throws PlacementResultsInProgressException {
        Placement placement = getOr404(id);
        verifyPlacementDontHaveInProgressTasks(placement);

        boolean isUpdateGroupNeeded = newPlacement.getGroup() != null && !newPlacement.getGroup().equals(placement.getGroup());

        placement.setName(newPlacement.getName());
        placement.setNumberOfClasses(newPlacement.getNumberOfClasses());

        if(isUpdateGroupNeeded){
            boolean newGroupIsNull = newPlacement.getGroup().getId() == null;
            if(newGroupIsNull){
                placement.setGroup(null);
            } else {
                Group newGroup = groupService.getOr404(newPlacement.getGroup().getId());

                Set<Long> newPlacementIdsForGroup = placement.getGroup() == null ? new HashSet<>() : placement.getGroup().getPlacementIds();
                if(placement.getGroup() != null){
                    newPlacementIdsForGroup.remove(placement.getId());
                }

                placement.setGroup(newGroup);
                newGroup.setPlacements(placementRepository.getAllByIdIn(newPlacementIdsForGroup));
            }
        }

        return placementRepository.save(placement);
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws PlacementResultsInProgressException {
        Placement placement = getOr404(id);
        verifyPlacementDontHaveInProgressTasks(placement);

        Group group = placement.getGroup();

        if(group != null){
            group.removePlacement(placement);
        }
        deleteAllPlacementResults(placement);

        placementRepository.delete(placement);
    }

    private void verifyPlacementDontHaveInProgressTasks(Placement placement) throws PlacementResultsInProgressException {
        Long numOfInProgressTasks = placement.getResults().stream()
                .filter(placementResult -> PlacementResult.Status.IN_PROGRESS.equals(placementResult.getStatus()))
                .count();
        if(numOfInProgressTasks > 0){
            throw new PlacementResultsInProgressException();
        }
    }

    @Transactional
    protected PlacementResult savePlacementResult(Placement placement, PlacementResult placementResult) {
        placementResult.setPlacement(placement);

        // Save classes of result
        placementResult.getClasses().forEach(placementClassroom -> placementClassroom.setPlacementResult(placementResult));
        placementClassroomRepository.saveAll(placementResult.getClasses());

        // Add each class for each pupil
        placementResult.getClasses().forEach(placementClassroom -> {
            placementClassroom.getPupils().forEach(pupil -> {
                Set<Long> classIds = pupil.getClassroomIds();
                classIds.add(placementClassroom.getId());
                pupil.setClassrooms(new HashSet<>(placementClassroomRepository.getAllByIdIn(classIds)));
            });
            pupilRepository.saveAll(placementClassroom.getPupils());
        });

        // Save placement result
        PlacementResult savedResult = placementResultRepository.save(placementResult);

        // Save placement
        placement.addResult(savedResult);
        placementRepository.save(placement);

        return savedResult;
    }

    public PlacementResult updatePlacementResult(Placement placement, Long resultId, PlacementResult result) throws Placement.ResultNotExistsException {
        PlacementResult resultFromDB = getResultById(placement, resultId);
        resultFromDB.setName(result.getName());
        resultFromDB.setDescription(result.getDescription());
        placementResultRepository.save(resultFromDB);
        return resultFromDB;
    }

    @Transactional
    public void deleteAllPlacementResults(Placement placement) throws PlacementResultsInProgressException {
        List<Long> resultIdsToRemove = getOr404(placement.getId()).getResults().stream()
                .map(BaseEntity::getId).toList();
        for(Long resultId : resultIdsToRemove){
            try {
                deletePlacementResultById(placement, resultId);
            } catch (Placement.ResultNotExistsException ignored) {
                System.out.println("Error: " + ignored.getMessage());
            }
        }
    }

    @Transactional
    public void deletePlacementResultById(Placement placement, Long resultId)
            throws Placement.ResultNotExistsException, PlacementResultsInProgressException {

        PlacementResult placementResult = getOr404(placement.getId()).getResultById(resultId);
        if(PlacementResult.Status.IN_PROGRESS.equals(placementResult.getStatus())){
            throw new PlacementResultsInProgressException();
        }

        placement.removeResult(placementResult);
        placementResult.setPlacement(null);

        placementResult.getClasses().forEach(placementClassroom -> {
            placementClassroom.setPlacementResult(null);
            placementClassroom.getPupils().forEach(pupil -> pupil.removeFromClassrooms(placementClassroom));
            placementClassroom.setPupils(new HashSet<>());
        });

        placementClassroomRepository.deleteAll(placementResult.getClasses());
        placementResultRepository.deleteById(resultId);
    }

    public PlacementResult generatePlacementResult(Placement placement, String name, String description, Long configId) throws PlacementWithoutGroupException, PlacementWithoutPupilsInGroupException {
        verifyPlacementGroupContainsPupils(placement);

        PlaceEngine placeEngine = new PlaceEngine(placement, getGlobalConfig(configId));

        PlacementResult placementResult = new PlacementResult();
        if (name != null) placementResult.setName(name);
        if (description != null) placementResult.setDescription(description);
        placementResult.setGroup(placement.getGroup());

        placementResult = savePlacementResult(placement, placementResult);
        Long resultId = placementResult.getId();

        // update result will be called after the generation will finish
        executor.submit(() -> generateAndUpdateResultStatus(placeEngine, resultId, placement.getId()));

        return placementResult;
    }

    protected synchronized void generateAndUpdateResultStatus(PlaceEngine placeEngine, Long resultId, Long placementId) {
        Placement placement = getOr404(placementId);
        try{
            PlacementResult placementResult = getResultById(placement, resultId);
            try {
                PlacementResult algResult = placeEngine.generatePlacementResult();
                placementResult.setClasses(algResult.getClasses());
                placementResult.setStatus(PlacementResult.Status.COMPLETED);

            } catch (Exception e){
                placementResult.setStatus(PlacementResult.Status.FAILED);
                logger.error("error during generation: " + e.getMessage());
            }
            savePlacementResult(placement, placementResult);

        } catch (Placement.ResultNotExistsException e) {
            logger.error("Could not start result generation" + e.getMessage());
        }
    }

    private void verifyPlacementContainGroup(Placement placement) throws PlacementWithoutGroupException {
        if(placement.getGroup() == null){
            throw new PlacementWithoutGroupException();
        }
    }

    private void verifyPlacementGroupContainsPupils(Placement placement) throws PlacementWithoutPupilsInGroupException, PlacementWithoutGroupException {
        verifyPlacementContainGroup(placement);
        if(placement.getGroup().getPupils().size() == 0){
            throw new PlacementWithoutPupilsInGroupException();
        }
    }

    private void verifyPlacementGroupContainTemplate(Placement placement) throws PlacementWithoutGroupException, PlacementWithoutTemplateInGroupException {
        verifyPlacementContainGroup(placement);
        if(placement.getGroup().getTemplate() == null){
            throw new PlacementWithoutTemplateInGroupException();
        }
    }

    public Page<PlacementResult> getPlacementResults(Placement placement, PageRequest pageRequest) {
        return placementResultRepository.getAllByIdIn(placement.getResultIds(), pageRequest);
    }

    public Page<PlacementClassroom> getPlacementResultClasses(PlacementResult placementResult, PageRequest pageRequest) {
        return placementClassroomRepository.getAllByIdIn(placementResult.getClassesIds(), pageRequest);
    }

    public List<PlacementClassroom> getAllPlacementResultClasses(PlacementResult placementResult) {
        return placementClassroomRepository.getAllByIdIn(placementResult.getClassesIds());
    }

    public PlacementResult getResultById(Placement placement, Long resultID) throws Placement.ResultNotExistsException {
        PlacementResult placementResult = getOr404(placement.getId()).getResultById(resultID);
        return placementResultRepository.findById(placementResult.getId()).orElseThrow(() -> new NotFound("Could not find placement result " + placementResult.getId()));
    }

    public PlacementResult getSelectedResult(Placement placement) throws Placement.NoSelectedResultException {
        PlacementResult placementResult = placement.getSelectedResult();
        if(placementResult == null){
            throw new Placement.NoSelectedResultException();
        }
        return placementResult;
    }

    public PlacementResult setSelectedResult(Placement placement, Long resultId) throws Placement.ResultNotExistsException, PlacementResult.NotCompletedException {
        PlacementResult placementResult = placement.getResultById(resultId);
        placement.setSelectedResult(placementResult);
        placementRepository.save(placement);
        return placementResult;
    }

    public PlaceEngineConfig getGlobalConfig(Long configId) {
        PlaceEngineConfig config = engineConfigRepository.findById(configId).get();

        if ( config == null) {
            throw new NotFound("Config not found");
        }

        return config;
    }

    public PlaceEngineConfig updateGlobalConfig(PlaceEngineConfig placeEngineConfig) {
        return engineConfigRepository.save(placeEngineConfig);
    }

    public PlaceEngineConfig resetGlobalConfig(Long configId) {
        PlaceEngineConfig config = getGlobalConfig(configId);
        config.ResetToDefault(configId);
        return engineConfigRepository.save(config);
    }

    @Transactional
    public String exportCsvHeadersByPlacement(Placement placement) throws CsvUtils.CsvContent.CsvNotValidException, PlacementWithoutTemplateInGroupException, PlacementWithoutGroupException {
        verifyPlacementGroupContainTemplate(placement);
        List<String> columns = importExportUtils.getColumnNames(placement);
        CsvUtils.CsvContent csvContent = new CsvUtils.CsvContent(columns);
        return csvContent.getHeadersLine();
    }

    @Transactional
    public String exportCsvDataByPlacement(Placement placement) throws CsvUtils.CsvContent.CsvNotValidException, Group.PupilNotBelongException, IllegalAccessException, NoSuchFieldException, PlacementWithoutTemplateInGroupException, PlacementWithoutGroupException {
        verifyPlacementGroupContainTemplate(placement);
        Group group = placement.getGroup();
        List<String> columns = importExportUtils.getColumnNames(placement);
        List<String> rows = importExportUtils.createRowDataForPupils(group, columns);
        CsvUtils.CsvContent csvContent = new CsvUtils.CsvContent(columns, rows);
        return csvContent.getFullFileContent();
    }

    @Transactional
    public String exportCsvDataByPlacementResult(PlacementResult placementResult) throws CsvUtils.CsvContent.CsvNotValidException, Group.PupilNotBelongException, IllegalAccessException, NoSuchFieldException, PlacementWithoutTemplateInGroupException, PlacementWithoutGroupException {
        Placement placement = placementResult.getPlacement();
        verifyPlacementGroupContainTemplate(placement);
        Group group = placement.getGroup();
        List<String> columns = importExportUtils.getColumnNamesWithClasses(placement);
        List<String> rows = importExportUtils.getRowsForPupilsWithClasses(group, columns, getAllPlacementResultClasses(placementResult));
        CsvUtils.CsvContent csvContent = new CsvUtils.CsvContent(columns, rows);
        return csvContent.getFullFileContent();
    }

    @Transactional
    public OperationInfo importDataFromCsv(Placement placement, String input) throws CsvUtils.CsvContent.CsvNotValidException, PlacementWithoutTemplateInGroupException, PlacementWithoutGroupException {
        verifyPlacementGroupContainTemplate(placement);

        CsvUtils.CsvContent csvContent = new CsvUtils.CsvContent(input);

        // parse data and create pupils map
        return importExportUtils.parseAndAddDataFromFile(csvContent, placement);
    }

    @Transactional
    public List<PlacementClassroom> movePupilBetweenClassrooms(PlacementResult result, Long classroomFromId, Long classroomToId, Long pupilId) throws Group.PupilNotBelongException {
        Pupil pupil = result.getGroup().getPupilById(pupilId);
        PlacementClassroom classroomFrom = result.getClasses()
                .stream()
                .filter(classroom -> classroom.getId().equals(classroomFromId))
                .findFirst()
                .orElse(null);

        if (classroomFrom == null) {
            throw new BadRequest("Classroom " + classroomFromId + " is invalid");
        }

        PlacementClassroom classroomTo = result.getClasses()
                .stream()
                .filter(classroom -> classroom.getId().equals(classroomToId))
                .findFirst()
                .orElse(null);

        if (classroomFrom == null) {
            throw new BadRequest("Classroom " + classroomToId + " is invalid");
        }

        classroomFrom.removePupilFromClass(pupil);
        classroomTo.addPupilToClass(pupil);
        placementClassroomRepository.save(classroomFrom);
        placementClassroomRepository.save(classroomTo);
        placementResultRepository.save(result);
        return getAllPlacementResultClasses(result);
    }

    public List<PlaceEngineConfig> allConfigs() {
        return engineConfigRepository.findAll();
    }

    public static class PlacementWithoutGroupException extends Exception {
        public PlacementWithoutGroupException(){
            super("Placement must be assigned to a Group");
        }
    }

    public static class PlacementResultsInProgressException extends Exception {
        public PlacementResultsInProgressException(){
            super("Cant perform the action. Placement have results that are in progress.");
        }
    }

    public static class PlacementWithoutPupilsInGroupException extends Exception {
        public PlacementWithoutPupilsInGroupException(){
            super("Group have no pupils.");
        }
    }

    public static class PlacementWithoutTemplateInGroupException extends Exception {
        public PlacementWithoutTemplateInGroupException(){
            super("Group have no template.");
        }
    }
}