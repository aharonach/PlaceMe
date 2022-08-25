package jen.web.service;

import jen.web.entity.Pupil;
import jen.web.util.PagesAndSortHandler;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

// @TODO Add a 'save' method without requesting an ID, only item object.
public interface EntityService<T> {
    T add(T item);

    T getOr404(Long id);

    Page<T> all() throws PagesAndSortHandler.FieldNotSortableException;

    Page<T> all(Optional<Integer> pageNumber, Optional<String> sortBy) throws PagesAndSortHandler.FieldNotSortableException;

    T updateById(Long id, T newItem) throws PlacementService.PlacementResultsInProgressException, Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException;

    void deleteById(Long id) throws PlacementService.PlacementResultsInProgressException;
}