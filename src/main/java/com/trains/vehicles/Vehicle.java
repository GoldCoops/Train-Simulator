package com.trains.vehicles;

import com.trains.network.*;

public class Vehicle {
    private double x, y;
    // GridPos is a record, and as such the values inside are immutable, trains need to move, so we need something different.
    private boolean isStopped;
    private PathwaySegment curSegment;
    private int capacity;
    private float maxSpeed;
    private float speed;
    private final float acceleration;


    public Vehicle(int x, int y, PathwaySegment curSegment, float maxSpeed, float acceleration) {
        //position initialisation to be added...
        this.x = x;
        this.y = y;
        this.curSegment = curSegment;
        this.isStopped = true;
        this.capacity = 0;
        this.maxSpeed = maxSpeed;
        this.speed = 0;
        this.acceleration = acceleration;
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
            decelerateTrain();
        } else {
            accelerateTrain();
        }
    }


    /**
     * Accelerates the train until it reaches its max speed value
     */
    private void accelerateTrain() { // this belongs in vehicle, not train, rewrite. also, this does not do what you think it does, this will instantly accelerate to max speed.
        /*
        while(super.speed > currentSpeed) {
            currentSpeed += acceleration;
        }
         */
    }

    /**
     * Decelerates the train until it stops
     */
    private void decelerateTrain() { // again, this belongs in vehicle, not train, rewrite. also does the same thing, instant deceleration.
        /*
        while(currentSpeed > 0) {
            currentSpeed -= acceleration;
        }

        currentSpeed = 0;
        */
    }

    public void updateSpeed() {
        //to either increase or decrease speed when going to a station
    }


}
