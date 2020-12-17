package gameClient;

import api.NodeData;
import gameClient.models.Agent;
import gameClient.models.Pokemon;
import gameClient.util.Point3D;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AgentTest {

    @Test
    void updateFromInstance() {
        Agent a1 = new Agent(1);
        a1.setSrcNode(123);
        a1.setDestNode(321);
        a1.setValue(111);
        a1.setAssignedPokemon(new Pokemon(new Point3D(1, 2), 1, 1, 1, null));
        a1.setCalculatedPath(new ArrayList<>() {{
            new NodeData(13312);
        }});
        a1.setPos(new Point3D(1, 3));

        Agent a2 = new Agent(2);
        a2.updateFromInstance(a1);

        assertEquals(a1.getSrcNode(), a2.getSrcNode());
        assertEquals(a1.getCalculatedPath(), a2.getCalculatedPath());
        assertEquals(a1.getPos(), a2.getPos());
        assertEquals(a1.getAssignedPokemon(), a2.getAssignedPokemon());
        assertEquals(a1.getValue(), a2.getValue());
    }

    @Test
    void updateFromJSON() throws JSONException {
        Agent a1 = new Agent(1);

        JSONObject j = new JSONObject();
        j.put("id", 123);
        j.put("pos", "1,2,3");
        j.put("src", 1);
        j.put("dest", 1);
        j.put("value", 555);


        JSONObject container = new JSONObject();
        container.put("Agent", j);
        a1.updateFromJSON(container.toString());

        assertEquals(a1.getSrcNode(), j.getInt("src"));
        assertEquals(a1.getDestNode(), j.getInt("dest"));
        assertEquals(a1.getValue(), j.getDouble("value"));
        assertEquals(a1.getPos(), new Point3D(j.getString("pos")));
    }

    @Test
    void isMoving() {
        Agent a = new Agent(1);
        a.setDestNode(123);
        assertTrue(a.isMoving());
        a.setDestNode(-1);
        assertFalse(a.isMoving());
    }

    @Test
    void isFree() {
        Agent a = new Agent(1);
        assertTrue(a.isFree());
        a.setAssignedPokemon(new Pokemon(new Point3D(1, 1), 1, 1, 1, null));
        assertFalse(a.isFree());
        a.setFree();
        assertTrue(a.isFree());
    }
}