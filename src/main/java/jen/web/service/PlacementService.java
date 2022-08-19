package jen.web.service;

import jen.web.engine.PlaceEngine;
import jen.web.entity.*;
import jen.web.exception.EntityAlreadyExists;
import jen.web.exception.NotFound;
import jen.web.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    @Transactional
    public Placement add(Placement placement) {
        Long id = placement.getId();
        if (id != null && placementRepository.existsById(id)) {
            throw new EntityAlreadyExists("Placement with Id '" + id + "' already exists.");
        }

        if(placement.getGroup() != null){
            Group group = groupService.getOr404(placement.getGroup().getId());
            group.addPlacement(placement);
            placement.setGroup(group);
        }

        Placement res = placementRepository.save(placement);
        return res;
    }

    @Override
    public Placement getOr404(Long id) {
        return placementRepository.findById(id).orElseThrow(() -> new NotFound("Could not find placement " + id));
    }

    public PlacementResult getPlacementResultOr404(Long placementId, Long resultId) {
        getOr404(placementId);
        return placementResultRepository.findById(resultId).orElseThrow(() -> new NotFound("Could not find placement result " + resultId));
    }

    @Override
    public List<Placement> all() {
        return placementRepository.findAll();
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
            Group newGroup = groupService.getOr404(newPlacement.getGroup().getId());

            Set<Long> newPlacementIdsForGroup =placement.getGroup().getPlacementIds();
            if(placement.getGroup() != null){
                newPlacementIdsForGroup.remove(placement.getId());
            }

            placement.setGroup(newGroup);
            newGroup.setPlacements(placementRepository.getAllByIdIn(newPlacementIdsForGroup));
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
    public PlacementResult savePlacementResult(Placement placement, PlacementResult placementResult) {
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

    public void deleteAllPlacementResults(Placement placement) throws PlacementResultsInProgressException {
        for(PlacementResult placementResult : placement.getResults()){
            try {
                deletePlacementResultById(placement, placementResult.getId());
            } catch (Placement.ResultNotExistsException ignored) {
            }
        }
    }

    @Transactional
    public void deletePlacementResultById(Placement placement, Long resultId)
            throws Placement.ResultNotExistsException, PlacementResultsInProgressException {

        PlacementResult placementResult = placement.getResultById(resultId);
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

        PlaceEngineConfig config = this.getGlobalConfig();
        PlaceEngine placeEngine = new PlaceEngine(placement, config);

        PlacementResult placementResult = new PlacementResult();
        placementResult = savePlacementResult(placement, placementResult);

        Long resultId = placementResult.getId();

        // update result will be called after the generation will finish
        executor.submit(() -> updateResultStatus(placeEngine, resultId, placement.getId()));

        return placementResult;
    }

    private synchronized void updateResultStatus(PlaceEngine placeEngine, Long resultId, Long placementId){
        PlacementResult placementResult = getPlacementResultOr404(placementId, resultId);
        Placement placement = getOr404(placementId);

        try{
            PlacementResult algResult = placeEngine.generatePlacementResult();
            placementResult.setClasses(algResult.getClasses());
            placementResult.setStatus(PlacementResult.Status.COMPLETED);

        } catch (Exception e){
            placementResult.setStatus(PlacementResult.Status.FAILED);
            logger.error("error during generation: " + e.getMessage());
        }

        savePlacementResult(placement, placementResult);
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

    public PlacementResult getResultById(Placement placement, Long resultID) throws Placement.ResultNotExistsException {
        return placement.getResultById(resultID);
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