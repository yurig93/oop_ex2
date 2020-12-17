package api;

public class EdgeLocation implements edge_location {
    private edge_data edgeData;
    private double ratio;

    public EdgeLocation(edge_data edgeData, double ratio) {
        this.edgeData = edgeData;
        this.ratio = ratio;
    }

    @Override
    public edge_data getEdge() {
        return this.edgeData;
    }

    @Override
    public double getRatio() {
        return this.ratio;
    }
}
