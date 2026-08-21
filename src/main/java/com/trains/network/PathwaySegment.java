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
        int xdist = Math.abs(start.getPos().x() - end.getPos().x());
        int ydist = Math.abs(start.getPos().y() - end.getPos().y());
        this.length = Math.sqrt(Math.pow(xdist, 2) + Math.pow(ydist,2));
    }
}
