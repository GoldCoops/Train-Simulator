package com.trains.network;


import com.trains.utils.GridPos;

import java.lang.reflect.Constructor;
import java.util.*;


/**
 * Handles/manages the overall network
 */
public final class Network {
    private final Map<GridPos, Node> nodes = new LinkedHashMap<>();
    private final Set<PathwaySegment> pathways = new LinkedHashSet<>();

    /**
     * Network Constructor, doesnt do anything yet
     */
    public Network() {}

    /**
     * Add a node to the network
     * usage: network.addNode(new GridPos(x,y))
     * @param pos the position to add the {@link Node}
     * @return The created {@link Node} object
     * @throws IllegalArgumentException If a node already exists at the supplied position
     */
    public Node addNode(GridPos pos) throws IllegalArgumentException{
        return register(new Node(pos));
    }

    /**
     * Add a station to the network
     * usage: network.addStation(new GridPos(x,y), capacity)
     * @param pos the position to add the {@link Station}
     * @param capacity the capacity of the {@link Station} - final, cannot be changed after creation
     * @return the created station object
     * @throws IllegalArgumentException If a node already exists at the supplied position
     */
    public Station addStation(GridPos pos, int capacity) throws IllegalArgumentException{
        return register(new Station(pos, capacity));
    }



    /**
     * Add a station to the network
     * usage: network.addStation(new GridPos(x,y), capacity)
     * @param pos the position to add the {@link Station}
     * @param capacity the capacity of the {@link Station} - final, cannot be changed after creation
     * @param name the name of the {@link Station}
     * @return the created station object
     * @throws IllegalArgumentException If a node already exists at the supplied position
     */
    public Station addStation(GridPos pos, int capacity, String name) throws IllegalArgumentException{
        return register(new Station(pos, capacity, name));
    }

    /** Registers a node in nodes if one does not already exist, if it does, it throws */
    private <T extends Node> T register(T node) throws IllegalArgumentException{
        Node existing = nodes.putIfAbsent(node.getPos(), node);
        if (existing != null) { // if there is an object already in the map containing node.getPos(), it will be returned by putIfAbsent(), else it returns null if the item has been successfully put in the map
            throw new IllegalArgumentException(existing.getClass().getSimpleName() + " already exists at " + node.getPos());
        }
        return node;
    }
 


    /**
     * Convenience method for adding multiple {@link Node}s at one time
     * If any of the positions supplied have nodes on them, this method will bail and throw before mutating the network
     * @param positions The positions to add {@link Node}s at
     * @return a {@link List} of the {@link Node}s added
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


    public <T extends Node> List<T> addNodes(Class<T> clazz, Map<GridPos, Integer> positions) throws IllegalArgumentException {
        if (positions.isEmpty()) {
            throw new IllegalArgumentException("No positions supplied");
        }
        List<T> created = new ArrayList<>(positions.size());
        if (positions.keySet().stream().anyMatch(this::isNodeAt)) {
            throw new IllegalArgumentException("Some positions supplied already have nodes on them");
        }
        Constructor<T> constructor;
        try {
            if (clazz == Node.class) {
                List<GridPos> trimmed = new ArrayList<>(positions.keySet());
                constructor = clazz.getDeclaredConstructor(trimmed.getClass());
            } else {
                Map<GridPos, Integer> casted = Map.copyOf(positions);
                GridPos firstKey = casted.keySet().stream().findFirst().get();
                int firstValue = casted.values().stream().findFirst().get();
                constructor = clazz.getDeclaredConstructor();
            }

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Could not find " + clazz.getSimpleName() + " constructor");
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Could not get the first key of " + positions);
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
     * Returns the distance between two nodes
     * <p>
     *     Nodes do not have to be connected, this will be needed for A*
     * </p>
     * @param a The first node
     * @param b the second node
     * @return the distance between the two nodes
     */
    public double distanceBetween(Node a, Node b) {
        return a.getPos().distanceTo(b.getPos());
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