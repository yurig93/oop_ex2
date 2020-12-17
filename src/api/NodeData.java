package api;

public class NodeData implements node_data {
    private static int nodeIdCounter = 0;


    private int key;
    private double weight;
    private String info;
    private int tag;
    private geo_location geoLocation;


    public NodeData() {
        this.key = nodeIdCounter;
        this.info = "";
        this.tag = 0;
        this.weight = 0;
        this.geoLocation = null;
        nodeIdCounter++;
    }

    public NodeData(node_data n) {
        this();
        this.key = n.getKey();
        this.info = n.getInfo();
        this.tag = n.getTag();
        this.weight = n.getWeight();
        this.geoLocation = n.getLocation() != null ? new GeoLocation(n.getLocation()) : null;
    }

    public NodeData(int key) {
        this();
        this.key = key;
    }

    @Override
    public int getKey() {
        return this.key;
    }

    @Override
    public geo_location getLocation() {
        return this.geoLocation;
    }

    @Override
    public void setLocation(geo_location p) {
        this.geoLocation = p;
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(double w) {
        this.weight = w;
    }

    @Override
    public String getInfo() {
        return this.info;
    }

    @Override
    public void setInfo(String s) {
        this.info = s;
    }

    @Override
    public int getTag() {
        return this.tag;
    }

    @Override
    public void setTag(int t) {
        this.tag = t;
    }
}
