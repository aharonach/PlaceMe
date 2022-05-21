package jen.example.hibernate.assembler;

import jen.example.hibernate.controller.PupilRestController;
import jen.example.hibernate.dto.PupilDto;
import jen.example.hibernate.entity.Pupil;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PupilModelAssembler implements RepresentationModelAssembler<Pupil, EntityModel<PupilDto>> {
    Class<PupilRestController> controller = PupilRestController.class;

    @Override
    public EntityModel<PupilDto> toModel(Pupil entity) {
        return EntityModel.of(new PupilDto(entity),
                linkTo(methodOn(controller).get(entity.getId())).withSelfRel(),
                linkTo(methodOn(controller).getAll()).withRel("pupils")
        );
    }

    @Override
    public CollectionModel<EntityModel<PupilDto>> toCollectionModel(Iterable<? extends Pupil> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(controller).getAll()).withSelfRel());
    }
}