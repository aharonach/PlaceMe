package jen.web.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaceEngineConfigTest {
    @Test
    void testCreateInstanceForSelector() {
        PlaceEngineConfig.SELECTOR selector = PlaceEngineConfig.SELECTOR.TournamentSelector;
        Object instance = PlaceEngineConfig.createInstanceForSelector(selector);

        assertNotNull(instance);
        assertEquals(instance.getClass().getName(), "io.jenetics." + selector);
    }

    @Test
    void createInstanceForAlterer() {
        PlaceEngineConfig.ALTERER alterer = PlaceEngineConfig.ALTERER.SwapMutator;
        Object instance = PlaceEngineConfig.createInstanceForAlterer(alterer, 0.5);

        assertNotNull(instance);
        assertEquals(instance.getClass().getName(), "io.jenetics." + alterer);
    }
}