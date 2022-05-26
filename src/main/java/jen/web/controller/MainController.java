package jen.web.controller;

import io.jenetics.BitGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import jen.web.engine.PlaceEngine;
import jen.web.entity.Placement;
import jen.web.entity.PlacementResult;
import jen.web.service.PlacementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final PlacementService placementService;

    @GetMapping("/")
    public String test() {
        return "test";
    }

    @GetMapping("/run/{id}")
    public ResponseEntity<?> runAlgorithm(@PathVariable Long id) {
        Placement placement = placementService.getOr404(id);

        PlaceEngine placeEngine = new PlaceEngine(placement);
        Engine<BitGene, Double> engine = placeEngine.getEngine();

        final Phenotype<BitGene, Double> best = engine
                .stream()
                .limit(Limits.bySteadyFitness(7))
                .limit(100)
                .peek(r -> System.out.println(r.totalGenerations() + " : " + r.bestPhenotype() + ", worst:" + r.worstFitness()))
                .collect(EvolutionResult.toBestPhenotype());

        PlacementResult placementResult = placeEngine.decode(best.genotype());

        return ResponseEntity
                .ok()
                .body(EntityModel.of(placementResult));
    }
}