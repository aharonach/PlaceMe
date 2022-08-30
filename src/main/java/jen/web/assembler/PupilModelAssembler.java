package jen.web.assembler;

import jen.web.controller.PupilRestController;
import jen.web.entity.Pupil;
import jen.web.util.PagesAndSortHandler;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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
                linkTo(methodOn(controller).getPupilGroups(entity.getId(), new PagesAndSortHandler.PaginationInfo())).withRel("pupil_groups"),
                linkTo(methodOn(controller).getAll(new PagesAndSortHandler.PaginationInfo())).withRel("pupils")
        );
    }

    @Override
    public CollectionModel<EntityModel<Pupil>> toCollectionModel(Iterable<? extends Pupil> entities) {
        throw new RuntimeException("use page instead");
    }

    public CollectionModel<EntityModel<Pupil>> toCollectionModelWithoutPages(Iterable<? extends Pupil> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(controller).getAll(new PagesAndSortHandler.PaginationInfo())).withSelfRel());
    }

    public CollectionModel<EntityModel<Pupil>> toPageCollection(Page<? extends Pupil> page) {
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(page.getSize(),page.getNumber(),page.getTotalElements(),page.getTotalPages());
        CollectionModel<EntityModel<Pupil>> collectionModel = RepresentationModelAssembler.super.toCollectionModel(page);
        return PagedModel.of(collectionModel.getContent(), pageMetadata)
                .add(linkTo(methodOn(controller).getAll(new PagesAndSortHandler.PaginationInfo())).withSelfRel());
    }
}