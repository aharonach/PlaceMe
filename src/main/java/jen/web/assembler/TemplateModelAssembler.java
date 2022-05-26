package jen.web.assembler;

import jen.web.controller.TemplateRestController;
import jen.web.entity.Template;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TemplateModelAssembler implements RepresentationModelAssembler<Template, EntityModel<Template>> {
    Class<TemplateRestController> controller = TemplateRestController.class;

    @Override
    public EntityModel<Template> toModel(Template entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(controller).get(entity.getId())).withSelfRel(),
                linkTo(methodOn(controller).getAll()).withRel("templates")
        );
    }

    @Override
    public CollectionModel<EntityModel<Template>> toCollectionModel(Iterable<? extends Template> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(controller).getAll()).withSelfRel());
    }
}