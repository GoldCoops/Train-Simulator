package com.trains.sim;

import com.trains.network.Network;
import com.trains.vehicles.Vehicle;

import java.util.*;


public class Simulation {
    // Should hold the network and a list of vehicles, with a tick method that advances time.
    private final Network network;
    private final List<Vehicle> vehicles;

    public Simulation() {
        this(new Network(), new ArrayList<>());
    }

    public Simulation(Network network, List<Vehicle> vehicles) {
        this.network = network;
        this.vehicles = vehicles;
    }

    public Network getNetwork() {
        return network;
    }
    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicles.remove(vehicle);
    }
    public void tick() {
        for ( Vehicle vehicle : vehicles) {
            vehicle.update();
        }
    }
}
