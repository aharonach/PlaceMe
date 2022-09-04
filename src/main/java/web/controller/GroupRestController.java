package web.controller;

import web.assembler.GroupModelAssembler;
import web.assembler.PupilModelAssembler;
import web.dto.PreferenceDto;
import web.entity.Group;
import web.entity.Preference;
import web.entity.Pupil;
import web.exception.BadRequest;
import web.service.GroupService;
import web.service.PupilService;
import web.util.FieldSortingMaps;
import web.util.PagesAndSortHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
@RequestMapping("/groups")
public class GroupRestController extends BaseRestController<Group> {

    private static final Logger logger = LoggerFactory.getLogger(GroupRestController.class);
    private final GroupService groupService;
    private final PupilService pupilService;
    private final GroupModelAssembler groupAssembler;
    private final PupilModelAssembler pupilAssembler;
    private final PagesAndSortHandler pagesAndSortHandler;


    @Override
    @GetMapping()
    public ResponseEntity<?> getAll(@ParameterObject @ModelAttribute PagesAndSortHandler.PaginationInfo pageInfo) {
        try {
            PageRequest pageRequest = pagesAndSortHandler.getPageRequest(pageInfo, FieldSortingMaps.groupMap);
            CollectionModel<EntityModel<Group>> pagesModel = groupAssembler.toPageCollection(groupService.all(pageRequest));
            return ResponseEntity.ok().body(pagesModel);

        } catch (PagesAndSortHandler.FieldNotSortableException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @Override
    @GetMapping("/{groupId}")
    public ResponseEntity<?> get(@PathVariable Long groupId) {
        EntityModel<Group> entity = groupAssembler.toModel(groupService.getOr404(groupId));

        return ResponseEntity
                .ok()
                .body(entity);
    }

    @Override
    @PutMapping()
    public ResponseEntity<?> create(@RequestBody Group newRecord) {
        EntityModel<Group> entity = groupAssembler.toModel(groupService.add(newRecord));

        return ResponseEntity
                .created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entity);
    }

    @Override
    @PostMapping("/{groupId}")
    public ResponseEntity<?> update(@PathVariable Long groupId,
                                    @RequestBody Group updatedRecord) {
        EntityModel<Group> entity = groupAssembler.toModel(groupService.updateById(groupId, updatedRecord));

        return ResponseEntity
                .ok()
                .body(entity);
    }

    @Override
    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> delete(@PathVariable Long groupId) {
        groupService.deleteById(groupId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupId}/pupils")
    public ResponseEntity<?> getPupilsOfGroup(@PathVariable Long groupId,
                                              @ParameterObject @ModelAttribute PagesAndSortHandler.PaginationInfo pageInfo){
        Group group = groupService.getOr404(groupId);

        try {
            PageRequest pageRequest = pagesAndSortHandler.getPageRequest(pageInfo, FieldSortingMaps.pupilMap);
            Page<Pupil> pages = groupService.getPupilOfGroup(group, pageRequest);
            CollectionModel<EntityModel<Pupil>> allEntities = pupilAssembler.toPageCollection(pages);
            return ResponseEntity.ok().body(allEntities);

        } catch (PagesAndSortHandler.FieldNotSortableException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @GetMapping("/{groupId}/pupils/all")
    public ResponseEntity<?> getAllPupilsOfGroup(@PathVariable Long groupId,
                                              @ParameterObject @ModelAttribute PagesAndSortHandler.PaginationInfo pageInfo){
        Group group = groupService.getOr404(groupId);

        List<Pupil> pages = groupService.getAllPupilOfGroup(group);
        CollectionModel<EntityModel<Pupil>> allEntities = pupilAssembler.toCollectionModelWithoutPages(pages);
        return ResponseEntity.ok().body(allEntities);
    }

    @GetMapping("/{groupId}/preferences")
    public ResponseEntity<?> getPreferences(@PathVariable Long groupId){
        Set<Preference> preferences = groupService.getOr404(groupId).getPreferences();
        CollectionModel<PreferenceDto> allEntities = preferencesToModelCollection(groupId, preferences);

        return ResponseEntity
                .ok()
                .body(allEntities);
    }

    @GetMapping("/{groupId}/preferences/{pupilId}")
    public ResponseEntity<?> getPreferencesForPupil(@PathVariable Long groupId,
                                                    @PathVariable Long pupilId){
        Group group = groupService.getOr404(groupId);
        Pupil pupil = pupilService.getOr404(pupilId);
        List<Preference> preferences = groupService.getAllPreferencesForPupil(group, pupil);
        CollectionModel<PreferenceDto> allEntities = preferencesToModelCollection(groupId, preferences);

        return ResponseEntity
                .ok()
                .body(allEntities);
    }

    @PutMapping("/{groupId}/preferences")
    public ResponseEntity<?> addOrUpdatePreference(@PathVariable Long groupId,
                                                   @RequestBody Preference preference) {

        try {
            Group group = groupService.getOr404(groupId);
            List<Preference> preferences = groupService.addPupilPreference(group, preference);
            CollectionModel<PreferenceDto> allEntities = preferencesToModelCollection(groupId, preferences);
            return ResponseEntity.ok().body(allEntities);

        } catch (Group.PupilNotBelongException | Preference.SamePupilException e) {
            throw new BadRequest(e.getMessage());
        }

    }

    @DeleteMapping("/{groupId}/preferences")
    public ResponseEntity<?> deletePreference(@PathVariable Long groupId,
                                              @RequestBody Preference preference) {

        Group group = groupService.getOr404(groupId);
        Long selectorId = preference.getSelectorSelectedId().getSelectorId();
        Long selectedId = preference.getSelectorSelectedId().getSelectedId();

        List<Preference> preferences = groupService.deletePupilPreferences(group, selectorId, selectedId);
        CollectionModel<PreferenceDto> allEntities = preferencesToModelCollection(groupId, preferences);
        return ResponseEntity.ok().body(allEntities);
    }

    private CollectionModel<PreferenceDto> preferencesToModelCollection(Long groupId, Collection<Preference> preferences){
        return  CollectionModel.of(preferences.stream().map(preference -> PreferenceDto.fromPreference(preference, groupService.getPupilByIdMapForGroup(groupId))).toList(),
                linkTo(methodOn(this.getClass()).get(groupId)).withRel("group"),
                linkTo(methodOn(this.getClass()).getPreferences(groupId)).withSelfRel()
        );
    }
}