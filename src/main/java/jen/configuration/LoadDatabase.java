package jen.configuration;

import jen.hibernate.entity.*;
import jen.hibernate.service.GroupService;
import jen.hibernate.service.PlacementService;
import jen.hibernate.service.PupilService;
import jen.hibernate.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class LoadDatabase {
    private static final Logger logger = LoggerFactory.getLogger(LoadDatabase.class);

    private final TemplateService templateService;
    private final PupilService pupilService;
    private final GroupService groupService;
    private final PlacementService placementService;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            // add
            createTemplates();
            createPupils();
            createGroups();
            createAttributeValues();
            createPlacements();

            // print
            System.out.println("Pupils:");
            pupilService.all().forEach(pupil -> {
                System.out.println(pupil);
                System.out.println(pupil.getAttributeValues());
            });

            System.out.println("Templates:");
            templateService.all().forEach(System.out::println);

            System.out.println("Groups:");
            groupService.all().forEach(System.out::println);

            System.out.println("Placements:");
            placementService.all().forEach(placement -> {
                System.out.println(placement);
                System.out.println(placement.getGroup());
            });
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
                new Pupil("204054845", "Gal", "Yeshua", Pupil.Gender.MALE, LocalDate.of(1992, 7, 28))
        ));

        logger.info("Preloading " + pupilService.add(
                new Pupil("308338318", "Aharon", "Achildiev", Pupil.Gender.MALE, LocalDate.of(1993, 2, 28))
        ));

        logger.info("Preloading " + pupilService.add(
                new Pupil("307944710", "Shir", "Halfon", Pupil.Gender.FEMALE, LocalDate.of(1993, 4, 6))
        ));
    }

    private void createGroups(){
        Template template = templateService.getOr404(2L);

        Group group = groupService.add(new Group("group 1", "group 1 desc", template));
        pupilService.all().forEach(group::addPupil);

        logger.info("Preloading " + groupService.add(group));
    }

    private void createAttributeValues(){
        Group group = groupService.getOr404(1L);
        Template template = group.getTemplate();
        Pupil pupil = pupilService.getOr404(1L);

        Map<Long, Double> attributeValues = new HashMap<>(template.getAttributes().size());

        template.getAttributes().forEach(attribute -> attributeValues.put(attribute.getId(), 4D));

        pupilService.addAttributeValues(pupil, group, attributeValues);
    }

    private void createPlacements(){
        Group group = groupService.getOr404(1L);
        logger.info("Preloading " + placementService.add(new Placement("placement 1", 3, group)));
    }
}
