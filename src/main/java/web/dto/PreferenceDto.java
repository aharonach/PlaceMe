package web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import web.entity.Preference;
import web.entity.Pupil;
import web.entity.SelectorSelectedId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
public class PreferenceDto {
    @Getter
    private SelectorSelectedId selectorSelectedId = new SelectorSelectedId();
    @Getter
    private Boolean isSelectorWantToBeWithSelected;
    @JsonIgnore
    private Pupil selector;
    @JsonIgnore
    private Pupil selected;

    public static PreferenceDto fromPreference(Preference preference, Map<Long, Pupil> pupilsMap){
        Pupil selector = pupilsMap.get(preference.getSelectorSelectedId().getSelectorId());
        Pupil selected = pupilsMap.get(preference.getSelectorSelectedId().getSelectedId());

        return new PreferenceDto(preference.getSelectorSelectedId(), preference.getIsSelectorWantToBeWithSelected(), selector, selected);
    }

    public String getSelectorFirstName(){
        return selector.getFirstName();
    }

    public String getSelectorLastName(){
        return selector.getLastName();
    }

    public String getSelectedFirstName(){
        return selected.getFirstName();
    }

    public String getSelectedLastName(){
        return selected.getLastName();
    }
}
