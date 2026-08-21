package com.trains.network;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NetworkTest {
    private static Network network = new Network();


    public static Network getNetwork() {
        return network;
    }

    public static void setNetwork(Network network) {
        NetworkTest.network = network;
    }

    @Test
    void addNodeTest() {
        network.addNode(new GridPos(10, 20), Network.NodeType.NODE);
        assertEquals(1, network.getNodes().size());
        assertNotNull(network.getNodes().get(new GridPos(10, 20)));
        network.addNode(new GridPos(15, 20), Network.NodeType.STATION);
        assertEquals(2, network.getNodes().size());
        assertThrows(IllegalArgumentException.class, () -> network.addNode(new GridPos(10, 20), Network.NodeType.NODE)); // Node already at position
        assertThrows(IllegalArgumentException.class, () -> network.addNode(new GridPos(10, 20), Network.NodeType.STATION));
        assertEquals(2, network.getNodes().size());
    }

    @Test
    void addNodesTest() {

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
