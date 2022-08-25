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

    @Value("${placement.items.per.page}")
    public Integer PlacementsPerPage;

    @Value("${group.items.per.page}")
    public Integer GroupsPerPage;

    @Value("${pupil.items.per.page}")
    public Integer PupilsPerPage;

    @Value("${template.items.per.page}")
    public Integer TemplatesPerPage;
    @Value("${default.items.per.page}")
    public Integer DefaultItemsPerPage;

    public PageRequest getPageRequest(Optional<Integer> pageNumber, Optional<String> sortBy, Map<String, Sort> fieldSortingMap) throws FieldNotSortableException {
        return getPageRequest(pageNumber, sortBy, fieldSortingMap, DefaultItemsPerPage);
    }

    public PageRequest getPageRequest(Optional<Integer> pageNumber, Optional<String> sortBy, Map<String, Sort> fieldSortingMap, int itemsPerPage) throws FieldNotSortableException {
        PageRequest pageRequest = PageRequest.ofSize(itemsPerPage);
        Optional<Sort> sort = getSortByOptionalString(sortBy, fieldSortingMap);
        if(pageNumber.isPresent()){
            pageRequest = pageRequest.withPage(pageNumber.get());
        }
        if(sort.isPresent()){
            pageRequest = pageRequest.withSort(sort.get());
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
