package jen.web.service;

import jen.web.entity.Attribute;
import jen.web.entity.BaseEntity;
import jen.web.entity.Group;
import jen.web.entity.Template;
import jen.web.exception.EntityAlreadyExists;
import jen.web.exception.NotFound;
import jen.web.repository.AttributeRepository;
import jen.web.repository.AttributeValueRepository;
import jen.web.repository.GroupRepository;
import jen.web.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateService implements EntityService<Template> {

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);

    private final TemplateRepository templateRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;

    @Override
    public Template add(Template template) {
        Long id = template.getId();
        if (id != null && templateRepository.existsById(id)) {
            throw new EntityAlreadyExists("Template with Id '" + id + "' already exists.");
        }

        template.clearGroups();
        return templateRepository.save(template);
    }

    @Override
    public Template getOr404(Long id) {
        return templateRepository.findById(id).orElseThrow(() -> new NotFound("Could not find template " + id));
    }

    public Attribute getAttributeOr404(Long id) {
        return attributeRepository.findById(id).orElseThrow(() -> new NotFound("Could not find attribute " + id));
    }

    @Override
    public List<Template> all() {
        return templateRepository.findAll();
    }

    @Override
    @Transactional
    // its updates attrs with id, add attrs without id and delete attrs that not in the new template
    public Template updateById(Long id, Template newTemplate) {
        Template template = getOr404(id);

        List<Long> newAttributeIds = newTemplate.getAttributes().stream().map(Attribute::getId).filter(Objects::nonNull).toList();
        List<Attribute> attributesToDelete = template.getAttributes().stream().filter(attribute -> !newAttributeIds.contains(attribute.getId())).toList();

        for(Attribute attribute : attributesToDelete){
            try {
                deleteAttributeForTemplateById(template, attribute);
            } catch (Template.AttributeNotBelongException ignored) {
            }
        }

        template.updateAttributes(newTemplate.getAttributes());
        template.setName(newTemplate.getName());
        template.setDescription(newTemplate.getDescription());

        return templateRepository.save(template);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Template template = getOr404(id);

        template.getAttributes().forEach(attribute -> {
            attributeValueRepository.deleteAttributeValuesByAttributeId(attribute.getId());
        });
        template.getGroups().forEach(group -> {
            group.setTemplate(null);
            template.getGroups().remove(group);
        });

        attributeRepository.deleteAll(template.getAttributes());
        templateRepository.delete(template);
    }

    @Transactional
    public void deleteAttributeForTemplateById(Template template, Attribute attribute) throws Template.AttributeNotBelongException {
        template.verifyAttributeBelongsToTemplate(attribute.getId());
        attributeValueRepository.deleteAttributeValuesByAttributeId(attribute.getId());
        template.deleteAttribute(attribute.getId());
        attributeRepository.delete(attribute);
    }

    public Template updateAttributeForTemplateById(Template template, Attribute oldAttribute, Attribute newAttribute) throws Template.AttributeNotBelongException {
        template.verifyAttributeBelongsToTemplate(oldAttribute.getId());
        template.updateAttribute(oldAttribute.getId(), newAttribute);
        return templateRepository.save(template);
    }

    public Template addAttributeForTemplateById(Template template, Attribute newAttribute){
        template.addAttribute(newAttribute);
        return templateRepository.save(template);
    }
}
