package com.trains.network;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class NetworkTest {
    private Network network;

    @Test
    void addNodeTest() {
        network = new Network();
        network.addNode(new GridPos(10, 20));
        assertEquals(1, network.getNodes().size());
        assertNotNull(network.getNodes().get(new GridPos(10, 20)));
        network.addNode(new GridPos(15, 20));
        assertEquals(2, network.getNodes().size());
        assertThrows(IllegalArgumentException.class, () -> network.addNode(new GridPos(10, 20))); // Node already at position
        assertThrows(IllegalArgumentException.class, () -> network.addNode(new GridPos(10, 20)));
        assertEquals(2, network.getNodes().size());
    }

    @Test
    void addNodesTest() {
        network = new Network();
        network.addNodes(new GridPos(10,20),new GridPos(20,20), new GridPos(15,20));
        assertEquals(3, network.getNodes().size());
        assertTrue(network.isNodeAt(new GridPos(10,20)));
        assertTrue(network.isNodeAt(new GridPos(20,20)));
        assertTrue(network.isNodeAt(new GridPos(15,20)));
        assertThrows(IllegalArgumentException.class ,() -> network.addNodes(new GridPos(300,20),new GridPos(20,20), new GridPos(15,20))); // throws if any of the nodes already exist in the network
        assertEquals(3, network.getNodes().size());
        assertThrows(IllegalArgumentException.class ,() -> network.addNodes(new GridPos(100,50),new GridPos(100,50), new GridPos(30,20))); // denies duplicates
        assertEquals(3, network.getNodes().size());
    }

    @Test
    void isStationAtTest() {
        network = new Network();
        network.addStation(new GridPos(10, 20), 20);
        network.addStation(new GridPos(25, 20), 100);
        network.addNode(new GridPos(15, 20));
        assertTrue(network.isStationAt(new GridPos(10, 20)));
        assertTrue(network.isStationAt(new GridPos(25, 20)));
        assertFalse(network.isStationAt(new GridPos(15, 20)));
        assertFalse(network.isStationAt(new GridPos(100, 200)));
    }

    @Test
    void isStationTest() {
        network = new Network();
        Station station = new Station(new GridPos(10, 20), 10);
        assertTrue(network.isStation(station));
        assertFalse(network.isStation(new Node(new GridPos(10, 20))));
        assertFalse(network.isStation(null));
    }

    @Test
    void isNodeAtTest() {
        network = new Network();
        network.addNode(new GridPos(10, 20));
        network.addNode(new GridPos(25, 20));
        network.addNode(new GridPos(15, 20));
        network.addNode(new GridPos(95, 200));
        assertTrue(network.isNodeAt(new GridPos(10, 20)));
        assertTrue(network.isNodeAt(new GridPos(25, 20)));
        assertTrue(network.isNodeAt(new GridPos(95, 200)));
        assertTrue(network.isNodeAt(new GridPos(15, 20)));
        assertFalse(network.isNodeAt(new GridPos(100, 200)));
    }

    @Test
    void connectNodesTest() {
        network = new Network();
        Node a = network.addNode(new GridPos(10, 20));
        Node b = network.addNode(new GridPos(25, 20));
        Node c = network.addNode(new GridPos(15, 20));
        Node d = network.addNode(new GridPos(95, 200));
        network.connectNodes(a, b);
        network.connectNodes(c, d);
        network.connectNodes(a, c);
        assertEquals(3, network.getPathways().size());
        assertEquals(b, a.getConnections().getFirst().getEnd());
        assertEquals(a, b.getConnections().getFirst().getStart());
        assertEquals(d, c.getConnections().getFirst().getEnd());
        assertEquals(c, d.getConnections().getFirst().getStart());
        assertEquals(c, a.getConnections().get(1).getEnd());
        assertEquals(a, c.getConnections().get(1).getStart());
    }

    @Test
    void disconnectNodesTest() {

    }

    @Test
    void removeNodeTest() {

    }

    @Test
    void removeNodeByPosTest() {

    }

}
