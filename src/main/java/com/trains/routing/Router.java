// Router currently finds the cheapest route by total physical distance, 
// not the fastest by travel time. gScore just sums segment.getLength(), so it has no idea about vehicle speed or station dwell time yet.
// We're keeping the naming as "cost"/"cheapest" rather than "fastest" on purpose, 
// since the same A* logic will still work unchanged once cost gets swapped from distance to time (speed and stop patterns), 
// only what we feed into gScore needs to change, not the algorithm.
package com.trains.routing;

import com.trains.network.Network;
import com.trains.network.Node;
import com.trains.network.PathwaySegment;

import java.util.*;

public class Router {
    // Handles pathfinding logic, I'm probably going to try using A* here, but if
    // that fails I'll just use Dijkstra
    private final Network network;

    public Router(Network network) {
        this.network = network;
    }

    public Route findRoute(Node origin, Node destination) {
        if (origin == destination) {
            throw new IllegalArgumentException("Origin and destination cannot be the same");
        }

        // Guard against nodes that don't actually belong to this Router's network -
        // without this, we'd silently fail to find a route instead of saying why.
        if (!network.getNodes().containsValue(origin) || !network.getNodes().containsValue(destination)) {
            throw new IllegalArgumentException("Origin and destination must belong to this Router's network");
        }

        // gScore(node) = cheapest cost from start to node
        // Nodes not in this map are considered to have infinite gScore

        Map<Node, Double> gScore = new HashMap<>();
        gScore.put(origin, 0.0);

        // cameFrom(node) = the PathwaySegment that most efficiently leads to node
        // current-cheapest path. This is what lets us walk backwards from the
        // destination
        // to the origin once we were done, to build the actual route.
        Map<Node, PathwaySegment> cameFrom = new HashMap<>();

        // The "frontier": nodes we know how to reach but haven't explored the
        // neighbours of
        // yet, ordered cheapest-fScore-first so we always expand the most promising one
        // next.
        PriorityQueue<Node> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(node -> gScore.get(node) + heuristic(node, destination)));
        openSet.add(origin);

        // Nodes we've fully explored already (looked at all their neighbours). Once a
        // node
        // is settled here its gScore is final, so we never need to revisit it.
        Set<Node> settled = new HashSet<>();

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            // Java's PriorityQueue can't update an entry's priority in place, so when we
            // find
            // a cheaper way to reach a node (further down), we just add it again rather
            // than
            // reordering the old entry. That leaves stale duplicate copies of `current`
            // sitting
            // in the queue. This check throws those stale copies away instead of
            // reprocessing them.
            if (!settled.add(current)) {
                continue;
            }

            if (current == destination) {
                return buildRoute(origin, destination, cameFrom);
            }

            for (PathwaySegment segment : current.getConnections()) {
                Node neighbor = segment.opposite(current);
                if (settled.contains(neighbor)) {
                    continue; // Ignore the neighbor which is already evaluated.
                }

                double tentativeGScore = gScore.get(current) + segment.getLength();
                if (tentativeGScore < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    // Found a cheaper way to reach neighbour than any we knew about before,
                    // record it and (re)queue neighbour so it gets explored with this new score.
                    gScore.put(neighbor, tentativeGScore);
                    cameFrom.put(neighbor, segment);
                    openSet.add(neighbor);
                }
            }
        }
        
        return null; // No route found
    }

    /**
     * Straight-line distance from a node to the destination, used as the A* heuristic.
     * @param from the node to estimate from
     * @param destination the target node
     * @return the estimated (straight-line) remaining distance
     */
    private double heuristic(Node from, Node destination) {
        return from.getPos().distanceTo(destination.getPos());
    }

    /**
     * Walks the cameFrom chain backwards from destination to origin to reconstruct the
     * ordered list of segments that make up the path, then wraps it in a Route.
     * @param origin the starting node
     * @param destination the node we finished on
     * @param cameFrom the segment used to reach each node on its cheapest known path
     * @return the assembled Route, origin -> destination
     */
    private Route buildRoute(Node origin, Node destination, Map<Node, PathwaySegment> cameFrom) {
        LinkedList<PathwaySegment> segments = new LinkedList<>();
        Node current = destination;
        while (current != origin) {
            PathwaySegment segment = cameFrom.get(current);
            segments.addFirst(segment); // build in reverse, so the final order is origin -> destination
            current = segment.opposite(current);
        }
        return new Route(origin, destination, segments);
    }
}

