package jen.web.service;

import jen.web.entity.Placement;
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
}