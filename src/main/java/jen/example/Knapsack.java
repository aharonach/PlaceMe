package jen.example;

import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

import java.util.function.Function;
import java.util.random.RandomGenerator;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class Knapsack {

    final static class Item{
        public final int size;
        public final int value;

        public Item(final int size, final int value){
            this.size = size;
            this.value = value;
        }

        // create random item
        static Item random(){
            RandomGenerator r = RandomRegistry.random();
            return new Item((int)(r.nextDouble()*100), (int)(r.nextDouble()*100));
        }

        static Collector<Item, ?, Item> toSum(){
            return Collector.of(
                    () -> new int[2],
                    (a, b) -> {a[0] += b.size; a[1] += b.value;},
                    (a, b) -> {a[0] += b[0]; a[1] += b[1]; return a;},
                    r -> new Item(r[0], r[1])
            );
        }

        @Override
        public String toString() {
            return "Item{" +
                    "size=" + size +
                    ", value=" + value +
                    '}';
        }
    }

    static Function<ISeq<Item>, Integer> fitness(final int size){
        return items -> {
            final Item sum = items.stream().collect(Item.toSum());
            return sum.size <= size ? sum.value : 0;
        };
    }

    public void start(){

        final int numOfItems = 15;
        final int ksSize = numOfItems * 100 / 5;
        System.out.println("max size: " + ksSize);

        final ISeq<Item> items = Stream.generate(Item::random).limit(numOfItems).collect(ISeq.toISeq());
        items.stream().forEach(System.out::println);

        final Codec<ISeq<Item>, BitGene> codec = Codecs.ofSubSet(items);

        System.out.println("\nbefore");
        codec.encoding().instances().limit(1).forEach(i -> {
            System.out.println(i);
            System.out.println(codec.decode(i));
        });
        System.out.println("after\n");

        Engine<BitGene, Integer> engine = Engine
                .builder(fitness(ksSize), codec)
                .populationSize(500)
                .survivorsSelector(new TournamentSelector<>(5))
                .offspringSelector(new RouletteWheelSelector<>())
                .alterers(
                        new Mutator<>(0.015),
                        new SinglePointCrossover<>(0.16)
                )
                .build();

        final Phenotype<BitGene, Integer> best = engine
                .stream()
                .limit(Limits.bySteadyFitness(7))
                .limit(100)
                .peek(r -> System.out.println(r.totalGenerations() + " : " + r.bestPhenotype() + " : " + r.killCount()))
                .collect(EvolutionResult.toBestPhenotype());

        //System.out.println(best);

        final ISeq<Item> res = codec.decode(best.genotype());
        res.stream().forEach(System.out::println);

        int fillSize = res.stream().mapToInt(i -> i.size).sum();
        int fillValue = res.stream().mapToInt(i -> i.value).sum();

        System.out.println("size: " + fillSize + ", value: " + fillValue);
    }
}
