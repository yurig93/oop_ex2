package gameClient.strategy;

import api.directed_weighted_graph;
import gameClient.models.Agent;
import gameClient.models.Pokemon;
import gameClient.util.Range2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public interface IStrategy {
    /**
     * @return Places agents on map based on strategy.
     */
    ArrayList<Integer> getInitialAgentPlacements(int maxAgents);

    /**
     * Calculates distance data for all free agents and pokemons.
     * @return true if new paths were set else false.
     */
    boolean calculateAgentPaths();

    /**
     * Generates move command based on previosuly calculated paths for agents.
     * @return HashMap with Agent ID as key and dest node ID as value.
     */
    HashMap<Integer, Integer> generateMoveCommands();

    void setPokemons(ConcurrentHashMap<String, Pokemon> pokemons);

    ConcurrentHashMap<Integer, Agent> getAgents();

    void setAgents(ConcurrentHashMap<Integer, Agent> agents);

    /**
     * @return Borders set.
     */
    ConcurrentHashMap<String, Range2D> getBorders();

    /**
     * @return Set graph.
     */
    directed_weighted_graph getGraph();

}