package jen.web.configuration;

import jen.web.entity.*;
import jen.web.service.GroupService;
import jen.web.service.PlacementService;
import jen.web.service.PupilService;
import jen.web.service.TemplateService;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
            // add
            createEngineConfig();
            createTemplates();
            createPupils();
            createGroups();
            createAttributeValues();
            createPlacements();
            addPreferences();
            createPlacementResult();


            // print
            System.out.println("Pupils:");
            pupilService.allWithoutPages().forEach(pupil -> {
                System.out.println(pupil);
                System.out.println(pupil.getAttributeValues());
            });

            System.out.println("Templates:");
            templateService.allWithoutPages().forEach(System.out::println);

            System.out.println("Groups:");
            groupService.allWithoutPages().forEach(System.out::println);

            System.out.println("Placements:");
            placementService.allWithoutPages().forEach(placement -> {
                System.out.println(placement);
                System.out.println(placement.getGroup());
                System.out.println(placement.getGroup().getPupils());
            });

            System.out.println("Prefs:");
            System.out.println(groupService.allWithoutPages().get(0).getPreferences());

            System.out.println("Result:");
            PlacementResult placementResult = placementService.getOr404(1L).getResultById(1L);
            System.out.println(placementResult.getPlacementScore());
            System.out.println(placementResult);
            placementResult.getClasses().forEach(classInfo -> {
                System.out.print("[Pupils: " + classInfo.getNumOfPupils() + " (Males: " + classInfo.getNumOfPupilsByGender(Pupil.Gender.MALE) + " ,Females: " + classInfo.getNumOfPupilsByGender(Pupil.Gender.FEMALE) + " ,Delta: " + classInfo.getDeltaBetweenMalesAndFemales() + ") ");
                System.out.print("Pupils Score: " + classInfo.getSumScoreOfPupils() + " Class Score: " + classInfo.getClassScore() + " ");
                System.out.print("Include is OK: " + (classInfo.getNumberOfWrongConnectionsToInclude() == 0) + ", ");
                System.out.print("Exclude is OK: " + (classInfo.getNumberOfWrongConnectionsToExclude() == 0));
                System.out.print("] | ");
                System.out.println(classInfo.getPupils());
            });
        };
    }

    private void createEngineConfig() {
        PlaceEngineConfig placeEngineConfig = new PlaceEngineConfig();
        placementService.updateGlobalConfig(placeEngineConfig);
    }

    private void createTemplates() throws Template.AttributeAlreadyExistException {
        logger.info("Preloading " + templateService.add(new Template("template 1", "template 1 desc")));

        logger.info("Preloading " + templateService.add(new Template("template 2", "template 2 desc", Set.of(
                new RangeAttribute("attr 1", "attr 1 for template 2", 10),
                new RangeAttribute("attr 2", "attr 2 for template 2", 20)
        ))));

        logger.info("Preloading " + templateService.add(new Template("template 3", "template 3 desc", Set.of(
                new RangeAttribute("attr_1", "attr 1 for template 3", 45),
                new RangeAttribute("attr_2", "attr 2 for template 3", 24)
        ))));
    }

    private void createPupils() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        logger.info("Preloading " + pupilService.add(
                new Pupil("204054845", "Gal", "Yeshua", Pupil.Gender.MALE, LocalDate.of(1992, 7, 28))
        ));

        logger.info("Preloading " + pupilService.add(
                new Pupil("308338318", "Aharon", "Achildiev", Pupil.Gender.MALE, LocalDate.of(1993, 2, 28))
        ));

        logger.info("Preloading " + pupilService.add(
                new Pupil("307944710", "Shir", "Halfon", Pupil.Gender.FEMALE, LocalDate.of(1993, 4, 6))
        ));

//        // add more pupils
//        for(int i=0; i< 9; i++){
//            logger.info("Preloading " + pupilService.add(
//                    new Pupil(String.valueOf(i), "name" + i, "sdfsdfas", Pupil.Gender.FEMALE, LocalDate.of(1990, 1, 1))
//            ));
//        }
//
//        for(int i=20; i< 34; i++){
//            logger.info("Preloading " + pupilService.add(
//                    new Pupil(String.valueOf(i), "name" + i, "sdfsdfas", Pupil.Gender.MALE, LocalDate.of(1990, 1, 1))
//            ));
//        }

    }

    private void createGroups() throws PagesAndSortHandler.FieldNotSortableException {
        Template template = templateService.getOr404(2L);

        Group group1 = groupService.add(new Group("group 1", "group 1 desc", template));
        pupilService.allWithoutPages().forEach(pupil -> groupService.linkPupilToGroup(group1, pupil));
        logger.info("Preloading " + group1);

        Group group2 = groupService.add(new Group("group 2", "group 2 desc", template));
        logger.info("Preloading " + group2);
    }

    private void createAttributeValues() throws PagesAndSortHandler.FieldNotSortableException {
        Group group = groupService.getOr404(1L);
        Template template = group.getTemplate();

        pupilService.allWithoutPages().forEach(pupil -> {
            Map<Long, Double> attributeValues = new HashMap<>(template.getAttributes().size());
            template.getAttributes().forEach(attribute -> attributeValues.put(attribute.getId(), 4D));
            try {
                pupilService.addOrUpdateAttributeValuesFromIdValueMap(pupil, group, attributeValues);
            } catch (Group.PupilNotBelongException | Template.AttributeNotBelongException |
                     AttributeValue.ValueOutOfRangeException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void createPlacements(){
        Group group = groupService.getOr404(1L);
        logger.info("Preloading " + placementService.add(new Placement("placement 1", 4, group)));
    }

    private void addPreferences() throws Preference.SamePupilException, Group.PupilNotBelongException, PagesAndSortHandler.FieldNotSortableException {
        Group group = groupService.allWithoutPages().get(0);
        Pupil pupil1 = pupilService.getOr404(1L);
        Pupil pupil2 = pupilService.getOr404(2L);
        Pupil pupil3 = pupilService.getOr404(3L);

        groupService.addPupilPreference(group, new Preference(pupil1, pupil2, true));
        groupService.addPupilPreference(group, new Preference(pupil2, pupil3, true));
        groupService.addPupilPreference(group, new Preference(pupil3, pupil1, false));
    }

    private void createPlacementResult() throws PlacementService.PlacementWithoutGroupException, PlacementService.PlacementWithoutPupilsInGroupException, PagesAndSortHandler.FieldNotSortableException {
        Placement placement = placementService.allWithoutPages().get(0);
        placementService.generatePlacementResult(placement, "name", "description");
    }
}