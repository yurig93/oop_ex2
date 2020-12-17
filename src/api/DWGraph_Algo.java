package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DWGraph_Algo implements dw_graph_algorithms {
    public static final double DEFAULT_INF = Double.POSITIVE_INFINITY;
    public static final int DEFAULT_NOT_FOUND = -1;
    public static final double INVALID_DIST = -1;

    private static final int STATUS_NOT_VISITED = 0;
    private static final int STATUS_VISITED = 1;
    private static final int STATUS_QUEUED = 2;

    private ArrayList<ArrayList<Integer>> components;
    private HashMap<Integer, Integer> nodeIdToLowlink;
    private ArrayList<Integer> inStack;
    private Stack<Integer> tarjanStack;
    private int lowLinkCounter;

    private directed_weighted_graph g;
    private int lastVisitedCount;
    private double lastVisitedEdgeSum;

    public DWGraph_Algo() {
        this.g = null;
        this.lastVisitedCount = 0;
        this.lastVisitedEdgeSum = 0;
    }

    @Override
    public void init(directed_weighted_graph g) {
        this.g = g;
        this.lastVisitedCount = 0;
        this.lastVisitedEdgeSum = 0;
    }

    @Override
    public directed_weighted_graph getGraph() {
        return this.g;
    }

    @Override
    public directed_weighted_graph copy() {
        return new DWGraph_DS(this.g);
    }

    @Override
    public boolean isConnected() {
        return !(tarjan().size() > 1);
    }

    private ArrayList<ArrayList<Integer>> tarjan() {
        this.components = new ArrayList<>();
        this.nodeIdToLowlink = new HashMap<>();
        this.inStack = new ArrayList<>();
        this.tarjanStack = new Stack<>();
        this.lowLinkCounter = 0;

        this.g.getV().forEach(node -> node.setTag(STATUS_NOT_VISITED));
        this.g.getV().forEach(node -> {
            if (node.getTag() == STATUS_NOT_VISITED) {
                tarjanDFS(node);
            }
        });

        return this.components;
    }

    private void tarjanDFS(node_data node) {
        node.setTag(STATUS_VISITED);
        this.tarjanStack.push(node.getKey());
        this.nodeIdToLowlink.put(node.getKey(), ++lowLinkCounter);

        boolean isRoot = true;
        Iterator<edge_data> it = this.g.getE(node.getKey()).iterator();
        while (it.hasNext()) {
            edge_data edge = it.next();
            node_data neighbour = this.g.getNode(edge.getDest());
            if (neighbour.getTag() == STATUS_NOT_VISITED) {
                this.tarjanDFS(neighbour);
            }
            int myLowlink = nodeIdToLowlink.get(node.getKey());
            int neighbourLowLink = nodeIdToLowlink.get(neighbour.getKey());

            if (tarjanStack.contains(neighbour.getKey()) &&
                    neighbourLowLink < myLowlink) {
                nodeIdToLowlink.put(node.getKey(), neighbourLowLink);
                isRoot = false;
            }
        }

        if (isRoot) {
            ArrayList<Integer> component = new ArrayList<>();
            while (true) {
                int nodeId = this.tarjanStack.pop();
                component.add(nodeId);
                nodeIdToLowlink.put(nodeId, nodeIdToLowlink.get(node.getKey()));
                if (node.getKey() == nodeId) {
                    break;
                }
            }
            this.components.add(component);
        }

    }

    @Override
    public double shortestPathDist(int src, int dest) {
        List<node_data> path = this.shortestPath(src, dest);
        return path != null ? this.lastVisitedEdgeSum : INVALID_DIST;
    }

    public List<node_data> backtrackPath(int src, node_data neededNode) {
        LinkedList<node_data> path = new LinkedList<>();

        if (neededNode != null && !neededNode.getInfo().isEmpty()) {
            node_data backtrackingNode = neededNode;
            while (backtrackingNode.getKey() != src) {
                path.offerFirst(backtrackingNode);
                if (!backtrackingNode.getInfo().isEmpty()) {
                    backtrackingNode = this.g.getNode(Integer.parseInt(backtrackingNode.getInfo()));
                }
            }
            path.offerFirst(backtrackingNode);
        }
        return path;
    }

    @Override
    public List<node_data> shortestPath(int src, int dest) {
        this.lastVisitedCount = 0;

        HashMap<Integer, Double> distances = new HashMap<>();
        Queue<node_data> q = new PriorityQueue<>(new NodeDistanceComperator(distances));
        LinkedList<node_data> visitedNodes = new LinkedList<>();

        try {
            q.add(this.g.getNode(src));
        } catch (NullPointerException n) {
            System.out.println(n.getMessage());
        }

        distances.put(src, 0.0);
        node_data neededNode = null;

        while (!q.isEmpty()) {

            // Update needed node info and tracking info
            node_data node = q.remove();
            visitedNodes.add(node);
            node.setTag(STATUS_VISITED);
            neededNode = node.getKey() == dest ? node : neededNode;

            for (edge_data edge : this.g.getE(node.getKey())) {
                node_data neighbor = this.g.getNode(edge.getDest());
                if (neighbor.getTag() == STATUS_VISITED) {
                    continue;
                }

                double newNeigbourDistance = distances.get(node.getKey()) + edge.getWeight();

                if (newNeigbourDistance < distances.getOrDefault(neighbor.getKey(), DEFAULT_INF)) {
                    distances.put(neighbor.getKey(), newNeigbourDistance);
                    q.add(neighbor);
                    neighbor.setTag(STATUS_QUEUED);
                    neighbor.setInfo(Integer.toString(node.getKey()));
                }
            }
        }

        // Build path
        List<node_data> path = this.backtrackPath(src, neededNode);

        // Revert previous state, can also be done on start of scan, but this code was reused from BFS.
        visitedNodes.forEach(node ->
                node.setTag(STATUS_NOT_VISITED));

        this.lastVisitedCount = visitedNodes.size();
        this.lastVisitedEdgeSum = distances.getOrDefault(dest, (double) DEFAULT_NOT_FOUND);
        return path.size() > 0 ? path : null;
    }

    @Override
    public boolean save(String file) {
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            Gson gson = new Gson();
            bw.write(gson.toJson(this.g));
            bw.close();
            fw.close();
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    @Override
    public boolean load(String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(node_data.class, new NodeDataCreator());
            gsonBuilder.registerTypeAdapter(edge_data.class, new EdgeDataCreator());
            gsonBuilder.registerTypeAdapter(geo_location.class, new GeoLocationCreator());

            // Assuming encoding is actually utf-8
            String data = new String(fis.readAllBytes(), StandardCharsets.UTF_8);
            this.g = gsonBuilder.create().fromJson(data, DWGraph_DS.class);
            fis.close();
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    private class NodeDataCreator implements InstanceCreator<node_data> {

        @Override
        public node_data createInstance(Type type) {
            // create new object with our additional property
            return new NodeData();
        }
    }

    private class EdgeDataCreator implements InstanceCreator<edge_data> {

        @Override
        public edge_data createInstance(Type type) {
            // create new object with our additional property
            return new EdgeData();
        }
    }

    private class GeoLocationCreator implements InstanceCreator<geo_location> {

        @Override
        public geo_location createInstance(Type type) {
            // create new object with our additional property
            return new GeoLocation(0, 0, 0);
        }
    }

    class NodeDistanceComperator implements Comparator<node_data> {
        HashMap<Integer, Double> distances;

        public NodeDistanceComperator(HashMap<Integer, Double> d) {
            this.distances = d;
        }

        public int compare(node_data n1, node_data n2) {
            double n1Distance = this.distances.getOrDefault(n1.getKey(), DEFAULT_INF);
            double n2Distance = this.distances.getOrDefault(n2.getKey(), DEFAULT_INF);
            return Double.compare(n1Distance, n2Distance);
        }
    }
}

