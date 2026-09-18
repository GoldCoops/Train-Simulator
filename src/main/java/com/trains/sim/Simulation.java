package com.trains.sim;

import com.trains.network.Network;
import com.trains.vehicles.Vehicle;

import java.util.*;

/**
 * The Simulation class
 * <p>
 *     Holds the network and a list of vehicles, and provides a tick method to advance the simulation by one tick.
 * </p>
 */
public final class Simulation {
    // Should hold the network and a list of vehicles, with a tick method that advances time.
    private final Network network;
    private final List<Vehicle> vehicles;

    /**
     * Simulation constructor with empty network and vehicle list
     */
    public Simulation() {
        this(new Network(), new ArrayList<>());
    }

    /**
     * Simulation constructor with empty vehicle list
     * <p>
     *     Instantiates a blank vehicle list, uses the network provided
     * </p>
     * @param network the network of the simulation
     */
    public Simulation(Network network) {
        this(network, new ArrayList<>());
    }

    /**
     * Simulation constructor
     * @param network the network of the simulation
     * @param vehicles the list of vehicles in the simulation
     */
    public Simulation(Network network, List<Vehicle> vehicles) {
        this.network = network;
        this.vehicles = vehicles;
    }

    /**
     * Gets the {@link Network} of the simulation
     * @return the network
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * Gets the list of {@link Vehicle}s in the simulation
     * @return an unmodifiable list of vehicles
     */
    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    /**
     * Adds a {@link Vehicle} to the simulation
     * @param vehicle vehicle to be added
     */
    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    /**
     * Removes a {@link Vehicle} from the simulation
     * @param vehicle vehicle to be removed
     */
    public void removeVehicle(Vehicle vehicle) {
        vehicles.remove(vehicle);
    }

    /**
     * Advances the simulation by one tick
     * @param dt elapsed simulated time since last tick
     */
    public void tick(double dt) {
        for ( Vehicle vehicle : vehicles) {
            vehicle.update(dt);
        }
    }
}
