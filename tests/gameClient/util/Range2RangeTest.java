package gameClient.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Range2RangeTest {

    @Test
    void world2frame() {
        Range2D worldRange = new Range2D(new Range(1, 2), new Range(1, 2));
        Range2D frameRange = new Range2D(new Range(10, 20), new Range(10, 20));

        Point3D worldPortion = worldRange.getPortion(new Point3D(1.5, 1.5));
        Point3D frameFromPortion = frameRange.fromPortion(worldPortion);

        assertEquals(frameFromPortion.x(),  15.0);
        assertEquals(frameFromPortion.y(),  15.0);
    }
}