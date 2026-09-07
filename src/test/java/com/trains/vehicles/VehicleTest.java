package com.trains.vehicles;

import com.trains.cargo.Cargo;
import com.trains.cargo.CargoHold;
import com.trains.cargo.CargoTransfer;
import com.trains.cargo.CargoType;
import com.trains.network.Network;
import com.trains.network.Station;
import com.trains.routing.Route;
import com.trains.routing.Router;
import com.trains.utils.GridPos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleTest {
    @Test
    void boardPassengersMovesPassengerFromStationToVehicle(){
        Network network = new Network();

        Station origin = network.addStation(new GridPos(0, 0), 10);

        Station destination = network.addStation(new GridPos(10, 0), 10);

        network.connectNodes(origin, destination);

        Router router = new Router(network);
        Route route = router.findRoute(origin, destination);

        Itinerary itinerary = new Itinerary(route);

        CargoHold vehicleHold = CargoHold.forType(10, CargoType.PASSENGER);

        Vehicle vehicle = new Vehicle(itinerary, 10, 1, vehicleHold);

        Cargo passenger = Cargo.passenger(destination.getPos());
    
    CargoTransfer.insertCargo(origin.getCargoHold(), passenger);

    int boarded = vehicle.boardPassengers(origin);

    assertEquals(1, boarded);

    assertEquals(0, origin.getCargoHold().getUsedUnits());

    assertEquals(1, vehicle.getCargoHold().getUsedUnits());
    }

    @Test
    void unloadPassengersRemovesPassengerAtDestination(){
        Network network = new Network();

        Station origin = network.addStation(new GridPos(0,0), 10);
        Station destination = network.addStation(new GridPos(10, 0), 10);

        network.connectNodes(origin, destination);

        Router router = new Router(network);
        Route route = router.findRoute(origin, destination);
        Itinerary itinerary = new Itinerary(route);

        CargoHold vehicleHold = CargoHold.forType(10, CargoType.PASSENGER);

        Vehicle vehicle = new Vehicle(itinerary, 10, 1, vehicleHold);

        Cargo passenger = Cargo.passenger(destination.getPos());

        CargoTransfer.insertCargo(vehicle.getCargoHold(), passenger);

        itinerary.advance();

        int unload = vehicle.unloadPassengers(destination);

        assertEquals(1, unload);
        assertEquals(0, vehicle.getCargoHold().getUsedUnits());
    }

    @Test
    void passengerStaysOnAtWrongStation(){
        Network network = new Network();

        Station origin = network.addStation(new GridPos(0, 0), 10);
        Station middle = network.addStation(new GridPos(10, 0), 10);
        Station destination = network.addStation(new GridPos(20, 0), 10);

        network.connectNodes(origin, middle);
        network.connectNodes(middle, destination);

        Router router = new Router(network);
        Route route = router.findRoute(origin, destination);
        Itinerary itinerary = new Itinerary(route);

        CargoHold vehicleHold = CargoHold.forType(10, CargoType.PASSENGER);

        Vehicle vehicle = new Vehicle(itinerary, 10, 1, vehicleHold);

        Cargo passenger = Cargo.passenger(destination.getPos());

        CargoTransfer.insertCargo(vehicle.getCargoHold(), passenger);

        int unload = vehicle.unloadPassengers(middle);

        assertEquals(0, unload);
        assertEquals(1, vehicle.getCargoHold().getUsedUnits());
    }

}
