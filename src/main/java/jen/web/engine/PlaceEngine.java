package jen.web.engine;

import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.IntRange;
import jen.web.dto.PupilsConnectionsDto;
import jen.web.entity.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlaceEngine {

    private final Placement placement;
    private final List<Pupil> pupils;
    private final PupilsConnectionsDto connectionsToInclude;
    private final PupilsConnectionsDto connectionsToExclude;
    private int numOfMales;
    private int numOfFemales;
    private final PlaceEngineConfig config;

    public PlaceEngine(Placement placement, PlaceEngineConfig config){
        this.placement = placement;
        this.config = config;
        this.pupils = placement.getGroup().getPupils().stream().toList();
        this.connectionsToInclude = PupilsConnectionsDto.fromSelectorSelectedSet(getSelectorSelectedIds(placement, true));
        this.connectionsToExclude = PupilsConnectionsDto.fromSelectorSelectedSet(getSelectorSelectedIds(placement, false));

        numOfMales = (int) pupils.stream().filter(p -> p.getGender()== Pupil.Gender.MALE).count();
        numOfFemales = (int) pupils.stream().filter(p -> p.getGender()== Pupil.Gender.FEMALE).count();

        pupils.forEach(System.out::println);
    }

    private Set<SelectorSelectedId> getSelectorSelectedIds(Placement placement, boolean isWantToBeWithSelected){
        return placement.getGroup().getPreferences().stream()
                .filter(preference -> preference.getIsSelectorWantToBeWithSelected().equals(isWantToBeWithSelected))
                .map(Preference::getSelectorSelectedId)
                .collect(Collectors.toSet());
    }

    private Engine<BitGene, Double> getEngine(){

        Codec<PlacementResult, BitGene> codec = Codec.of(
                Genotype.of(BitChromosome.of(getNumOfPupils(), 0.5), placement.getNumberOfClasses()),
                this::decode
        );

        // define how to test the genotype and how to repair it
        Constraint<BitGene, Double> constraint = Constraint.of(
                phenotype -> isValid(phenotype.genotype()),
                (phenotype, gen) -> Phenotype.of(repair(phenotype.genotype()), gen)
        );

        return Engine
                .builder(PlaceEngine::score, codec)
                .populationSize(config.getPopulationSize())
                .offspringSelector((Selector<BitGene, Double>) config.createInstanceForSelector()) //new RouletteWheelSelector<>(),
                .minimizing()
                .alterers(
                        (Alterer<BitGene, Double>) config.createInstanceForAltererFirst(),
                        (Alterer<BitGene, Double>) config.createInstanceForAltererSecond()
                )
                .constraint(constraint)
                .build();
    }

    public PlacementResult generatePlacementResult(){
        final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();
        Engine<BitGene, Double> engine = getEngine();

        final Phenotype<BitGene, Double> best = engine
                .stream()
                .limit(Limits.bySteadyFitness(config.getLimitBySteadyFitness()))
                .limit(config.getGenerationsLimit())
                .peek(r -> System.out.println(r.totalGenerations() + " : " + r.bestPhenotype() + ", worst:" + r.worstFitness()))
                .peek(statistics)
                .collect(EvolutionResult.toBestPhenotype());

        PlacementResult placementResult = decode(best.genotype());
        System.out.println("Placement result is valid: " + PlaceEngine.isValid(best.genotype()));
        System.out.println(statistics);

        return placementResult;
    }

    private static double score(final PlacementResult placementResult){
        return placementResult.getPlacementScore();
    }

    public static boolean isValid(final Genotype<BitGene> classes){
        // check that all pupils appears        (V)
        // check that each pupil appear once    (V)
        int numOfAvailablePupils = classes.chromosome().length();

        boolean allPupilArePlaced = getUnplacedPupils(classes).count() == 0;
        boolean numOfPlacedPupilsEqualToNumOfPupils = getNumOfPlacedPupils(classes) == numOfAvailablePupils;

        return allPupilArePlaced && numOfPlacedPupilsEqualToNumOfPupils;
    }

    public static Genotype<BitGene> repair(final Genotype<BitGene> placement) {
        int numOfAvailablePupils = getNumOfAvailablePupils(placement);
        int numOfAvailableClasses = placement.length();
        Map<Integer, Set<Integer>> classesByPupilIndex = createClassesByPupilIndexMap(numOfAvailablePupils, placement);

        // save all chromosome to recreate the placement at the end
        Map<Integer, BitChromosome> newChromosomesMap = new HashMap<>(numOfAvailableClasses);
        for (int i = 0; i < numOfAvailableClasses; i++) {
            newChromosomesMap.put(i, placement.get(i).as(BitChromosome.class));
        }

        // classesByPupilIndex contains : pairs of [pupil number -> classes that he place in]
        classesByPupilIndex.forEach((key, value) -> {
            boolean pupilHasNoClass = value.size() == 0;
            int pupilIndex = key;

            if (pupilHasNoClass) {
                // get the class index with the lowest pupils size
                int chromosomeToChange = IntStream.range(0, numOfAvailableClasses).boxed()
                        .max((o1, o2) -> (int) (newChromosomesMap.get(o1).as(BitChromosome.class).zeros().count() - newChromosomesMap.get(o2).as(BitChromosome.class).zeros().count()))
                        .get();
                setBitOnChromosomeMap(newChromosomesMap, chromosomeToChange, pupilIndex);
            } else {
                while (value.size() > 1) {
                    // get the class index with The highest pupils size
                    int chromosomeToChange = value
                            .stream()
                            .max((o1, o2) -> (int) (newChromosomesMap.get(o1).as(BitChromosome.class).ones().count() - newChromosomesMap.get(o2).as(BitChromosome.class).ones().count()))
                            .get();
                    clearBitOnChromosomeMap(newChromosomesMap, chromosomeToChange, pupilIndex);
                    value.remove(chromosomeToChange);
                }
            }
        });

        List<BitChromosome> newChromosomes = new ArrayList<>(newChromosomesMap.size());
        IntStream.range(0, newChromosomesMap.size()).forEach(i -> newChromosomes.add(newChromosomesMap.get(i)));
        return Genotype.of(newChromosomes);
    }

    private static Map<Integer, Set<Integer>> createClassesByPupilIndexMap(int numOfAvailablePupils, Genotype<BitGene> placement){
        Map<Integer, Set<Integer>> classesByPupilIndex = new HashMap<>(numOfAvailablePupils);
        for (int i = 0; i < numOfAvailablePupils; i++) {
            classesByPupilIndex.put(i, new HashSet<>(placement.length()));
        }
        IntRange.of(0, placement.length()).stream().forEach(classNumber -> {
            placement.get(classNumber).as(BitChromosome.class).ones().forEach(pupil -> {
                classesByPupilIndex.get(pupil).add(classNumber);
            });
        });
        return classesByPupilIndex;
    }

    private static void setBitOnChromosomeMap(Map<Integer, BitChromosome> newChromosomesMap, int chromosomeToChange, int bitIndex){
        BitSet bits = newChromosomesMap.get(chromosomeToChange).toBitSet();
        bits.set(bitIndex);
        newChromosomesMap.put(chromosomeToChange, BitChromosome.of(bits, newChromosomesMap.get(0).length()));
    }

    private static void clearBitOnChromosomeMap(Map<Integer, BitChromosome> newChromosomesMap, int chromosomeToChange, int bitIndex){
        BitSet bits = newChromosomesMap.get(chromosomeToChange).toBitSet();
        bits.clear(bitIndex);
        newChromosomesMap.put(chromosomeToChange, BitChromosome.of(bits, newChromosomesMap.get(0).length()));
    }

    private static int getNumOfPlacedPupils(final Genotype<BitGene> classes) {
        int numOfPlacedPupils = 0;
        for (Chromosome<BitGene> ch : classes) {
            numOfPlacedPupils += ch.as(BitChromosome.class).ones().count();
        }
        return numOfPlacedPupils;
    }

    private static IntStream getUnplacedPupils(final Genotype<BitGene> placement){
        int numOfAvailablePupils = getNumOfAvailablePupils(placement);
        BitSet placedPupils = new BitSet(numOfAvailablePupils);
        for (Chromosome<BitGene> ch : placement) {
            placedPupils.or(ch.as(BitChromosome.class).toBitSet());
        }
        return BitChromosome.of(placedPupils, numOfAvailablePupils).zeros();
    }

    private static int getNumOfAvailablePupils(final Genotype<BitGene> placement){
        return placement.chromosome().length();
    }

    private PlacementResult decode(Genotype<BitGene> gt){
        List<PlacementClassroom> allClasses = new ArrayList<>(placement.getNumberOfClasses());

        gt.forEach(chromosome -> {
            List<Pupil> pupilsInClass = new ArrayList<>(getNumOfPupils());
            chromosome.as(BitChromosome.class).ones().forEach(index -> pupilsInClass.add(pupils.get(index)));
            allClasses.add(new PlacementClassroom(pupilsInClass, connectionsToInclude, connectionsToExclude, numOfMales, numOfFemales));
        });

        return new PlacementResult(allClasses);
    }

    private int getNumOfPupils(){
        return pupils.size();
    }
}