package jen.example;

import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.RandomRegistry;

import java.util.*;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PupilInClassesNew {
    final static int NUM_OF_PUPILS = 60;
    final static int NUM_OF_CLASSES = 4;
    final static List<Attribute> TEMPLATE = List.of(
            new RangeAttribute("atr1", "atr 1 desc", 10),
            new RangeAttribute("atr2", "atr 2 desc", 20)
    );
    final static ISeq<Pupil> PUPILS = Stream.generate(Pupil::random).limit(NUM_OF_PUPILS).collect(ISeq.toISeq());
    final static int AVG_PUPILS_IN_CLASS = NUM_OF_PUPILS / NUM_OF_CLASSES;



    enum Gender{
        MALE, FEMALE;
        public static Gender of(int index){
            return (index%2) == 0 ? MALE : FEMALE;
        }
    }

    public static abstract class Attribute{
        protected String name;
        protected String description;
        protected int priority;

        public Attribute(String name, String description, int priority){
            this.name = name;
            this.priority = priority;
            this.description = description;
        }

        abstract int calculate(int score);
    }

    public static class RangeAttribute extends Attribute {
        public RangeAttribute(String name, String description, int priority) {
            super(
                    name,
                    description,
                    priority
            );
        }

        int calculate(int score) {
            return score * priority;
        }
    }

    final static class Pupil implements Comparable<Pupil> {
        public final Gender gender;
        private Map<String, Integer> attributeValues;

        public Pupil(final Gender gender, Map<String, Integer> attributeValues){
            this.gender = gender;
            this.attributeValues = attributeValues; // todo: change it to load each attribute and update the score
        }

        public Gender getGender() {
            return gender;
        }

        public int getScore() {
            int totalScore = 0;
            for(Attribute attribute : TEMPLATE){
                totalScore += attribute.calculate(attributeValues.get(attribute.name));
            }
            return (totalScore * TEMPLATE.size()) / 100;
            //return 0;
        }

        static Pupil random(){
            RandomGenerator r = RandomRegistry.random();
            Map<String, Integer> attributes = new HashMap<>(TEMPLATE.size());
            for(Attribute attribute : TEMPLATE){
                attributes.put(attribute.name, (int)(r.nextDouble()*100)%5);
            }
            return new Pupil(Gender.of(r.nextInt()), attributes);
        }

        @Override
        public String toString() {
            return "Pupil{" +
                    "gender=" + gender +
                    ", score=" + getScore() +
                    '}';
        }

        @Override
        public int compareTo(Pupil o) {
            return getScore() - o.getScore();
        }
    }

    final static class ClassInfo{
        private final List<Pupil> pupils;

        public ClassInfo(List<Pupil> pupils){
            this.pupils = pupils;
        }

        public int getScore(){
            // numOfPupils
            // deltaBetweenMalesAndFemales
            // num of correct rules

            // the target is to get lowest score
            long sum = getDeltaBetweenMalesAndFemales() * 10L
                    + getDeltaBetweenNumOfPupils() * 20L
                    + getSumScoreOfPupils() * 15L;

            // males/females, num of pupils in class, num of correct rules
            return (int)sum; // temp
        }

        public long getDeltaBetweenNumOfPupils(){
            return Math.abs(getNumOfPupils() - AVG_PUPILS_IN_CLASS);
        }

        public long getDeltaBetweenMalesAndFemales(){
            return Math.abs(getNumOfMales() - getNumOfFemales());
        }

        private int getSumScoreOfPupils(){
            return pupils.stream().mapToInt(Pupil::getScore).sum();
        }

        public long getNumOfPupils(){
            return pupils.size();
        }

        public long getNumOfMales(){
            return getNumOfPupilsByGender(Gender.MALE);
        }

        public long getNumOfFemales(){
            return getNumOfPupilsByGender(Gender.FEMALE);
        }

        private long getNumOfPupilsByGender(Gender gender){
            return pupils.stream().filter(p -> p.getGender()==gender).count();
        }

        @Override
        public String toString() {
            return "ClassInfo{" +
                    "pupils=" + pupils +
                    '}';
        }
    }

    final static class Placement {
        public final List<ClassInfo> classes;

        public Placement(List<ClassInfo> classes){
            this.classes = classes;
        }

        public int getScore(){
            int scoreOfAllClasses = classes.stream().mapToInt(ClassInfo::getScore).sum();
            int avgScore = scoreOfAllClasses / NUM_OF_CLASSES;

            int deltaSum = 0;
            for(ClassInfo classInfo : classes){
                deltaSum += Math.abs(classInfo.getScore() - avgScore);
            }

            return deltaSum; //classes.stream().mapToInt(ClassInfo::getScore).sum(); // temp
        }

        public List<ClassInfo> getClasses() {
            return classes;
        }

        @Override
        public String toString() {
            return "Placement{" +
                    "classes=" + classes +
                    '}';
        }
    }

    private static int score(final Placement placement){
        return placement.getScore();
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
        int numOfAvailablePupils = placement.chromosome().length();
        Map<Integer, Set<Integer>> classesByPupilIndex = createClassesByPupilIndexMap(numOfAvailablePupils, placement);

        // save all chromosome to recreate the placement at the end
        Map<Integer, BitChromosome> newChromosomesMap = new HashMap<>(placement.length());
        for (int i = 0; i < placement.length(); i++) {
            newChromosomesMap.put(i, placement.get(i).as(BitChromosome.class));
        }

        // classesByPupilIndex contains : pairs of [pupil number -> classes that he place in]
        classesByPupilIndex.entrySet().forEach(integerSetEntry -> {
            boolean pupilHasNoClass = integerSetEntry.getValue().size() == 0;
            int pupilIndex = integerSetEntry.getKey();

            if ( pupilHasNoClass ) {
                // get the class index with the lowest pupils size
                int chromosomeToChange = IntStream.range(0, placement.length()).boxed()
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
        Map<Integer, Set<Integer>> classesByPupilIndex = new HashMap<>();
        for (int i = 0; i < numOfAvailablePupils; i++) {
            classesByPupilIndex.put(i, new HashSet<>(placement.length()));
        }
        IntRange.of(0, placement.length()).stream().forEach(classNumber -> {
            placement.get(classNumber).as(BitChromosome.class).ones().forEach( pupil -> {
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

    private static IntStream getUnplacedPupils(final Genotype<BitGene> classes){
        int numOfAvailablePupils = classes.chromosome().length();
        BitSet placedPupils = new BitSet(numOfAvailablePupils);
        for (Chromosome<BitGene> ch : classes) {
            placedPupils.or(ch.as(BitChromosome.class).toBitSet());
        }
        return BitChromosome.of(placedPupils, numOfAvailablePupils).zeros();
    }

    public static Placement decode(Genotype<BitGene> gt){
        List<ClassInfo> allClasses = new ArrayList<>(NUM_OF_CLASSES);

        gt.forEach(chromosome -> {
            List<Pupil> pupilsInClass = new ArrayList<>(NUM_OF_PUPILS);
            chromosome.as(BitChromosome.class).ones().forEach(index -> pupilsInClass.add(PUPILS.get(index)));
            allClasses.add(new ClassInfo(pupilsInClass));
        });

        return new Placement(allClasses);
    }

    public void start(){
        PUPILS.stream().forEach(System.out::println);

        Codec<Placement, BitGene> codec = Codec.of(
                Genotype.of(BitChromosome.of(NUM_OF_PUPILS, 0.5), NUM_OF_CLASSES),
                PupilInClassesNew::decode
        );

        // define how to test the genotype and how to repair it
        Constraint <BitGene, Integer> constraint = Constraint.of(
                penotype -> isValid(penotype.genotype()),
                (penotype, gen) -> Phenotype.of(repair(penotype.genotype()), gen)
        );

        Engine<BitGene, Integer> engine = Engine
                .builder(PupilInClassesNew::score, codec)
                .populationSize(100)
                .survivorsSelector(new TournamentSelector<>(10))
                .offspringFraction(1)
                .offspringSelector(new RouletteWheelSelector<>())
                .minimizing()
                .alterers(
                        //new SwapMutator<>(),
                        new Mutator<>(0.015),
                        new SinglePointCrossover<>(0.36)
//                        new Mutator<>(0.015),
//                        new SinglePointCrossover<>(0.16)
                )
                .constraint(constraint)
                .build();

        final EvolutionStatistics<Integer, ?> statistics = EvolutionStatistics.ofNumber();

        final Phenotype<BitGene, Integer> best = engine
                .stream()
                .limit(Limits.bySteadyFitness(7))
                .limit(100)
                .peek(r -> System.out.println(r.totalGenerations() + " : " + r.bestPhenotype()))
                .peek(statistics)
                .collect(EvolutionResult.toBestPhenotype());

        System.out.println("Result:");
        System.out.println(best.genotype());

        System.out.println("is valid: " + isValid(best.genotype()));

        Placement placement = codec.decode(best.genotype());
        //System.out.println(placement);

        placement.getClasses().stream().forEach(classInfo -> {
            System.out.print("[Pupils: " + classInfo.getNumOfPupils() + " (Males: " + classInfo.getNumOfMales() + " ,Females: " + classInfo.getNumOfFemales() + " ,Delta: " + classInfo.getDeltaBetweenMalesAndFemales() + ") ");
            System.out.print("Pupils Score: " + classInfo.getSumScoreOfPupils() + " Class Score: " + classInfo.getScore() + "] | ");
            System.out.println(classInfo);
        });
        System.out.println(placement.getScore());

        //placement.getClasses().stream().forEach(System.out::println);
        //System.out.println(statistics);
    }
}