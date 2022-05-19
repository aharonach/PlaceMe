package jen.example.configuration;

import jen.example.hibernate.entity.RangeAttribute;
import jen.example.hibernate.entity.Template;
import jen.example.hibernate.repository.TemplateRepository;
import jen.example.hibernate.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;


@Configuration
@Profile("!test")
public class LoadDatabase {
    private static final Logger logger = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(TemplateRepository repository, TemplateService service) {
        return args -> {

            // add templates
            logger.info("Preloading " + repository.save(new Template("template 1", "template 1 desc")));

            logger.info("Preloading " + repository.save(new Template("template 2", "template 2 desc", Arrays.asList(
                    new RangeAttribute("attr 1", "attr 1 for template 2", 10),
                    new RangeAttribute("attr 2", "attr 2 for template 2", 20)
            ))));

            logger.info("Preloading " + repository.save(new Template("template 3", "template 3 desc", Arrays.asList(
                    new RangeAttribute("attr_1", "attr 1 for template 3", 45),
                    new RangeAttribute("attr_2", "attr 2 for template 3", 24)
            ))));

            logger.info("Preloading " + repository.findAll());
        };
    }
}
