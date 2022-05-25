package jen.example.hibernate.controller;

import jen.example.hibernate.assembler.PlacementModelAssembler;
import jen.example.hibernate.entity.Placement;
import jen.example.hibernate.service.PlacementService;
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
    public ResponseEntity<?> startPlacement(@PathVariable Long id) {
        // @todo start the EA module: placement.start();
        return null;
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<?> getResults(@PathVariable Long id) {
        // @todo show an error if not finished to run the algorithm.
        // @todo output: placement.getResults()
        return null;
    }

    @GetMapping("/{id}/results/{resultId}")
    public ResponseEntity<?> getResult(@PathVariable Long id, @PathVariable Long resultId) {
        // @todo show an error if not finished to run the algorithm.
        // @todo output: placement.getResults().get(resultId);
        return null;
    }

    @DeleteMapping("/{id}/results/{resultId}")
    public ResponseEntity<?> deleteResult(@PathVariable Long id, @PathVariable Long resultId) {
        // @todo remove an instance of PlacementResult: placement.removeResult(resultId)
        return null;
    }
}
