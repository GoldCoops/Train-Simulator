package com.trains.vehicles;

import com.trains.network.*;

public class Vehicle {
    private double x, y;
    // GridPos is a record, and as such the values inside are immutable, trains need to move, so we need something different.
    private boolean isStopped;
    private PathwaySegment curSegment;
    private int capacity;
    protected float speed;


    public Vehicle(int x, int y, PathwaySegment curSegment) {
        //position initialisation to be added...
        this.x = x;
        this.y = y;
        this.curSegment = curSegment;
        this.isStopped = true;
        this.capacity = 0;
        this.speed = 0;
    }


    public double getX() {
        return x;
    }
    public double getY() {
        return y;
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
