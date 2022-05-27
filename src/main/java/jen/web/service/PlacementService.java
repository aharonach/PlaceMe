package jen.web.service;

import io.jenetics.BitGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import jen.web.engine.PlaceEngine;
import jen.web.entity.Placement;
import jen.web.entity.PlacementResult;
import jen.web.exception.NotFound;
import jen.web.repository.PlacementRepository;
import jen.web.repository.PlacementResultRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlacementService implements EntityService<Placement> {

    private static final Logger logger = LoggerFactory.getLogger(PlacementService.class);

    private final PlacementRepository repository;

    private final PlacementResultRepository placementResultRepository;

    @Override
    public Placement add(Placement placement) {
        return repository.save(placement);
    }

    @Override
    public Placement getOr404(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFound("Could not find placement " + id));
    }

    @Override
    public List<Placement> all() {
        return repository.findAll();
    }

    @Override
    public Placement updateById(Long id, Placement newPlacement) {
        Placement placement = getOr404(id);

        placement.setName(newPlacement.getName());
        placement.setNumberOfClasses(newPlacement.getNumberOfClasses());
        placement.setGroup(newPlacement.getGroup());

        return repository.save(placement);
    }

    @Override
    public void deleteById(Long id) {
        Placement placement = getOr404(id);
        repository.delete(placement);
    }

    public void deletePlacementResultById(Long id, Long resultId){
        Placement placement = getOr404(id);
        placementResultRepository.delete(placement.getResults().get(resultId));
    }

    public PlacementResult startPlacement(Placement placement) {
        PlaceEngine placeEngine = new PlaceEngine(placement);
        Engine<BitGene, Double> engine = placeEngine.getEngine();

        final Phenotype<BitGene, Double> best = engine
                .stream()
                .limit(Limits.bySteadyFitness(7))
                .limit(100)
                .peek(r -> System.out.println(r.totalGenerations() + " : " + r.bestPhenotype() + ", worst:" + r.worstFitness()))
                .collect(EvolutionResult.toBestPhenotype());

        return placeEngine.decode(best.genotype());
    }
}