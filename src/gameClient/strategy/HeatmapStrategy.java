package gameClient.strategy;

import api.directed_weighted_graph;
import api.geo_location;
import gameClient.models.Pokemon;
import gameClient.util.Range;
import gameClient.util.Range2D;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class HeatmapStrategy extends SimpleStrategy implements IStrategy {

    public HeatmapStrategy(directed_weighted_graph g) {
        super(g);
    }

    /**
     * Sets a border using a chosen agent result.
     * @param c Chosen candidate.
     * @return Generated Border.
     */
    private Range2D setBorderByCandidate(AgentPathCandidate c) {
        double x = c.getPokemon().getLocation().x();
        double y = c.getPokemon().getLocation().y();

        Range2D r = new Range2D(new Range(x - this.borderSizeX / 2, x + this.borderSizeX / 2),
                new Range(y - this.borderSizeY / 2, y + this.borderSizeY / 2));
        this.borders.put(c.getPokemon().getId(), r);
        return r;
    }

    private boolean isGeoLocationInBorders(geo_location g) {
        Iterator it = this.borders.values().iterator();
        while (it.hasNext()) {
            Range2D r = (Range2D) it.next();
            if (r.isGeoLocationInRange(g)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Chooses agent by finding the closest pokemon outside set borders (best effort). If none found then it gets closest pokemon.
     * @param candidates Generated distance data for all agents and pokemon.
     * @return Chosen agent.
     */
    @Override
    public AgentPathCandidate chooseCandidate(ArrayList<AgentPathCandidate> candidates) {
        Collections.sort(candidates, new Comparator<AgentPathCandidate>() {
            public int compare(AgentPathCandidate one, AgentPathCandidate two) {
                return Double.compare(one.getDistance(), two.getDistance());
            }
        });

        AgentPathCandidate chosen = candidates.get(0);

        for (AgentPathCandidate c : candidates) {
            if (!isGeoLocationInBorders(c.getPokemon().getLocation())) {
                chosen = c;
                break;
            }
        }

        // Bind Agent and Pokemon
        if (chosen != null) {
            chosen.getAgent().setCalculatedPath(new ArrayList<>(chosen.getPath()));
            chosen.getAgent().setAssignedPokemon(chosen.getPokemon());
            chosen.getPokemon().setHandlingAgent(chosen.getAgent().getId());
        }
        return chosen;
    }

    @Override
    protected AgentPathCandidate chooseAndSetCandidate(ArrayList<AgentPathCandidate> candidates) {
        AgentPathCandidate chosen = super.chooseAndSetCandidate(candidates);
        this.setBorderByCandidate(chosen);
        return chosen;
    }

    @Override
    public void setPokemons(ConcurrentHashMap<String, Pokemon> pokemons) {
        super.setPokemons(pokemons);
        Iterator<Map.Entry<String, Range2D>> iter = this.borders.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, Range2D> entry = iter.next();
            if (!this.pokemons.containsKey(entry.getKey())) {
                iter.remove();
            }
        }
    }

}
