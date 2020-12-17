package api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;

import java.util.*;

public class DWGraph_DS implements directed_weighted_graph {
    private int edgeCount;
    private int modeCount;
    private HashMap<Integer, node_data> nodes;
    private HashMap<Integer, HashMap<Integer, edge_data>> links;

    public DWGraph_DS(directed_weighted_graph dw) {
        this();

        dw.getV().forEach(node -> {
            this.addNode(new NodeData(node));
        });

        dw.getV().forEach(node -> {
            dw.getE(node.getKey()).forEach(edge -> {
                this.connect(node.getKey(), edge.getDest(), edge.getWeight());
            });
        });

        this.modeCount = dw.getMC();
    }

    public DWGraph_DS() {
        this.nodes = new HashMap<>();
        this.links = new HashMap<>();
        this.edgeCount = 0;
        this.modeCount = 0;
    }

    public static directed_weighted_graph fromMatalaJSON(String str) {
        directed_weighted_graph newGraph = new DWGraph_DS();

        try {
            Gson gson = new Gson();
            Map map = gson.fromJson(str, Map.class);

            if (!map.containsKey("Nodes")) {
                throw new RuntimeException("Missing Nodes key");
            }
            if (!map.containsKey("Edges")) {
                throw new RuntimeException("Missing Edges key");
            }

            // relying on casting exceptions here as type validation
            ArrayList<LinkedTreeMap> nodes = (ArrayList<LinkedTreeMap>) map.get("Nodes");
            ArrayList<LinkedTreeMap> edges = (ArrayList<LinkedTreeMap>) map.get("Edges");

            nodes.forEach(nodeEntry -> {
                double id = (double) nodeEntry.get("id");
                String pos = (String) nodeEntry.get("pos");
                node_data n = new NodeData((int) id);
                if (pos != null) {
                    n.setLocation(new GeoLocation(pos));
                }
                newGraph.addNode(n);
            });

            edges.forEach(edgeEntry -> {
                double src = (double) edgeEntry.get("src");
                double dest = (double) edgeEntry.get("dest");
                double weight = (double) edgeEntry.get("w");
                newGraph.connect((int) src, (int) dest, weight);
            });

            return newGraph;

        } catch (JsonSyntaxException e) {
            System.out.println(e.getMessage());
            return null;
        } catch (ClassCastException e) {
            System.out.println("Malformed JSON structure.");
            return null;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DWGraph_DS)) return false;
        DWGraph_DS wGraph_ds = (DWGraph_DS) o;

        boolean linksEqual = true;
        Iterator it = links.entrySet().iterator();

        // Manually compare hashmaps since we store Objects and Java checks both with equals and == on them...
        while (it.hasNext() && linksEqual) {
            Map.Entry mapElement = (Map.Entry) it.next();
            HashMap<Integer, edge_data> myLinkedNeighbours = (HashMap<Integer, edge_data>) mapElement.getValue();
            HashMap<Integer, edge_data> targetLinkedNeighbours = wGraph_ds.links.getOrDefault(mapElement.getKey(), new HashMap<>());
            linksEqual = myLinkedNeighbours.keySet().equals(targetLinkedNeighbours.keySet());
        }

        return edgeCount == wGraph_ds.edgeCount &&
                modeCount == wGraph_ds.modeCount &&
                nodes.keySet().equals(wGraph_ds.nodes.keySet()) &&
                linksEqual;
    }

    @Override
    public node_data getNode(int key) {
        return this.nodes.get(key);
    }

    @Override
    public edge_data getEdge(int src, int dest) {
        return this.links.getOrDefault(src, new HashMap<>()).get(dest);
    }

    @Override
    public void addNode(node_data n) {
        if (!this.nodes.containsKey(n.getKey())) {
            this.nodes.put(n.getKey(), n);
            this.links.put(n.getKey(), new HashMap<>());
            this.modeCount++;
        }

    }

    @Override
    public void connect(int src, int dest, double w) {
        if (this.links.containsKey(src)) {
            edge_data currentEdgeData = this.links.get(src).get(dest);
            if(currentEdgeData != null && currentEdgeData.getWeight() == w){
                return;
            }
            if (!this.links.get(src).containsKey(dest)) {
                this.links.get(src).put(dest, new EdgeData(src, dest, w, EdgeData.DEFAULT_INFO, EdgeData.DEFAULT_TAG));
                this.edgeCount += (src == dest) ? 0 : 1;
                this.modeCount++;
            }
        }
    }

    @Override
    public Collection<node_data> getV() {
        return this.nodes.values();
    }

    @Override
    public Collection<edge_data> getE(int node_id) {
        return this.links.getOrDefault(node_id, new HashMap<>()).values();
    }

    @Override
    public node_data removeNode(int key) {
        node_data removedNode = this.getNode(key);

        if (removedNode == null) {
            return null;
        }

        for (Integer entry : new HashSet<>(this.links.get(key).keySet())) {
            this.removeEdge(key, entry);
        }

        for (Map.Entry<Integer, HashMap<Integer, edge_data>> entry : this.links.entrySet()) {
            this.removeEdge(entry.getKey(), key);
        }

        this.modeCount += 1;
        this.links.remove(key);
        this.nodes.remove(key);
        return removedNode;
    }

    @Override
    public edge_data removeEdge(int src, int dest) {
        edge_data removed = null;

        if (this.links.containsKey(src)) {
            removed = this.links.get(src).remove(dest);
        }

        if (removed != null) {
            this.modeCount += 1;
            this.edgeCount -= src == dest ? 0 : 1;
        }

        return removed;
    }

    @Override
    public int nodeSize() {
        return this.nodes.size();
    }

    @Override
    public int edgeSize() {
        return this.edgeCount;
    }

    @Override
    public int getMC() {
        return this.modeCount;
    }

}
