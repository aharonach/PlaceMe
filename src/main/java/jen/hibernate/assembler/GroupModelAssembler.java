package jen.hibernate.assembler;

import jen.hibernate.controller.GroupRestController;
import jen.hibernate.entity.Group;
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
                linkTo(methodOn(controller).getPupilsOfGroup(entity.getId())).withRel("group_pupils"),
                linkTo(methodOn(controller).getGroupTemplate(entity.getId())).withRel("group_template"),
                linkTo(methodOn(controller).getAll()).withRel("groups")
        );
    }

    @Override
    public CollectionModel<EntityModel<Group>> toCollectionModel(Iterable<? extends Group> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(controller).getAll()).withSelfRel());
    }
}