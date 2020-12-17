package api;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DWGraph_DS_TestCustom {
    @Test
    void testCopy() {
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData(0));
        g.addNode(new NodeData(1));
        g.addNode(new NodeData(1));
        g.connect(0,1,1);

        directed_weighted_graph g2 = new DWGraph_DS(g);
        assertEquals(g, g2);
    }

}
