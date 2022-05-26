package jen.hibernate.controller;

import jen.hibernate.assembler.TemplateModelAssembler;
import jen.hibernate.entity.Attribute;
import jen.hibernate.entity.Template;
import jen.hibernate.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping({"/templates"})
@RequiredArgsConstructor
public class TemplateRestController extends BaseRestController<Template> {

    private static final Logger logger = LoggerFactory.getLogger(TemplateRestController.class);

    private final TemplateService service;
    private final TemplateModelAssembler assembler;


    @Override
    @GetMapping()
    public ResponseEntity<?> getAll() {
        CollectionModel<EntityModel<Template>> allEntities = assembler.toCollectionModel(service.all());

        return ResponseEntity
                .ok()
                .body(allEntities);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        EntityModel<Template> entity = assembler.toModel(service.getOr404(id));

        return ResponseEntity
                .ok()
                .body(entity);
    }

    @Override
    @PutMapping()
    public ResponseEntity<?> create(@RequestBody Template newRecord) {
        EntityModel<Template> entity = assembler.toModel(service.add(newRecord));

        return ResponseEntity
                .created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entity);
    }

    @Override
    @PostMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Template updatedRecord) {
        EntityModel<Template> entity = assembler.toModel(service.updateById(id, updatedRecord));

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

    //Manage attributes
    @DeleteMapping("/{templateId}/attributes/{attributeId}")
    public ResponseEntity<?> deleteAttributeForTemplate(@PathVariable Long templateId, @PathVariable Long attributeId) {
        EntityModel<Template> entity = assembler.toModel(service.deleteAttributeForTemplateById(templateId, attributeId));

        return ResponseEntity
                .ok()
                .body(entity);
    }

    @PostMapping("/{templateId}/attributes/{attributeId}")
    public ResponseEntity<?> updateAttributeForTemplate(@PathVariable Long templateId,
                                        @PathVariable Long attributeId,
                                        @RequestBody Attribute newAttribute) {
        EntityModel<Template> entity = assembler.toModel(service.updateAttributeForTemplateById(templateId, attributeId, newAttribute));

        return ResponseEntity
                .ok()
                .body(entity);
    }

    @PutMapping("/{templateId}/attributes")
    public ResponseEntity<?> addAttributeForTemplate(@PathVariable Long templateId,
                                     @RequestBody Attribute newAttribute) {
        EntityModel<Template> entity = assembler.toModel(service.addAttributeForTemplateById(templateId, newAttribute));

        return ResponseEntity
                .created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entity);
    }
}