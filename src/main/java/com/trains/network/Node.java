package com.trains.network;

import java.util.*;

/**
 * The Node Class
 * Handles the underlying logic of each individual node, holds a position, and the pathways which it is connected to.
 */
public class Node {
    private final GridPos pos;
    private final ArrayList<PathwaySegment> connections = new ArrayList<>();


    Node(GridPos pos) {
        this.pos = pos;
    }
    /**
     * Gets all Pathways connected to this Node
     * @return List of PathwaySegments
     */
    public List<PathwaySegment> getConnections() {
        return Collections.unmodifiableList(connections);
    }

    /**
     * Adds a connection
     * @param segment the PathwaySegment to be added
     */
    void addConnection(PathwaySegment segment) {
        connections.add(segment);
    }

    /**
     * Removes a connection
     * @param segment segment to be removed
     */
    void removeConnection(PathwaySegment segment) {
        connections.remove(segment);
    }


    /**
     * Gets the GridPos record of the node
     * @return the GridPos record
     */
    public GridPos getPos() {return pos;}

    /**
     * get the x coordinate of a node
     * @return the x coordinate
     */
    public int getX() {return pos.x();}

    /**
     * get the y coordinate of a node
     * @return the y coordinate
     */
    public int getY() {return pos.y();}

    /**
     * Checks if the calling node is connected to the supplied node
     * @param other The node to check for
     * @return true if the calling node is connected to the supplied, false otherwise
     */
    public boolean isConnectedTo(Node other) {
        for (PathwaySegment segment : connections) {
            if (segment.opposite(this) == other) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.pos.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Node otherNode) {
            return this.pos.equals(otherNode.getPos());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[ " + pos + "]";
    }
}
