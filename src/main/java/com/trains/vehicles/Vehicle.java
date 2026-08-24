package com.trains.vehicles;

import com.trains.cargo.CargoHold;
import com.trains.network.*;

public class Vehicle {
    private double x, y;
    // GridPos is a record, and as such the values inside are immutable, trains need to move, so we need something different.
    private boolean isStopped;
    private PathwaySegment curSegment;
    private final CargoHold cargoHold; // CargoHold now manages capacity per hold
    private float speed;
    private final float maxSpeed;
    private final float acceleration;


    public Vehicle(int x, int y, float maxSpeed, float acceleration, PathwaySegment curSegment, CargoHold cargoHold) {
        //position initialisation to be added...
        this.x = x;
        this.y = y;
        this.curSegment = curSegment;
        this.cargoHold = cargoHold;
        this.isStopped = true;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration + (1000 / this.cargoHold.getCapacity()); // slightly changes acceleration value based on train capacity (NOT FINAL FORMULA)
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
    public CargoHold getCargoHold() {
        return cargoHold;
    }

    public void setNextSegment(PathwaySegment nextSegment) {
        this.curSegment = nextSegment;
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
        }
    }

        /**
     * Moves the train towards the target
     * @param current
     * @param target
     * @throws IllegalArgumentException if the nodes supplied to the methods are the same node
     */
    protected void moveTowardsNextNode(Node current, Node target) { // to be reworked to use PathwaySegment instead of the current node
        if(current == target) {
            throw new IllegalArgumentException("Train cannot move between the nodes at the same position");
        }

        int currentX = current.getX();
        int targetX = target.getX();
        int currentY = current.getY();
        int targetY = target.getY();

        // Need to implement an error where the nodes supplied are not adjacent
        // if()
        // ...

        if(currentX < targetX) {
            currentX += speed;
        }

        if(currentX > targetX) {
            currentX -= speed;
        }

        if(currentY < targetY) {
            currentY += speed;
        }

        if(currentY > targetY) {
            currentY -= speed;
        }
    }


    public void updateSpeed() { // Redundant due to existence of accelerate() and decelerate() methods. To be removed
        //to either increase or decrease speed when going to a station
    }
}
