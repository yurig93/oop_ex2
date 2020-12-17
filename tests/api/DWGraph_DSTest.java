package api;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_DSTest {
    private static Random _rnd = null;

    /**
     * Generate a random graph with v_size nodes and e_size edges
     *
     * @param v_size
     * @param e_size
     * @param seed
     * @return
     */
    public static directed_weighted_graph graph_creator(int v_size, int e_size, int seed) {
        directed_weighted_graph g = new DWGraph_DS();
        _rnd = new Random(seed);
        for (int i = 0; i < v_size; i++) {
            g.addNode(new NodeData(i));
        }
        // Iterator<node_data> itr = V.iterator(); // Iterator is a more elegant and generic way, but KIS is more important
        int[] nodes = nodes(g);
        while (g.edgeSize() < e_size) {
            int a = nextRnd(0, v_size);
            int b = nextRnd(0, v_size);
            int i = nodes[a];
            int j = nodes[b];
            double w = _rnd.nextDouble();
            g.connect(i, j, w);
        }
        return g;
    }

    private static int nextRnd(int min, int max) {
        double v = nextRnd(0.0 + min, (double) max);
        int ans = (int) v;
        return ans;
    }

    private static double nextRnd(double min, double max) {
        double d = _rnd.nextDouble();
        double dx = max - min;
        double ans = d * dx + min;
        return ans;
    }

    /**
     * Simple method for returning an array with all the node_data of the graph,
     * Note: this should be using an Iterator<node_edge> to be fixed in Ex1
     *
     * @param g
     * @return
     */
    private static int[] nodes(directed_weighted_graph g) {
        int size = g.nodeSize();
        Collection<node_data> V = g.getV();
        node_data[] nodes = new node_data[size];
        V.toArray(nodes); // O(n) operation
        int[] ans = new int[size];
        for (int i = 0; i < size; i++) {
            ans[i] = nodes[i].getKey();
        }
        Arrays.sort(ans);
        return ans;
    }

    @Test
    void nodeSize() {
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData(0));
        g.addNode(new NodeData(1));
        g.addNode(new NodeData(1));

        g.removeNode(2);
        g.removeNode(1);
        g.removeNode(1);
        int s = g.nodeSize();
        assertEquals(1, s);

    }

    @Test
    void copy() {
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData(0));
        g.addNode(new NodeData(1));
        g.addNode(new NodeData(2));
        g.addNode(new NodeData(3));
        g.connect(0, 1, 1);
        g.connect(0, 2, 2);
        g.connect(0, 3, 3);
        g.connect(0, 1, 1);

        directed_weighted_graph copiedGraph = new DWGraph_DS(g);

        g.getNode(0).setTag(111);
        copiedGraph.getNode(0).setTag(-111);
        g.removeEdge(0, 1);

        assertNotEquals(g.getNode(0).getTag(), copiedGraph.getNode(0).getTag());
        assertNotEquals(g.edgeSize(), copiedGraph.edgeSize());
    }


    @Test
    void isConnectedFalse() {
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData(0));
        g.addNode(new NodeData(1));
        g.addNode(new NodeData(2));
        g.addNode(new NodeData(3));
        g.addNode(new NodeData(4));
        g.addNode(new NodeData(5));
        g.addNode(new NodeData(6));
        g.addNode(new NodeData(7));


        g.connect(0, 1, 1);
        g.connect(1, 2, 1);
        g.connect(2, 1, 1);
        g.connect(2, 2, 1);
        g.connect(2, 3, 1);
        g.connect(3, 5, 1);
        g.connect(5, 4, 1);
        g.connect(4, 3, 1);
        g.connect(1, 6, 1);
        g.connect(6, 7, 1);
        g.connect(7, 0, 1);

        DWGraph_Algo algo = new DWGraph_Algo();
        algo.init(g);
        assertFalse(algo.isConnected());
    }

    @Test
    void isConnectedTrue() {
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData(0));
        g.addNode(new NodeData(1));
        g.addNode(new NodeData(2));
        g.addNode(new NodeData(3));
        g.addNode(new NodeData(4));
        g.addNode(new NodeData(5));
        g.addNode(new NodeData(6));
        g.addNode(new NodeData(7));


        g.connect(0, 1, 1);
        g.connect(1, 2, 1);
        g.connect(2, 1, 1);
        g.connect(2, 2, 1);
        g.connect(2, 3, 1);
        g.connect(3, 5, 1);
        g.connect(5, 4, 1);
        g.connect(4, 3, 1);
        g.connect(5, 6, 1);
        g.connect(6, 7, 1);
        g.connect(7, 0, 1);

        DWGraph_Algo algo = new DWGraph_Algo();
        algo.init(g);
        assertTrue(algo.isConnected());
    }


    @Test
    void edgeSize() {
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData(0));
        g.addNode(new NodeData(1));
        g.addNode(new NodeData(2));
        g.addNode(new NodeData(3));

        g.connect(0, 1, 1);
        g.connect(0, 2, 2);
        g.connect(0, 3, 3);
        g.connect(0, 1, 1);
        int e_size = g.edgeSize();
        assertEquals(3, e_size);
        edge_data w03 = g.getEdge(0, 3);
        edge_data w30 = g.getEdge(3, 0);
        assertEquals(w03.getWeight(), 3);
        assertNull(w30);
    }

    @Test
    void getV() {
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData(0));
        g.addNode(new NodeData(1));
        g.addNode(new NodeData(2));
        g.addNode(new NodeData(3));

        g.connect(0, 1, 1);
        g.connect(0, 2, 2);
        g.connect(0, 3, 3);
        g.connect(0, 1, 1);
        Collection<node_data> v = g.getV();
        Iterator<node_data> iter = v.iterator();
        while (iter.hasNext()) {
            node_data n = iter.next();
            assertNotNull(n);
        }
    }


    ///////////////////////////////////

