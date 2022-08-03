package jen.web.service;

import io.jenetics.BitGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import jen.web.engine.PlaceEngine;
import jen.web.entity.Group;
import jen.web.entity.Placement;
import jen.web.entity.PlacementClassroom;
import jen.web.entity.PlacementResult;
import jen.web.exception.EntityAlreadyExists;
import jen.web.exception.NotFound;
import jen.web.repository.GroupRepository;
import jen.web.repository.PlacementClassroomRepository;
import jen.web.repository.PlacementRepository;
import jen.web.repository.PlacementResultRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlacementService implements EntityService<Placement> {

    private static final Logger logger = LoggerFactory.getLogger(PlacementService.class);

    private final PlacementRepository placementRepository;

    private final PlacementResultRepository placementResultRepository;

    private final PlacementClassroomRepository placementClassroomRepository;

    private final GroupService groupService;

    @Override
    @Transactional
    public Placement add(Placement placement) {
        Long id = placement.getId();
        if (id != null && placementRepository.existsById(id)) {
            throw new EntityAlreadyExists("Placement with Id '" + id + "' already exists.");
        }

        if(placement.getGroup() != null){
            Group group = groupService.getOr404(placement.getGroup().getId());
            placement.setGroup(group);
        }

        Placement res = placementRepository.save(placement);
        return res;
    }

    @Override
    public Placement getOr404(Long id) {
        return placementRepository.findById(id).orElseThrow(() -> new NotFound("Could not find placement " + id));
    }

    @Override
    public List<Placement> all() {
        return placementRepository.findAll();
    }

    @Override
    @Transactional
    public Placement updateById(Long id, Placement newPlacement) {
        Placement placement = getOr404(id);
        boolean isUpdateGroupNeeded = newPlacement.getGroup() != null && !newPlacement.getGroup().equals(placement.getGroup());

        placement.setName(newPlacement.getName());
        placement.setNumberOfClasses(newPlacement.getNumberOfClasses());

        if(isUpdateGroupNeeded){
            Group newGroup = groupService.getOr404(newPlacement.getGroup().getId());
            placement.setGroup(newGroup);
        }

        Placement res = placementRepository.save(placement);
        return res;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Placement placement = getOr404(id);
        Group group = placement.getGroup();

        group.getPlacements().remove(placement);
        deleteAllPlacementResults(placement);

        placementRepository.delete(placement);
    }

    @Transactional
    public void savePlacementResult(Placement placement, PlacementResult placementResult){
        placementResult.setPlacement(placement);
        PlacementResult savedResult = placementResultRepository.save(placementResult);

        savedResult.getClasses().forEach(placementClassroom -> placementClassroom.setPlacementResult(savedResult));
        placementClassroomRepository.saveAll(savedResult.getClasses());

        placement.getResults().add(savedResult);
        placementRepository.save(placement);
    }

    public void deleteAllPlacementResults(Placement placement) {
        for(PlacementResult placementResult : placement.getResults()){
            try {
                deletePlacementResultById(placement, placementResult.getId());
            } catch (Placement.ResultNotExistsException ignored) {
            }
        }
    }

    @Transactional
    public void deletePlacementResultById(Placement placement, Long resultId) throws Placement.ResultNotExistsException {
        PlacementResult placementResult = placement.getResultById(resultId);

        placement.getResults().remove(placementResult);
        placementResult.setPlacement(null);
        placementResult.getClasses().forEach(placementClassroom -> {
            placementClassroom.setPlacementResult(null);
            placementClassroom.setPupils(new HashSet<>());
        });

        placementClassroomRepository.deleteAll(placementResult.getClasses());
        placementResultRepository.deleteById(resultId);
    }

    public PlacementResult generatePlacementResult(Placement placement) {
        PlaceEngine placeEngine = new PlaceEngine(placement);
        Engine<BitGene, Double> engine = placeEngine.getEngine();

        final Phenotype<BitGene, Double> best = engine
                .stream()
                .limit(Limits.bySteadyFitness(7))
                .limit(100)
                .peek(r -> System.out.println(r.totalGenerations() + " : " + r.bestPhenotype() + ", worst:" + r.worstFitness()))
                .collect(EvolutionResult.toBestPhenotype());

        PlacementResult placementResult = placeEngine.decode(best.genotype());
        System.out.println("Placement result is valid: " + PlaceEngine.isValid(best.genotype()));

        savePlacementResult(placement, placementResult);
        return placementResult;
    }
}