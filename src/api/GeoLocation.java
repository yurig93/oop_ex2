package api;

public class GeoLocation implements geo_location {
    private double x;
    private double y;
    private double z;

    public GeoLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public GeoLocation(geo_location g) {
        this(g.x(), g.y(), g.z());
    }

    public GeoLocation(String str) {
        try {
            String[] a = str.split(",");
            this.x = Double.parseDouble(a[0]);
            this.y = Double.parseDouble(a[1]);
            this.z = Double.parseDouble(a[2]);
        } catch (IllegalArgumentException e) {
            System.err.println("Bad string format, got:" + str + " should be of format: x,y,x");
            throw (e);
        }
    }

    @Override
    public double x() {
        return this.x;
    }

    @Override
    public double y() {
        return this.y;
    }

    @Override
    public double z() {
        return this.z;
    }

    @Override
    public double distance(geo_location g) {
        return Math.sqrt(Math.pow(x - g.x(), 2) + Math.pow(y - g.y(), 2) + Math.pow(z - g.z(), 2));
    }

    @Override
    public String toString() {
        return String.format("%f,%f,%f", this.x(), this.y(), this.z());
    }
}
