package jen.example.placePupils;

import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.IntRange;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class PlaceEngine {

    private final List<Pupil> pupils;
    private final int numOfClasses;
    private final PupilsConnections connectionsToInclude;
    private final PupilsConnections connectionsToExclude;

    public Engine<BitGene, Double> getEngine(){
        pupils.forEach(System.out::println);

        Codec<Placement, BitGene> codec = Codec.of(
                Genotype.of(BitChromosome.of(getNumOfPupils(), 0.5), numOfClasses),
                this::decode
        );

        // define how to test the genotype and how to repair it
        Constraint<BitGene, Double> constraint = Constraint.of(
                phenotype -> isValid(phenotype.genotype()),
                (phenotype, gen) -> Phenotype.of(repair(phenotype.genotype()), gen)
        );

        return Engine
                .builder(PlaceEngine::score, codec)
                .populationSize(100)
//                .survivorsSelector(new TournamentSelector<>(10))
//                .offspringFraction(1)
                .offspringSelector(new TournamentSelector<>()) //new RouletteWheelSelector<>(),
                .minimizing()
                .alterers(
                        new SwapMutator<>(),
                        //new Mutator<>(0.015),
                        new SinglePointCrossover<>(0.36)
    //                        new Mutator<>(0.015),
    //                        new SinglePointCrossover<>(0.16)
                )
                .constraint(constraint)
                .build();
    }

    private static double score(final Placement placement){
        return placement.getPlacementScore();
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
        classesByPupilIndex.entrySet().forEach(integerSetEntry -> {
            boolean pupilHasNoClass = integerSetEntry.getValue().size() == 0;
            int pupilIndex = integerSetEntry.getKey();

            if ( pupilHasNoClass ) {
                // get the class index with the lowest pupils size
                int chromosomeToChange = IntStream.range(0, numOfAvailableClasses).boxed()
                        .max((o1, o2) -> (int) (newChromosomesMap.get(o1).as(BitChromosome.class).zeros().count() - newChromosomesMap.get(o2).as(BitChromosome.class).zeros().count()))
                        .get();
                setBitOnChromosomeMap(newChromosomesMap, chromosomeToChange, pupilIndex);
            } else {
                while(integerSetEntry.getValue().size() > 1){
                    // get the class index with The highest pupils size
                    int chromosomeToChange = integerSetEntry.getValue()
                            .stream()
                            .max((o1, o2) -> (int) (newChromosomesMap.get(o1).as(BitChromosome.class).ones().count() - newChromosomesMap.get(o2).as(BitChromosome.class).ones().count()))
                            .get();
                    clearBitOnChromosomeMap(newChromosomesMap, chromosomeToChange, pupilIndex);
                    integerSetEntry.getValue().remove(chromosomeToChange);
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

    public Placement decode(Genotype<BitGene> gt){
        List<ClassInfo> allClasses = new ArrayList<>(numOfClasses);

        gt.forEach(chromosome -> {
            List<Pupil> pupilsInClass = new ArrayList<>(getNumOfPupils());
            chromosome.as(BitChromosome.class).ones().forEach(index -> pupilsInClass.add(pupils.get(index)));
            allClasses.add(new ClassInfo(pupilsInClass, connectionsToInclude, connectionsToExclude));
        });

        return new Placement(allClasses);
    }

    private int getNumOfPupils(){
        return pupils.size();
    }
}