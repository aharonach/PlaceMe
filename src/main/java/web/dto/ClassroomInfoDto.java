package web.dto;

import web.engine.PlaceEngine;
import web.entity.BaseEntity;
import web.entity.PlacementResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ClassroomInfoDto {
    private Map<Long, Set<Long>> preferToBeList;
    private Map<Long, Set<Long>> preferNotToBeList;
    private Map<Long, Integer> numberOfFriendsInClass;

    public static ClassroomInfoDto fromPlacementResult(PlacementResult placementResult){
        Map<Long, Set<Long>> preferToBeList = PupilsConnectionsDto
                .fromSelectorSelectedSet(PlaceEngine.getSelectorSelectedIds(placementResult.getGroup(), true))
                .getValues();
        Map<Long, Set<Long>> preferNotToBeList = PupilsConnectionsDto
                .fromSelectorSelectedSet(PlaceEngine.getSelectorSelectedIds(placementResult.getGroup(), false))
                .getValues();
        Map<Long, Integer> numberOfFriendsInClass = new HashMap<>();

        placementResult.getGroup().getPupils().forEach(pupil -> {
            if(!preferToBeList.containsKey(pupil.getId())){
                preferToBeList.put(pupil.getId(), new HashSet<>());
            }
            if(!preferNotToBeList.containsKey(pupil.getId())){
                preferNotToBeList.put(pupil.getId(), new HashSet<>());
            }
        });

        placementResult.getClasses().forEach(placementClassroom -> {
            Set<Long> pupilIds = placementClassroom.getPupils().stream().map(BaseEntity::getId).collect(Collectors.toSet());
            placementClassroom.getPupils().forEach(pupil -> {
                int numOfFriendForPupil = (int) preferToBeList.get(pupil.getId()).stream().filter(pupilIds::contains).count();
                numberOfFriendsInClass.put(pupil.getId(), numOfFriendForPupil);
            });
        });

        return new ClassroomInfoDto(preferToBeList, preferNotToBeList, numberOfFriendsInClass);
    }
}
