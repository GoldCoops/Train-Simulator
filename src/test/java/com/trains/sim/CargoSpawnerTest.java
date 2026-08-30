package com.trains.sim;

import com.trains.cargo.Cargo;
import com.trains.network.Network;
import com.trains.network.Station;
import com.trains.utils.GridPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CargoSpawnerTest {
    @Test
    void spawnPassengerAddsPassengerToOriginStation(){
        Network network = new Network();

        Station origin = network.addStation(new GridPos(10,20), 10);
        Station destination = network.addStation(new GridPos(30,20), 10);

        CargoSpawner spawner = new CargoSpawner(network);

        boolean spawned = spawner.spawnPassenger(origin, destination);

        assertTrue(spawned);
        assertEquals(1, origin.getCargoHold().getUsedUnits());
    }

    @Test
    void spawnPassengerRejectsSameOriginAndDestination(){
        Network network = new Network();

        Station station = network.addStation(new GridPos(10,20), 10);

        CargoSpawner spawner = new CargoSpawner(network);

        assertThrows(IllegalArgumentException.class, () -> spawner.spawnPassenger(station, station));
    }

    @Test
    void spwawnPassengerUsesStations(){
        Network network = new Network();

        Station first = network.addStation(new GridPos(10,20), 20); 
        Station second = network.addStation(new GridPos(30,20), 10);
        
        CargoSpawner spawner = new CargoSpawner(network);

        boolean spawned = spawner.spawnPassenger();

        int totalPassengers = first.getCargoHold().getUsedUnits() + second.getCargoHold().getUsedUnits();

        assertTrue(spawned);
        assertEquals(1, totalPassengers);
    }

    @Test
    void spawnPassengerLessThanTwoStation(){
        Network network = new Network();

        network.addStation(new GridPos(10, 20), 10);

        CargoSpawner spawner = new CargoSpawner(network);

        assertFalse(spawner.spawnPassenger());
    }
}
