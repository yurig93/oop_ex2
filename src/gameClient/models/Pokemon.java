package gameClient.models;

import api.edge_data;
import gameClient.util.Point3D;

public class Pokemon {
    public static int INVALID_AGENT = -1;

    private edge_data _edge;
    private double _value;
    private int _type;
    private Point3D _pos;
    private int handlingAgent;

    public Pokemon(Point3D p, int t, double v, double s, edge_data e) {
        _type = t;
        _value = v;
        set_edge(e);
        _pos = p;
        this.handlingAgent = INVALID_AGENT;
    }

    public String getId() {
        return this._pos.toString();
    }

    public boolean isBeingHandled() {
        return this.handlingAgent != INVALID_AGENT;

    }

    public int getHandlingAgent() {
        return handlingAgent;
    }

    public void setHandlingAgent(int handlingAgent) {
        this.handlingAgent = handlingAgent;
    }

    public String toString() {
        return "F:{v=" + _value + ", t=" + _type + "}";
    }

    public edge_data get_edge() {
        return _edge;
    }

    public void set_edge(edge_data _edge) {
        this._edge = _edge;
    }

    public Point3D getLocation() {
        return _pos;
    }

    public int getType() {
        return _type;
    }

    public double getValue() {
        return _value;
    }

}
