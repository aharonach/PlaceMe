package jen.web.assembler;

import jen.web.entity.PlacementResult;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class PlacementResultModelAssembler implements RepresentationModelAssembler<PlacementResult, EntityModel<PlacementResult>>{
    //Class<PlacementRestController> controller = PlacementRestController.class;

    @Override
    public EntityModel<PlacementResult> toModel(PlacementResult entity) {
//        return EntityModel.of(entity,
//                linkTo(methodOn(controller).get(entity.getId())).withSelfRel(),
//                linkTo(methodOn(controller).getAll()).withRel("placements"),
//                linkTo(methodOn(controller).getResults(entity.getId())).withRel("placements")
//        );
        return null;
    }

    @Override
    public CollectionModel<EntityModel<PlacementResult>> toCollectionModel(Iterable<? extends PlacementResult> entities) {
                //RepresentationModelAssembler.super.toCollectionModel(entities)
                //.add(linkTo(methodOn(controller).getResults()).withSelfRel());
        return null;
    }
}