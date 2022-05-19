package jen.example.hibernate.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "templates")
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Setter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Attribute> attributes = new ArrayList<>();

    public Template(String name, String description){
        this.name = name;
        this.description = description;
    }

    public Template(String name, String description, List<Attribute> attributes){
        this.name = name;
        this.description = description;
        this.attributes = attributes;
    }

//    public static EntityModel<Template> toModel(Template entity) {
//        return EntityModel.of(entity,
//                linkTo(methodOn(TemplateController.class).one(entity.getId())).withSelfRel(),
//                linkTo(methodOn(TemplateController.class).all()).withRel("templates")
//        );
//    }

    public void addAttribute(Attribute attribute){
        attributes.add(attribute);
    }

    public void deleteAttribute(Long attributeId){
        attributes.stream()
                .filter(attribute -> attribute.getId().equals(attributeId))
                .findFirst()
                .ifPresent(attribute -> attributes.remove(attribute));
    }

    public void updateAttribute(Long attributeId, Attribute newAttribute){
        attributes.stream()
                .filter(attribute -> attribute.getId().equals(attributeId))
                .findFirst()
                .ifPresent(attribute -> {
                    attribute.setName(newAttribute.getName());
                    attribute.setDescription(newAttribute.getDescription());
                    attribute.setPriority(newAttribute.getPriority());
                });
    }

    public void updateAttributes(List<Attribute> newAttributes){

        List<Long> newAttributeIds = newAttributes.stream().filter(attribute -> attribute.getId() != null).map(Attribute::getId).toList();
        List<Attribute> attributesToDelete = getAttributes().stream().filter(attribute -> !newAttributeIds.contains(attribute.getId())).toList();
        getAttributes().removeAll(attributesToDelete);

        newAttributes.forEach(attribute -> {
            if(attribute.getId() == null){
                addAttribute(attribute);
            } else {
                updateAttribute(attribute.getId(), attribute);
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Template template = (Template) o;
        return id != null && Objects.equals(id, template.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}