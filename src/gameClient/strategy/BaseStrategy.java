package gameClient.strategy;

import api.*;
import gameClient.models.Agent;
import gameClient.models.Pokemon;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A base class for strategy implementation.
 */
abstract public class BaseStrategy implements IStrategy {
    public static int PARTS_SCREEN = 3;

    protected final dw_graph_algorithms algo;
    protected ConcurrentHashMap<String, Pokemon> pokemons;
    protected ConcurrentHashMap<Integer, Agent> agents;
    protected Range2D graphRange;
    protected double borderSizeX, borderSizeY;
    protected ConcurrentHashMap<String, Range2D> borders;

    public BaseStrategy(directed_weighted_graph g) {
        this.algo = new DWGraph_Algo();
        this.algo.init(g);
        this.graphRange = Range2Range.GraphRange(g);
        this.borders = new ConcurrentHashMap<>();
        this.borderSizeX = this.graphRange.get_x_range().get_length() / PARTS_SCREEN;
        this.borderSizeY = this.graphRange.get_y_range().get_length() / PARTS_SCREEN * 2;
    }

    /**
     * @param maxAgents Number of max agents.
     * @return A list of node ids to attach to.
     */
    public ArrayList<Integer> getInitialAgentPlacements(int maxAgents) {
        ArrayList<Integer> targetNodes = new ArrayList<>();

        for (Pokemon p : this.pokemons.values()) {
            edge_data edge = p.get_edge();

            if (edge != null) {
                targetNodes.add(edge.getSrc());
            }

            if (targetNodes.size() >= maxAgents) {
                break;
            }
        }
        return targetNodes;
    }

    /**
     * Generates move command based on previosuly calculated paths for agents.
     * @return HashMap with Agent ID as key and dest node ID as value.
     */
    public HashMap<Integer, Integer> generateMoveCommands() {
        HashMap<Integer, Integer> commands = new HashMap<>();

        for (Agent a : this.agents.values()) {
            if (!a.isFree() && !a.isMoving()) {
                // ignore self if in src already
                while (a.getCalculatedPath().size() > 0) {
                    node_data nextNode = a.getCalculatedPath().get(0);
                    a.getCalculatedPath().remove(0);
                    if (nextNode.getKey() != a.getSrcNode()) {
                        commands.put(a.getId(), nextNode.getKey());
                        break;
                    }
                }
            }
        }
        return commands;
    }

    /**
     * Calculates distance data for all free agents and pokemons. Then issues a decision request to chooseAndSetCandidate.
     * @return true if new paths were set else false.
     */
    public boolean calculateAgentPaths() {
        boolean newPathsSet = false;

        for (Agent a : this.agents.values()) {
            if (!a.isFree()) {
                continue;
            }

            ArrayList<AgentPathCandidate> candidates = new ArrayList<>();

            for (Pokemon c : this.pokemons.values()) {
                if (c.isBeingHandled()) {
                    continue;
                }

                List<node_data> path = new LinkedList<>();
                int edgeHead = c.get_edge().getSrc();
                int edgeEnd = c.get_edge().getDest();
                double totalPathDist = 0;

                // From current agent position to edge head.
                if (a.getSrcNode() != edgeHead) {
                    double srcToEdgeHeadDist = this.algo.shortestPathDist(a.getSrcNode(), edgeHead);
                    if (srcToEdgeHeadDist == DWGraph_Algo.INVALID_DIST) {
                        continue;
                    }
                    totalPathDist += srcToEdgeHeadDist;
                    path.addAll(this.algo.shortestPath(a.getSrcNode(), edgeHead));
                }

                // From edge head to edge end.
                if (edgeHead != edgeEnd) {
                    double edgeHeadToEdgeEndDist = this.algo.shortestPathDist(edgeHead, edgeEnd);
                    if (edgeHeadToEdgeEndDist == DWGraph_Algo.INVALID_DIST) {
                        continue;
                    }
                    totalPathDist += edgeHeadToEdgeEndDist;
                    path.addAll(this.algo.shortestPath(edgeHead, edgeEnd));
                }

                candidates.add(new AgentPathCandidate(c, a, totalPathDist, path));
            }

            if (candidates.size() > 0) {
                AgentPathCandidate chosenCandidate = this.chooseAndSetCandidate(candidates);
                if (chosenCandidate != null) {
                    newPathsSet = true;
                }
            }
        }

        return newPathsSet;
    }

    /**
     * @param candidates Generated distance data for all agents and pokemon.
     * @return Chosen agent to perform the collection.
     */
    abstract AgentPathCandidate chooseCandidate(ArrayList<AgentPathCandidate> candidates);

    protected AgentPathCandidate setCandidate(AgentPathCandidate chosen) {
        if (chosen != null) {
            chosen.getAgent().setCalculatedPath(new ArrayList<>(chosen.getPath()));
            chosen.getAgent().setAssignedPokemon(chosen.getPokemon());
            chosen.getPokemon().setHandlingAgent(chosen.getAgent().getId());
        }
        return chosen;
    }

    /**
     * Find the best agent to perform the collection and binds the agent with the pokemon.
     * @param candidates Generated distance data for all agents and pokemon.
     * @returnChosen agent to perform the collection.
     */
    protected AgentPathCandidate chooseAndSetCandidate(ArrayList<AgentPathCandidate> candidates) {
        AgentPathCandidate chosen = this.chooseCandidate(candidates);
        return this.setCandidate(chosen);
    }

    public void setPokemons(ConcurrentHashMap<String, Pokemon> pokemons) {
        this.pokemons = pokemons;
    }

    public ConcurrentHashMap<String, Range2D> getBorders() {
        return borders;
    }

    public void setBorders(ConcurrentHashMap<String, Range2D> borders) {
        this.borders = borders;
    }

    public directed_weighted_graph getGraph() {
        return this.algo.getGraph();
    }

    @Override
    public ConcurrentHashMap<Integer, Agent> getAgents() {
        return agents;
    }

    public void setAgents(ConcurrentHashMap<Integer, Agent> agents) {
        this.agents = agents;
    }
}
