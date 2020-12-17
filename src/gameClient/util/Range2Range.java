package gameClient.util;

import api.directed_weighted_graph;
import api.geo_location;
import api.node_data;

import java.util.Iterator;

/**
 * This class represents a simple world 2 frame conversion (both ways).
 *
 * @author boaz.benmoshe
 */

public class Range2Range {
    private Range2D _world, _frame;

    public Range2Range(Range2D w, Range2D f) {
        _world = new Range2D(w);
        _frame = new Range2D(f);
    }

    public static Range2D GraphRange(directed_weighted_graph g) {
        Iterator<node_data> itr = g.getV().iterator();
        double x0 = 0, x1 = 0, y0 = 0, y1 = 0;
        boolean first = true;
        while (itr.hasNext()) {
            geo_location p = itr.next().getLocation();
            if (first) {
                x0 = p.x();
                x1 = x0;
                y0 = p.y();
                y1 = y0;
                first = false;
            } else {
                if (p.x() < x0) {
                    x0 = p.x();
                }
                if (p.x() > x1) {
                    x1 = p.x();
                }
                if (p.y() < y0) {
                    y0 = p.y();
                }
                if (p.y() > y1) {
                    y1 = p.y();
                }
            }
        }
        Range xr = new Range(x0, x1);
        Range yr = new Range(y0, y1);
        return new Range2D(xr, yr);
    }

    public static Range2Range w2f(directed_weighted_graph g, Range2D frame) {
        Range2D world = GraphRange(g);
        return new Range2Range(world, frame);
    }

    public geo_location world2frame(geo_location p) {
        Point3D d = _world.getPortion(p);
        Point3D ans = _frame.fromPortion(d);
        return ans;
    }

    public geo_location frame2world(geo_location p) {
        Point3D d = _frame.getPortion(p);
        Point3D ans = _world.fromPortion(d);
        return ans;
    }

    public Range2D getWorld() {
        return _world;
    }

    public Range2D getFrame() {
        return _frame;
    }
}
