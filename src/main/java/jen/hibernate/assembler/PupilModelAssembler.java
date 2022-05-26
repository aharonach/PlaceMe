package jen.hibernate.assembler;

import jen.hibernate.controller.PupilRestController;
import jen.hibernate.entity.Pupil;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PupilModelAssembler implements RepresentationModelAssembler<Pupil, EntityModel<Pupil>> {
    Class<PupilRestController> controller = PupilRestController.class;

    @Override
    public EntityModel<Pupil> toModel(Pupil entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(controller).get(entity.getId())).withSelfRel(),
                linkTo(methodOn(controller).getPupilGroups(entity.getId())).withRel("pupil_groups"),
                linkTo(methodOn(controller).getAll()).withRel("pupils")
        );
    }

    @Override
    public CollectionModel<EntityModel<Pupil>> toCollectionModel(Iterable<? extends Pupil> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(controller).getAll()).withSelfRel());
    }
}