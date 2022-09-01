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
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Attribute> attributes = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "template")
    private Set<Group> groups = new LinkedHashSet<>();

    public Template(String name, String description){
        this.name = name;
        this.description = description;
    }

    public Template(String name, String description, Set<Attribute> attributes){
        this.name = name;
        this.description = description;
        this.attributes.addAll(attributes);
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

    public void addAttribute(Attribute attribute){
        this.attributes.add(attribute);
    }

    public void deleteAttribute(Long attributeId){
        this.attributes.stream()
                .filter(attribute -> attribute.getId().equals(attributeId))
                .findFirst()
                .ifPresent(attribute -> attributes.remove(attribute));
    }

    public void updateAttribute(Long attributeId, Attribute newAttribute){
        this.attributes.stream()
                .filter(attribute -> attribute.getId().equals(attributeId))
                .findFirst()
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