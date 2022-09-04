package jen.web.configuration;

import jen.web.entity.*;
import jen.web.service.GroupService;
import jen.web.service.PlacementService;
import jen.web.service.PupilService;
import jen.web.service.TemplateService;
import jen.web.util.CsvUtils;
import jen.web.util.IsraeliIdValidator;
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
        PlaceEngineConfig placeEngineConfig = new PlaceEngineConfig();
        placementService.updateGlobalConfig(placeEngineConfig);
    }

    private void createTemplates() throws Template.AttributeAlreadyExistException {
        logger.info("Preloading " + templateService.add(new Template("template", "template description", Set.of(
                new RangeAttribute("attr 1", "attr 1 for template", 25),
                new RangeAttribute("attr 2", "attr 2 for template", 17),
                new RangeAttribute("attr 3", "attr 3 for template", 10),
                new RangeAttribute("attr 4", "attr 4 for template", 20)
        ))));
    }

    private void createGroups() {
        Template template = templateService.getOr404(1L);

        Group group1 = groupService.add(new Group("group 1", "group 1 desc", template));
        logger.info("Preloading " + group1);
    }

    private void createPlacements() throws FileNotFoundException, CsvUtils.CsvContent.CsvNotValidException, PlacementService.PlacementWithoutTemplateInGroupException, PlacementService.PlacementWithoutGroupException {
        Group group1 = groupService.getOr404(1L);
        String content1 = getFileContent("template1with166pupils.csv"); // placement1 // template1with166pupils
        Placement placement1 = placementService.add(new Placement("placement 1", 4, group1));
        System.out.println(placementService.importDataFromCsv(placement1, content1).getErrors());
        logger.info("Preloading " + placement1);
    }

    private String getFileContent(String fileName) throws FileNotFoundException {
        File placementCsv1 = Paths.get("src", "main", "java", "jen", "web", "configuration", "csv", fileName).toFile();

        try(Scanner myReader = new Scanner(placementCsv1)){
            StringBuilder fileContent = new StringBuilder();
            while (myReader.hasNextLine()) {
                fileContent.append(myReader.nextLine()).append("\n");
            }
            return fileContent.toString().trim();
        }
    }
}