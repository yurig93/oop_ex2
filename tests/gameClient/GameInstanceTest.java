package gameClient;

import gameClient.strategy.StrategyType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameInstanceTest {

    @Test
    void getLoginId() {
        GameInstance g = new GameInstance(123,123, StrategyType.HEAT_MAP);
        assertEquals(g.getLoginId(), 123);
    }

    @Test
    void getCurrentScenario() {
        GameInstance g = new GameInstance(123,123,StrategyType.HEAT_MAP);
        assertEquals(g.getCurrentScenario(), 123);
    }

    @Test
    void changeScenario() {
        GameInstance g = new GameInstance(123,123,StrategyType.HEAT_MAP);
        g.changeScenario(1, false);
        assertEquals(g.getCurrentScenario(), 1);
    }

    @Test
    void changeStrategy() {
        GameInstance g = new GameInstance(123,123,StrategyType.HEAT_MAP);
        g.changeStrategy(StrategyType.SIMPLE);
        assertEquals(g.getCurrentStrategy(), StrategyType.SIMPLE);
    }

    @Test
    void getCurrentStrategy() {
        GameInstance g = new GameInstance(123,123,StrategyType.HEAT_MAP);
        assertEquals(g.getCurrentStrategy(), StrategyType.HEAT_MAP);

    }
}