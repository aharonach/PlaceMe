package jen.web.configuration;

import jen.web.entity.*;
import jen.web.service.GroupService;
import jen.web.service.PlacementService;
import jen.web.service.PupilService;
import jen.web.service.TemplateService;
import jen.web.util.CsvUtils;
import jen.web.util.IsraeliIdValidator;
import jen.web.util.PagesAndSortHandler;
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
import java.time.LocalDate;
import java.util.List;
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

            // create template, group, placement and import data
            createTemplates();
            createGroups();
            createPlacements(); // create placement and import data from csv
            createPlacementResult();

//            // print
//            System.out.println("Pupils:");
//            pupilService.allWithoutPages().forEach(pupil -> {
//                System.out.println(pupil);
//                System.out.println(pupil.getAttributeValues());
//            });
//
//            System.out.println("Templates:");
//            templateService.allWithoutPages().forEach(System.out::println);
//
//            System.out.println("Groups:");
//            groupService.allWithoutPages().forEach(System.out::println);
//
//            System.out.println("Placements:");
//            placementService.allWithoutPages().forEach(placement -> {
//                System.out.println(placement);
//                System.out.println(placement.getGroup());
//                System.out.println(placement.getGroup().getPupils());
//            });
//
//            System.out.println("Prefs:");
//            System.out.println(groupService.allWithoutPages().get(0).getPreferences());
//
//            System.out.println("Result:");
//            PlacementResult placementResult = placementService.getOr404(1L).getResultById(1L);
//            System.out.println(placementResult.getPlacementScore());
//            System.out.println(placementResult);
//            placementResult.getClasses().forEach(classInfo -> {
//                System.out.print("[Pupils: " + classInfo.getNumOfPupils() + " (Males: " + classInfo.getNumOfPupilsByGender(Pupil.Gender.MALE) + " ,Females: " + classInfo.getNumOfPupilsByGender(Pupil.Gender.FEMALE) + " ,Delta: " + classInfo.getDeltaBetweenMalesAndFemales() + ") ");
//                System.out.print("Pupils Score: " + classInfo.getSumScoreOfPupils() + " Class Score: " + classInfo.getClassScore() + " ");
//                System.out.print("Include is OK: " + (classInfo.getNumberOfWrongConnectionsToInclude() == 0) + ", ");
//                System.out.print("Exclude is OK: " + (classInfo.getNumberOfWrongConnectionsToExclude() == 0));
//                System.out.print("] | ");
//                System.out.println(classInfo.getPupils());
//            });
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

    private void createPlacements() throws FileNotFoundException, CsvUtils.CsvContent.CsvNotValidException {
        Group group = groupService.getOr404(1L);
        String content = getFileContent("placement1.csv");

        Placement placement1 = placementService.add(new Placement("placement 1", 4, group));
        placementService.importDataFromCsv(placement1, content);
        logger.info("Preloading " + placement1);

        Placement placement2 = placementService.add(new Placement("placement 2", 6, group));
        //placementService.importDataFromCsv(placement2, content);
        logger.info("Preloading " + placement2);

        Placement placement3 = placementService.add(new Placement("placement 3", 8, group));
        //placementService.importDataFromCsv(placement3, content);
        logger.info("Preloading " + placement3);
    }

    private void createPlacementResult() throws PlacementService.PlacementWithoutGroupException, PlacementService.PlacementWithoutPupilsInGroupException, PagesAndSortHandler.FieldNotSortableException {
        List<Placement> placements = placementService.allWithoutPages();
        placementService.generatePlacementResult(placements.get(0), "name", "test 4 classes");
        placementService.generatePlacementResult(placements.get(1), "name", "test 6 classes");
        placementService.generatePlacementResult(placements.get(2), "name", "test 8 classes");
    }

    private String getFileContent(String fileName) throws FileNotFoundException {
        File placementCsv1 = Paths.get("src", "main", "java", "jen", "web", "configuration", "csv", fileName).toFile();
        Scanner myReader = new Scanner(placementCsv1);
        StringBuilder fileContent = new StringBuilder();
        while (myReader.hasNextLine()) {
            fileContent.append(myReader.nextLine()).append("\n");
        }
        return fileContent.toString();
    }
}