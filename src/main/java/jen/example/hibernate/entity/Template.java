package jen.example.hibernate.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

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

    private LocalDateTime createdTime = LocalDateTime.now();
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

    public List<Attribute> getAttributes(){
        return Collections.unmodifiableList(attributes);
    }
    public void updateAttributes(List<Attribute> newAttributes){

        List<Long> newAttributeIds = newAttributes.stream().map(Attribute::getId).filter(Objects::nonNull).toList();
        List<Attribute> attributesToDelete = getAttributes().stream().filter(attribute -> !newAttributeIds.contains(attribute.getId())).toList();
        attributes.removeAll(attributesToDelete);

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