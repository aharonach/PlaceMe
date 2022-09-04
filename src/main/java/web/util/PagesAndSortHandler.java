package web.util;

import lombok.*;
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
        PaginationInfo paginationInfo = new PaginationInfo();
        return getPageRequest(paginationInfo, FieldSortingMaps.defaultMap);
    }

    public PageRequest getPageRequest(PaginationInfo paginationInfo, Map<String, Sort> fieldSortingMap) throws FieldNotSortableException {
        return getPageRequest(paginationInfo, fieldSortingMap, DefaultItemsPerPage);
    }

    public PageRequest getPageRequest(PaginationInfo paginationInfo, Map<String, Sort> fieldSortingMap,
                                      int itemsPerPage) throws FieldNotSortableException {
        PageRequest pageRequest = PageRequest.ofSize(itemsPerPage);
        Optional<Sort> optionalSort = getSortByOptionalString(paginationInfo.getSortField(), fieldSortingMap);
        if(paginationInfo.getPageNumber().isPresent()){
            pageRequest = pageRequest.withPage(paginationInfo.getPageNumber().get());
        }
        if(optionalSort.isPresent()){
            Sort sort = paginationInfo.getSortDirection().isDescending() ? optionalSort.get().descending() : optionalSort.get();
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
            Sort sort = fieldSortingMap.get(sortKey);
            return Optional.of(Sort.by(sort.get().map(Sort.Order::ignoreCase).toList()));
        } else {
            throw new FieldNotSortableException(fieldSortingMap.keySet());
        }
    }

    @ToString
    @Getter
    @RequiredArgsConstructor
    public static class PaginationInfo{
        private final Optional<Integer> pageNumber;
        private final Optional<String> sortField;
        private final Sort.Direction sortDirection;

        public PaginationInfo(){
            this.pageNumber = Optional.empty();
            this.sortField = Optional.empty();
            this.sortDirection = Sort.Direction.ASC;
        }
    }

    public static class FieldNotSortableException extends Exception{
        public FieldNotSortableException(Set<String> acceptableFields){
            super("Can sort by the following fields: " + acceptableFields);
        }
    }
}
