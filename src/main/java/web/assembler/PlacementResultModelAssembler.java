package web.assembler;

import web.controller.PlacementRestController;
import web.entity.PlacementResult;
import web.util.PagesAndSortHandler;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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
                linkTo(methodOn(controller).getResults(entity.getPlacement().getId(), new PagesAndSortHandler.PaginationInfo())).withRel("placement_results"),
                linkTo(methodOn(controller).get(entity.getPlacement().getId())).withRel("placement")
        );
    }

    @Override
    public CollectionModel<EntityModel<PlacementResult>> toCollectionModel(Iterable<? extends PlacementResult> entities) {
        throw new RuntimeException("use Page instead");
    }

    public CollectionModel<EntityModel<PlacementResult>> toCollectionModelWithoutPages(Iterable<? extends PlacementResult> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }

    public CollectionModel<EntityModel<PlacementResult>> toPageCollection(Page<? extends PlacementResult> page) {
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(page.getSize(),page.getNumber(),page.getTotalElements(),page.getTotalPages());
        CollectionModel<EntityModel<PlacementResult>> collectionModel = RepresentationModelAssembler.super.toCollectionModel(page);
        return PagedModel.of(collectionModel.getContent(), pageMetadata);
    }
}