package jen.example.hibernate.service;

import jen.example.hibernate.entity.Template;
import jen.example.hibernate.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService{

    private final TemplateRepository repository;

    @Override
    public Template save(Template template) {
        // verify
        return repository.save(template);
    }

    @Override
    public Template fetch(Long id) {
        return repository.getById(id);
    }

    @Override
    public List<Template> fetchAll() {
        return repository.findAll();
    }
}
