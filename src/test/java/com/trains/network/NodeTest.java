package com.trains.network;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NodeTest {
    private Network network;

    @Test
    void isConnectedToTest() {
        network = new Network();
        Node a = network.addNode(new GridPos(10, 20));
        Node b = network.addNode(new GridPos(25, 20));
        Node c = network.addNode(new GridPos(15, 20));
        network.connectNodes(a, b);
        assertTrue(a.isConnectedTo(b));
        assertFalse(a.isConnectedTo(c));
    }
}