/*    @Test
    void hasEdge() {
        int v = 10, e = v * (v - 1) / 2;
        directed_weighted_graph g = graph_creator(v, e, 1);
        for (int i = 0; i < v; i++) {
            for (int j = i + 1; j < v; j++) {
                boolean b = g.hasEdge(i, j);
                assertTrue(b);
                assertTrue(g.hasEdge(j, i));
            }
        }
    }*/

    @Test
    void connect() {
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData(0));
        g.addNode(new NodeData(1));
        g.addNode(new NodeData(2));
        g.addNode(new NodeData(3));

        g.connect(0, 1, 1);
        g.connect(0, 2, 2);
        g.connect(0, 3, 3);
        g.removeEdge(0, 1);
//        assertFalse(g.hasEdge(1, 0));
        g.removeEdge(2, 1);
        g.connect(0, 1, 1);
        edge_data w = g.getEdge(1, 0);
        assertNull(w);
        assertEquals(g.getEdge(0, 1).getWeight(), 1);

    }

    @Test
    void removeNode() {
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData(0));
        g.addNode(new NodeData(1));
        g.addNode(new NodeData(2));
        g.addNode(new NodeData(3));

        g.connect(0, 1, 1);
        g.connect(0, 2, 2);
        g.connect(0, 3, 3);
        g.removeNode(4);
        g.removeNode(0);
//        assertFalse(g.hasEdge(1, 0));
        int e = g.edgeSize();
        assertEquals(0, e);
        assertEquals(3, g.nodeSize());
    }

    @Test
    void removeEdge() {
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData(0));
        g.addNode(new NodeData(1));
        g.addNode(new NodeData(2));
        g.addNode(new NodeData(3));

        g.connect(0, 1, 1);
        g.connect(0, 2, 2);
        g.connect(0, 3, 3);
        g.removeEdge(0, 3);
        edge_data w = g.getEdge(0, 3);
        assertNull(w);
    }
}
