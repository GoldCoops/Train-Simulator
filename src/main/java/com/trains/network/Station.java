package com.trains.network;


import com.trains.cargo.CargoHold;
import com.trains.utils.GridPos;

/**
 * The Station class
 * Keeps a cargo hold on top of regular nodes, which allows it to store cargo/passengers
 */
public class Station extends Node {
    private final CargoHold cargoHold;
    private String name;



    Station(GridPos pos, int capacity) {
        super(pos);
        this.cargoHold = CargoHold.mixed(capacity);
    }

    Station(GridPos pos, int capacity, String name) {
        this(pos, capacity);
        this.name = name;
    }

    /**
     * gets the total capacity of the station
     * @return the total capacity
     */
    public int getCapacity() {
        return cargoHold.getCapacity();
    }


    /**
     * Sets the station name/id
     * @param name the name/id
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the station name/id
     * @return the name/id
     */
    public String getName() {
        return name;
    }

    /**
     * gets the current amount of passengers at this station
     * @return the current amount of passengers
     */
    public CargoHold getCargoHold() {
        return cargoHold;
    }

    @Override
    public String toString() {
        return name + " { " + super.toString() + " }";
    }





}
