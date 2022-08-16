package jen.web.controller;

import jen.web.assembler.PlacementModelAssembler;
import jen.web.assembler.PlacementResultModelAssembler;
import jen.web.entity.PlaceEngineConfig;
import jen.web.entity.Placement;
import jen.web.entity.PlacementResult;
import jen.web.exception.BadRequest;
import jen.web.exception.PreconditionFailed;
import jen.web.service.PlacementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/placements")
public class PlacementRestController extends BaseRestController<Placement> {

    private static final Logger logger = LoggerFactory.getLogger(PlacementRestController.class);
    private final PlacementService service;
    private final PlacementModelAssembler placementModelAssembler;
    
    private final PlacementResultModelAssembler placementResultModelAssembler;

    @Override
    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(placementModelAssembler.toCollectionModel(service.all()));
    }

    @Override
    @GetMapping("/{placementId}")
    public ResponseEntity<?> get(@PathVariable Long placementId) {
        return ResponseEntity.ok(placementModelAssembler.toModel(service.getOr404(placementId)));
    }

    @Override
    @PutMapping()
    public ResponseEntity<?> create(@RequestBody Placement newRecord) {
        EntityModel<Placement> entity = placementModelAssembler.toModel(service.add(newRecord));

        return ResponseEntity
                .created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entity);
    }

    @Override
    @PostMapping("/{placementId}")
    public ResponseEntity<?> update(@PathVariable Long placementId, @RequestBody Placement updatedRecord) {
        return ResponseEntity.ok(placementModelAssembler.toModel(service.updateById(placementId, updatedRecord)));
    }

    @Override
    @DeleteMapping("/{placementId}")
    public ResponseEntity<?> delete(@PathVariable Long placementId) {
        service.deleteById(placementId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{placementId}/results/generate")
    public ResponseEntity<?> startPlacement(@PathVariable Long placementId) {
        Placement placement = service.getOr404(placementId);

        PlacementResult placementResult = null;
        try {
            placementResult = service.generatePlacementResult(placement);
        } catch (PlacementService.PlacementWithoutGroupException e) {
            throw new PreconditionFailed(e.getMessage());
        }

        return ResponseEntity
                .ok()
                .body(EntityModel.of(placementResult));
    }

    @GetMapping("/{placementId}/results")
    public ResponseEntity<?> getResults(@PathVariable Long placementId) {
        CollectionModel<EntityModel<PlacementResult>> allEntities = placementResultModelAssembler.toCollectionModel(service.getOr404(placementId).getResults());
        return ResponseEntity.ok().body(allEntities);
    }

    @GetMapping("/{placementId}/results/{resultId}")
    public ResponseEntity<?> getResult(@PathVariable Long placementId, @PathVariable Long resultId) {

        Placement placement = service.getOr404(placementId);

        try {
            PlacementResult placementResult = service.getResultById(placement, resultId);
            EntityModel<PlacementResult> entityModel = placementResultModelAssembler.toModel(placementResult);
            return ResponseEntity.ok().body(entityModel);
        } catch (Placement.ResultNotExistsException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @DeleteMapping("/{placementId}/results/{resultId}")
    public ResponseEntity<?> deleteResult(@PathVariable Long placementId, @PathVariable Long resultId) {
        Placement placement = service.getOr404(placementId);

        try {
            service.deletePlacementResultById(placement, resultId);
        } catch (Placement.ResultNotExistsException e) {
            throw new BadRequest(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/configs")
    public ResponseEntity<?> getConfig() {
        return ResponseEntity.ok(EntityModel.of(service.getGlobalConfig()));
    }

    @PostMapping("/configs")
    public ResponseEntity<?> updateConfig(@RequestBody PlaceEngineConfig config) {
        return ResponseEntity.ok(service.updateGlobalConfig(config));
    }
}
