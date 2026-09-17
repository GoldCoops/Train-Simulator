package com.trains.cargo;


import java.util.Objects;

import com.trains.utils.GridPos;

public final class Cargo {
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


    /**
     * Passenger Cargo factory method
     * @param destination the destination of the cargo
     * @return The created Cargo Object
     * @throws NullPointerException if destination is null
     */
    public static Cargo passenger(GridPos destination) throws NullPointerException{
        return new Cargo(destination, CargoType.PASSENGER, 1);
    }

    /**
     * Freight Cargo factory method
     * @param destination The destination of the cargo
     * @param units The number of units of freight
     * @return The created Cargo Object
     * @throws IllegalArgumentException if units is less than 1
     * @throws NullPointerException if destination is null
     */
    public static Cargo freight(GridPos destination, int units) throws IllegalArgumentException{
        return new Cargo(destination, CargoType.FREIGHT, units);
    }


    /**
     * Returns the number of units this cargo takes up
     * @return
     */
    public int getUnits() {
        return units;
    }

    /**
     * Returns the type of cargo
     * @return the type of cargo
     */
    public CargoType getType() {
        return type;
    }

    /**
     * Returns the destination of the cargo as a {@link GridPos}
     * @return the destination of the cargo
     */
    public GridPos getDestination() {
        return destination;
    }

}
