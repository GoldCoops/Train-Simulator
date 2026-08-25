package com.trains.network;


import com.trains.cargo.CargoHold;
import com.trains.utils.GridPos;

/**
 * The Station class
 * Keeps a cargo hold on top of regular nodes, which allows it to store cargo/passengers
 */
public class Station extends Node {
    private final CargoHold cargoHold;



    Station(GridPos pos, int capacity) {
        super(pos);
        this.cargoHold = CargoHold.mixed(capacity);
    }

    /**
     * gets the total capacity of the station
     * @return the total capacity
     */
    public int getCapacity() {
        return cargoHold.getCapacity();
    }


    /**
     * gets the current amount of passengers at this station
     * @return the current amount of passengers
     */
    public CargoHold getCargoHold() {
        return cargoHold;
    }





}
