package jen.example.hibernate.assembler;

import jen.example.hibernate.controller.GroupRestController;
import jen.example.hibernate.entity.Group;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GroupModelAssembler implements RepresentationModelAssembler<Group, EntityModel<Group>>  {
    Class<GroupRestController> controller = GroupRestController.class;

    @Override
    public EntityModel<Group> toModel(Group entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(controller).get(entity.getId())).withSelfRel(),
                linkTo(methodOn(controller).getAll()).withRel("groups")
        );
    }

    @Override
    public CollectionModel<EntityModel<Group>> toCollectionModel(Iterable<? extends Group> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(controller).getAll()).withSelfRel());
    }
}