package gameClient;

import api.DWGraph_DS;
import api.NodeData;
import api.directed_weighted_graph;
import gameClient.models.Agent;
import gameClient.models.Pokemon;
import gameClient.strategy.AgentPathCandidate;
import gameClient.strategy.HeatmapStrategy;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;


class HeatmapStrategyTest {

    @Test
    void chooseCandidate() {
        directed_weighted_graph graph = new DWGraph_DS();
        HeatmapStrategy s = new HeatmapStrategy(graph);

        NodeData n1 = new NodeData(1);
        NodeData n2 = new NodeData(2);
        NodeData n3 = new NodeData(3);
        NodeData n4 = new NodeData(4);

        // Create a world 1000x1000 with nodes in all corners
        n1.setLocation(new Point3D(1, 1));
        n2.setLocation(new Point3D(1, 1000));
        n3.setLocation(new Point3D(1000, 0));
        n4.setLocation(new Point3D(1000, 1000));

        Pokemon p1 = new Pokemon(new Point3D(1, 1), 1, 1, 1, null);
        Pokemon p2 = new Pokemon(new Point3D(1, 1000), 1, 1, 1, null);
        Pokemon p3 = new Pokemon(new Point3D(1000, 1), 1, 1, 1, null);

        ArrayList<AgentPathCandidate> candidates = new ArrayList<>();
        candidates.add(new AgentPathCandidate(p1, new Agent(0), 1, new ArrayList<>()));
        candidates.add(new AgentPathCandidate(p2, new Agent(0), 100, new ArrayList<>()));
        candidates.add(new AgentPathCandidate(p3, new Agent(0), 500, new ArrayList<>()));

        AgentPathCandidate chosen = s.chooseCandidate(candidates);
        assertEquals(chosen, candidates.get(0));

        // Put a border on prev chosen location
        s.setBorders(new ConcurrentHashMap<>() {{
            Point3D p1Location = p1.getLocation();
            put("STAM", new Range2D(new Range(p1Location.x(), p1Location.x()),
                    new Range(p1Location.y(), p1Location.y())));

        }});

        // Closest outside border should be chosen
        chosen = s.chooseCandidate(candidates);
        assertEquals(chosen, candidates.get(1));


        // Put a border on same location
        s.setBorders(new ConcurrentHashMap<>() {{
            // NO "FREE" space left, choose the closet to me (best effort).
            put("STAM", new Range2D(new Range(1, 1000),
                    new Range(1, 1000)));

        }});

        // Choose the closet to me since all pokemons are either way bordered.
        chosen = s.chooseCandidate(candidates);
        assertEquals(chosen, candidates.get(0));
    }
}