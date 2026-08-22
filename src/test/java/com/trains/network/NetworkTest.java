package com.trains.network;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static com.trains.network.Network.NodeType.*;
import static org.junit.jupiter.api.Assertions.*;

public class NetworkTest {
    private Network network;

    @Test
    void addNodeTest() {
        network = new Network();
        network.addNode(new GridPos(10, 20), NODE);
        assertEquals(1, network.getNodes().size());
        assertNotNull(network.getNodes().get(new GridPos(10, 20)));
        network.addNode(new GridPos(15, 20), STATION);
        assertEquals(2, network.getNodes().size());
        assertThrows(IllegalArgumentException.class, () -> network.addNode(new GridPos(10, 20), NODE)); // Node already at position
        assertThrows(IllegalArgumentException.class, () -> network.addNode(new GridPos(10, 20), STATION));
        assertEquals(2, network.getNodes().size());
    }

    @Test
    void addNodesTest() {
        network = new Network();
        HashMap<GridPos, Network.NodeType> nodes = new HashMap<>();
        nodes.put(new GridPos(10, 20), NODE);
        nodes.put(new GridPos(15, 20), STATION);
        nodes.put(new GridPos(20, 20), STATION);
        nodes.put(new GridPos(25, 20), NODE);
        network.addNodes(nodes);
        assertEquals(4, network.getNodes().size());
        HashMap<GridPos, Network.NodeType> nodes1 = new HashMap<>();
        nodes1.put(new GridPos(150, 220), STATION);
        nodes1.put(new GridPos(10, 20), NODE);
        nodes1.put(new GridPos(100, 200), NODE);
        nodes1.put(new GridPos(15, 20), STATION);
        assertThrows(IllegalArgumentException.class,() -> network.addNodes(nodes1)); // addNodes should bail and not add anything if any of the positions supplied are full.
        assertEquals(4, network.getNodes().size());
    }

    @Test
    void isStationAtTest() {
        network = new Network();
        network.addNode(new GridPos(10, 20), STATION);
        network.addNode(new GridPos(25, 20), STATION);
        network.addNode(new GridPos(15, 20), NODE);
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
    void isNoteAtTest() {
        network = new Network();
        network.addNode(new GridPos(10, 20), NODE);
        network.addNode(new GridPos(25, 20), STATION);
        network.addNode(new GridPos(15, 20), STATION);
        network.addNode(new GridPos(95, 200), NODE);
        assertTrue(network.isNodeAt(new GridPos(10, 20)));
        assertTrue(network.isNodeAt(new GridPos(25, 20)));
        assertTrue(network.isNodeAt(new GridPos(95, 200)));
        assertTrue(network.isNodeAt(new GridPos(15, 20)));
        assertFalse(network.isNodeAt(new GridPos(100, 200)));
    }

    @Test
    void connectNodesTest() {
        network = new Network();
        Node a = network.addNode(new GridPos(10, 20), NODE);
        Node b = network.addNode(new GridPos(25, 20), STATION);
        Node c = network.addNode(new GridPos(15, 20), STATION);
        Node d = network.addNode(new GridPos(95, 200), NODE);
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
