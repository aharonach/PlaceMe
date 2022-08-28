package jen.web.service;

import jen.web.entity.*;
import jen.web.exception.EntityAlreadyExists;
import jen.web.exception.NotFound;
import jen.web.repository.GroupRepository;
import jen.web.repository.PreferenceRepository;
import jen.web.repository.PupilRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    public List<Group> allWithoutPages() {
        return groupRepository.findAll();
    }

    @Override
    public Page<Group> all(PageRequest pageRequest) {
        return groupRepository.findAll(pageRequest);
    }

    @Override
    @Transactional
    public Group updateById(Long id, Group newGroup) {
        Group group = getOr404(id);

        group.setName(newGroup.getName());
        group.setDescription(newGroup.getDescription());

        if(newGroup.getTemplate() != null && !group.getTemplate().equals(newGroup.getTemplate())){
            Template newTemplate = templateService.getOr404(newGroup.getTemplate().getId());
            group.setTemplate(newTemplate);
        }

        return groupRepository.save(group);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Group group = getOr404(id);

        if(group.getTemplate() != null){
            group.getTemplate().removeGroup(group);
        }
        group.setTemplate(null);
        deleteAllPreferencesFromGroup(group);
        unlinkAllPupilsFromGroup(group);

        group.getPlacements().forEach(placement -> {
            placement.setGroup(null);
        });

        groupRepository.delete(group);
    }

    public List<Group> getByIdsWithoutPages(Set<Long> ids) {
        return groupRepository.getAllByIdInOrderById(ids);
    }

    public Page<Group> getByIds(Set<Long> ids, PageRequest pageRequest) {
        return groupRepository.getAllByIdIn(ids, pageRequest);
    }

    public Page<Pupil> getPupilOfGroup(Group group, PageRequest pageRequest) {
        Set<Long> pupilIds = group.getPupils().stream().map(BaseEntity::getId).collect(Collectors.toSet());
        return pupilRepository.getAllByIdIn(pupilIds, pageRequest);
    }

    public void linkPupilToGroup(Group group, Pupil pupil){
        group.addPupil(pupil);
        pupilRepository.save(pupil);
    }

    public void unlinkPupilFromGroup(Group group, Pupil pupil){
        group.removePupil(pupil);
        deletePupilPreferences(group, pupil);
        pupilRepository.save(pupil);
    }

    @Transactional
    public void unlinkAllPupilsFromGroup(Group group){
        new ArrayList<>(group.getPupils()).forEach(pupil -> pupil.removeFromGroup(group));
        deleteAllPreferencesFromGroup(group);
        group.clearPupils();
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
    public void deletePupilPreferences(Group group, Long selectorId, Long selectedId) {

        Set<SelectorSelectedId> selectorSelectedIds = group.getPreferenceForPupils(selectorId, selectedId)
                .stream()
                .map(Preference::getSelectorSelectedId)
                .collect(Collectors.toSet());
        group.getPreferenceForPupils(selectorId, selectedId).ifPresent(group::deletePreference);
        preferenceRepository.deleteAllById(selectorSelectedIds);
    }

    @Transactional
    public void deletePupilPreferences(Group group, Pupil pupil){
        Set<SelectorSelectedId> selectorSelectedIds = group.getAllPreferencesForPupil(pupil.getId())
                .stream()
                .map(Preference::getSelectorSelectedId)
                .collect(Collectors.toSet());
        new HashSet<>(group.getAllPreferencesForPupil(pupil.getId())).forEach(group::deletePreference);
        preferenceRepository.deleteAllBySelectorSelectedIdInAndGroupId(selectorSelectedIds, group.getId());
    }

    @Transactional
    public void deleteAllPreferencesFromGroup(Group group){
        Set<SelectorSelectedId> selectorSelectedIds = group.getPreferences().stream()
                .map(Preference::getSelectorSelectedId)
                .collect(Collectors.toSet());
        group.clearPreferences();
        preferenceRepository.deleteAllBySelectorSelectedIdInAndGroupId(selectorSelectedIds, group.getId());
    }

    public List<Preference> getAllPreferencesForPupil(Group group, Pupil pupil){
        return group.getAllPreferencesForPupil(pupil.getId()).stream()
                .sorted()
                .collect(Collectors.toList());
    }
}
