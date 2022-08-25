package jen.web.controller;

import jen.web.assembler.PlacementClassroomModelAssembler;
import jen.web.assembler.PlacementModelAssembler;
import jen.web.assembler.PlacementResultModelAssembler;
import jen.web.entity.*;
import jen.web.exception.BadRequest;
import jen.web.exception.NotFound;
import jen.web.exception.PreconditionFailed;
import jen.web.service.PlacementService;
import jen.web.util.FieldSortingMaps;
import jen.web.util.PagesAndSortHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/placements")
public class PlacementRestController extends BaseRestController<Placement> {

    private static final Logger logger = LoggerFactory.getLogger(PlacementRestController.class);
    private final PlacementService placementService;
    private final PlacementModelAssembler placementModelAssembler;
    private final PlacementResultModelAssembler placementResultModelAssembler;
    private final PlacementClassroomModelAssembler placementClassroomModelAssembler;
    private final PagesAndSortHandler pagesAndSortHandler;

    @Value("${placement.max.allowed.results.on.generate}")
    private Integer maxAllowedResultsOnGenerate;


    @Override
    @GetMapping()
    public ResponseEntity<?> getAll(@RequestParam Optional<Integer> page, @RequestParam Optional<String> sortBy) {

        try {
            PageRequest pageRequest = pagesAndSortHandler.getPageRequest(page, sortBy, FieldSortingMaps.placementMap);
            CollectionModel<EntityModel<Placement>> pagesModel = placementModelAssembler.toPageCollection(placementService.all(pageRequest));
            return ResponseEntity.ok().body(pagesModel);

        } catch (PagesAndSortHandler.FieldNotSortableException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @Override
    @GetMapping("/{placementId}")
    public ResponseEntity<?> get(@PathVariable Long placementId) {
        return ResponseEntity.ok(placementModelAssembler.toModel(placementService.getOr404(placementId)));
    }

    @Override
    @PutMapping()
    public ResponseEntity<?> create(@RequestBody Placement newRecord) {
        EntityModel<Placement> entity = placementModelAssembler.toModel(placementService.add(newRecord));

        return ResponseEntity
                .created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entity);
    }

    @Override
    @PostMapping("/{placementId}")
    public ResponseEntity<?> update(@PathVariable Long placementId, @RequestBody Placement updatedRecord) {
        try {
            return ResponseEntity.ok(placementModelAssembler.toModel(placementService.updateById(placementId, updatedRecord)));

        } catch (PlacementService.PlacementResultsInProgressException e) {
            throw new PreconditionFailed(e.getMessage());
        }
    }

    @Override
    @DeleteMapping("/{placementId}")
    public ResponseEntity<?> delete(@PathVariable Long placementId) {
        try {
            placementService.deleteById(placementId);
        } catch (PlacementService.PlacementResultsInProgressException e) {
            throw new PreconditionFailed(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{placementId}/results/generate")
    public ResponseEntity<?> startPlacement(@PathVariable Long placementId, @RequestBody Optional<Integer> amountOfResults) {
        Placement placement = placementService.getOr404(placementId);

        int numOfResults = getHowManyResultsToGenerate(amountOfResults);
        Set<PlacementResult> results = new HashSet<>(numOfResults);

        for(int i=0; i< numOfResults; i++) {
            try {
                results.add(placementService.generatePlacementResult(placement));
            } catch (PlacementService.PlacementWithoutGroupException | PlacementService.PlacementWithoutPupilsInGroupException e) {
                throw new PreconditionFailed(e.getMessage());
            }
        }

        return ResponseEntity.ok(placementResultModelAssembler.toCollectionModel(results));
    }

    private int getHowManyResultsToGenerate(Optional<Integer> amountOfResults){
        int numOfResults = 1;

        if(amountOfResults.isPresent()){
            if(amountOfResults.get() > maxAllowedResultsOnGenerate){
                throw new IllegalNumberOfResultsException();
            }
            numOfResults = amountOfResults.get();
        }

        return numOfResults;
    }

    @GetMapping("/{placementId}/results")
    public ResponseEntity<?> getResults(@PathVariable Long placementId) {
        CollectionModel<EntityModel<PlacementResult>> allEntities = placementResultModelAssembler.toCollectionModel(placementService.getOr404(placementId).getResults());
        return ResponseEntity.ok().body(allEntities);
    }

    @PostMapping("/{placementId}/results/selected")
    public ResponseEntity<?> setSelectedResult(@PathVariable Long placementId, @RequestBody Long resultId) {
        try {
            Placement placement = placementService.getOr404(placementId);
            placementService.setSelectedResult(placement, resultId);
            return ResponseEntity.ok().build();
        } catch (Placement.ResultNotExistsException e) {
            throw new NotFound(e.getMessage());
        } catch (PlacementResult.NotCompletedException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @GetMapping("/{placementId}/results/{resultId}")
    public ResponseEntity<?> getResult(@PathVariable Long placementId, @PathVariable Long resultId) {

        Placement placement = placementService.getOr404(placementId);

        try {
            PlacementResult placementResult = placementService.getResultById(placement, resultId);
            EntityModel<PlacementResult> entityModel = placementResultModelAssembler.toModel(placementResult);
            return ResponseEntity.ok().body(entityModel);
        } catch (Placement.ResultNotExistsException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @GetMapping("/{placementId}/results/{resultId}/classes")
    public ResponseEntity<?> getResultClasses(@PathVariable Long placementId, @PathVariable Long resultId) {

        Placement placement = placementService.getOr404(placementId);

        try {
            PlacementResult placementResult = placementService.getResultById(placement, resultId);
            CollectionModel<EntityModel<PlacementClassroom>> entities = placementClassroomModelAssembler.toCollectionModel(placementResult.getClasses());
            return ResponseEntity.ok().body(entities);
        } catch (Placement.ResultNotExistsException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @DeleteMapping("/{placementId}/results/{resultId}")
    public ResponseEntity<?> deleteResult(@PathVariable Long placementId, @PathVariable Long resultId) {
        Placement placement = placementService.getOr404(placementId);

        try {
            placementService.deletePlacementResultById(placement, resultId);
        } catch (Placement.ResultNotExistsException | PlacementService.PlacementResultsInProgressException e) {
            throw new BadRequest(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/configs")
    public ResponseEntity<?> getConfig() {
        return ResponseEntity.ok(EntityModel.of(placementService.getGlobalConfig()));
    }

    @PostMapping("/configs")
    public ResponseEntity<?> updateConfig(@RequestBody PlaceEngineConfig config) {
        return ResponseEntity.ok(placementService.updateGlobalConfig(config));
    }

    public class IllegalNumberOfResultsException extends BadRequest {
        public IllegalNumberOfResultsException(){
            super("Number of result must be from 1 to " + maxAllowedResultsOnGenerate + ".");
        }
    }
}
