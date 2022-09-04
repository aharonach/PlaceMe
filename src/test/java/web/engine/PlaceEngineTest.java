package web.engine;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PlaceEngineTest {
    private static final int CLASS_OF_FOUR_PUPILS = 4;
    private static final int CLASS_OF_ONE_PUPIL = 1;
    private static final int CLASS_OF_TEN_PUPILS = 10;

    @Test
    public void testIsValidMethod() {
        validateIsValid(CLASS_OF_FOUR_PUPILS);
        validateIsValid(CLASS_OF_ONE_PUPIL);
        validateIsValid(CLASS_OF_TEN_PUPILS);
    }

    @Test
    public void testRepairMethod() {
        validateRepair(CLASS_OF_FOUR_PUPILS);
        validateRepair(CLASS_OF_ONE_PUPIL);
        validateRepair(CLASS_OF_TEN_PUPILS);
    }

    private void validateIsValid(int numOfPupils){
        Genotype<BitGene> pupilPlacedMoreThanOnce = Genotype.of(
                BitChromosome.of(numOfPupils, 1),
                BitChromosome.of(numOfPupils, 1)
        );
        assertFalse(PlaceEngine.isValid(pupilPlacedMoreThanOnce));

        Genotype<BitGene> pupilPlacedMoreThanOnceInFewClasses = Genotype.of(
                BitChromosome.of(numOfPupils, 1),
                BitChromosome.of(numOfPupils, 1),
                BitChromosome.of(numOfPupils, 1)
        );
        assertFalse(PlaceEngine.isValid(pupilPlacedMoreThanOnceInFewClasses));


        Genotype<BitGene> notAllPupilsPlaced = Genotype.of(
                BitChromosome.of(numOfPupils, 0),
                BitChromosome.of(numOfPupils, 0)
        );
        assertFalse(PlaceEngine.isValid(notAllPupilsPlaced));

        Genotype<BitGene> notAllPupilsPlacedInFewClasses = Genotype.of(
                BitChromosome.of(numOfPupils, 0),
                BitChromosome.of(numOfPupils, 0),
                BitChromosome.of(numOfPupils, 0)
        );
        assertFalse(PlaceEngine.isValid(notAllPupilsPlacedInFewClasses));


        Genotype<BitGene> goodPlacement = Genotype.of(
                BitChromosome.of(numOfPupils, 1),
                BitChromosome.of(numOfPupils, 0)
        );
        assertTrue(PlaceEngine.isValid(goodPlacement));
    }

    private void validateRepair(int numOfPupils) {
        Genotype<BitGene> pupilPlacedMoreThanOnce = Genotype.of(
                BitChromosome.of(numOfPupils, 1),
                BitChromosome.of(numOfPupils, 1)
        );
        assertFalse(PlaceEngine.isValid(pupilPlacedMoreThanOnce));
        assertTrue(PlaceEngine.isValid(PlaceEngine.repair(pupilPlacedMoreThanOnce)));

        Genotype<BitGene> pupilPlacedMoreThanOnceInFewClasses = Genotype.of(
                BitChromosome.of(numOfPupils, 1),
                BitChromosome.of(numOfPupils, 1),
                BitChromosome.of(numOfPupils, 1)
        );
        assertFalse(PlaceEngine.isValid(pupilPlacedMoreThanOnceInFewClasses));
        assertTrue(PlaceEngine.isValid(PlaceEngine.repair(pupilPlacedMoreThanOnceInFewClasses)));


        Genotype<BitGene> notAllPupilsPlaced = Genotype.of(
                BitChromosome.of(numOfPupils, 0),
                BitChromosome.of(numOfPupils, 0)
        );
        assertFalse(PlaceEngine.isValid(notAllPupilsPlaced));
        assertTrue(PlaceEngine.isValid(PlaceEngine.repair(notAllPupilsPlaced)));

        Genotype<BitGene> notAllPupilsPlacedInFewClasses = Genotype.of(
                BitChromosome.of(numOfPupils, 0),
                BitChromosome.of(numOfPupils, 0),
                BitChromosome.of(numOfPupils, 0)
        );
        assertFalse(PlaceEngine.isValid(notAllPupilsPlacedInFewClasses));
        assertTrue(PlaceEngine.isValid(PlaceEngine.repair(notAllPupilsPlacedInFewClasses)));


        Genotype<BitGene> pupilPlacedWithMixedClasses = Genotype.of(
                BitChromosome.of(numOfPupils, 1),
                BitChromosome.of(numOfPupils, 1),
                BitChromosome.of(numOfPupils, 0)
        );
        assertFalse(PlaceEngine.isValid(pupilPlacedWithMixedClasses));
        assertTrue(PlaceEngine.isValid(PlaceEngine.repair(pupilPlacedWithMixedClasses)));


        Genotype<BitGene> goodPlacement = Genotype.of(
                BitChromosome.of(numOfPupils, 1),
                BitChromosome.of(numOfPupils, 0)
        );
        assertTrue(PlaceEngine.isValid(goodPlacement));
        assertTrue(PlaceEngine.isValid(PlaceEngine.repair(goodPlacement)));
    }
}