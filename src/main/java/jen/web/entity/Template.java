package jen.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "templates")
public class Template extends BaseEntity {
    private String name;
    private String description;
    @Setter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Attribute> attributes = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Group> groups = new HashSet<>();

    public Template(String name, String description){
        this.name = name;
        this.description = description;
    }

    public Template(String name, String description, Set<Attribute> attributes){
        this.name = name;
        this.description = description;
        this.attributes = attributes;
    }

    public Set<Group> getGroups() {
        return Collections.unmodifiableSet(groups);
    }

    public void clearGroups(){
        groups.clear();
    }

    public void addGroup(Group group){
        groups.add(group);
    }

    @JsonIgnore
    public Set<Long> getGroupIds(){
        return this.groups.stream().map(BaseEntity::getId).collect(Collectors.toSet());
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

    public Set<Attribute> getAttributes(){
        return Collections.unmodifiableSet(attributes);
    }

    public Attribute getAttribute(Long attributeId) throws AttributeNotBelongException {
        Optional<Attribute> attribute = attributes.stream().filter(attr -> attr.getId().equals(attributeId)).findFirst();

        if(attribute.isEmpty()){
            throw new AttributeNotBelongException(attributeId);
        }

        return attribute.get();
    }

    public boolean verifyAttributeBelongsToTemplate(Long attributeId) throws AttributeNotBelongException {
        getAttribute(attributeId);
        return true;
    }

    public void updateAttributes(Set<Attribute> newAttributes){
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

    public static class AttributeNotBelongException extends Exception {
        public AttributeNotBelongException(Long attributeId){
            super("Template does not contain attribute with id: " + attributeId);
        }
    }
}