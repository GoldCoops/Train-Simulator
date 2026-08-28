package com.trains.sim;
import com.trains.cargo.*;
import com.trains.network.*;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    /**
     * Get all current stations in the network
     * @return a list containing all the stations in the network
     */

    private List<Station> getStations(){
        List<Station> stations = new ArrayList<>();

        for(Node node: network.getNodes().values()){
            if (node instanceof Station){
                stations.add((Station) node);
            }
        }

        return stations;
    }

    /**
     * Spawn the passenger using stations from that network
     * @return true if the passenger was siccessfully spawned
     */
    public boolean spawnPassenger(){
        List<Station> stations = getStations();

        if (stations.size() < 2){
            return false;
        }

        Random random = new Random();

        Station origin = stations.get(random.nextInt(stations.size()));

        Station destination;
        do{
            destination = stations.get(random.nextInt(stations.size()));
        } while (destination == origin);

        return spawnPassenger(origin, destination);
    }

}
