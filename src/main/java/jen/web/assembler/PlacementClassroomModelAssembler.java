package jen.web.assembler;

import jen.web.entity.PlacementClassroom;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;


@Component
public class PlacementClassroomModelAssembler implements RepresentationModelAssembler<PlacementClassroom, EntityModel<PlacementClassroom>> {
    @Override
    public EntityModel<PlacementClassroom> toModel(PlacementClassroom entity) {
        return EntityModel.of(entity);
    }

    @Override
    public CollectionModel<EntityModel<PlacementClassroom>> toCollectionModel(Iterable<? extends PlacementClassroom> entities) {
        throw new RuntimeException("use Page instead");
    }

    public CollectionModel<EntityModel<PlacementClassroom>> toCollectionModelWithoutPages(Iterable<? extends PlacementClassroom> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }

    public CollectionModel<EntityModel<PlacementClassroom>> toPageCollection(Page<? extends PlacementClassroom> page) {
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(page.getSize(),page.getNumber(),page.getTotalElements(),page.getTotalPages());
        CollectionModel<EntityModel<PlacementClassroom>> collectionModel = RepresentationModelAssembler.super.toCollectionModel(page);
        return PagedModel.of(collectionModel.getContent(), pageMetadata);
    }
}
