package jen.example.hibernate.assembler;

import jen.example.hibernate.controller.TemplateRestController;
import jen.example.hibernate.entity.Template;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TemplateModelAssembler implements RepresentationModelAssembler<Template, EntityModel<Template>> {

    @Override
    public EntityModel<Template> toModel(Template entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(TemplateRestController.class).get(entity.getId())).withSelfRel(),
                linkTo(methodOn(TemplateRestController.class).getAll()).withRel("templates")
        );
    }

    @Override
    public CollectionModel<EntityModel<Template>> toCollectionModel(Iterable<? extends Template> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(TemplateRestController.class).getAll()).withSelfRel());
    }
}
