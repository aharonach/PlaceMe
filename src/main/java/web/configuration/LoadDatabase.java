package web.configuration;

import web.entity.*;
import web.service.GroupService;
import web.service.PlacementService;
import web.service.PupilService;
import web.service.TemplateService;
import web.util.CsvUtils;
import web.util.IsraeliIdValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Set;


@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class LoadDatabase {
    private static final Logger logger = LoggerFactory.getLogger(LoadDatabase.class);

    @NonNull TemplateService templateService;
    @NonNull PupilService pupilService;
    @NonNull GroupService groupService;
    @NonNull PlacementService placementService;

    @Value("${validate.israeli.id}")
    public Boolean ValidateId;

    @Bean
    CommandLineRunner initDatabase() {
        IsraeliIdValidator.validateId = ValidateId;

        return args -> {
            // config for alg
            createEngineConfig();

            createTemplates();
            createGroups();
            createPlacements();
        };
    }

    private void createEngineConfig() {
        placementService.updateGlobalConfig(new PlaceEngineConfig(1L));
        placementService.updateGlobalConfig(new PlaceEngineConfig(2L));
        placementService.updateGlobalConfig(new PlaceEngineConfig(3L));
    }

    private void createTemplates() throws Template.AttributeAlreadyExistException {
        logger.info("Preloading " + templateService.add(new Template("template", "template description", Set.of(
                new RangeAttribute("attr 1", "attr 1 for template", 25),
                new RangeAttribute("attr 2", "attr 2 for template", 17),
                new RangeAttribute("attr 3", "attr 3 for template", 10),
                new RangeAttribute("attr 4", "attr 4 for template", 20)
        ))));

        logger.info("Preloading " + templateService.add(new Template("Questionnaire", "Real use case", Set.of(
                new RangeAttribute("Behavior", "", 50),
                new RangeAttribute("Educational Index", "", 50),
                new RangeAttribute("Emotional Index", "", 50),
                new RangeAttribute("Sociability", "", 50),
                new RangeAttribute("Disability Benefits", "", 50)
        ))));
    }

    private void createGroups() {
        Template template1 = templateService.getOr404(1L);
        Template template2 = templateService.getOr404(2L);

        Group group1 = groupService.add(new Group("group 1", "group 1 desc", template1));
        logger.info("Preloading " + group1);

        Group group2 = groupService.add(new Group("group 2", "real based", template2));
        logger.info("Preloading " + group2);
    }

    private void createPlacements() throws FileNotFoundException, CsvUtils.CsvContent.CsvNotValidException, PlacementService.PlacementWithoutTemplateInGroupException, PlacementService.PlacementWithoutGroupException {
        Group group1 = groupService.getOr404(1L);
        String content1 = getFileContent("template1with166pupils.csv"); // placement1 // template1with166pupils
        Placement placement1 = placementService.add(new Placement("placement 1", 4, group1));
        System.out.println(placementService.importDataFromCsv(placement1, content1).getErrors());
        logger.info("Preloading " + placement1);

        Group group2 = groupService.getOr404(2L);
        Placement placement2 = placementService.add(new Placement("real based", 3, group2));
        logger.info("Preloading " + placement2);
    }

    private String getFileContent(String fileName) throws FileNotFoundException {
        File placementCsv1 = Paths.get("src", "main", "java", "web", "configuration", "csv", fileName).toFile();

        try(Scanner myReader = new Scanner(placementCsv1)){
            StringBuilder fileContent = new StringBuilder();
            while (myReader.hasNextLine()) {
                fileContent.append(myReader.nextLine()).append("\n");
            }
            return fileContent.toString().trim();
        }
    }
}