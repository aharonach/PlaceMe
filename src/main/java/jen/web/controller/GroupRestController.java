package jen.web.controller;

import jen.web.assembler.GroupModelAssembler;
import jen.web.assembler.PupilModelAssembler;
import jen.web.assembler.TemplateModelAssembler;
import jen.web.entity.Group;
import jen.web.entity.Preference;
import jen.web.entity.Pupil;
import jen.web.entity.Template;
import jen.web.exception.BadRequest;
import jen.web.service.GroupService;
import jen.web.service.PupilService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final TemplateModelAssembler templateAssembler;
    private final PupilModelAssembler pupilAssembler;

    @Override
    @GetMapping()
    public ResponseEntity<?> getAll() {
        CollectionModel<EntityModel<Group>> allEntities = groupAssembler.toCollectionModel(groupService.all());

        return ResponseEntity
                .ok()
                .body(allEntities);
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
    public ResponseEntity<?> update(@PathVariable Long groupId, @RequestBody Group updatedRecord) {
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
    public ResponseEntity<?> getPupilsOfGroup(@PathVariable Long groupId){
        CollectionModel<EntityModel<Pupil>> allEntities =
                pupilAssembler.toCollectionModel(groupService.getOr404(groupId).getPupils());

        return ResponseEntity
                .ok()
                .body(allEntities);
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
    public ResponseEntity<?> getPreferencesForPupil(@PathVariable Long groupId, @PathVariable Long pupilId){
        Group group = groupService.getOr404(groupId);
        Pupil pupil = pupilService.getOr404(pupilId);
        Set<Preference> preferences = groupService.getAllPreferencesForPupil(group, pupil);
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

    private CollectionModel<Preference> preferencesToModelCollection(Long groupId, Set<Preference> preferences){
        return  CollectionModel.of(preferences,
                linkTo(methodOn(this.getClass()).get(groupId)).withRel("group"),
                linkTo(methodOn(this.getClass()).getPreferences(groupId)).withSelfRel()
        );
    }
}