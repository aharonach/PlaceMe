package jen.web.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class PagesAndSortHandler {
    @Value("${default.items.per.page}")
    public Integer DefaultItemsPerPage;

    @Value("${placement.items.per.page}")
    public Integer PlacementsPerPage;

    @Value("${placement.results.items.per.page}")
    public Integer PlacementResultsPerPage;

    @Value("${placement.result.classes.items.per.page}")
    public Integer PlacementResultClassesPerPage;

    @Value("${group.items.per.page}")
    public Integer GroupsPerPage;

    @Value("${pupil.items.per.page}")
    public Integer PupilsPerPage;

    @Value("${template.items.per.page}")
    public Integer TemplatesPerPage;

    public PageRequest getFirstPageRequest() throws PagesAndSortHandler.FieldNotSortableException {
        return getPageRequest(Optional.of(0), Optional.of("id"), FieldSortingMaps.defaultMap, false);
    }

    public PageRequest getPageRequest(Optional<Integer> pageNumber, Optional<String> sortBy,
                                      Map<String, Sort> fieldSortingMap, boolean descending) throws FieldNotSortableException {
        return getPageRequest(pageNumber, sortBy, fieldSortingMap, DefaultItemsPerPage, descending);
    }

    public PageRequest getPageRequest(Optional<Integer> pageNumber, Optional<String> sortBy,
                                      Map<String, Sort> fieldSortingMap, int itemsPerPage,
                                      boolean descending) throws FieldNotSortableException {
        PageRequest pageRequest = PageRequest.ofSize(itemsPerPage);
        Optional<Sort> optionalSort = getSortByOptionalString(sortBy, fieldSortingMap);
        if(pageNumber.isPresent()){
            pageRequest = pageRequest.withPage(pageNumber.get());
        }
        if(optionalSort.isPresent()){
            Sort sort = descending ? optionalSort.get().descending() : optionalSort.get();
            pageRequest = pageRequest.withSort(sort);
        }
        return pageRequest;
    }

    private Optional<Sort> getSortByOptionalString(Optional<String> sortBy, Map<String, Sort> fieldSortingMap) throws FieldNotSortableException {
        String sortKey = "id";
        if(sortBy.isPresent()){
            sortKey = sortBy.get();
        }

        if(fieldSortingMap.containsKey(sortKey)){
            return Optional.of(fieldSortingMap.get(sortKey));
        } else {
            throw new FieldNotSortableException(fieldSortingMap.keySet());
        }
    }

    public static class FieldNotSortableException extends Exception{
        public FieldNotSortableException(Set<String> acceptableFields){
            super("Can sort by the following fields: " + acceptableFields);
        }
    }
}
