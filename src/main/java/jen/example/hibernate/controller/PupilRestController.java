package jen.example.hibernate.controller;

import jen.example.hibernate.assembler.GroupModelAssembler;
import jen.example.hibernate.assembler.PupilModelAssembler;
import jen.example.hibernate.entity.Group;
import jen.example.hibernate.entity.Pupil;
import jen.example.hibernate.service.GroupService;
import jen.example.hibernate.service.PupilService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pupils")
public class PupilRestController extends BaseRestController<Pupil> {

    private static final Logger logger = LoggerFactory.getLogger(PupilRestController.class);
    private final PupilService service;
    private final GroupService groupService;
    private final PupilModelAssembler assembler;
    private final GroupModelAssembler groupAssembler;

    @Override
    @GetMapping()
    public ResponseEntity<?> getAll() {
        List<Pupil> allPupils = service.all();

        return ResponseEntity.ok().body(assembler.toCollectionModel(allPupils));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Pupil pupil = service.getOr404(id);

        return ResponseEntity.ok().body(assembler.toModel(pupil));
    }

    @Override
    @PutMapping()
    public ResponseEntity<?> create(@RequestBody Pupil newRecord) {
        EntityModel<Pupil> record = assembler.toModel(service.add(newRecord));

        return ResponseEntity
                .created(record.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(record);
    }

    @Override
    @PostMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Pupil updatedRecord) {
        Pupil record = service.updateById(id, updatedRecord);

        return ResponseEntity.ok().body(record);
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
        Set<Group> groups = pupil.getGroups();

        return ResponseEntity.ok().body(groupAssembler.toCollectionModel(groups));
    }

    /**
     * Assign pupil to a group.
     *
     * @param id the pupil ID
     * @param groupIds list of group IDs
     */
    @RequestMapping(path = "/{id}/groups", method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<?> updateGroup(@PathVariable Long id, @RequestBody Set<Long> groupIds) {
        Pupil pupil = service.getOr404(id);

        // @TODO this probably should be in service class
        Set<Group> groups = groupService.getByIds(groupIds);
        groups.forEach(pupil::addGroup);
        service.updateById(id, pupil);

        return ResponseEntity.ok().body(groupAssembler.toCollectionModel(groups));
    }

    /**
     * Delete a group from pupil.
     *
     * @param id pupil ID
     */
    @DeleteMapping("/{id}/groups")
    public ResponseEntity<?> deleteGroups(@PathVariable Long id, @RequestBody Set<Long> groupIds) {
        return null;
    }

    /**
     * Get attribute values of a pupil within a template.
     *
     * @param id the pupil ID
     * @param templateId (optional) the template ID
     */
    @GetMapping("/{id}/attributes")
    public ResponseEntity<?> getAttributes(@PathVariable Long id,
                                                        @RequestBody(required = false) Long templateId) {
        Pupil pupil = service.getOr404(id);

        return ResponseEntity.ok().body(pupil.getAttributeValues());
    }

    /**
     * Create attribute values of a pupil within a template.
     *
     * @param id the pupil ID
     * @param attributeValues list of attribute ids with values
     */
    @PutMapping("/{id}/attributes")
    public ResponseEntity<?> putAttributes(@PathVariable Long id, @RequestBody Map<Long, Double> attributeValues) {
        Pupil pupil = service.getOr404(id);

        return null;
    }

    /**
     * Update single value of attribute of a pupil.
     *
     * @param id
     * @param attributeId
     * @param value
     * @return
     */
    @PostMapping("/{id}/attributes/{attributeId}")
    public ResponseEntity<?> updateAttributes(@PathVariable Long id, @PathVariable Long attributeId,
                                        @RequestBody Double value) {
        return null;
    }

    /**
     * Delete a attribute value from student.
     *
     * @param id
     * @param attributeId
     * @return
     */
    @DeleteMapping("/{id}/attributes/{attributeId}")
    public ResponseEntity<?> deleteAttribute(@PathVariable Long id, @PathVariable Long attributeId) {
        return null;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        logger.warn("Returning HTTP 400 Bad Request", e);
    }
}
