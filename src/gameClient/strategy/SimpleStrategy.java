package gameClient.strategy;

import api.directed_weighted_graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class SimpleStrategy extends BaseStrategy implements IStrategy {
    public SimpleStrategy(directed_weighted_graph g) {
        super(g);
    }

    /**
     * Chooses the closest agent to pokemon by edge weight.
     * @param candidates Generated distance data for all agents and pokemon.
     * @return Chosen agent.
     */
    @Override
    protected AgentPathCandidate chooseCandidate(ArrayList<AgentPathCandidate> candidates) {
        Collections.sort(candidates, new Comparator<AgentPathCandidate>() {
            public int compare(AgentPathCandidate one, AgentPathCandidate two) {
                return Double.compare(one.getDistance(), two.getDistance());
            }
        });

        return candidates.get(0);
    }
}
