package jen.web.controller;

import jen.web.assembler.GroupModelAssembler;
import jen.web.assembler.PupilModelAssembler;
import jen.web.entity.AttributeValue;
import jen.web.entity.Group;
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

import java.util.Map;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


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
    @GetMapping("/{pupilId}")
    public ResponseEntity<?> get(@PathVariable Long pupilId) {
        EntityModel<Pupil> entity = pupilAssembler.toModel(pupilService.getOr404(pupilId));

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
    @PostMapping("/{pupilId}")
    public ResponseEntity<?> update(@PathVariable Long pupilId, @RequestBody Pupil updatedRecord) {
        EntityModel<Pupil> entity = pupilAssembler.toModel(pupilService.updateById(pupilId, updatedRecord));

        return ResponseEntity
                .ok()
                .body(entity);
    }

    @Override
    @DeleteMapping("/{pupilId}")
    public ResponseEntity<?> delete(@PathVariable Long pupilId) {
        pupilService.deleteById(pupilId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{pupilId}/groups")
    public ResponseEntity<?> getPupilGroups(@PathVariable Long pupilId) {
        Pupil pupil = pupilService.getOr404(pupilId);
        CollectionModel<EntityModel<Group>> allEntities = groupAssembler.toCollectionModel(pupil.getGroups());

        return ResponseEntity
                .ok()
                .body(allEntities);
    }
    
    @PutMapping("/{pupilId}/groups")
    public ResponseEntity<?> assignGroupForPupil(@PathVariable Long pupilId, @RequestBody Long groupId) {
        Pupil pupil = pupilService.getOr404(pupilId);
        Group group = groupService.getOr404(groupId);

        pupil.addToGroup(group);
        CollectionModel<EntityModel<Group>> allEntities = groupAssembler.toCollectionModel(pupilService.updateById(pupilId, pupil).getGroups());

        return ResponseEntity
                .ok()
                .body(allEntities);
    }
    
    @PostMapping(path = "/{pupilId}/groups")
    public ResponseEntity<?> updatePupilGroup(@PathVariable Long pupilId, @RequestBody Set<Long> groupIds) {
        Pupil pupil = pupilService.getOr404(pupilId);

        pupil.setGroups(groupService.getByIds(groupIds));
        Pupil updatedPupil = pupilService.updateById(pupilId, pupil);
        CollectionModel<EntityModel<Group>> allEntities = groupAssembler.toCollectionModel(updatedPupil.getGroups());

        return ResponseEntity
                .ok()
                .body(allEntities);
    }
    
    @DeleteMapping("/{pupilId}/groups")
    public ResponseEntity<?> deleteGroupFromPupil(@PathVariable Long pupilId, @RequestBody Long groupId) {
        Pupil pupil = pupilService.getOr404(pupilId);
        Group group = groupService.getOr404(groupId);

        pupil.removeFromGroup(group);
        pupilService.updateById(pupilId, pupil);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(path="/{pupilId}/groups/{groupId}/attributes", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<?> updateAttributeValues(@PathVariable Long pupilId,
                                                   @PathVariable Long groupId,
                                                   @RequestBody Map<Long, Double> attributeValues) {
        Pupil pupil = pupilService.getOr404(pupilId);
        Group group = groupService.getOr404(groupId);

        try {
            pupilService.addAttributeValues(pupil, group, attributeValues);
            return ResponseEntity.ok().build();

        } catch (Group.PupilNotBelongException | Template.AttributeNotBelongException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @DeleteMapping("/{pupilId}/groups/{groupId}/attributes")
    public ResponseEntity<?> deleteAttributeValues(@PathVariable Long pupilId,
                                                   @PathVariable Long groupId,
                                                   @RequestBody Set<Long> attributeIds) {
        Pupil pupil = pupilService.getOr404(pupilId);
        Group group = groupService.getOr404(groupId);

        try {
            pupilService.removeAttributeValues(pupil, group, attributeIds);
            return ResponseEntity.ok().build();

        } catch (Group.PupilNotBelongException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @GetMapping("/{pupilId}/groups/{groupId}/attributes")
    public ResponseEntity<?> getAttributeValues(@PathVariable Long pupilId,
                                                   @PathVariable Long groupId) {
        Pupil pupil = pupilService.getOr404(pupilId);
        Group group = groupService.getOr404(groupId);

        try {
            pupilService.getAttributeValues(pupil, group);
            Set<AttributeValue> attributeValues = pupilService.getAttributeValues(pupil, group);

            CollectionModel<AttributeValue> allEntities = preferencesToModelCollection(pupil.getId(), group.getId(), attributeValues);
            return ResponseEntity.ok().body(allEntities);

        } catch (Group.PupilNotBelongException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    private CollectionModel<AttributeValue> preferencesToModelCollection(Long pupilId, Long groupId, Set<AttributeValue> attributeValues){
        return  CollectionModel.of(attributeValues,
                linkTo(methodOn(this.getClass()).get(pupilId)).withRel("pupil"),
                linkTo(methodOn(this.getClass()).getPupilGroups(groupId)).withRel("group"),
                linkTo(methodOn(this.getClass()).getAttributeValues(pupilId, groupId)).withSelfRel()
        );
    }
}
