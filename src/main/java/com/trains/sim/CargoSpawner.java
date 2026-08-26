package com.trains.sim;
import com.trains.cargo.*;
import com.trains.network.*;


import java.util.Objects;

public final class CargoSpawner {
    private final Network network;

    /**
     * CargoSpawner constructor
     * @param network The main network object
     * @throws NullPointerException If network is null
     */
    public CargoSpawner(Network network) throws NullPointerException{
        this.network = Objects.requireNonNull(network);
    }

    public boolean spawnPassenger(Station origin, Station destination){
        Objects.requireNonNull(origin);
        Objects.requireNonNull(destination);

        if(origin == destination){
            throw new IllegalArgumentException("Origin and destination must be different");
        }

        Cargo passenger = Cargo.passenger(destination.getPos());

        return CargoTransfer.insertCargo(origin.getCargoHold(), passenger);
    }

}
