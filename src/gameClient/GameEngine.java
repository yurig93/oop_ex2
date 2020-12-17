package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import gameClient.models.Agent;
import gameClient.models.Pokemon;
import gameClient.strategy.IStrategy;
import gameClient.strategy.StrategyFactory;
import gameClient.strategy.StrategyType;
import gameClient.util.Point3D;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Main Game logic engine.
 */
public class GameEngine implements Runnable {
    public static final double EPS1 = 0.001, EPS2 = EPS1 * EPS1;
    public static final int DEFAULT_MAX_AGENTS = 10;

    private final int loginId;
    private final int scenario;
    private final IStrategy gameStrategy;
    private final ConcurrentHashMap<String, Pokemon> pokemons = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Agent> agents = new ConcurrentHashMap<>();
    private game_service gameService;
    private directed_weighted_graph g;
    private EngineStatus status;


    /**
     * @param loginId ID of student.
     * @param scenario Scenario ID.
     * @param strategy Chosen strategy for the game.
     */
    GameEngine(int loginId, int scenario, StrategyType strategy) {
        this.loginId = loginId;
        this.scenario = scenario;

        // There is a bug with the server. If you try to get an instance with a bad scenario AFTER a successful
        // instance creation with a good scenario, you will get a good instance with the old good scenario.
        this.gameService = Game_Server_Ex2.getServer(this.scenario);

        if (this.gameService == null) {
            throw new RuntimeException("Got no server for scenario " + scenario);
        }

        this.gameService.login(this.loginId);
        this.setGraph(DWGraph_DS.fromMatalaJSON(this.gameService.getGraph()));
        this.gameStrategy = StrategyFactory.getStrategy(strategy, this.getGraph());
        this.status = EngineStatus.STOPPED;
    }

