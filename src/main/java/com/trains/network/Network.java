package com.trains.network;

import java.util.*;


/**
 * Handles/manages the overall network
 */
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
            throw new IllegalArgumentException("Node already exists at " + pos);
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
     * @param pos The position to check
     * @return true if there is a node at that position and it is a station, false otherwise
     */
    public boolean isStationAt(GridPos pos) {
        return nodes.containsKey(pos) && nodes.get(pos) instanceof Station;
    }

    /**
     * @param node The node to check
     * @return true if the node supplied is a station and if it is in the network, false otherwise
     */
    public boolean isStation(Node node) {
        return nodes.containsValue(node) && node instanceof Station;
    }

    /**
     * Connects two nodes with a PathwaySegment
     * @param a The first node
     * @param b The Second node
     * @return The created PathwaySegment
     */
    public PathwaySegment connectNodes(Node a, Node b) {
        PathwaySegment segment = new PathwaySegment(a, b);
        pathways.add(segment);
        return segment;
    }

    /**
     * Disconnects two nodes via the supplied segment
     * @param segment the segment to disconnect
     * @throws IllegalArgumentException if segment is not found in pathways list
     */
    public void disconnectNodes(PathwaySegment segment) throws IllegalArgumentException{
        if (pathways.contains(segment)) {
            segment.getStart().removeConnection(segment);
            segment.getEnd().removeConnection(segment);
            pathways.remove(segment);
        } else {
            throw new IllegalArgumentException("Segment not found in pathways list");
        }
    }


    /**
     * Removes a Node from the network and destroys all connections
     * @param node the Node to remove
     */
    public void removeNode(Node node) {
        for (PathwaySegment segment : node.getConnections()) {
            disconnectNodes(segment);
        }
        nodes.remove(node.getPos());
    }

    /**
     * Removes a Node found at the given GridPos from the network and destroys all connections
     * @param pos The Position of the node to remove
     */
    public void removeNode(GridPos pos) {
        if (nodes.containsKey(pos)) {
            removeNode(nodes.get(pos));
        }
    }

    /**
     * Gets the Map of nodes in the network
     * @return an unmodifiable Map of GridPos, Node
     */
    public Map<GridPos, Node> getNodes() {return Collections.unmodifiableMap(nodes);}

    /**
     * Gets the list of pathways in the network
     * @return an unmodifiable list of the pathways in the network
     */
    public Set<PathwaySegment> getPathways() {return Collections.unmodifiableSet(pathways);}
}