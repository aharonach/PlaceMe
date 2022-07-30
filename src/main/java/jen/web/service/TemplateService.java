package jen.web.service;

import jen.web.entity.Attribute;
import jen.web.entity.Template;
import jen.web.exception.EntityAlreadyExists;
import jen.web.exception.NotFound;
import jen.web.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService implements EntityService<Template> {

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);

    private final TemplateRepository templateRepository;

    @Override
    public Template add(Template template) {
        Long id = template.getId();
        if (id != null && templateRepository.existsById(id)) {
            throw new EntityAlreadyExists("Template with Id '" + id + "' already exists.");
        }

        return templateRepository.save(template);
    }

    @Override
    public Template getOr404(Long id) {
        return templateRepository.findById(id).orElseThrow(() -> new NotFound("Could not find template " + id));
    }

    @Override
    public List<Template> all() {
        return templateRepository.findAll();
    }

    @Override
    @Transactional
    public Template updateById(Long id, Template newTemplate) {
        Template template = getOr404(id);

        template.setName(newTemplate.getName());
        template.setDescription(newTemplate.getDescription());
        template.updateAttributes(newTemplate.getAttributes());

        return templateRepository.save(template);
    }

    @Override
    public void deleteById(Long id) {
        Template template = getOr404(id);
        templateRepository.delete(template);
    }

    // @todo: handle it (all the section)
    // handle attributes
    public Template deleteAttributeForTemplateById(Long templateId, Long attributeId){
        Template template = getOr404(templateId);
        template.deleteAttribute(attributeId);
        return templateRepository.save(template);
    }

    public Template updateAttributeForTemplateById(Long templateId, Long attributeId, Attribute newAttribute){
        Template template = getOr404(templateId);
        template.updateAttribute(attributeId, newAttribute);
        return templateRepository.save(template);
    }

    public Template addAttributeForTemplateById(Long templateId, Attribute newAttribute){
        Template template = getOr404(templateId);
        template.addAttribute(newAttribute);
        return templateRepository.save(template);
    }
}
