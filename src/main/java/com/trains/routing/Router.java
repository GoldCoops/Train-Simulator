package com.trains.routing;

import com.trains.network.Network;
import com.trains.network.Node;

public class Router {
    // Handles pathfinding logic, I'm probably going to try using A* here, but if that fails I'll just use Dijkstra
    private final Network network;
    public Router(Network network) {
        this.network = network;
    }

    public Route findRoute(Node origin, Node destination) {
        return null;
    }
}
