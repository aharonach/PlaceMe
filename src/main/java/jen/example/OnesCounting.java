package jen.example;

import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;

public class OnesCounting {

    private static Integer count(final Genotype<BitGene> gt){
        return gt.chromosome().as(BitChromosome.class).bitCount();
    }

    public void start(){

        Engine<BitGene, Integer> engine = Engine
                .builder(
                        OnesCounting::count,
                        BitChromosome.of(20, 0.15)
                        )
                .populationSize(500)
                .selector(new RouletteWheelSelector<>())
                .offspringFraction(1)
                .alterers(
                        new Mutator<>(0.55),
                        new SinglePointCrossover<>(0.06)
                )
                .build();

        final EvolutionStatistics<Integer, ?> statistics = EvolutionStatistics.ofNumber();

        final Phenotype<BitGene, Integer> result = engine
                .stream()
                .limit(Limits.bySteadyFitness(7))  // Truncate the evolution stream after 7 "steady"
                .limit(100) // max 100 generations
                .peek(statistics)
                .peek(r -> System.out.println(r.totalGenerations() + " : " + r.bestPhenotype() + " : " + r.killCount()))

                .collect(EvolutionResult.toBestPhenotype());

        System.out.println(statistics);
        System.out.println(result);
    }
}
