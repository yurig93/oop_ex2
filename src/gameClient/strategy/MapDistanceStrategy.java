package gameClient.strategy;

import api.directed_weighted_graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MapDistanceStrategy extends SimpleStrategy implements IStrategy {

    public MapDistanceStrategy(directed_weighted_graph g) {
        super(g);
    }

    /**
     * Chooses a agent closest to pokemon base on location on map on not on edge weight.
     * @param candidates Generated distance data for all agents and pokemon.
     * @return Chosen agent.
     */
    @Override
    public AgentPathCandidate chooseCandidate(ArrayList<AgentPathCandidate> candidates) {
        Collections.sort(candidates, new Comparator<AgentPathCandidate>() {
            public int compare(AgentPathCandidate one, AgentPathCandidate two) {
                return Double.compare(one.getAgent().getPos().distance(one.getPokemon().getLocation()),
                        two.getAgent().getPos().distance(two.getPokemon().getLocation()));
            }
        });

        return candidates.get(0);
    }

}
