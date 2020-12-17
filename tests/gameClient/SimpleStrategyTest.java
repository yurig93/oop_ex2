package gameClient;

import api.DWGraph_DS;
import api.directed_weighted_graph;
import gameClient.models.Agent;
import gameClient.models.Pokemon;
import gameClient.strategy.IStrategy;
import gameClient.strategy.SimpleStrategy;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class SimpleStrategyTest {


    static IStrategy getStrategy(String worldPath, String pokemonPath) throws IOException {
        FileInputStream fis = new FileInputStream(worldPath);
        String data = new String(fis.readAllBytes(), StandardCharsets.UTF_8);
        directed_weighted_graph g = DWGraph_DS.fromMatalaJSON(data);

        FileInputStream fisPokemons = new FileInputStream(pokemonPath);
        String dataPokemons = new String(fisPokemons.readAllBytes(), StandardCharsets.UTF_8);
        ArrayList<Pokemon> pokemons = GameEngine.pokemonsFromJSON(dataPokemons);

        ConcurrentHashMap<String, Pokemon> mapPokemons = new ConcurrentHashMap<>();
        pokemons.forEach(p -> {
            p.set_edge(g.getEdge(0, 1));
            mapPokemons.put(p.getId(), p);
        });

        SimpleStrategy s = new SimpleStrategy(g);
        s.setPokemons(mapPokemons);
        return s;
    }


    static ConcurrentHashMap<Integer, Agent> generateAgentsForStrategy(IStrategy s, int num) {
        ConcurrentHashMap<Integer, Agent> agents = new ConcurrentHashMap<>();

        for (int i = 0; i < num; ++i) {
            Agent a = new Agent(i);
            a.setSrcNode(i);
            agents.put(a.getId(), a);
        }

        if (s != null) {
            s.setAgents(agents);
        }
        return agents;
    }


    @Test
    void testInitialPlacement() throws IOException {

        IStrategy s = getStrategy("tests/gameClient/data/A5",
                "tests/gameClient/data/A5-Pokemons.json");

        directed_weighted_graph g = s.getGraph();
        ArrayList<Integer> placements = s.getInitialAgentPlacements(1);
        ArrayList<Integer> expected = new ArrayList<>() {{
            add(0);
        }};

        assertArrayEquals(placements.toArray(), expected.toArray());
    }

    @Test
    void testPathCalculation() throws IOException {

        IStrategy s = getStrategy("tests/gameClient/data/A5",
                "tests/gameClient/data/A5-Pokemons.json");

        ConcurrentHashMap<Integer, Agent> agents = generateAgentsForStrategy(s, 1);
        boolean calculated = s.calculateAgentPaths();
        assertTrue(calculated);

        Agent a = agents.get(0);
        assertNotNull(a.getCalculatedPath());

        assertEquals(a.getCalculatedPath().size(), 2);
        assertFalse(a.isFree());
        assertFalse(a.isMoving());
    }

    @Test
    void testCommandGeneration() throws IOException {
        IStrategy s = getStrategy("tests/gameClient/data/A5",
                "tests/gameClient/data/A5-Pokemons.json");
        ConcurrentHashMap<Integer, Agent> agents = generateAgentsForStrategy(s, 1);

        assertTrue(s.calculateAgentPaths());
        HashMap<Integer, Integer> commands = s.generateMoveCommands();
        assertEquals(commands.size(), 1);
        assertEquals(commands.get(0), 1);

        commands = s.generateMoveCommands();
        assertEquals(commands.size(), 0);
    }
}