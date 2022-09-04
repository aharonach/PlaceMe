package web.util;

import org.springframework.data.domain.Sort;

import java.util.Map;

public class FieldSortingMaps {

    public static final Map<String, Sort> defaultMap = Map.of(
            "id", Sort.by("id"),
            "createdTime", Sort.by("createdTime")
    );

    public static final Map<String, Sort> pupilMap = Map.of(
            "id", Sort.by("id"),
            "givenId", Sort.by("givenId", "firstName", "lastName"),
            "birthDate", Sort.by("birthDate", "firstName", "lastName", "givenId"),
            "firstName", Sort.by("firstName", "lastName", "givenId"),
            "lastName", Sort.by("lastName", "firstName", "givenId"),
            "gender", Sort.by("gender", "firstName", "lastName", "givenId"),
            "createdTime", Sort.by("createdTime")
    );

    public static final Map<String, Sort> placementMap = Map.of(
            "id", Sort.by("id"),
            "name", Sort.by("name"),
            "numberOfClasses", Sort.by("numberOfClasses"),
            "createdTime", Sort.by("createdTime")
    );

    public static final Map<String, Sort> templateMap = Map.of(
            "id", Sort.by("id"),
            "name", Sort.by("name"),
            "createdTime", Sort.by("createdTime")
    );

    public static final Map<String, Sort> groupMap = Map.of(
            "id", Sort.by("id"),
            "name", Sort.by("name"),
            "createdTime", Sort.by("createdTime")
    );
}
