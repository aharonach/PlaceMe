package jen.web.controller;

import jen.web.assembler.PlacementModelAssembler;
import jen.web.entity.Placement;
import jen.web.entity.PlacementResult;
import jen.web.service.PlacementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final PlacementModelAssembler assembler;

    @Override
    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(assembler.toCollectionModel(service.all()));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.getOr404(id)));
    }

    @Override
    @PutMapping()
    public ResponseEntity<?> create(@RequestBody Placement newRecord) {
        EntityModel<Placement> entity = assembler.toModel(service.add(newRecord));

        return ResponseEntity
                .created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entity);
    }

    @Override
    @PostMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Placement updatedRecord) {
        return ResponseEntity.ok(assembler.toModel(service.updateById(id, updatedRecord)));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/start")
    //@GetMapping("/{id}/start")
    public ResponseEntity<?> startPlacement(@PathVariable Long id) {
        Placement placement = service.getOr404(id);
        PlacementResult placementResult = service.startPlacement(placement);

        return ResponseEntity
                .ok()
                .body(EntityModel.of(placementResult));
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<?> getResults(@PathVariable Long id) {
        // @todo show an error if not finished to run the algorithm.
        //CollectionModel<EntityModel<PlacementResult>> allEntities = assembler.toCollectionModel(service.getOr404(id).getResults().values());
        return ResponseEntity.ok().body(service.getOr404(id).getResults());
    }

//    @GetMapping("/{id}/results/{resultId}")
//    public ResponseEntity<?> getResult(@PathVariable Long id, @PathVariable Long resultId) {
//        // @todo show an error if not finished to run the algorithm.
//        return ResponseEntity.ok().body(service.getOr404(id).getResults().get(resultId));
//    }

//    @DeleteMapping("/{id}/results/{resultId}")
//    public ResponseEntity<?> deleteResult(@PathVariable Long id, @PathVariable Long resultId) {
//        // @todo remove an instance of PlacementResult: placement.removeResult(resultId)
//        service.deletePlacementResultById(id, resultId);
//        return ResponseEntity.ok().build();
//    }
}
