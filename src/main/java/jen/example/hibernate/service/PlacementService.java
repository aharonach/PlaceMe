package jen.example.hibernate.service;

import jen.example.hibernate.entity.Attribute;
import jen.example.hibernate.entity.Placement;
import jen.example.hibernate.entity.Pupil;
import jen.example.hibernate.repository.PlacementRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlacementService implements EntityService<Placement> {

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);

    private final PlacementRepository repository;

    @Override
    public Placement add(Placement placement) {
        // verify that all attributes are new (without ids) and other template fields
        return repository.save(placement);
    }

    @Override
    public Placement getOr404(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFound(id));
    }

    @Override
    public List<Placement> all() {
        return null;
    }

    @Override
    @Transactional
    public Placement updateById(Long id, Placement item) {
        Placement placement = getOr404(id);

        placement.setName(item.getName());
        placement.setPupilsGroup(item.getPupilsGroup());
        placement.setNumberOfClasses(item.getNumberOfClasses());
        return repository.save(placement);
    }

    @Override
    public void deleteById(Long id) {
        Placement placement = getOr404(id);
        repository.delete(placement);
    }
    public Placement startPlacement(){
        //To Do
        return null;
    }

    // handle exceptions
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class NotFound extends RuntimeException{
        public NotFound(Long id){
            super("Could not find placement " + id);
        }
    }
}
