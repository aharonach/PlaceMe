package jen.example.hibernate.assembler;

import jen.example.hibernate.controller.TemplateController;
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
                linkTo(methodOn(TemplateController.class).one(entity.getId())).withSelfRel(),
                linkTo(methodOn(TemplateController.class).all()).withRel("templates")
        );
    }

    @Override
    public CollectionModel<EntityModel<Template>> toCollectionModel(Iterable<? extends Template> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(TemplateController.class).all()).withSelfRel());
    }
}
