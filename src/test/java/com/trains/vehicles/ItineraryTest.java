package com.trains.vehicles;

import com.trains.network.Network;
import com.trains.network.Station;
import com.trains.routing.Route;
import com.trains.routing.Router;
import com.trains.utils.GridPos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItineraryTest {

    @Test
    void willVisitReturnsTrueForDestinationAhead(){
        Network network = new Network();

        Station stationA = network.addStation(new GridPos(0,0), 10);
        Station stationB = network.addStation(new GridPos(10, 0), 10);
        Station stationC = network.addStation(new GridPos(20,0), 10);

        network.connectNodes(stationA, stationB);
        network.connectNodes(stationB, stationC);

        Router router = new Router(network);
        Route route = router.findRoute(stationA, stationC);

        Itinerary itinerary = new Itinerary(route);

        assertTrue(itinerary.willVisit(stationC.getPos()));
    }
}
