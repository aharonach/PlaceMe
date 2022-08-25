package jen.web.util;

import org.springframework.data.domain.Sort;

import java.util.Map;

public class FieldSortingMaps {

    public static final Map<String, Sort> defaultMap = Map.of(
            "id", Sort.by("id")
    );

    public static final Map<String, Sort> pupilMap = Map.of(
            "id", Sort.by("id"),
            "age", Sort.by("birthDate", "firstName", "LastName", "givenId"),
            "full_name", Sort.by("firstName", "LastName", "givenId")
    );

    public static final Map<String, Sort> placementMap = defaultMap;
    public static final Map<String, Sort> templateMap = defaultMap;
    public static final Map<String, Sort> groupMap = defaultMap;
}
