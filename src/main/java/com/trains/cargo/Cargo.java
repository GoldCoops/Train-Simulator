package com.trains.cargo;

import com.trains.network.GridPos;

import java.util.Objects;

public class Cargo {
    private final GridPos destination;
    private final CargoType type;
    private final int units;


    private Cargo(GridPos destination, CargoType type, int units) {
        this.destination = Objects.requireNonNull(destination);
        this.type = Objects.requireNonNull(type);
        this.units = units;
    }

    public static Cargo passenger(GridPos destination) {return new Cargo(destination, CargoType.PASSENGER, 1);}
    public static Cargo freight(GridPos destination,int units) {return new Cargo(destination, CargoType.FREIGHT, units);}


    public int getUnits() {
        return units;
    }
    public CargoType getType() {
        return type;
    }

    public GridPos getDestination() {
        return destination;
    }

}
