package jen.web.controller;

import jen.web.assembler.GroupModelAssembler;
import jen.web.assembler.PupilModelAssembler;
import jen.web.entity.Group;
import jen.web.entity.Pupil;
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

import java.util.Map;
import java.util.Set;


@RequiredArgsConstructor
@RestController
@RequestMapping("/pupils")
public class PupilRestController extends BaseRestController<Pupil> {

    private static final Logger logger = LoggerFactory.getLogger(PupilRestController.class);
    private final PupilService pupilService;
    private final GroupService groupService;
    private final PupilModelAssembler pupilAssembler;
    private final GroupModelAssembler groupAssembler;

    @Override
    @GetMapping()
    public ResponseEntity<?> getAll() {
        CollectionModel<EntityModel<Pupil>> allEntities = pupilAssembler.toCollectionModel(pupilService.all());

        return ResponseEntity
                .ok()
                .body(allEntities);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        EntityModel<Pupil> entity = pupilAssembler.toModel(pupilService.getOr404(id));

        return ResponseEntity
                .ok()
                .body(entity);
    }

    @Override
    @PutMapping()
    public ResponseEntity<?> create(@RequestBody Pupil newRecord) {
        EntityModel<Pupil> entity = pupilAssembler.toModel(pupilService.add(newRecord));

        return ResponseEntity
                .created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entity);
    }

    @Override
    @PostMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Pupil updatedRecord) {
        EntityModel<Pupil> entity = pupilAssembler.toModel(pupilService.updateById(id, updatedRecord));

        return ResponseEntity
                .ok()
                .body(entity);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        pupilService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Get pupil's group.
     *
     * @param id the pupil ID
     * @return List of pupil groups
     */
    @GetMapping("/{id}/groups")
    public ResponseEntity<?> getPupilGroups(@PathVariable Long id) {
        Pupil pupil = pupilService.getOr404(id);
        CollectionModel<EntityModel<Group>> allEntities = groupAssembler.toCollectionModel(pupil.getGroups());

        return ResponseEntity
                .ok()
                .body(allEntities);
    }

    /**
     * Delete a group from pupil.
     *
     * @param id pupil ID
     */
    @PutMapping("/{id}/groups")
    public ResponseEntity<?> assignGroupForPupil(@PathVariable Long id, @RequestBody Long groupId) {
        Pupil pupil = pupilService.getOr404(id);
        Group group = groupService.getOr404(groupId);

        pupil.addToGroup(group);
        CollectionModel<EntityModel<Group>> allEntities = groupAssembler.toCollectionModel(pupilService.updateById(id, pupil).getGroups());

        return ResponseEntity
                .ok()
                .body(allEntities);
    }

    /**
     * Assign pupil to a group.
     *
     * @param id the pupil ID
     * @param groupIds list of group IDs
     */
    @PostMapping(path = "/{id}/groups")
    public ResponseEntity<?> updatePupilGroup(@PathVariable Long id, @RequestBody Set<Long> groupIds) {
        Pupil pupil = pupilService.getOr404(id);

        pupil.setGroups(groupService.getByIds(groupIds));
        CollectionModel<EntityModel<Group>> allEntities = groupAssembler.toCollectionModel(pupilService.updateById(id, pupil).getGroups());

        return ResponseEntity
                .ok()
                .body(allEntities);
    }

    /**
     * Delete a group from pupil.
     *
     * @param id pupil ID
     */
    @DeleteMapping("/{id}/groups")
    public ResponseEntity<?> deleteGroupFromPupil(@PathVariable Long id, @RequestBody Long groupId) {
        Pupil pupil = pupilService.getOr404(id);
        Group group = groupService.getOr404(groupId);

        pupil.removeFromGroup(group);
        pupilService.updateById(id, pupil);

        return ResponseEntity.ok().build();
    }

    /**
     * Create attribute values of a pupil within a template.
     *
     * @param id the pupil ID
     * @param groupId group ID which the pupil belongs to
     */
    @RequestMapping(path="/{id}/groups/{groupId}/attributes", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<?> updateAttributeValues(@PathVariable Long id, @PathVariable Long groupId, @RequestBody Map<Long,
            Double> attributeValues) {
        Pupil pupil = pupilService.getOr404(id);
        Group group = groupService.getOr404(groupId);

        pupilService.addAttributeValues(pupil, group, attributeValues);
        return ResponseEntity.ok().build();
    }

    /**
     * Create attribute values of a pupil within a template.
     *
     * @param id the pupil ID
     * @param groupId group ID which the pupil belongs to
     * @param attributeIds a set of attribute ids to delete
     */
    @DeleteMapping("/{id}/groups/{groupId}/attributes")
    public ResponseEntity<?> deleteAttributeValues(@PathVariable Long id, @PathVariable Long groupId,
                                                   @RequestBody Set<Long> attributeIds) {
        Pupil pupil = pupilService.getOr404(id);
        Group group = groupService.getOr404(groupId);

        pupilService.removeAttributeValues(pupil, group, attributeIds);
        return ResponseEntity.ok().build();
    }
}
