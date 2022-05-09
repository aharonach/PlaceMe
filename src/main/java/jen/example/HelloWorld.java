package jen.example;

import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.util.CharSeq;
import io.jenetics.util.Factory;

import java.util.stream.IntStream;

public class HelloWorld {

    private static final String TARGET = "HELLO WORLD";

    private static int score(final Genotype<CharacterGene> gt){
        Chromosome<CharacterGene> src = gt.chromosome();
        return IntStream.range(0, TARGET.length()).map(i -> src.get(i).allele() == TARGET.charAt(i) ? 1 : 0)
                .sum();
    }

    public void start(){
        final CharSeq chars = CharSeq.of("A-Z ");
        final Factory<Genotype<CharacterGene>> gtf = Genotype.of(CharacterChromosome.of(chars, TARGET.length()));

//        Genotype<CharacterGene> test = gtf.newInstance();

//        System.out.println(test); // chromosome
//        System.out.println(test.get(0)); // string
//        System.out.println(test.chromosome()); // string
//        System.out.println(test.chromosome().get(2)); // char
//
//        System.out.println(score(test)); // score of the word

        Engine<CharacterGene, Integer> engine = Engine
                .builder(HelloWorld::score, gtf)
                .populationSize(150)
                .selector(new TournamentSelector<>())
                .offspringFraction(1)
                .alterers(
                        new Mutator<>(0.05),
                        new SinglePointCrossover<>(1)
                )
                .build();

        final Phenotype<CharacterGene, Integer> result = engine
                .stream()
                .limit(Limits.byFitnessThreshold(TARGET.length() - 1))
                .peek(r -> System.out.println(r.totalGenerations() + " : " + r.bestPhenotype() + " : " + r.killCount()))
                .collect(EvolutionResult.toBestPhenotype());

        System.out.println(result);

    }
}
