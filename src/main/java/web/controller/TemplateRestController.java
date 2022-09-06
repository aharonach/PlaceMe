package web.controller;

import web.assembler.TemplateModelAssembler;
import web.entity.Attribute;
import web.entity.Template;
import web.exception.BadRequest;
import web.service.TemplateService;
import web.util.FieldSortingMaps;
import web.util.PagesAndSortHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
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

    private final TemplateService templateService;
    private final TemplateModelAssembler templateAssembler;
    private final PagesAndSortHandler pagesAndSortHandler;

    @Override
    @GetMapping()
    public ResponseEntity<?> getAll(@ParameterObject @ModelAttribute PagesAndSortHandler.PaginationInfo pageInfo) {

        try {
            PageRequest pageRequest = pagesAndSortHandler.getPageRequest(pageInfo, FieldSortingMaps.templateMap);
            CollectionModel<EntityModel<Template>> pagesModel = templateAssembler.toPageCollection(templateService.all(pageRequest));
            return ResponseEntity.ok().body(pagesModel);

        } catch (PagesAndSortHandler.FieldNotSortableException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @Override
    @GetMapping("/{templateId}")
    public ResponseEntity<?> get(@PathVariable Long templateId) {
        EntityModel<Template> entity = templateAssembler.toModel(templateService.getOr404(templateId));

        return ResponseEntity
                .ok()
                .body(entity);
    }

    @Override
    @PutMapping()
    public ResponseEntity<?> create(@RequestBody Template newRecord) {
        try{
            EntityModel<Template> entity = templateAssembler.toModel(templateService.add(newRecord));
            return ResponseEntity.created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entity);
        } catch (Template.AttributeAlreadyExistException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @Override
    @PostMapping("/{templateId}")
    public ResponseEntity<?> update(@PathVariable Long templateId,
                                    @RequestBody Template updatedRecord) {
        try{
            Template updatedTemplate = templateService.updateById(templateId, updatedRecord);
            EntityModel<Template> entity = templateAssembler.toModel(updatedTemplate);
            return ResponseEntity.ok().body(entity);

        } catch (Template.AttributeAlreadyExistException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @Override
    @DeleteMapping("/{templateId}")
    public ResponseEntity<?> delete(@PathVariable Long templateId) {
        templateService.deleteById(templateId);
        return ResponseEntity.ok().build();
    }

    //Manage attributes
    @DeleteMapping("/{templateId}/attributes/{attributeId}")
    public ResponseEntity<?> deleteAttributeForTemplate(@PathVariable Long templateId,
                                                        @PathVariable Long attributeId) {

        Template template = templateService.getOr404(templateId);
        Attribute attribute = templateService.getAttributeOr404(attributeId);
        try {
            templateService.deleteAttributeForTemplate(template, attribute);
        } catch (Template.AttributeNotBelongException e) {
            throw new BadRequest(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{templateId}/attributes/{attributeId}")
    public ResponseEntity<?> updateAttributeForTemplate(@PathVariable Long templateId,
                                                        @PathVariable Long attributeId,
                                                        @RequestBody Attribute newAttribute) {

        Template template = templateService.getOr404(templateId);
        Attribute attribute = templateService.getAttributeOr404(attributeId);
        try {
            Template updatedTemplate = templateService.updateAttributeForTemplate(template, attribute, newAttribute);
            EntityModel<Template> entity = templateAssembler.toModel(updatedTemplate);
            return ResponseEntity.ok().body(entity);

        } catch (Template.AttributeNotBelongException | Template.AttributeAlreadyExistException e) {
            throw new BadRequest(e.getMessage());
        }
    }

    @PutMapping("/{templateId}/attributes")
    public ResponseEntity<?> addAttributeForTemplate(@PathVariable Long templateId,
                                                     @RequestBody Attribute newAttribute) {

        Template template = templateService.getOr404(templateId);
        try{
            Template updatedTemplate = templateService.addAttributeForTemplate(template, newAttribute);
            EntityModel<Template> entity = templateAssembler.toModel(updatedTemplate);

            return ResponseEntity.created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entity);

        } catch (Template.AttributeAlreadyExistException e) {
            throw new BadRequest(e.getMessage());
        }
    }
}