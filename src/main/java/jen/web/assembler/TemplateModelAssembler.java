package jen.web.assembler;

import jen.web.controller.TemplateRestController;
import jen.web.entity.Pupil;
import jen.web.entity.Template;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TemplateModelAssembler implements RepresentationModelAssembler<Template, EntityModel<Template>> {
    Class<TemplateRestController> controller = TemplateRestController.class;

    @Override
    public EntityModel<Template> toModel(Template entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(controller).get(entity.getId())).withSelfRel(),
                linkTo(methodOn(controller).getAll(Optional.empty(),Optional.empty())).withRel("templates")
        );
    }

    @Override
    public CollectionModel<EntityModel<Template>> toCollectionModel(Iterable<? extends Template> entities) {
        throw new RuntimeException("use Page instead");
    }

    public CollectionModel<EntityModel<Template>> toPageCollection(Page<? extends Template> page) {
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(page.getSize(),page.getNumber(),page.getTotalElements(),page.getTotalPages());
        CollectionModel<EntityModel<Template>> collectionModel = RepresentationModelAssembler.super.toCollectionModel(page);
        return PagedModel.of(collectionModel.getContent(), pageMetadata)
                .add(linkTo(methodOn(controller).getAll(Optional.empty(),Optional.empty())).withSelfRel());
    }
}