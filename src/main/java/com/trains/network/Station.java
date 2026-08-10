package com.trains.network;


import java.util.ArrayList;
public class Station extends Node{

    private final int capacity;
    private int currentPassengers;

    public Station(GridPos pos, int capacity) {
        super(pos);
        this.capacity = capacity;
    }



}
