package com.trains.network;
import com.trains.utils.GridPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PathwaySegmentTest {
    private Network network;

    @Test
    void oppositeTest() {
        network = new Network();
        Node a = network.addNode(new GridPos(10, 20));
        Node b = network.addNode(new GridPos(25, 20));
        Node c = network.addNode(new GridPos(15, 20));
        Node d = network.addNode(new GridPos(95, 200));
        PathwaySegment segment1 = network.connectNodes(a, b);
        PathwaySegment segment2 = network.connectNodes(c, d);
        assertEquals(a, segment1.opposite(b));
        assertEquals(b, segment1.opposite(a));
        assertEquals(c, segment2.opposite(d));
        assertEquals(d, segment2.opposite(c));
    }
}
