package jen.web.service;

import jen.web.engine.PlaceEngine;
import jen.web.entity.*;
import jen.web.exception.EntityAlreadyExists;
import jen.web.exception.NotFound;
import jen.web.repository.*;
import jen.web.util.CsvUtils;
import jen.web.util.ImportExportUtils;
import jen.web.util.OperationInfo;
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

        Placement res = placementRepository.save(placement);
        return res;
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

        Placement res = placementRepository.save(placement);
        return res;
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

        placementResult.getClasses().forEach(placementClassroom -> placementClassroom.setPlacementResult(placementResult));
        placementClassroomRepository.saveAll(placementResult.getClasses());

        placementResult.getClasses().forEach(placementClassroom -> {
            placementClassroom.getPupils().forEach(pupil -> {
                Set<Long> classIds = pupil.getClassroomIds();
                classIds.add(placementClassroom.getId());
                pupil.setClassrooms(placementClassroomRepository.getAllByIdIn(classIds));
            });
            pupilRepository.saveAll(placementClassroom.getPupils());
        });

        PlacementResult savedResult = placementResultRepository.save(placementResult);
        placement.addResult(savedResult);
        placementRepository.save(placement);

        return savedResult;
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
            placementClassroom.getPupils().forEach(pupil -> {
                pupil.removeFromClassrooms(placementClassroom);
            });
            placementClassroom.setPupils(new HashSet<>());
        });

        placementClassroomRepository.deleteAll(placementResult.getClasses());
        placementResultRepository.deleteById(resultId);
    }

    public PlacementResult generatePlacementResult(Placement placement) throws PlacementWithoutGroupException, PlacementWithoutPupilsInGroupException {
        verifyPlacementContainsDataForGeneration(placement);

        PlaceEngine placeEngine = new PlaceEngine(placement, getGlobalConfig());

        PlacementResult placementResult = new PlacementResult();
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

    private void verifyPlacementContainsDataForGeneration(Placement placement) throws PlacementWithoutGroupException, PlacementWithoutPupilsInGroupException {
        if(placement.getGroup() == null){
            throw new PlacementWithoutGroupException();
        }

        if(placement.getGroup().getPupils().size() == 0){
            throw new PlacementWithoutPupilsInGroupException();
        }

        // @todo: attr values will be empty. should we avoid starting a placement in this case?
        if(placement.getGroup().getTemplate() == null){
            System.out.println("group not have template");
        }
    }

    public Page<PlacementResult> getPlacementResults(Placement placement, PageRequest pageRequest) {
        return placementResultRepository.getAllByIdIn(placement.getResultIds(), pageRequest);
    }

    public Page<PlacementClassroom> getPlacementResultClasses(PlacementResult placementResult, PageRequest pageRequest) {
        return placementClassroomRepository.getAllByIdIn(placementResult.getClassesIds(), pageRequest);
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

    public void setSelectedResult(Placement placement, Long resultId) throws Placement.ResultNotExistsException, PlacementResult.NotCompletedException {
        PlacementResult placementResult = placement.getResultById(resultId);
        placement.setSelectedResult(placementResult);
        placementRepository.save(placement);
    }

    public PlaceEngineConfig getGlobalConfig() {
        return engineConfigRepository.findById(1L).get();
    }

    public PlaceEngineConfig updateGlobalConfig(PlaceEngineConfig placeEngineConfig) {
        placeEngineConfig.setId(1L);
        return engineConfigRepository.save(placeEngineConfig);
    }

    public String exportCsvHeadersByPlacement(Placement placement) throws CsvUtils.CsvContent.CsvNotValidException {
        List<String> columns = importExportUtils.getColumnNames(placement);
        CsvUtils.CsvContent csvContent = new CsvUtils.CsvContent(columns);
        return csvContent.getHeadersLine();
    }

    public String exportCsvDataByPlacement(Placement placement){
        return "";
    }

    public OperationInfo importDataFromCsv(Placement placement, String input) throws CsvUtils.CsvContent.CsvNotValidException {
        OperationInfo operationInfo = new OperationInfo();
        CsvUtils.CsvContent csvContent = new CsvUtils.CsvContent(input);
        List<Map<String, String>> contentData = csvContent.getData();
        int lineNumber = 2; // first line + headers

        for(Map<String, String> rowMap : contentData){
            System.out.println(rowMap);
            try {
                Pupil newPupil = importExportUtils.createPupilFromRowMap(rowMap, lineNumber);
                System.out.println(newPupil);
                operationInfo.addSuccess();
            } catch (ImportExportUtils.ParseValueException e) {
                operationInfo.addError(e.getMessage());
            } finally {
                lineNumber++;
            }
        }

        return operationInfo;
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
}