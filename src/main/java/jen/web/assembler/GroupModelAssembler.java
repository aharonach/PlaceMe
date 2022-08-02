package jen.web.assembler;

import jen.web.controller.GroupRestController;
import jen.web.controller.TemplateRestController;
import jen.web.entity.Group;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GroupModelAssembler implements RepresentationModelAssembler<Group, EntityModel<Group>>  {
    Class<GroupRestController> groupController = GroupRestController.class;
    Class<TemplateRestController> templateController = TemplateRestController.class;

    @Override
    public EntityModel<Group> toModel(Group entity) {
        EntityModel<Group> entityModel = EntityModel.of(entity,
                linkTo(methodOn(groupController).get(entity.getId())).withSelfRel(),
                linkTo(methodOn(groupController).getPupilsOfGroup(entity.getId())).withRel("group_pupils"),
                linkTo(methodOn(groupController).getPreferences(entity.getId())).withRel("preferences")
        );

        if(entity.getTemplate() != null){
            entityModel.add(linkTo(methodOn(templateController).get(entity.getTemplateId())).withRel("group_template"));
        }

        entityModel.add(linkTo(methodOn(groupController).getAll()).withRel("groups"));

        return entityModel;
    }

    @Override
    public CollectionModel<EntityModel<Group>> toCollectionModel(Iterable<? extends Group> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(groupController).getAll()).withSelfRel());
    }
}