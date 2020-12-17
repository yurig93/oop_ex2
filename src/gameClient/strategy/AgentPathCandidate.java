package gameClient.strategy;

import api.node_data;
import gameClient.models.Agent;
import gameClient.models.Pokemon;

import java.util.List;

public class AgentPathCandidate {
    private Pokemon pokemon;
    private Agent agent;
    private double distance;
    private List<node_data> path;

    public AgentPathCandidate(Pokemon pokemon, Agent agent, double distance, List<node_data> path) {
        this.pokemon = pokemon;
        this.agent = agent;
        this.distance = distance;
        this.path = path;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public Agent getAgent() {
        return agent;
    }

    public double getDistance() {
        return distance;
    }

    public List<node_data> getPath() {
        return path;
    }
}