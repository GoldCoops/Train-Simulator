package com.trains.vehicles;

import com.trains.network.PathwaySegment;
import com.trains.routing.Route;


/**
 * Stores where we are along the route
 */
public class Itinerary {
    private final Route route;
    private int leg;


    public Itinerary(Route route) {
        this.route = route;
        this.leg = 0;
    }

    /**
     * True if we are at the end segment
     * @return true if we are at the end segment
     */
    public boolean isComplete() {
        return leg >= route.getSegments().size();
    }

    /**
     * Gets the current segment the vehicle *should* be on
     * @return The current segment
     */
    public PathwaySegment getCurrentSegment() {
        return route.getSegments().get(leg);
    }

    /**
     * advances the leg of the route we are on by one, only call when moving to the next pathway
     * @return The next pathway
     */
    public PathwaySegment advance() {
        leg++; // leg++ returns the value of leg then increments leg, which would return the current pathwaySegment we are on, we need to increment then get the segment from the incremented value.
        return route.getSegments().get(leg);
    }
}
