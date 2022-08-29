package jen.web.controller;

import jen.web.assembler.GroupModelAssembler;
import jen.web.assembler.PupilModelAssembler;
import jen.web.entity.Group;
import jen.web.entity.Preference;
import jen.web.entity.Pupil;
import jen.web.exception.BadRequest;
import jen.web.service.GroupService;
import jen.web.service.PupilService;
import jen.web.util.FieldSortingMaps;
import jen.web.util.PagesAndSortHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public ResponseEntity<?> getAll(@RequestParam Optional<Integer> page,
                                    @RequestParam Optional<String> sortBy,
                                    @RequestParam(required = false) boolean descending) {
        try {
            PageRequest pageRequest = pagesAndSortHandler.getPageRequest(page, sortBy, FieldSortingMaps.groupMap, descending);
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
                                              @RequestParam Optional<Integer> page,
                                              @RequestParam Optional<String> sortBy,
                                              @RequestParam(required = false) boolean descending){
        Group group = groupService.getOr404(groupId);

        try {
            PageRequest pageRequest = pagesAndSortHandler.getPageRequest(page, sortBy, FieldSortingMaps.pupilMap, descending);
            Page<Pupil> pages = groupService.getPupilOfGroup(group, pageRequest);
            CollectionModel<EntityModel<Pupil>> allEntities = pupilAssembler.toPageCollection(pages);
            return ResponseEntity.ok().body(allEntities);

        } catch (PagesAndSortHandler.FieldNotSortableException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @GetMapping("/{groupId}/preferences")
    public ResponseEntity<?> getPreferences(@PathVariable Long groupId){
        Set<Preference> preferences = groupService.getOr404(groupId).getPreferences();
        CollectionModel<Preference> allEntities = preferencesToModelCollection(groupId, preferences);

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
        CollectionModel<Preference> allEntities = preferencesToModelCollection(groupId, preferences);

        return ResponseEntity
                .ok()
                .body(allEntities);
    }

    @PutMapping("/{groupId}/preferences")
    public ResponseEntity<?> addOrUpdatePreference(@PathVariable Long groupId,
                                                   @RequestBody Preference preference) {

        try {
            Group group = groupService.getOr404(groupId);
            groupService.addPupilPreference(group, preference);
            return ResponseEntity.ok().build();

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

        groupService.deletePupilPreferences(group, selectorId, selectedId);

        return ResponseEntity.ok().build();
    }

    private CollectionModel<Preference> preferencesToModelCollection(Long groupId, Iterable<Preference> preferences){
        return  CollectionModel.of(preferences,
                linkTo(methodOn(this.getClass()).get(groupId)).withRel("group"),
                linkTo(methodOn(this.getClass()).getPreferences(groupId)).withSelfRel()
        );
    }
}