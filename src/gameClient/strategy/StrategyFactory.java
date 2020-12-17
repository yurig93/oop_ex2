package gameClient.strategy;

import api.directed_weighted_graph;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class StrategyFactory {
    public static List<String> getNames() {
        return Arrays.stream(StrategyType.values()).map(Enum::name)
                .collect(Collectors.toList());
    }

    public static IStrategy getStrategy(StrategyType strategy, directed_weighted_graph g) {
        switch (strategy) {
            case SIMPLE:
                return new SimpleStrategy(g);
            case HEAT_MAP:
                return new HeatmapStrategy(g);
            case MAP_DISTANCE:
                return new MapDistanceStrategy(g);
        }
        return null;
    }

}
