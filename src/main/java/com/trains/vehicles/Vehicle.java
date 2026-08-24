package com.trains.vehicles;

import com.trains.network.*;

public class Vehicle {
    private double x, y;
    // GridPos is a record, and as such the values inside are immutable, trains need to move, so we need something different.
    private boolean isStopped;
    private PathwaySegment curSegment;
    private int capacity;
    private float speed;
    private final float maxSpeed;
    private final float acceleration;


    public Vehicle(int x, int y, PathwaySegment curSegment, float maxSpeed, float acceleration) {
        //position initialisation to be added...
        this.x = x;
        this.y = y;
        this.curSegment = curSegment;
        this.isStopped = true;
        this.capacity = 0;
        this.speed = 0;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration + (1000 / this.capacity); // slightly changes acceleration value based on train capacity (NOT FINAL FORMULA)
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
    }
    
    public void go() {
        this.isStopped = false;
    }

    protected void update() { // this is just an example of what we should be doing, it needs to be edited.
        if (isStopped) {
            decelerate();
        } else {
            accelerate();
        }
    }


    /**
     * Accelerates the train until it reaches its max speed value
     */
    private void accelerate() {
        if(speed < maxSpeed) {
            speed += acceleration;
            isStopped = false;
        }
    }

    /**
     * Decelerates the train until it stops
     */
    private void decelerate() {
        if(speed > 0) {
            speed -= acceleration;
        }

        if(speed < 0) {
            speed = 0;
            isStopped = true;
        }
    }

    public void updateSpeed() {
        //to either increase or decrease speed when going to a station
    }
}
