package jen.example.hibernate.controller;

import jen.example.hibernate.assembler.GroupModelAssembler;
import jen.example.hibernate.assembler.PupilModelAssembler;
import jen.example.hibernate.dto.PupilDto;
import jen.example.hibernate.entity.Group;
import jen.example.hibernate.entity.Pupil;
import jen.example.hibernate.service.GroupService;
import jen.example.hibernate.service.PupilService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/pupils")
public class PupilRestController extends BaseRestController<Pupil> {

    private static final Logger logger = LoggerFactory.getLogger(PupilRestController.class);
    private final PupilService service;
    private final GroupService groupService;
    private final PupilModelAssembler pupilAssembler;
    private final GroupModelAssembler groupAssembler;

    @Override
    @GetMapping()
    public ResponseEntity<?> getAll() {
        CollectionModel<EntityModel<PupilDto>> allEntities = pupilAssembler.toCollectionModel(service.all());

        return ResponseEntity
                .ok()
                .body(allEntities);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        EntityModel<PupilDto> entity = pupilAssembler.toModel(service.getOr404(id));

        return ResponseEntity
                .ok()
                .body(entity);
    }

    @Override
    @PutMapping()
    public ResponseEntity<?> create(@RequestBody Pupil newRecord) {
        EntityModel<PupilDto> entity = pupilAssembler.toModel(service.add(newRecord));

        return ResponseEntity
                .created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entity);
    }

    @Override
    @PostMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Pupil updatedRecord) {
        EntityModel<PupilDto> entity = pupilAssembler.toModel(service.updateById(id, updatedRecord));

        return ResponseEntity
                .ok()
                .body(entity);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Get pupil's group.
     *
     * @param id the pupil ID
     * @return List of attribute values
     */
    @GetMapping("/{id}/groups")
    public ResponseEntity<?> getGroups(@PathVariable Long id) {
        Pupil pupil = service.getOr404(id);
        CollectionModel<EntityModel<Group>> allEntities = groupAssembler.toCollectionModel(pupil.getGroups());

        return ResponseEntity
                .ok()
                .body(allEntities);
    }

//    /**
//     * Assign pupil to a group.
//     *
//     * @param id the pupil ID
//     * @param groupIds list of group IDs
//     */
//    @RequestMapping(path = "/{id}/groups", method = {RequestMethod.PUT, RequestMethod.POST})
//    public ResponseEntity<?> updateGroup(@PathVariable Long id, @RequestBody Set<Long> groupIds) {
//        Pupil pupil = service.getOr404(id);
//
//        Set<Group> groups = groupService.getByIds(groupIds);
//        groups.forEach(pupil::addToGroup);
//        service.updateById(id, pupil);
//
//        return ResponseEntity.ok().body(groupAssembler.toCollectionModel(groups));
//    }
//
//    /**
//     * Delete a group from pupil.
//     *
//     * @param id pupil ID
//     */
//    @DeleteMapping("/{id}/groups")
//    public ResponseEntity<?> deleteGroups(@PathVariable Long id, @RequestBody Set<Long> groupIds) {
//        Pupil pupil = service.getOr404(id);
//
//        Set<Group> groups = groupService.getByIds(groupIds);
//        groups.forEach(pupil::removeFromGroup);
//        service.updateById(id, pupil);
//
//        return null;
//    }

//    /**
//     * Create attribute values of a pupil within a template.
//     *
//     * @param id the pupil ID
//     * @param attributeValues list of attribute ids with values
//     */
//    @PutMapping("/{id}/attributes")
//    public ResponseEntity<?> putAttributes(@PathVariable Long id, @RequestBody Map<Long, Double> attributeValues) {
//        service.addAttributeValues(id, attributeValues);
//        return ResponseEntity.ok().build();
//    }

//    /**
//     * Update single value of attribute of a pupil.
//     *
//     * @param id
//     * @param attributeId
//     * @param value
//     * @return
//     */
//    @PostMapping("/{id}/attributes/{attributeId}")
//    public ResponseEntity<?> updateAttributes(@PathVariable Long id, @PathVariable Long attributeId,
//                                        @RequestBody Double value) {
//        service.addAttributeValue(id, attributeId, value);
//        return ResponseEntity.ok().build();
//    }

//    /**
//     * Delete a attribute value from student.
//     *
//     * @param id
//     * @param attributeId
//     * @return
//     */
//    @DeleteMapping("/{id}/attributes/{attributeId}")
//    public ResponseEntity<?> deleteAttribute(@PathVariable Long id, @PathVariable Long attributeId) {
//        service.removeAttributeValue(id, attributeId);
//        return ResponseEntity.ok().build();
//    }
}
