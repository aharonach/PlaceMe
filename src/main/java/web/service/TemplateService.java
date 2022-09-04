package web.service;

import web.entity.Attribute;
import web.entity.Template;
import web.exception.EntityAlreadyExists;
import web.exception.NotFound;
import web.repository.AttributeRepository;
import web.repository.AttributeValueRepository;
import web.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TemplateService implements EntityService<Template> {

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);

    private final TemplateRepository templateRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;


    @Override
    public Template add(Template template) throws Template.AttributeAlreadyExistException {
        Long id = template.getId();
        if (id != null && templateRepository.existsById(id)) {
            throw new EntityAlreadyExists("Template with Id '" + id + "' already exists.");
        }
        Template newTemplate = new Template(template.getName(), template.getDescription(), template.getAttributes());

        return templateRepository.save(newTemplate);
    }

    @Override
    public Template getOr404(Long id) {
        return templateRepository.findById(id).orElseThrow(() -> new NotFound("Could not find template " + id));
    }

    @Override
    public List<Template> allWithoutPages() {
        return templateRepository.findAll();
    }

    public Attribute getAttributeOr404(Long id) {
        return attributeRepository.findById(id).orElseThrow(() -> new NotFound("Could not find attribute " + id));
    }

    @Override
    public Page<Template> all(PageRequest pageRequest) {
        return templateRepository.findAll(pageRequest);
    }

    @Override
    @Transactional
    // its updates attrs with id, add attrs without id and delete attrs that not in the new template
    public Template updateById(Long id, Template newTemplate) throws Template.AttributeAlreadyExistException {
        Template template = getOr404(id);

        if(newTemplate.getAttributes() != null){
            List<Long> newAttributeIds = newTemplate.getAttributes().stream().map(Attribute::getId).filter(Objects::nonNull).toList();
            List<Attribute> attributesToDelete = template.getAttributes().stream().filter(attribute -> !newAttributeIds.contains(attribute.getId())).toList();

            for(Attribute attribute : attributesToDelete){
                try {
                    deleteAttributeForTemplate(template, attribute);
                } catch (Template.AttributeNotBelongException ignored) {
                }
            }
            template.updateAttributes(newTemplate.getAttributes());
        }

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
        new HashSet<>(template.getGroups()).forEach(group -> {
            group.setTemplate(null);
        });
        template.clearGroups();

        attributeRepository.deleteAll(template.getAttributes());
        templateRepository.delete(template);
    }

    @Transactional
    public void deleteAttributeForTemplate(Template template, Attribute attribute) throws Template.AttributeNotBelongException {
        template.verifyAttributeBelongsToTemplate(attribute.getId());
        attributeValueRepository.deleteAttributeValuesByAttributeId(attribute.getId());
        template.deleteAttribute(attribute.getId());
        attributeRepository.delete(attribute);
    }

    public Template updateAttributeForTemplate(Template template, Attribute oldAttribute, Attribute newAttribute) throws Template.AttributeNotBelongException, Template.AttributeAlreadyExistException {
        template.verifyAttributeBelongsToTemplate(oldAttribute.getId());
        template.updateAttribute(oldAttribute.getId(), newAttribute);
        return templateRepository.save(template);
    }

    public Template addAttributeForTemplate(Template template, Attribute newAttribute) throws Template.AttributeAlreadyExistException {
        template.addAttribute(newAttribute);
        return templateRepository.save(template);
    }
}
