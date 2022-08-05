package jen.web.assembler;

import jen.web.controller.PlacementRestController;
import jen.web.entity.PlacementResult;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PlacementResultModelAssembler implements RepresentationModelAssembler<PlacementResult, EntityModel<PlacementResult>>{

    Class<PlacementRestController> controller = PlacementRestController.class;

    @Override
    public EntityModel<PlacementResult> toModel(PlacementResult entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(controller).getResult(entity.getPlacement().getId(), entity.getId())).withSelfRel(),
                linkTo(methodOn(controller).getResults(entity.getPlacement().getId())).withRel("placement_results"),
                linkTo(methodOn(controller).get(entity.getPlacement().getId())).withRel("placement")
        );
    }

    @Override
    public CollectionModel<EntityModel<PlacementResult>> toCollectionModel(Iterable<? extends PlacementResult> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}