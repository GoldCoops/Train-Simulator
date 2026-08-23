package com.trains.network;


import java.util.*;


/**
 * Handles/manages the overall network
 */
public class Network {
    private final Map<GridPos, Node> nodes = new HashMap<>();
    private final Set<PathwaySegment> pathways = new HashSet<>();

    /**
     * Network Constructor, doesnt do anything yet
     */

    public Network() {}

    /**
     * Add a node to the network
     * usage: network.addNode(new GridPos(x,y))
     * @param pos the position to add the node
     * @return The created node object
     * @throws IllegalArgumentException If a node already exists at the supplied position
     */

    public Node addNode(GridPos pos) throws IllegalArgumentException{
        return register(new Node(pos));
    }

    /**
     * Add a station to the network
     * usage: network.addStation(new GridPos(x,y), capacity)
     * @param pos the position to add the station
     * @param capacity the capacity of the station - final, cannot be changed after creation
     * @return the created station object
     * @throws IllegalArgumentException If a node already exists at the supplied position
     */
    public Station addStation(GridPos pos, int capacity) throws IllegalArgumentException{
        return register(new Station(pos, capacity));
    }

    private <T extends Node> T register(T node) throws IllegalArgumentException{
        if (nodes.putIfAbsent(node.getPos(), node) != null) { // if there is an object already in the map containing node.getPos(), it will be returned by putIfAbsent()
            throw new IllegalArgumentException("Node already exists at " + node.getPos());
        }
        return node;
    }



    /**
     * Convenience method for adding multiple nodes at one time, returns a list of the nodes added
     * If any of the positions supplied have nodes on them, this method will bail and throw before mutating the network
     * @param positions The positions to add nodes at
     * @return list of nodes added
     * @throws IllegalArgumentException if any of the supplied positions already have nodes on them, or if duplicate positions are supplied
     */
    public List<Node> addNodes(GridPos... positions) throws IllegalArgumentException {
        List<Node> created = new ArrayList<>(positions.length);
        Set<GridPos> distinct = new LinkedHashSet<>(Arrays.asList(positions)); //a set to auto-eliminate duplicates
        if (distinct.size() != positions.length) {
            throw new IllegalArgumentException("Duplicate positions supplied");
        }
        if (distinct.stream().anyMatch(this::isNodeAt)) {
            throw new IllegalArgumentException("Some positions supplied already have nodes on them");
        }
        for(GridPos pos : positions) {
            created.add(addNode(pos));
        }
        return created;
    }


    /**
     * Check if a station is at the given position
     * @param pos The position to check
     * @return true if there is a node at that position, if it is part of the network, and it is a station, false otherwise
     */
    public boolean isStationAt(GridPos pos) {
        return nodes.containsKey(pos) && nodes.get(pos) instanceof Station;
    }

    /**
     * Checks if a node is a station
     * @param node The node to check, does not check if the node is part of the network, just checks if it is a station.
     * @return true if the node supplied is a station, false otherwise
     */
    public boolean isStation(Node node) {
        return node instanceof Station;
    }

    /**
     * Checks if a given GridPos has a node
     * usage: network.isNodeAt(new GridPos(x,y));
     * @param pos the position to check
     * @return true if there is a node at that position, false otherwise
     */
    public boolean isNodeAt(GridPos pos) {
        return nodes.containsKey(pos);
    }

    /**
     * Connects two nodes with a PathwaySegment
     * @param a The first node
     * @param b The Second node
     * @return The created PathwaySegment
     * @throws IllegalArgumentException if nodes are the same or one/both dont belong to the network
     */
    public PathwaySegment connectNodes(Node a, Node b) throws IllegalArgumentException {
        if (Objects.equals(a, b)) {throw new IllegalArgumentException("Cannot connect a node to itself");}
        if (nodes.get(a.getPos()) != a || nodes.get(b.getPos()) != b) {throw new IllegalArgumentException("Nodes do not belong to the network");}
        if (areConnected(a, b)) {throw new IllegalArgumentException("Nodes are already connected");}
        PathwaySegment segment = new PathwaySegment(a, b);
        a.addConnection(segment);
        b.addConnection(segment);
        pathways.add(segment);
        return segment;
    }

    /**
     * Disconnects two nodes via the supplied segment
     * @param segment the segment to disconnect
     */
    public void disconnectNodes(PathwaySegment segment) throws IllegalArgumentException{
        if (!pathways.contains(segment)) {
            throw new IllegalArgumentException("Segment not found in pathway set");
        }
        unlink(segment);
    }

    /**
     * Takes two nodes, and if there is a connection between them, disconnects them
     * @param a the first node to disconnect
     * @param b the second node to disconnect
     * @return true if the two nodes were connected and have been successfully disconnected, false otherwise
     * @throws IllegalArgumentException if either node supplied is not in the network.
     */
    public boolean disconnectNodes(Node a, Node b) throws IllegalArgumentException{
        if (nodes.get(a.getPos()) != a || nodes.get(b.getPos()) != b) {
            throw new IllegalArgumentException("Nodes do not belong to the network");
        }
        PathwaySegment segment = segmentBetween(a, b);
        if (segment == null) {
            return false;
        }
        unlink(segment);
        return true;
    }

    private void unlink(PathwaySegment segment) {
        segment.getStart().removeConnection(segment);
        segment.getEnd().removeConnection(segment);
        pathways.remove(segment);
    }

    /**
     * Returns the segment between two nodes
     * @param a the first node
     * @param b the second node
     * @return the segment if the nodes are connected, {@code null} if not.
     */
    public PathwaySegment segmentBetween(Node a, Node b) {
        for (PathwaySegment segment : a.getConnections()) {
            if (segment.opposite(a) == b) {
                return segment;
            }
        }
        return null;
    }

    /**
     * Checks if two nodes are connected
     * @param a the first node
     * @param b the second node
     * @return true if the nodes are connected, false otherwise
     */
    public boolean areConnected(Node a, Node b) {
        return segmentBetween(a, b) != null;
    }


    /**
     * Removes a Node from the network and destroys all connections
     * @param node the Node to remove
     * @throws IllegalArgumentException if the node doesn't belong to the network
     */
    public void removeNode(Node node) throws IllegalArgumentException {
        if (nodes.get(node.getPos()) != node) {
            throw new IllegalArgumentException("Node does not belong to the network");
        }
        for (PathwaySegment segment : new ArrayList<>(node.getConnections())) {
            disconnectNodes(segment);
        }
        nodes.remove(node.getPos());
    }

    /**
     * Removes a Node found at the given GridPos from the network and destroys all connections
     * @param pos The Position of the node to remove
     * @throws IllegalArgumentException if the node is not part of the network
     */
    public void removeNode(GridPos pos) throws IllegalArgumentException {
        if (nodes.containsKey(pos)) {
            removeNode(nodes.get(pos));
        }
        else { throw new IllegalArgumentException("No node at position " + pos);}
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