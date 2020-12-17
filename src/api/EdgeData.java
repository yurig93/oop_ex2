package api;

public class EdgeData implements edge_data {
    final public static int DEFAULT_TAG = 0;
    final public static String DEFAULT_INFO = "";

    private int src;
    private int dest;
    private double weight;
    private String info;
    private int tag;

    public EdgeData() {
        this.src = -1;
        this.dest = -1;
        this.weight = -1;
        this.info = DEFAULT_INFO;
        this.tag = DEFAULT_TAG;
    }

    public EdgeData(int src, int dest, double weight, String info, int tag) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
        this.info = info;
        this.tag = tag;
    }


    @Override
    public int getSrc() {
        return this.src;
    }

    @Override
    public int getDest() {
        return this.dest;
    }

    @Override
    public double getWeight() {
        return this.weight;
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
