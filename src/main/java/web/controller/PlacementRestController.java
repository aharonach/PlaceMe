package web.controller;

import web.assembler.PlacementClassroomModelAssembler;
import web.assembler.PlacementModelAssembler;
import web.assembler.PlacementResultModelAssembler;
import web.dto.ClassroomInfoDto;
import web.entity.*;
import web.exception.BadRequest;
import web.exception.InternalError;
import web.exception.NotFound;
import web.exception.PreconditionFailed;
import web.service.PlacementService;
import web.util.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.annotations.ParameterObject;
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
    public ResponseEntity<?> getAll(@ParameterObject @ModelAttribute PagesAndSortHandler.PaginationInfo pageInfo) {

        try {
            PageRequest pageRequest = pagesAndSortHandler.getPageRequest(pageInfo, FieldSortingMaps.placementMap);
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
    public ResponseEntity<?> update(@PathVariable Long placementId,
                                    @RequestBody Placement updatedRecord) {
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
    public ResponseEntity<?> startPlacement(@PathVariable Long placementId,
                                            @RequestParam Optional<Integer> amountOfResults,
                                            @RequestBody PlacementResult result) {
        Placement placement = placementService.getOr404(placementId);
        int numOfResults = getHowManyResultsToGenerate(amountOfResults);
        Set<PlacementResult> results = new HashSet<>(numOfResults);

        for(int i=1; i <= numOfResults; i++) {
            try {
                String name = numOfResults > 1 ? result.getName().concat(" " + i) : result.getName();
                results.add(placementService.generatePlacementResult(placement, name, result.getDescription()));
            } catch (PlacementService.PlacementWithoutGroupException | PlacementService.PlacementWithoutPupilsInGroupException e) {
                throw new PreconditionFailed(e.getMessage());
            }
        }

        return ResponseEntity.ok(placementResultModelAssembler.toCollectionModelWithoutPages(results));
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
    public ResponseEntity<?> getResults(@PathVariable Long placementId,
                                        @ParameterObject @ModelAttribute PagesAndSortHandler.PaginationInfo pageInfo) {
        Placement placement = placementService.getOr404(placementId);

        try {
            PageRequest pageRequest = pagesAndSortHandler.getPageRequest(pageInfo, FieldSortingMaps.groupMap);
            CollectionModel<EntityModel<PlacementResult>> pagesModel = placementResultModelAssembler.toPageCollection(placementService.getPlacementResults(placement, pageRequest));
            return ResponseEntity.ok().body(pagesModel);

        } catch (PagesAndSortHandler.FieldNotSortableException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @PostMapping("/{placementId}/results/selected")
    public ResponseEntity<?> setSelectedResult(@PathVariable Long placementId,
                                               @RequestBody Long resultId) {
        try {
            Placement placement = placementService.getOr404(placementId);
            PlacementResult result = placementService.setSelectedResult(placement, resultId);
            EntityModel<PlacementResult> entityModel = placementResultModelAssembler.toModel(result);
            return ResponseEntity.ok().body(entityModel);
        } catch (Placement.ResultNotExistsException e) {
            throw new NotFound(e.getMessage());
        } catch (PlacementResult.NotCompletedException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @GetMapping("/{placementId}/results/{resultId}")
    public ResponseEntity<?> getResult(@PathVariable Long placementId,
                                       @PathVariable Long resultId) {

        Placement placement = placementService.getOr404(placementId);

        try {
            PlacementResult placementResult = placementService.getResultById(placement, resultId);
            EntityModel<PlacementResult> entityModel = placementResultModelAssembler.toModel(placementResult);
            return ResponseEntity.ok().body(entityModel);
        } catch (Placement.ResultNotExistsException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @PostMapping("/{placementId}/results/{resultId}")
    public ResponseEntity<?> updateResult(@PathVariable Long placementId,
                                          @PathVariable Long resultId,
                                          @RequestBody PlacementResult updatedResult) {
        Placement placement = placementService.getOr404(placementId);

        try {
            updatedResult = placementService.updatePlacementResult(placement, resultId, updatedResult);
            EntityModel<PlacementResult> entityModel = placementResultModelAssembler.toModel(updatedResult);
            return ResponseEntity.ok().body(entityModel);
        } catch (Placement.ResultNotExistsException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @GetMapping("/{placementId}/results/{resultId}/classes")
    public ResponseEntity<?> getResultClasses(@PathVariable Long placementId,
                                              @PathVariable Long resultId,
                                              @ParameterObject @ModelAttribute PagesAndSortHandler.PaginationInfo pageInfo) {

        Placement placement = placementService.getOr404(placementId);

        try {
            PageRequest pageRequest = pagesAndSortHandler.getPageRequest(pageInfo, FieldSortingMaps.groupMap);
            PlacementResult placementResult = placementService.getResultById(placement, resultId);
            CollectionModel<EntityModel<PlacementClassroom>> entities = placementClassroomModelAssembler.toPageCollection(placementService.getPlacementResultClasses(placementResult, pageRequest));
            return ResponseEntity.ok().body(entities);
        } catch (Placement.ResultNotExistsException | PagesAndSortHandler.FieldNotSortableException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @GetMapping("/{placementId}/results/{resultId}/classes/info")
    public ResponseEntity<?> getResultClassesInfo(@PathVariable Long placementId,
                                              @PathVariable Long resultId) {

        Placement placement = placementService.getOr404(placementId);
        try {
            PlacementResult placementResult = placementService.getResultById(placement, resultId);
            return ResponseEntity.ok().body(EntityModel.of(ClassroomInfoDto.fromPlacementResult(placementResult)));
        } catch (Placement.ResultNotExistsException e) {
            throw new BadRequest(e.getMessage());
        }

    }

    @DeleteMapping("/{placementId}/results/{resultId}")
    public ResponseEntity<?> deleteResult(@PathVariable Long placementId,
                                          @PathVariable Long resultId) {
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

    @GetMapping("/{placementId}/export/columns")
    public ResponseEntity<?> exportCsvColumnsForPlacement(@PathVariable Long placementId) {

        Placement placement = placementService.getOr404(placementId);
        try {
            String columnsString = placementService.exportCsvHeadersByPlacement(placement);
            return ResponseEntity.ok().body(columnsString);
        } catch (CsvUtils.CsvContent.CsvNotValidException e) {
            throw new BadRequest(e.getMessage());
        } catch (PlacementService.PlacementWithoutTemplateInGroupException | PlacementService.PlacementWithoutGroupException e) {
            throw new PreconditionFailed(e.getMessage());
        }
    }

    @GetMapping("/{placementId}/export")
    public ResponseEntity<?> exportCsvDataForPlacement(@PathVariable Long placementId) {

        Placement placement = placementService.getOr404(placementId);
        try {
            String columnsString = placementService.exportCsvDataByPlacement(placement);
            return ResponseEntity.ok().body(columnsString);
        } catch (CsvUtils.CsvContent.CsvNotValidException | Group.PupilNotBelongException e) {
            throw new BadRequest(e.getMessage());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new InternalError(e.getMessage());
        } catch (PlacementService.PlacementWithoutTemplateInGroupException | PlacementService.PlacementWithoutGroupException e) {
            throw new PreconditionFailed(e.getMessage());
        }
    }

    @PostMapping("/{placementId}/import")
    public ResponseEntity<?> updateConfig(@PathVariable Long placementId,
                                          @RequestBody String csvContent) {

        Placement placement = placementService.getOr404(placementId);
        try {
            OperationInfo operationInfo = placementService.importDataFromCsv(placement, csvContent);
            return ResponseEntity.ok().body(operationInfo);
        } catch (CsvUtils.CsvContent.CsvNotValidException e) {
            throw new BadRequest(e.getMessage());
        } catch (PlacementService.PlacementWithoutTemplateInGroupException | PlacementService.PlacementWithoutGroupException e) {
            throw new PreconditionFailed(e.getMessage());
        }

    }

    public class IllegalNumberOfResultsException extends BadRequest {
        public IllegalNumberOfResultsException(){
            super("Number of result must be from 1 to " + maxAllowedResultsOnGenerate + ".");
        }
    }
}
