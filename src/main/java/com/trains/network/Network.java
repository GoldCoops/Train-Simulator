package com.trains.network;

import java.util.*;

public class Network {
    private final HashMap<GridPos, Node> nodes = new HashMap<>();
    private final Set<PathwaySegment> pathways = new HashSet<>();

    public Network() {
    }

    /**
     * Adds a Node at the given position
     * @param pos the position to add the node
     * @throws IllegalArgumentException if there is already a node at the pos provided
     * @return Returns the created node.
     */
    public Node addNode(GridPos pos) throws IllegalArgumentException {
        if (nodes.containsKey(pos)) {
            throw new IllegalArgumentException("Station already exists at " + pos);
        }
        Node node = new Node(pos);
        nodes.put(pos, node);
        return node;
    }

    /**
     * Adds a Station at the given position
     * @param pos the position to add the station/node
     * @param capacity The capacity of the station
     * @return the created station
     * @throws IllegalArgumentException if a node already exists at the pos provided
     */
    public Station addStation(GridPos pos, int capacity) throws IllegalArgumentException{
        if (nodes.containsKey(pos)) {
            throw new IllegalArgumentException("Station already exists at " + pos);
        }
        Station station = new Station(pos, capacity);
        nodes.put(pos, station);
        return station;
    }




    /**
     * Connects two nodes with a PathwaySegment
     * @param a The first node
     * @param b The Second node
     * @return The created PathwaySegment
     */
    public PathwaySegment connectNodes(Node a, Node b) {
        return null;
    }

    /**
     * Disconnects
     * @param segment
     */
    public void disconnectNodes(PathwaySegment segment) {}
    public void removeNode(Node node) {}
    public void removeNode(GridPos pos) {}
    public Map<GridPos, Node> getNodes() {return Collections.unmodifiableMap(nodes);}
    public Set<PathwaySegment> getPathways() {return Collections.unmodifiableSet(pathways);}
}