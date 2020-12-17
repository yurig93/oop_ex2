package gameClient.models;

import api.geo_location;
import api.node_data;
import gameClient.util.Point3D;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Agent {
    public static int NO_DEST = -1;

    private int id;
    private ArrayList<node_data> calculatedPath;
    private Pokemon assignedPokemon;
    private geo_location pos;
    private int srcNode;
    private int destNode;
    private double value;

    public Agent(int id) {
        this.id = id;
        this.calculatedPath = null;
        this.assignedPokemon = null;
        this.pos = null;
        this.destNode = -1;
        this.srcNode = -1;
        this.value = -1;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "id=" + id +
                ", calculatedPath=" + calculatedPath +
                ", assignedPokemon=" + assignedPokemon +
                ", pos=" + pos +
                ", srcNode=" + srcNode +
                ", destNode=" + destNode +
                ", value=" + value +
                '}';
    }

    public void updateFromInstance(Agent a) {
        ArrayList<node_data> calculatedPath = a.getCalculatedPath();
        Pokemon assignedPokemon = a.getAssignedPokemon();
        geo_location pos = a.getPos();
        int srcNode = a.getSrcNode();
        int destNode = a.getDestNode();
        double value = a.getValue();

        if (calculatedPath != null) {
            this.setCalculatedPath(calculatedPath);
        }
        if (assignedPokemon != null) {
            this.setAssignedPokemon(assignedPokemon);
        }
        if (pos != null) {
            this.setPos(pos);
        }

        this.setSrcNode(srcNode);
        this.setDestNode(destNode);
        this.setValue(value);
    }

    public void updateFromJSON(String json) {
        JSONObject line;
        try {
            line = new JSONObject(json);
            JSONObject agent = line.getJSONObject("Agent");

            int id = agent.getInt("id");
            String p = agent.getString("pos");
            Point3D pp = new Point3D(p);
            int src = agent.getInt("src");
            int dest = agent.getInt("dest");
            double value = agent.getDouble("value");

            this.setId(id);
            this.setPos(pp);
            this.setSrcNode(src);
            this.setDestNode(dest);
            this.setValue(value);
        } catch (JSONException e) {
            System.out.print(e.getMessage());
        }
    }

    public boolean isMoving() {
        return this.destNode != NO_DEST;
    }

    public boolean isFree() {
        return this.assignedPokemon == null;
    }

    public void setFree() {
        this.assignedPokemon = null;
        this.calculatedPath = null;
        this.destNode = NO_DEST;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<node_data> getCalculatedPath() {
        return calculatedPath;
    }

    public void setCalculatedPath(ArrayList<node_data> calculatedPath) {
        this.calculatedPath = calculatedPath;
    }

    public Pokemon getAssignedPokemon() {
        return assignedPokemon;
    }

    public void setAssignedPokemon(Pokemon assignedPokemon) {
        this.assignedPokemon = assignedPokemon;
    }

    public geo_location getPos() {
        return pos;
    }

    public void setPos(geo_location pos) {
        this.pos = pos;
    }

    public int getSrcNode() {
        return srcNode;
    }

    public void setSrcNode(int srcNode) {
        this.srcNode = srcNode;
    }

    public int getDestNode() {
        return destNode;
    }

    public void setDestNode(int destNode) {
        this.destNode = destNode;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
