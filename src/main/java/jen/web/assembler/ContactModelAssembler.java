package jen.web.assembler;

import jen.web.controller.PupilRestController;
import jen.web.entity.Contact;
import jen.web.entity.Group;
import jen.web.entity.Pupil;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ContactModelAssembler implements RepresentationModelAssembler<Contact, EntityModel<Contact>> {

    Class<PupilRestController> controller = PupilRestController.class;

    @Override
    public EntityModel<Contact> toModel(Contact entity) {
        return null;

    }

    @Override
    public CollectionModel<EntityModel<Contact>> toCollectionModel(Iterable<? extends Contact> entities) {
        return null;
    }
}
