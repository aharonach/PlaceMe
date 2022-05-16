package jen.example.hibernate.service;

import jen.LoadDatabase;
import jen.example.hibernate.entity.Attribute;
import jen.example.hibernate.entity.Template;
import jen.example.hibernate.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateService implements EntityService<Template> {

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);

    private final TemplateRepository repository;

    @Override
    public Template add(Template template) {
        // verify that all attributes are new (without ids) and other template fields
        return repository.save(template);
    }

    @Override
    public Template getOr404(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFound(id));
    }

    @Override
    public List<Template> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Template updateById(Long id, Template item) {
        Template template = getOr404(id);

        template.setName(item.getName());
        template.setDescription(item.getDescription());

        List<Long> newAttributeIds = item.getAttributes().stream().filter(attribute -> attribute.getId() != null).map(Attribute::getId).collect(Collectors.toList());
        List<Attribute> toDelete = template.getAttributes().stream().filter(attribute -> !newAttributeIds.contains(attribute.getId())).collect(Collectors.toList());
        template.getAttributes().removeAll(toDelete);

        item.getAttributes().forEach(attribute -> {
            if(attribute.getId() == null){
                template.addAttribute(attribute);
            } else {
                template.updateAttribute(attribute.getId(), attribute);
            }
        });

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

    // handle exceptions
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class NotFound extends RuntimeException{
        public NotFound(Long id){
            super("Could not find template " + id);
        }
    }
}
