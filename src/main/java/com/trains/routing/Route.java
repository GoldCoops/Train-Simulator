package com.trains.routing;

import com.trains.network.Node;
import com.trains.network.PathwaySegment;

import java.util.Collections;
import java.util.List;

/**
 * A Route that vehicle can take
 * Itinerary handles where the vehicle is along this route
 */
public final class Route {
    private final Node origin, destination;
    private final List<PathwaySegment> segments;
    private final double totalLength;

    /**
     * The route constructor
     * package private to avoid impossible routes that haven't come from Router or DijkstraRouter
     * @param origin the origin node
     * @param destination the destination node
     * @param segments the segments in the path - must be pre-checked by the router to lead to the destination or something will get borked
     * @throws IllegalArgumentException if the route contains more than one segment, or the origin is the same node as the destination
     */
    Route(Node origin, Node destination, List<PathwaySegment> segments) {
        if (segments.isEmpty()) {
            throw new IllegalArgumentException("Route must contain at least one segment");
        }
        if (origin == destination) {
            throw new IllegalArgumentException("Origin and destination cannot be the same");
        }
        this.origin = origin;
        this.destination = destination;
        this.segments = segments;
        double length = 0;
        for (PathwaySegment segment : segments) {
            length += segment.getLength();
        }
        this.totalLength = length;
    }


    public Node getOrigin() {
        return origin;
    }
    public Node getDestination() {
        return destination;
    }

    public List<PathwaySegment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    /**
     * Total length of all of the segments combined
     * @return The total length
     */
    public double getTotalLength() {
        return totalLength;
    }
}
