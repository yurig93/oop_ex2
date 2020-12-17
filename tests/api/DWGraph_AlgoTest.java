
package api;


import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_AlgoTest {

    @Test
    void isConnected() {
        directed_weighted_graph g0 = new DWGraph_DS();
        g0.addNode(new NodeData(1));
        g0.addNode(new NodeData(2));

        dw_graph_algorithms ag0 = new DWGraph_Algo();
        ag0.init(g0);
        assertFalse(ag0.isConnected());

        g0.connect(1,2,1);
        g0.connect(2,1,1);
        assertTrue(ag0.isConnected());

        g0.addNode(new NodeData(3));
        g0.connect(2,3,1);
        assertFalse(ag0.isConnected());
    }

    @Test
    void shortestPathDist() {
            directed_weighted_graph g0 = small_graph();
            dw_graph_algorithms ag0 = new DWGraph_Algo();
            ag0.init(g0);
            double d = ag0.shortestPathDist(0,10);
            assertEquals(d, 5.1);


    }

    @Test
    void shortestPath() {
        directed_weighted_graph g0 = small_graph();
        dw_graph_algorithms ag0 = new DWGraph_Algo();
        ag0.init(g0);
        List<node_data> sp = ag0.shortestPath(0,10);
        //double[] checkTag = {0.0, 1.0, 2.0, 3.1, 5.1};
        int[] checkKey = {0, 1, 5, 7, 10};
        int i = 0;
        for(node_data n: sp) {
        	//assertEquals(n.getTag(), checkTag[i]);
        	assertEquals(n.getKey(), checkKey[i]);
        	i++;
        }
    }
    
    @Test
    void save_load() {
        directed_weighted_graph g0 = DWGraph_DSTest.graph_creator(10,30,1);
        node_data n = new NodeData();
        n.setLocation(new GeoLocation(1,2,3));
        g0.addNode(n);

        dw_graph_algorithms ag0 = new DWGraph_Algo();
        ag0.init(g0);
        String str = "g0.json";
        ag0.save(str);
        dw_graph_algorithms ag1 = new DWGraph_Algo();

        ag1.load(str);
        assertEquals(g0, ag1.getGraph());
        g0.removeNode(0);
        assertNotEquals(g0,ag1.getGraph());
    }

    private directed_weighted_graph small_graph() {
        directed_weighted_graph g0 = DWGraph_DSTest.graph_creator(11,0,1);
        g0.connect(0,1,1);
        g0.connect(0,2,2);
        g0.connect(0,3,3);

        g0.connect(1,4,17);
        g0.connect(1,5,1);
        g0.connect(2,4,1);
        g0.connect(3, 5,10);
        g0.connect(3,6,100);
        g0.connect(5,7,1.1);
        g0.connect(6,7,10);
        g0.connect(7,10,2);
        g0.connect(6,8,30);
        g0.connect(8,10,10);
        g0.connect(4,10,30);
        g0.connect(3,9,10);
        g0.connect(8,10,10);

        return g0;
    }
}
