package jen.example.configuration;

import jen.example.hibernate.entity.Group;
import jen.example.hibernate.entity.Pupil;
import jen.example.hibernate.entity.RangeAttribute;
import jen.example.hibernate.entity.Template;
import jen.example.hibernate.repository.GroupRepository;
import jen.example.hibernate.repository.TemplateRepository;
import jen.example.hibernate.service.GroupService;
import jen.example.hibernate.service.PupilService;
import jen.example.hibernate.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.Arrays;


@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class LoadDatabase {
    private static final Logger logger = LoggerFactory.getLogger(LoadDatabase.class);

    private final TemplateService templateService;
    private final PupilService pupilService;
    private final GroupService groupService;
    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            // add templates
            createTemplates();
            logger.info("Templates: " + templateService.all());

            // add pupils
            createPupils();
            logger.info("Pupils: " + pupilService.all());

            // add groups
            createGroups();
            logger.info("Groups: " + groupService.all());
            //logger.info("Groups: " + groupService.all().get(0).getPupils());
        };
    }

    private void createTemplates(){
        logger.info("Preloading " + templateService.add(new Template("template 1", "template 1 desc")));

        logger.info("Preloading " + templateService.add(new Template("template 2", "template 2 desc", Arrays.asList(
                new RangeAttribute("attr 1", "attr 1 for template 2", 10),
                new RangeAttribute("attr 2", "attr 2 for template 2", 20)
        ))));

        logger.info("Preloading " + templateService.add(new Template("template 3", "template 3 desc", Arrays.asList(
                new RangeAttribute("attr_1", "attr 1 for template 3", 45),
                new RangeAttribute("attr_2", "attr 2 for template 3", 24)
        ))));
    }

    private void createPupils(){
        logger.info("Preloading " + pupilService.add(
                new Pupil("01234", "Gal", "Yeshua", Pupil.Gender.MALE, LocalDate.now())
        ));
    }

    private void createGroups(){
        Template template = templateService.getOr404(2L);
        Pupil pupil = pupilService.getOr404(1L);

        Group group = new Group("group 1", "group 1 desc", template);
        group.addPupil(pupil);
        logger.info("Preloading " + groupService.add(group));
    }
}
