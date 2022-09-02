package jen.web.service;

import jen.web.entity.Pupil;
import jen.web.entity.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface EntityService<T> {
    T add(T item) throws Template.AttributeAlreadyExistException;

    T getOr404(Long id);
    List<T> allWithoutPages();
    Page<T> all(PageRequest pageRequest);

    T updateById(Long id, T newItem) throws PlacementService.PlacementResultsInProgressException, Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException, Template.AttributeAlreadyExistException;

    void deleteById(Long id) throws PlacementService.PlacementResultsInProgressException;
}