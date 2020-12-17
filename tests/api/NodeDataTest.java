package api;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeDataTest {
    @Test
    void testDefaultValues(){
        node_data n = new NodeData();
        assertEquals("", n.getInfo());
        assertEquals(0, n.getTag());
    }
}