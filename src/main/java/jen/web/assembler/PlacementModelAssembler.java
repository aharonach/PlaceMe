package jen.web.assembler;

import jen.web.controller.PlacementRestController;
import jen.web.entity.Placement;
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
public class PlacementModelAssembler implements RepresentationModelAssembler<Placement, EntityModel<Placement>> {
    Class<PlacementRestController> controller = PlacementRestController.class;

    @Override
    public EntityModel<Placement> toModel(Placement entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(controller).get(entity.getId())).withSelfRel(),
                linkTo(methodOn(controller).getAll(Optional.empty(),Optional.empty(), false)).withRel("placements"),
                linkTo(methodOn(controller).getResults(entity.getId(), Optional.empty(), Optional.empty())).withRel("placement_results")
        );
    }

    @Override
    public CollectionModel<EntityModel<Placement>> toCollectionModel(Iterable<? extends Placement> entities) {
        throw new RuntimeException("use Page instead");
    }

    public CollectionModel<EntityModel<Placement>> toCollectionModelWithoutPages(Iterable<? extends Placement> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(controller).getAll(Optional.empty(),Optional.empty(), false)).withSelfRel());
    }

    public CollectionModel<EntityModel<Placement>> toPageCollection(Page<? extends Placement> page) {
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(page.getSize(),page.getNumber(),page.getTotalElements(),page.getTotalPages());
        CollectionModel<EntityModel<Placement>> collectionModel = RepresentationModelAssembler.super.toCollectionModel(page);
        return PagedModel.of(collectionModel.getContent(), pageMetadata)
                .add(linkTo(methodOn(controller).getAll(Optional.empty(),Optional.empty(), false)).withSelfRel());
    }
}