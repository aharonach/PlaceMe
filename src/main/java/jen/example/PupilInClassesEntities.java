package jen.example;

import io.jenetics.BitGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;
import jen.web.engine.PlaceEngine;
import jen.example.placePupils.PupilsConnections;
import jen.web.entity.*;

import java.time.LocalDate;
import java.util.Map;

public class PupilInClassesEntities {

    public final static Template TEMPLATE = new Template("template 2", "template 2 desc");
    public final static Group GROUP = new Group("group 1", "group 1 desc", TEMPLATE);


    // todo: handle connectionsToInclude, connectionsToExclude
    private static final PupilsConnections connectionsToInclude = new PupilsConnections(Map.of());
    private static final PupilsConnections connectionsToExclude = new PupilsConnections(Map.of());


    final static int NUM_OF_CLASSES = 3;

    public void start() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        TEMPLATE.addAttribute(new RangeAttribute("attr 1", "attr 1 for template", 10));
        TEMPLATE.addAttribute(new RangeAttribute("attr 2", "attr 2 for template", 20));
        TEMPLATE.addAttribute(new RangeAttribute("attr 3", "attr 3 for template", 30));
        TEMPLATE.addAttribute(new RangeAttribute("attr 4", "attr 4 for template", 40));


        GROUP.addPupil(new Pupil("1111", "Gal", "", Pupil.Gender.MALE, LocalDate.now()));
        GROUP.addPupil(new Pupil("2222", "Aharon", "", Pupil.Gender.MALE, LocalDate.now()));
        GROUP.addPupil(new Pupil("3333", "Moshe", "", Pupil.Gender.MALE, LocalDate.now()));
        GROUP.addPupil(new Pupil("4444", "Danny", "", Pupil.Gender.MALE, LocalDate.now()));
        GROUP.addPupil(new Pupil("5555", "Mai", "", Pupil.Gender.FEMALE, LocalDate.now()));
        GROUP.addPupil(new Pupil("6666", "Shir", "", Pupil.Gender.FEMALE, LocalDate.now()));
        GROUP.addPupil(new Pupil("7777", "Dana", "", Pupil.Gender.FEMALE, LocalDate.now()));
        GROUP.addPupil(new Pupil("8888", "Sharon", "", Pupil.Gender.FEMALE, LocalDate.now()));


        Placement placement = new Placement("placement 1", NUM_OF_CLASSES, GROUP);

        PlaceEngine placeEngine = new PlaceEngine(placement);
        Engine<BitGene, Double> engine = placeEngine.getEngine();

        final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

        final Phenotype<BitGene, Double> best = engine
                .stream()
                .limit(Limits.bySteadyFitness(7))
                .limit(100)
                .peek(r -> System.out.println(r.totalGenerations() + " : " + r.bestPhenotype() + ", worst:" + r.worstFitness()))
                .peek(statistics)
                .collect(EvolutionResult.toBestPhenotype());


        System.out.println("Result:");
        System.out.println(best.genotype());

        System.out.println("is valid: " + PlaceEngine.isValid(best.genotype()));
        PlacementResult placementResult = placeEngine.decode(best.genotype());

        placementResult.getClasses().forEach(classInfo -> {
            System.out.print("[Pupils: " + classInfo.getNumOfPupils() + " (Males: " + classInfo.getNumOfPupilsByGender(Pupil.Gender.MALE) + " ,Females: " + classInfo.getNumOfPupilsByGender(Pupil.Gender.FEMALE) + " ,Delta: " + classInfo.getDeltaBetweenMalesAndFemales() + ") ");
            System.out.print("Pupils Score: " + classInfo.getSumScoreOfPupils() + " Class Score: " + classInfo.getClassScore() + " ");
            System.out.print("Include is OK: " + (classInfo.getNumberOfWrongConnectionsToInclude() == 0) + ", ");
            System.out.print("Exclude is OK: " + (classInfo.getNumberOfWrongConnectionsToExclude() == 0));
            System.out.print("] | ");
            System.out.println(classInfo.getPupils());
        });
        System.out.println(placementResult.getPlacementScore());
    }
}