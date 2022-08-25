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

import java.util.List;
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
        return ResponseEntity.ok().body(allEntities);
    }

    @Override
    @GetMapping("/{pupilId}")
    public ResponseEntity<?> get(@PathVariable Long pupilId) {
        EntityModel<Pupil> entity = pupilAssembler.toModel(pupilService.getOr404(pupilId));
        return ResponseEntity.ok().body(entity);
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

        // update general information and groups if exists
        try {
            Pupil updatedPupil = pupilService.updateById(pupilId, updatedRecord);
            EntityModel<Pupil> entity = pupilAssembler.toModel(updatedPupil);
            return ResponseEntity.ok().body(entity);

        } catch (Pupil.GivenIdContainsProhibitedCharsException | Pupil.GivenIdIsNotValidException e) {
            throw new BadRequest(e.getMessage());
        }

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

        return ResponseEntity.ok().body(allEntities);
    }
    
    @PutMapping("/{pupilId}/groups")
    public ResponseEntity<?> assignGroupForPupil(@PathVariable Long pupilId, @RequestBody Long groupId) {
        Pupil pupil = pupilService.getOr404(pupilId);
        Group groupToAdd = groupService.getOr404(groupId);

        Set<Group> updatedGroups = pupilService.linkPupilToGroup(pupil, groupToAdd);

        CollectionModel<EntityModel<Group>> allEntities = groupAssembler.toCollectionModel(updatedGroups);
        return ResponseEntity.ok().body(allEntities);
    }
    
    @PostMapping(path = "/{pupilId}/groups")
    public ResponseEntity<?> updatePupilGroup(@PathVariable Long pupilId, @RequestBody Set<Long> groupIds) {
        Pupil pupil = pupilService.getOr404(pupilId);
        List<Group> newGroups = groupService.getByIds(groupIds);

        Set<Group> updatedGroups = pupilService.setPupilGroups(pupil, newGroups);

        CollectionModel<EntityModel<Group>> allEntities = groupAssembler.toCollectionModel(updatedGroups);
        return ResponseEntity.ok().body(allEntities);
    }
    
    @DeleteMapping("/{pupilId}/groups")
    public ResponseEntity<?> unlinkPupilFromGroup(@PathVariable Long pupilId, @RequestBody Long groupId) {
        Pupil pupil = pupilService.getOr404(pupilId);
        Group group = groupService.getOr404(groupId);

        pupilService.unlinkPupilFromGroup(pupil, group);

        return ResponseEntity.ok().build();
    }

    @PostMapping(path="/{pupilId}/groups/{groupId}/attributes")
    public ResponseEntity<?> updateAttributeValues(@PathVariable Long pupilId,
                                                   @PathVariable Long groupId,
                                                   @RequestBody Map<Long, Double> attributeValues) {
        Pupil pupil = pupilService.getOr404(pupilId);
        Group group = groupService.getOr404(groupId);

        try {
            pupilService.addOrUpdateAttributeValuesFromIdValueMap(pupil, group, attributeValues);
            return ResponseEntity.ok().build();

        } catch (Group.PupilNotBelongException | Template.AttributeNotBelongException
                 | AttributeValue.ValueOutOfRangeException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @GetMapping("/{pupilId}/groups/{groupId}/attributes")
    public ResponseEntity<?> getAttributeValues(@PathVariable Long pupilId,
                                                   @PathVariable Long groupId) {
        Pupil pupil = pupilService.getOr404(pupilId);
        Group group = groupService.getOr404(groupId);

        try {
            Set<AttributeValue> attributeValues = pupilService.getAttributeValues(pupil, group);
            CollectionModel<AttributeValue> allEntities = preferencesToModelCollection(pupil.getId(), group.getId(), attributeValues);
            return ResponseEntity.ok().body(allEntities);

        } catch (Group.PupilNotBelongException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    private CollectionModel<AttributeValue> preferencesToModelCollection(Long pupilId, Long groupId, Iterable<AttributeValue> attributeValues){
        return  CollectionModel.of(attributeValues,
                linkTo(methodOn(this.getClass()).get(pupilId)).withRel("pupil"),
                linkTo(methodOn(this.getClass()).getPupilGroups(groupId)).withRel("group"),
                linkTo(methodOn(this.getClass()).getAttributeValues(pupilId, groupId)).withSelfRel()
        );
    }
}
