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

    }

    @Test
    void isStationTest() {

    }

    @Test
    void isNoteAtTest() {

    }

    @Test
    void connectNodesTest() {

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
