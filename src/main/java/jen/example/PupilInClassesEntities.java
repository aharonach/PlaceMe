package jen.example;

import io.jenetics.BitGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;
import jen.PlaceEngineEntities;
import jen.example.placePupils.PupilsConnections;
import jen.hibernate.entity.PlacementResult;
import jen.hibernate.entity.Pupil;
import jen.hibernate.entity.RangeAttribute;
import jen.hibernate.entity.Template;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PupilInClassesEntities {

    public final static Template TEMPLATE = new Template("template 2", "template 2 desc", List.of(
            new RangeAttribute("attr 1", "attr 1 for template", 10),
            new RangeAttribute("attr 2", "attr 2 for template", 20),
            new RangeAttribute("attr 3", "attr 3 for template", 30),
            new RangeAttribute("attr 4", "attr 4 for template", 40)
    ));

    // todo: handle attributes values
    private static final List<Pupil> PUPILS = List.of(
            new Pupil("1111", "Gal", "", Pupil.Gender.MALE, LocalDate.now()),
            new Pupil("2222", "Aharon", "", Pupil.Gender.MALE, LocalDate.now()),
            new Pupil("3333", "Moshe", "", Pupil.Gender.MALE, LocalDate.now()),
            new Pupil("4444", "Danny", "", Pupil.Gender.MALE, LocalDate.now()),

            new Pupil("5555", "Mai", "", Pupil.Gender.FEMALE, LocalDate.now()),
            new Pupil("6666", "Shir", "", Pupil.Gender.FEMALE, LocalDate.now()),
            new Pupil("7777", "Dana", "", Pupil.Gender.FEMALE, LocalDate.now()),
            new Pupil("8888", "Sharon", "", Pupil.Gender.FEMALE, LocalDate.now())
    );

    // todo: handle connectionsToInclude, connectionsToExclude
    private static final PupilsConnections connectionsToInclude = new PupilsConnections(Map.of());
    private static final PupilsConnections connectionsToExclude = new PupilsConnections(Map.of());


    final static int NUM_OF_CLASSES = 3;

    public void start(){
        PlaceEngineEntities placeEngine = new PlaceEngineEntities(PUPILS, NUM_OF_CLASSES, connectionsToInclude, connectionsToExclude);

        System.out.println(PUPILS);
        System.out.println(NUM_OF_CLASSES);
        System.out.println(connectionsToInclude);
        System.out.println(connectionsToExclude);

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

        System.out.println("is valid: " + PlaceEngineEntities.isValid(best.genotype()));
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
