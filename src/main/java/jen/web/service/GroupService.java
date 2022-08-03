package jen.web.service;

import jen.web.entity.*;
import jen.web.exception.EntityAlreadyExists;
import jen.web.exception.NotAcceptable;
import jen.web.exception.NotFound;
import jen.web.repository.GroupRepository;
import jen.web.repository.PreferenceRepository;
import jen.web.repository.PupilRepository;
import jen.web.repository.TemplateRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
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

    private final PupilRepository pupilRepository;
    private final TemplateRepository templateRepository;
    private final TemplateService templateService;

    @Override
    @Transactional
    public Group add(Group group) {
        Long id = group.getId();
        if (id != null && groupRepository.existsById(id)) {
            throw new EntityAlreadyExists("Group with Id '" + id + "' already exists.");
        }

        if(group.getTemplate() != null){
            Template template = templateService.getOr404(group.getTemplate().getId());
            group.setTemplate(template);
            template.addGroup(group);
        }

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

        if(newGroup.getTemplate() != null){
            Template newTemplate = templateService.getOr404(newGroup.getTemplate().getId());

            Set<Long> newGroupIds = newTemplate.getGroupIds();
            if(group.getTemplate() != null){
                newGroupIds.remove(group.getTemplate().getId());
            }

            group.setTemplate(newTemplate);
            newTemplate.setGroups(groupRepository.getAllByIdIn(newGroupIds));
        }

        return groupRepository.save(group);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Group group = getOr404(id);

        if(group.getTemplate() != null){
            group.getTemplate().getGroups().remove(group);
        }
        group.setTemplate(null);
        deleteAllPreferencesFromGroup(group);
        RemoveAllPupilsFromGroup(group);

        group.getPlacements().forEach(placement -> {
            placement.setGroup(null);
        });

        groupRepository.delete(group);
    }

    public Set<Group> getByIds(Set<Long> ids) {
        Set<Group> groups = new HashSet<>(ids.size());
        for(Long id : ids){
            groups.add(getOr404(id));
        }
        return groups;
        // @todo: decide what to do, this line gets the same result but its not throwing exception for non existing ids
        //return groupRepository.getAllByIdIn(ids);
    }


    public void addPupilToGroup(Group group, Pupil pupil){
        group.addPupil(pupil);
        pupilRepository.save(pupil);
    }

    @Transactional
    public void addPupilPreference(Group group, Preference preference) throws Group.PupilNotBelongException, Preference.SamePupilException {
        Pupil selector = group.getPupilById(preference.getSelectorSelectedId().getSelectorId());
        Pupil selected = group.getPupilById(preference.getSelectorSelectedId().getSelectedId());

        group.addOrUpdatePreference(selector, selected, preference.getIsSelectorWantToBeWithSelected());

        preferenceRepository.saveAllAndFlush(group.getPreferences());
        groupRepository.save(group);
    }

    @Transactional
    public void deletePupilPreferences(Group group, Preference preference) {
        Long selectorId = preference.getSelectorSelectedId().getSelectorId();
        Long selectedId = preference.getSelectorSelectedId().getSelectedId();

        Set<SelectorSelectedId> selectorSelectedIds = group.getPreferencesForPupils(selectorId, selectedId)
                .stream().map(Preference::getSelectorSelectedId).collect(Collectors.toSet());
        preferenceRepository.deleteAllById(selectorSelectedIds);
    }

    @Transactional
    public void deletePupilPreferences(Group group, Pupil pupil){
        Set<SelectorSelectedId> selectorSelectedIds = group.getAllPreferencesForPupil(pupil.getId())
                .stream().map(Preference::getSelectorSelectedId).collect(Collectors.toSet());
        preferenceRepository.deleteAllById(selectorSelectedIds);
    }

    @Transactional
    public void deleteAllPreferencesFromGroup(Group group){
        Set<SelectorSelectedId> selectorSelectedIds = group.getPreferences().stream()
                .map(Preference::getSelectorSelectedId).collect(Collectors.toSet());
        preferenceRepository.deleteAllById(selectorSelectedIds);
    }

    public Set<Preference> getAllPreferencesForPupil(Pupil pupil, Group group){
        return group.getAllPreferencesForPupil(pupil.getId());
    }

    @Transactional
    public void RemoveAllPupilsFromGroup(Group group){
        group.getPupils().forEach(pupil -> pupil.removeFromGroup(group));
        pupilRepository.saveAll(group.getPupils());
    }

    private void verifyGroupNotAssociated(Group group) throws GroupIsAssociatedException {
        if(group.getPlacements().size() > 0){
            Placement placement = group.getPlacements().stream().findFirst().get();
            throw new GroupIsAssociatedException(placement);
        }
    }

    public static class GroupIsAssociatedException extends NotAcceptable {
        public GroupIsAssociatedException(Placement placement){
            super("Group is associated with Placement " + placement.getId() + " (" + placement.getName() + ")");
        }
    }
}
