package jen.web.service;

import jen.web.entity.Pupil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface EntityService<T> {
    T add(T item);

    T getOr404(Long id);

    Page<T> all(PageRequest pageRequest);

    T updateById(Long id, T newItem) throws PlacementService.PlacementResultsInProgressException, Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException;

    void deleteById(Long id) throws PlacementService.PlacementResultsInProgressException;
}