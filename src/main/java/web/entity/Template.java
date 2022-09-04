package web.entity;

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
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Attribute> attributes = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "template")
    private Set<Group> groups = new LinkedHashSet<>();

    public Template(String name, String description){
        this.name = name;
        this.description = description;
    }

    public Template(String name, String description, Set<Attribute> attributes) throws AttributeAlreadyExistException {
        this.name = name;
        this.description = description;
        for(Attribute attribute : attributes){
            addAttribute(attribute);
        }
    }

    public Integer getNumberOfAttributes(){
        return attributes.size();
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

    public void removeGroup(Group group){
        groups.remove(group);
    }

    @JsonIgnore
    public Set<Long> getGroupIds(){
        return this.groups.stream().map(BaseEntity::getId).collect(Collectors.toSet());
    }

    public void addAttribute(Attribute attribute) throws AttributeAlreadyExistException {
        if(getAttribute(attribute.getName()).isPresent()){
            throw new AttributeAlreadyExistException(attribute.getName());
        }
        this.attributes.add(attribute);
    }

    public void deleteAttribute(Long attributeId){
        this.attributes.stream()
                .filter(attribute -> attribute.getId().equals(attributeId))
                .findFirst()
                .ifPresent(attribute -> attributes.remove(attribute));
    }

    public void updateAttribute(Long attributeId, Attribute newAttribute) throws AttributeAlreadyExistException {
        if(getAttribute(newAttribute.getName()).isPresent()){
            throw new AttributeAlreadyExistException(newAttribute.getName());
        }
        getAttribute(attributeId)
                .ifPresent(attribute -> {
                    attribute.setName(newAttribute.getName());
                    attribute.setDescription(newAttribute.getDescription());
                    attribute.setPriority(newAttribute.getPriority());
                });
    }

    public Set<Attribute> getAttributes(){
        return attributes == null ? null : Collections.unmodifiableSet(attributes);
    }

    public Set<Long> getAttributeIds(){
        return attributes.stream().map(BaseEntity::getId).collect(Collectors.toSet());
    }

    public Optional<Attribute> getAttribute(Long attributeId) {
        return attributes.stream().filter(attr -> attr.getId().equals(attributeId)).findFirst();
    }

    public Optional<Attribute> getAttribute(String name) {
        return attributes.stream().filter(attr -> attr.getName().equals(name)).findFirst();
    }

    public void verifyAttributeBelongsToTemplate(Long attributeId) throws AttributeNotBelongException {
        if(getAttribute(attributeId).isEmpty()){
            throw new AttributeNotBelongException(attributeId);
        }
    }

    public void updateAttributes(Set<Attribute> newAttributes) throws AttributeAlreadyExistException {
        for(Attribute attribute : newAttributes){
            if(attribute.getId() == null){
                addAttribute(attribute);
            } else {
                updateAttribute(attribute.getId(), attribute);
            }
        }
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

        public AttributeNotBelongException(String name){
            super("Template does not contain attribute with name: " + name);
        }
    }

    public static class AttributeAlreadyExistException extends Exception {
        public AttributeAlreadyExistException(String name){
            super("Attribute with the name '" + name + "' already exist. template can't contain attributes with the same name");
        }
    }
}