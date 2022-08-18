package jen.web.assembler;

import jen.web.entity.PlacementClassroom;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
