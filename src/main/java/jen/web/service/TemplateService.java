package jen.web.service;

import jen.web.entity.Attribute;
import jen.web.entity.Template;
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

    private final TemplateRepository repository;

    @Override
    public Template add(Template template) {
        // todo: validate that id dont exists
        // @TODO verify that all attributes are new (without ids) and other template fields
        return repository.save(template);
    }

    @Override
    public Template getOr404(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFound("Could not find template " + id));
    }

    @Override
    public List<Template> all() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Template updateById(Long id, Template newTemplate) {
        Template template = getOr404(id);

        template.setName(newTemplate.getName());
        template.setDescription(newTemplate.getDescription());
        template.updateAttributes(newTemplate.getAttributes());

        return repository.save(template);
    }

    @Override
    public void deleteById(Long id) {
        Template template = getOr404(id);
        repository.delete(template);
    }

    // handle attributes
    public Template deleteAttributeForTemplateById(Long templateId, Long attributeId){
        Template template = getOr404(templateId);
        template.deleteAttribute(attributeId);
        return repository.save(template);
    }

    public Template updateAttributeForTemplateById(Long templateId, Long attributeId, Attribute newAttribute){
        Template template = getOr404(templateId);
        template.updateAttribute(attributeId, newAttribute);
        return repository.save(template);
    }

    public Template addAttributeForTemplateById(Long templateId, Attribute newAttribute){
        Template template = getOr404(templateId);
        template.addAttribute(newAttribute);
        return repository.save(template);
    }
}
