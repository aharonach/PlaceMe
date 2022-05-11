package jen.example.hibernate.service;

import jen.example.hibernate.entity.Template;

import java.util.List;

public interface TemplateService {
    Template save(Template template);

    Template fetch(Long id);

    List<Template> fetchAll();
}
