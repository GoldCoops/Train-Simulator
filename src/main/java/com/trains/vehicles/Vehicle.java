package com.trains.vehicles;

import java.util.ArrayList;
import java.util.List;

import com.trains.cargo.CargoHold;
import com.trains.network.*;

import static com.trains.utils.MathUtils.*;

public class Vehicle {
    private double x, y;
    // GridPos is a record, and as such the values inside are immutable, trains need to move, so we need something different.
    private boolean isStopped;
    private PathwaySegment curSegment;
    private final CargoHold cargoHold; // CargoHold now manages capacity per hold
    private float speed;
    private final float maxSpeed;
    private final float acceleration;
    private Node targetNode;
    private final Itinerary itinerary;

    private double distanceAlong; // We should switch from using x and y values for the coordinates to a position along a PathwaySegment, and then interpolate X and Y when we need them for rendering in getX and getY



    public Vehicle(Itinerary itinerary, int x, int y, float maxSpeed, float acceleration, PathwaySegment curSegment, CargoHold cargoHold, Node targetNode) {
        this.itinerary = itinerary;
        this.x = x;
        this.y = y;
        this.curSegment = curSegment;
        this.cargoHold = cargoHold;
        this.isStopped = true;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration + (1000 / this.cargoHold.getCapacity()); // slightly changes acceleration value based on train capacity (NOT FINAL FORMULA)
        this.targetNode = targetNode;
    }


    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public Node getTargetNode() {
        return targetNode;
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



    public void update(double dt) { // this is just an example of what we should be doing, it needs to be edited.
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

    /**
     * Makes sure vehicle statisfes conditions required to depart from station
     * @return a list of delays found for a train to depart
     * Used as a helper function for canDepart()
     * The different delays etc can be changed later on when we are more clear on when a vehicle can leave a station
     */

    public List<String> checkDepartureBlockers() {
        List<String> blockers = new ArrayList<>();

        if (!isStopped) {
            blockers.add("Vehicle is moving");
        }

        if (curSegment == null) {
            blockers.add("Vehicle has no pathway segment");
        }

        if (maxSpeed <= 0) {
            blockers.add("Vehicle has no max speed assigned");
        }
        return blockers;
    }

    /* 
      @return a boolean to check whether a vehicle should depart or not from a station
    */

    public boolean canDepart() {
        return checkDepartureBlockers().isEmpty();
    }
}
