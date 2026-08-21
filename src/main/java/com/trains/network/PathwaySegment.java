package com.trains.network;

/**
 * The PathwaySegment class
 * May turn this into different classes for different types of pathways, but at the moment this tracks the nodes it connects and the distance between those nodes.
 */
public class PathwaySegment {
    private final Node start;
    private final Node end;
    private final double length;

    public PathwaySegment(Node start, Node end) {
        this.start = start;
        this.end = end;
        start.addConnection(this);
        end.addConnection(this);
        int xdist = Math.abs(start.getPos().x() - end.getPos().x());
        int ydist = Math.abs(start.getPos().y() - end.getPos().y());
        this.length = Math.sqrt(Math.pow(xdist, 2) + Math.pow(ydist,2));
    }


    /**
     * whether this pathway segment is connected to the given node
     * @param node the node to check
     * @return true if the segment is connected to the supplied node, false otherwise
     */
    public boolean isConnectedTo(Node node) {
        return start.equals(node) || end.equals(node);
    }
    /**
     * Getter for the starter node
     * @return the start node
     */
    public Node getStart() {
        return start;
    }
    /**
     * Getter for the end node
     * @return the end node
     */
    public Node getEnd() {
        return end;
    }
    /**
     * Getter for the length
     * @return the length of the pathway
     */
    public double getLength() {
        return length;
    }

    @Override
    public int hashCode() {
        return this.start.hashCode() + this.end.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof PathwaySegment otherPath) {
            return this.start.equals(otherPath.getStart()) && this.end.equals(otherPath.getEnd()); // May change if we decide to make multiple types of "Pathways"
        }
        return false;
    }
}
