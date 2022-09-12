package web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jenetics.SinglePointCrossover;
import io.jenetics.SwapMutator;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.lang.reflect.Constructor;

import static io.jenetics.Alterer.DEFAULT_ALTER_PROBABILITY;

@Getter
@Setter
@Entity
public class PlaceEngineConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @JsonIgnore
    private Long id;

    int populationSize;

    SELECTOR offspringSelector;

    ALTERER altererFirst;
    double altererFirstProbability;

    ALTERER altererSecond;
    double altererSecondProbability;

    int limitBySteadyFitness;

    int generationsLimit;

    public PlaceEngineConfig() {
        ResetToDefault();
    }

    public void ResetToDefault() {
        this.populationSize = 100;
        this.offspringSelector = SELECTOR.TournamentSelector;
        this.altererFirst = ALTERER.SwapMutator;
        this.altererFirstProbability = DEFAULT_ALTER_PROBABILITY;
        this.altererSecond = ALTERER.SinglePointCrossover;
        this.altererSecondProbability = 0.36;
        this.limitBySteadyFitness = 7;
        this.generationsLimit = 100;
    }

    public Object createInstanceForSelector() {
        return createInstanceForSelector(this.getOffspringSelector());
    }

    public Object createInstanceForAltererFirst() {
        return createInstanceForAlterer(this.getAltererFirst(), this.getAltererFirstProbability());
    }

    public Object createInstanceForAltererSecond() {
        return createInstanceForAlterer(this.getAltererSecond(), this.getAltererSecondProbability());
    }

    public static Object createInstanceForSelector(SELECTOR selector) {
        try {
            Class<?> clazz = Class.forName("io.jenetics." + selector.toString());
            Constructor<?> ctor = clazz.getConstructor();
            return ctor.newInstance();
        } catch( Exception exception ) {
            return null;
        }
    }

    public static Object createInstanceForAlterer(ALTERER alterer, double probability) {
        return switch (alterer) {
            case None -> null;
            case SwapMutator -> new SwapMutator<>(probability);
            case SinglePointCrossover -> new SinglePointCrossover<>(probability);
        };
    }

    enum SELECTOR {
        None, TournamentSelector, RouletteWheelSelector
    }

    enum ALTERER {
        None, SwapMutator, SinglePointCrossover
    }
}
