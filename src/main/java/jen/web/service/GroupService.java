package jen.web.service;

import jen.web.entity.*;
import jen.web.exception.BadRequest;
import jen.web.exception.NotFound;
import jen.web.repository.GroupRepository;
import jen.web.repository.PreferenceRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService implements EntityService<Group> {

    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);

    @Getter
    private final GroupRepository groupRepository;
    @Getter
    private final PreferenceRepository preferenceRepository;

    @Override
    @Transactional
    public Group add(Group group) {
        // todo: validate that id dont exists
        return groupRepository.save(group);
    }

    @Override
    public Group getOr404(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new NotFound("Could not find group " + id));
    }

    @Override
    public List<Group> all() {
        return groupRepository.findAll();
    }

    @Override
    @Transactional
    public Group updateById(Long id, Group newGroup) {
        Group group = getOr404(id);

        group.setName(newGroup.getName());
        group.setDescription(newGroup.getDescription());
        group.setTemplate(newGroup.getTemplate());
        group.setPupils(newGroup.getPupils());

        return groupRepository.save(group);
    }

    @Override
    public void deleteById(Long id) {
        Group group = getOr404(id);
        groupRepository.delete(group);
    }

    public Set<Group> getByIds(Set<Long> ids) {
        return groupRepository.getAllByIdIn(ids);
    }

    public void addPupilPreference(Preference preference){
        try {
            Group group = preference.getGroup();
            Pupil selector = group.getPupilById(preference.getSelectorSelectedId().getSelectorId());
            Pupil selected = group.getPupilById(preference.getSelectorSelectedId().getSelectedId());
            group.addPreference(selector, selected, preference.getIsSelectorWantToBeWithSelected());

            preferenceRepository.saveAllAndFlush(group.getPreferences());

        } catch (Group.NotBelongToGroupException | Preference.SamePupilException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    public void deletePupilPreferences(Preference preference) {
        Group group = preference.getGroup();
        Long selectorId = preference.getSelectorSelectedId().getSelectorId();
        Long selectedId = preference.getSelectorSelectedId().getSelectedId();

        Set<SelectorSelectedId> selectorSelectedIds = group.getPreferencesForPupils(selectorId, selectedId)
                .stream().map(Preference::getSelectorSelectedId).collect(Collectors.toSet());
        preferenceRepository.deleteAllById(selectorSelectedIds);
    }

    public void deletePupilPreferences(Pupil pupil, Group group){
        Set<SelectorSelectedId> selectorSelectedIds = group.getAllPreferencesForPupil(pupil.getId())
                .stream().map(Preference::getSelectorSelectedId).collect(Collectors.toSet());
        preferenceRepository.deleteAllById(selectorSelectedIds);
    }

    public Set<Preference> getAllPreferencesForPupil(Pupil pupil, Group group){
        return group.getAllPreferencesForPupil(pupil.getId());
    }
}
