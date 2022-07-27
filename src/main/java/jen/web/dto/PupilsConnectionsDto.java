package jen.web.dto;

import jen.web.entity.SelectorSelectedId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class PupilsConnectionsDto {
    @Getter
    private final Map<Long, Set<Long>> values;

    public static PupilsConnectionsDto fromSelectorSelectedSet(Set<SelectorSelectedId> selectorSelectedIds){
        Map<Long, Set<Long>> resMap = new HashMap<>(selectorSelectedIds.size());
        PupilsConnectionsDto res = new PupilsConnectionsDto(resMap);

        for(SelectorSelectedId selectorSelectedId : selectorSelectedIds){
            if(resMap.containsKey(selectorSelectedId.getSelectorId())){
                resMap.get(selectorSelectedId.getSelectorId()).add(selectorSelectedId.getSelectedId());
            } else {
                Set<Long> s = new HashSet<>();
                s.add(selectorSelectedId.getSelectedId());
                resMap.put(selectorSelectedId.getSelectorId(), s);
            }
        }

        return res;
    }

    @Override
    public String toString() {
        return "PupilsConnections{" +
                "values=" + values +
                '}';
    }
}
