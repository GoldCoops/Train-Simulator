package com.trains.cargo;
import com.trains.network.*;

import java.util.Objects;

public final class CargoSpawner {
    private final Network network;

    public CargoSpawner(Network network) {
        this.network = Objects.requireNonNull(network);
    }

    public Cargo passenger(GridPos destination) throws IllegalArgumentException {
        requireStation(destination);
        return new Cargo(destination, CargoType.PASSENGER, 1);
    }

    /**
     * Freight factory method - does not register cargo or place it in a CargoHold.
     * @param destination The destination of the freight
     * @param units the amount of space the freight should take
     * @return the created cargo object
     * @throws IllegalArgumentException
     */
    public Cargo freight(GridPos destination,int units) throws IllegalArgumentException {
        requireStation(destination);
        if (units < 1) {
            throw new IllegalArgumentException("Units must be greater than 0");
        }
        return new Cargo(destination, CargoType.FREIGHT, units);
    }

    /** This handles whether or not a station is on the supplied destination, however it does not guarantee that the station is reachable through connections, which needs to be added */
    private void requireStation(GridPos destination) {
        if (!network.isStationAt(destination)) {
            throw new IllegalArgumentException("No station at position " + destination);
        }
    }

}
