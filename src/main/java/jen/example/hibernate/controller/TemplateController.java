package jen.example.hibernate.controller;

import jen.LoadDatabase;
import jen.example.hibernate.entity.Attribute;
import jen.example.hibernate.entity.Template;
import jen.example.hibernate.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping({"/templates/", "/templates"})
@RequiredArgsConstructor
public class TemplateController {

    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);

    private final TemplateService service;

    @GetMapping()
    public List<Template> all(){
        return service.getAll();
    }

    // Manage Template
    @GetMapping("/{id}")
    EntityModel<Template> one(@PathVariable Long id) {
        Template template = service.getOr404(id);
        return EntityModel.of(template,
                linkTo(methodOn(this.getClass()).one(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).all()).withRel("templates")
        );
    }

    @PutMapping()
    public Template newTemplate(@RequestBody Template template){
        return service.add(template);
    }

    @PostMapping("/{id}")
    Template updateTemplate(@PathVariable Long id, @RequestBody Template newTemplate) {
        return service.updateById(id, newTemplate);
    }

    @DeleteMapping("/{id}")
    void deleteTemplate(@PathVariable Long id) {
        service.deleteById(id);
    }

    //Manage attributes
    @DeleteMapping("/{templateId}/attributes/{attributeId}")
    Template deleteAttributeForTemplate(@PathVariable Long templateId, @PathVariable Long attributeId) {
        return service.deleteAttributeForTemplateById(templateId, attributeId);
    }

    @PostMapping("/{templateId}/attributes/{attributeId}")
    Template updateAttributeForTemplate(@PathVariable Long templateId,
                                        @PathVariable Long attributeId,
                                        @RequestBody Attribute newAttribute) {
        return service.updateAttributeForTemplateById(templateId, attributeId, newAttribute);
    }

    @PutMapping("/{templateId}/attributes")
    Template addAttributeForTemplate(@PathVariable Long templateId,
                                     @RequestBody Attribute newAttribute) {
        return service.addAttributeForTemplateById(templateId, newAttribute);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        logger.warn("Returning HTTP 400 Bad Request", e);
    }
}
