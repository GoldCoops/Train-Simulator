package com.trains.network;



/**
 * The Station class
 * Tracks everything needed on top of the regular node for a station, such as capacity and the current amount of passengers.
 */
public class Station extends Node{

    private final int capacity;
    private int currentPassengers;

    public Station(GridPos pos, int capacity) {
        super(pos);
        this.capacity = capacity;
    }



}
