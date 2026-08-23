package com.trains.network;


import com.trains.cargo.Cargo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Station class
 * Tracks everything needed on top of the regular node for a station, such as capacity and the current amount of passengers.
 */
public class Station extends Node {
    private final int capacity;
    private int currentPassengers;



    Station(GridPos pos, int capacity) {
        super(pos);
        this.capacity = capacity;
    }

    /**
     * gets the total capacity of the station
     * @return the total capacity
     */
    public int getCapacity() {
        return capacity;
    }


    /**
     * gets the current amount of passengers at this station
     * @return the current amount of passengers
     */
    public List<Cargo> getCargo() {
        return Collections.unmodifiableList(cargo);
    }


    /**
     * if the station is full
     * @return true if the station is full, false otherwise
     */
    public boolean isFull() {
        return cargo.size() >= capacity;
    }




}
