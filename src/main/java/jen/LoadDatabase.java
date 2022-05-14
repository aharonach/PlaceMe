package jen;

import jen.example.hibernate.entity.RangeAttribute;
import jen.example.hibernate.entity.Template;
import jen.example.hibernate.repository.TemplateRepository;
import jen.example.hibernate.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;


@Configuration
public class LoadDatabase {
    private static final Logger logger = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(TemplateRepository repository, TemplateService service) {
        return args -> {
            logger.info("Preloading " + repository.save(new Template("name", "desc")));
            logger.info("Preloading " + repository.save(new Template("name", "desc", Arrays.asList(new RangeAttribute()))));
            logger.info("Preloading " + repository.save(new Template("name", "desc", Arrays.asList(new RangeAttribute("dfs", "dfad", 4)))));

            logger.info("Preloading " + repository.findAll());
        };
    }
}
