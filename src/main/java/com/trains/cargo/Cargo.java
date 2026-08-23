package com.trains.cargo;


import java.util.Objects;
import com.trains.network.*;

public class Cargo {
    private final GridPos destination;
    private final CargoType type;
    private final int units;


    Cargo(GridPos destination, CargoType type, int units) {
        this.destination = Objects.requireNonNull(destination);
        this.type = Objects.requireNonNull(type);
        this.units = units;
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
