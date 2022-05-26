package jen.web.controller;

import jen.web.assembler.GroupModelAssembler;
import jen.web.assembler.PupilModelAssembler;
import jen.web.assembler.TemplateModelAssembler;
import jen.web.entity.Group;
import jen.web.entity.Pupil;
import jen.web.entity.Template;
import jen.web.service.GroupService;
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
@RequestMapping("/groups")
public class GroupRestController extends BaseRestController<Group> {

    private static final Logger logger = LoggerFactory.getLogger(GroupRestController.class);
    private final GroupService groupService;
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
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        EntityModel<Group> entity = groupAssembler.toModel(groupService.getOr404(id));

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
    @PostMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Group updatedRecord) {
        EntityModel<Group> entity = groupAssembler.toModel(groupService.updateById(id, updatedRecord));

        return ResponseEntity
                .ok()
                .body(entity);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        groupService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/template")
    public ResponseEntity<?> getGroupTemplate(@PathVariable Long id){
        EntityModel<Template> entity = templateAssembler.toModel(groupService.getOr404(id).getTemplate());

        return ResponseEntity
                .ok()
                .body(entity);
    }

    @GetMapping("/{id}/pupils")
    public ResponseEntity<?> getPupilsOfGroup(@PathVariable Long id){
        CollectionModel<EntityModel<Pupil>> allEntities =
                pupilAssembler.toCollectionModel(groupService.getOr404(id).getPupils());

        return ResponseEntity
                .ok()
                .body(allEntities);
    }
}