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
    public int getCurrentPassengers() {
        return currentPassengers;
    }

    /**
     * Adds one passenger for each call
     */
    public void addPassenger() {
        currentPassengers++;
    }
    /**
     * Removes one passenger for each call
     */
    public void removePassenger() {
        currentPassengers--;
    }

    /**
     * Increments currentPassengers by the int supplied
     * @param num the amount to increment (or decrement with a negative value) currentPassengers by
     * @throws IllegalArgumentException if the num supplied would send currentPassengers outside of capacity or below 0.
     */
    public void incrementPassengers(int num) throws IllegalArgumentException{ // I really hate the name of this method, idk what else to call it though, any suggestions are welcome gang
        if (currentPassengers + num < 0 || currentPassengers + num > capacity) {
            throw new IllegalArgumentException("Cannot add " + num + " passengers to station");
        }
        currentPassengers += num;
    }

    /**
     * if the station is full
     * @return true if the station is full, false otherwise
     */
    public boolean isFull() {
        return currentPassengers >= capacity;
    }




}
