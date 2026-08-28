package com.trains.vehicles;

import com.trains.network.Node;
import com.trains.network.PathwaySegment;
import com.trains.routing.Route;

import java.util.Objects;


/**
 * Stores where we are along the route
 */
public final class Itinerary {
    private final Route route;
    private int leg;
    private Node entryNode;


    public Itinerary(Route route) {
        this.route = Objects.requireNonNull(route);
        this.leg = 0;
        this.entryNode = route.getOrigin();
    }

    /**
     * True if the route has been completed
     * @return true if the route has been fully completed
     */
    public boolean isComplete() {
        return leg >= route.getSegments().size();
    }

    /**
     * Gets the current segment the vehicle *should* be on
     * @return The current segment
     */
    public PathwaySegment getCurrentSegment() {
        if (isComplete()) {throw new IllegalStateException("Route is complete");}
        return route.getSegments().get(leg);
    }

    public Node getDestinationNode() {
        return route.getDestination();
    }

    /**
     * Gets the last node we were on
     * @return The last node
     */
    public Node getEntryNode() {
        return entryNode;
    }

    /**
     * Gets the current target node for the leg we are on
     * @return The target node
     */
    public Node getTargetNode() {
        return getCurrentSegment().opposite(entryNode);
    }

    /**
     * advances the leg of the route we are on by one, only call when moving to the next pathway
     * @return true if the route is now complete, false otherwise
     * @throws IllegalStateException if the route is complete
     */
    public boolean advance() throws IllegalStateException{
        if (isComplete()) {throw new IllegalStateException("Route is complete");}
        entryNode = getTargetNode();
        leg++; // leg++ returns the value of leg then increments leg, which would return the current pathwaySegment we are on, we need to increment then get the segment from the incremented value.
        return !isComplete();
    }
}
