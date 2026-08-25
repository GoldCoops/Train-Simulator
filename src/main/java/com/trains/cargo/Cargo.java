package com.trains.cargo;


import java.util.Objects;

import com.trains.utils.GridPos;

public class Cargo {
    private final GridPos destination;
    private final CargoType type;
    private final int units;


    Cargo(GridPos destination, CargoType type, int units) {
        if (units < 1) {
            throw new IllegalArgumentException("Units must be greater than 0");
        }
        this.destination = Objects.requireNonNull(destination);
        this.type = Objects.requireNonNull(type);
        this.units = units;
    }


    public static Cargo passenger(GridPos destination) throws IllegalArgumentException {
        return new Cargo(destination, CargoType.PASSENGER, 1);
    }

    public static Cargo freight(GridPos destination, int units) throws IllegalArgumentException{
        return new Cargo(destination, CargoType.FREIGHT, units);
    }



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
