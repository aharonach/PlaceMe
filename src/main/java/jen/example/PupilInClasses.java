package jen.example;

import io.jenetics.BitGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;
import io.jenetics.util.IntRange;
import jen.example.placePupils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PupilInClasses {

    public final static List<Attribute> TEMPLATE = List.of(
            new RangeAttribute("atr1", "atr 1 desc", 10),
            new RangeAttribute("atr2", "atr 2 desc", 20),
            new RangeAttribute("atr3", "atr 3 desc", 5),
            new RangeAttribute("atr4", "atr 4 desc", 30)
    );

    //private static final List<Pupil> pupils = Stream.generate(Pupil::random).limit(60).collect(Collectors.toList());
    private static final List<Pupil> pupils = List.of(
            new Pupil("Gal", Pupil.Gender.MALE, createAttributeValues(List.of(1, 4, 3, 2))),
            new Pupil("Aharon", Pupil.Gender.MALE, createAttributeValues(List.of(1, 2, 3, 1))),
            new Pupil("Moshe", Pupil.Gender.MALE, createAttributeValues(List.of(1, 2, 1, 1))),
            new Pupil("Danny", Pupil.Gender.MALE, createAttributeValues(List.of(5, 5, 5, 5))),

            new Pupil("Yossi", Pupil.Gender.MALE, createAttributeValues(List.of(1, 1, 1, 1))),
            new Pupil("Yaron", Pupil.Gender.MALE, createAttributeValues(List.of(1, 2, 1, 2))),
            new Pupil("Nir", Pupil.Gender.MALE, createAttributeValues(List.of(5, 5, 5, 2))),
            new Pupil("Amos", Pupil.Gender.MALE, createAttributeValues(List.of(1, 2, 2, 1))),
            new Pupil("Amit", Pupil.Gender.FEMALE, createAttributeValues(List.of(2, 4, 2, 3))),

            new Pupil("Shir", Pupil.Gender.FEMALE, createAttributeValues(List.of(4, 4, 3, 2))), //4
            new Pupil("Mai", Pupil.Gender.FEMALE, createAttributeValues(List.of(1, 1, 1, 5))), //5
            new Pupil("Dana", Pupil.Gender.FEMALE, createAttributeValues(List.of(3, 2, 2, 1))),
            new Pupil("Sharon", Pupil.Gender.FEMALE, createAttributeValues(List.of(5, 4, 3, 1)))
    );

    private static PupilsConnections connectionsToInclude = new PupilsConnections(Map.of(
            pupils.get(0), List.of(pupils.get(2)),
            pupils.get(1), List.of(pupils.get(4), pupils.get(5))
    ));

    private static PupilsConnections connectionsToExclude = new PupilsConnections(Map.of(
            pupils.get(0), List.of(pupils.get(1)),
            pupils.get(1), List.of(pupils.get(2))
    ));

    final static int NUM_OF_CLASSES = 3;

    public void start(){
        PlaceEngine placeEngine = new PlaceEngine(pupils, NUM_OF_CLASSES, connectionsToInclude, connectionsToExclude);
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
        Placement placement = placeEngine.decode(best.genotype());

        placement.getClasses().forEach(classInfo -> {
            System.out.print("[Pupils: " + classInfo.getNumOfPupils() + " (Males: " + classInfo.getNumOfPupilsByGender(Pupil.Gender.MALE) + " ,Females: " + classInfo.getNumOfPupilsByGender(Pupil.Gender.FEMALE) + " ,Delta: " + classInfo.getDeltaBetweenMalesAndFemales() + ") ");
            System.out.print("Pupils Score: " + classInfo.getSumScoreOfPupils() + " Class Score: " + classInfo.getClassScore() + " ");
            System.out.print("Include is OK: " + (classInfo.getNumberOfWrongConnectionsToInclude() == 0) + ", ");
            System.out.print("Exclude is OK: " + (classInfo.getNumberOfWrongConnectionsToExclude() == 0));
            System.out.print("] | ");
            System.out.println(classInfo);
        });
        System.out.println(placement.getPlacementScore());
    }

    private static List<AttributeValue> createAttributeValues(List<Integer> values){
        List<AttributeValue> attributeValues = new ArrayList<>();
        IntRange.of(0, values.size()).stream().forEach(index -> attributeValues.add(new AttributeValue(TEMPLATE.get(index), values.get(index))));
        return attributeValues;
    }
}