    public static ArrayList<Pokemon> pokemonsFromJSON(String fs) {

        ArrayList<Pokemon> ans = new ArrayList<>();
        try {
            JSONObject ttt = new JSONObject(fs);
            JSONArray ags = ttt.getJSONArray("Pokemons");
            for (int i = 0; i < ags.length(); i++) {
                JSONObject pp = ags.getJSONObject(i);
                JSONObject pk = pp.getJSONObject("Pokemon");
                int t = pk.getInt("type");
                double v = pk.getDouble("value");
                String p = pk.getString("pos");
                Pokemon f = new Pokemon(new Point3D(p), t, v, 0, null);
                ans.add(f);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ans;
    }

    private static boolean isOnEdge(geo_location p, geo_location src, geo_location dest) {
        boolean ans = false;
        double dist = src.distance(dest);
        double d1 = src.distance(p) + p.distance(dest);
        if (dist > d1 - EPS2) {
            ans = true;
        }
        return ans;
    }

    private static boolean isOnEdge(geo_location p, int s, int d, directed_weighted_graph g) {
        geo_location src = g.getNode(s).getLocation();
        geo_location dest = g.getNode(d).getLocation();
        return isOnEdge(p, src, dest);
    }

    /**
     * @param p Location in question.
     * @param e Edge in question.
     * @param type Direction.
     * @param g Nodes graph.
     * @return true if the object is on the edge else false.
     */
    private static boolean isOnEdge(geo_location p, edge_data e, int type, directed_weighted_graph g) {
        int src = g.getNode(e.getSrc()).getKey();
        int dest = g.getNode(e.getDest()).getKey();

        if (type < 0 && dest > src) {
            return false;
        }
        if (type > 0 && src > dest) {
            return false;
        }
        return isOnEdge(p, src, dest, g);
    }

    /**
     * @param fs Agents JSON.
     * @return A list of Agents.
     */
    public static ArrayList<Agent> agentsFromJSON(String fs) {
        ArrayList<Agent> ans = new ArrayList<>();
        try {
            JSONObject ttt = new JSONObject(fs);
            JSONArray ags = ttt.getJSONArray("Agents");
            for (int i = 0; i < ags.length(); i++) {
                Agent c = new Agent(-1);
                c.updateFromJSON(ags.get(i).toString());
                ans.add(c);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ans;
    }

    private int getMaxAgents() {
        // Although this API is not exposed, I'd still will want to know the number of allowed agents before setting them.
        try {
            JSONObject gsInfoJson = new JSONObject(this.gameService.toString());
            JSONObject gs = gsInfoJson.getJSONObject("GameServer");
            return gs.getInt("agents");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return DEFAULT_MAX_AGENTS;
    }

    public int getScore() {
        // Although this API is not exposed, I'd still will want to know the number of allowed agents before setting them.
        try {
            JSONObject gsInfoJson = new JSONObject(this.gameService.toString());
            JSONObject gs = gsInfoJson.getJSONObject("GameServer");
            return gs.getInt("grade");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public directed_weighted_graph getGraph() {
        synchronized (this) {
            return this.g;
        }
    }

    public void setGraph(directed_weighted_graph g) {
        synchronized (this) {
            this.g = g;
        }
    }

    /**
     * Attaches pokemons to edges.
     * @param pokemon Pokemon to snap.
     * @return true if attached else false.
     */
    public boolean snapPokemonToEdge(Pokemon pokemon) {
        Iterator<node_data> itr = this.g.getV().iterator();
        while (itr.hasNext()) {
            node_data v = itr.next();
            Iterator<edge_data> iter = this.g.getE(v.getKey()).iterator();
            while (iter.hasNext()) {
                edge_data e = iter.next();
                boolean f = isOnEdge(pokemon.getLocation(), e, pokemon.getType(), this.g);
                if (f) {
                    pokemon.set_edge(e);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Refreshes pokemons from the server.
     */
    private void refreshPokemons() {
        String pokemonsJSON = this.gameService.getPokemons();
        ArrayList<Pokemon> pokemons = pokemonsFromJSON(pokemonsJSON);
        ArrayList<String> freshPokemons = new ArrayList<>();

        // This doesn't allow for a pokemon to change internal state.
        pokemons.forEach(p -> {
            String pokemonID = p.getId();
            if (!this.pokemons.containsKey(pokemonID)) {
                this.snapPokemonToEdge(p);
                this.pokemons.put(pokemonID, p);
            }
            freshPokemons.add(pokemonID);
        });

        // Clean stale pokemons
        ArrayList<String> oldPokemons = new ArrayList<>(this.pokemons.keySet());
        oldPokemons.removeAll(freshPokemons);
        oldPokemons.forEach(this.pokemons::remove);
    }


    /**
     * Refreshes agents from server. Free agents without a pokemon on map.
     */
    private void refreshAgents() {
        String agentsJSON = this.gameService.getAgents();

        ArrayList<Agent> agents = agentsFromJSON(agentsJSON);

        for (Agent a : agents) {
            if (this.agents.containsKey(a.getId())) {
                this.agents.get(a.getId()).updateFromInstance(a);
            } else {
                this.agents.put(a.getId(), a);
            }

            Agent savedAgent = this.agents.get(a.getId());

            // If agent doesnt have position and has src node then set it to it.
            if (savedAgent.getPos() == null && savedAgent.getSrcNode() != -1) {
                savedAgent.setPos(this.g.getNode(savedAgent.getSrcNode()).getLocation());
            }

            Pokemon assigned_pokemon = savedAgent.getAssignedPokemon();

            // Free agent if he is done.
            if (assigned_pokemon != null) {
                if (!this.pokemons.containsKey(assigned_pokemon.getId()) && a.getDestNode() == Agent.NO_DEST) {
                    savedAgent.setFree();
                }
            }
        }
    }

    public ConcurrentHashMap<Integer, Agent> getAgents() {
        synchronized (this) {
            return this.agents;
        }
    }

    /**
     * @return Places agents on map based on strategy.
     */
    private int placeAgents() {
        int added = 0;
        ArrayList<Integer> targetNodes = this.gameStrategy.getInitialAgentPlacements(this.getMaxAgents());
        for (int i : targetNodes) {
            boolean success = this.gameService.addAgent(i);
            if (!success) {
                break;
            }
            ++added;
        }
        return added;
    }

    public ConcurrentHashMap<String, Pokemon> getPokemons() {
        synchronized (this) {
            return this.pokemons;
        }
    }

    public IStrategy getGameStrategy() {
        return gameStrategy;
    }

    public game_service getGameService() {
        return gameService;
    }

    public EngineStatus getStatus() {
        return status;
    }

    /**
     * Main logic loop.
     */
    @Override
    public void run() {
        this.status = EngineStatus.RUNNING;

        this.refreshPokemons();
        this.gameStrategy.setPokemons(this.pokemons);
        this.placeAgents();
        this.refreshAgents();
        this.gameStrategy.setAgents(this.agents);

        this.gameService.startGame();
        while (this.gameService.isRunning()) {
            this.refreshPokemons();
            this.gameStrategy.setPokemons(this.pokemons);
            this.refreshAgents();
            this.gameStrategy.calculateAgentPaths();

            HashMap<Integer, Integer> asd = this.gameStrategy.generateMoveCommands();
            asd.forEach((k, v) -> {
                System.out.println(k + " -> " + v);
                this.gameService.chooseNextEdge(k, v);
            });

            this.gameService.move();

            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                this.status = EngineStatus.STOPPING;
                break;
            }
            // No sleep here, got other matalot to do :(. But i'd measure the travel time of the agents with distance delta,
            // and will calculate the next move to be the min sleep time to destination based on the calcs.  ^
            // This will ensure that we will issue a move command on the "exact" point in time.
        }

        this.gameService.stopGame();
        System.out.print(this.gameService);
        this.status = EngineStatus.STOPPED;
    }
}
