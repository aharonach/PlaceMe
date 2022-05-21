package jen.example.hibernate.service;

import jen.example.hibernate.entity.Placement;
import jen.example.hibernate.exception.NotFound;
import jen.example.hibernate.repository.PlacementRepository;
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
}