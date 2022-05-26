package jen.hibernate.assembler;

import jen.hibernate.controller.PlacementRestController;
import jen.hibernate.entity.Placement;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PlacementModelAssembler implements RepresentationModelAssembler<Placement, EntityModel<Placement>> {
    Class<PlacementRestController> controller = PlacementRestController.class;

    @Override
    public EntityModel<Placement> toModel(Placement entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(controller).get(entity.getId())).withSelfRel(),
                linkTo(methodOn(controller).getAll()).withRel("placements"),
                linkTo(methodOn(controller).getResults(entity.getId())).withRel("placement_results")
        );
    }

    @Override
    public CollectionModel<EntityModel<Placement>> toCollectionModel(Iterable<? extends Placement> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(controller).getAll()).withSelfRel());
    }
}