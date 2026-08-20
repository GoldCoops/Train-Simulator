package com.trains.vehicles;

import com.trains.network.*;

public class Vehicle {
    private int x = 0, y = 0;
    // GridPos is a record, and as such the values inside are immutable, trains need to move, so we need something different.
    private boolean isStopped;
    private PathwaySegment curSegment;
    private int capacity; 
    private float speed;


    public Vehicle(PathwaySegment curSegment) {
        //position initialisation to be added...
        this.curSegment = curSegment;
        this.isStopped = true;
        this.capacity = 0;
        this.speed = 0;
    }

    public PathwaySegment getCurrentSegment() {
        return curSegment;
    }

    public void setNextSegment(PathwaySegment nextSegment) {
        this.curSegment = nextSegment;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    } 

    public void stop() {
        this.isStopped = true;
        this.speed = 0;
    }
    
    public void go() {
        this.isStopped = false;
    }

    public void updateSpeed() {
        //to either increase or decrease speed when going to a station
    }


}
