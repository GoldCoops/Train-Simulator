package com.trains.vehicles;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.trains.cargo.Cargo;
import com.trains.cargo.CargoHold;
import com.trains.network.*;
import com.trains.routing.Route;
import com.trains.utils.Point2D;
import com.trains.cargo.CargoTransfer;
import com.trains.cargo.CargoType;

import static com.trains.utils.MathUtils.*;

public class Vehicle {
    private boolean isStopped;
    private final CargoHold cargoHold; // CargoHold now manages capacity per hold
    private float speed;
    private final float maxSpeed;
    private final float acceleration;
    private Itinerary itinerary;
    private double distanceAlong; // We should switch from using x and y values for the coordinates to a position along a PathwaySegment, and then interpolate X and Y when we need them for rendering in getX and getY



    public Vehicle(Itinerary itinerary, float maxSpeed, float acceleration, CargoHold cargoHold) {
        this.itinerary = Objects.requireNonNull(itinerary);
        this.cargoHold = Objects.requireNonNull(cargoHold);
        this.isStopped = true;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration; // removed the acceleration depending on capacity line, as capacity = 0 is allowed, which would error.
    }


    public PathwaySegment getCurrentSegment() {
        return itinerary.getCurrentSegment();
    }

    public Node getTargetNode() {
        return itinerary.getTargetNode();
    }

    public Node getEntryNode() {
        return itinerary.getEntryNode();
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    /** @return the current speed in units per second */
    public float getSpeed() { return speed; }

    /** @return true if the vehicle is stopped or braking to a stop */
    public boolean isStopped() { return isStopped; }

    /** @return true if the vehicle has finished its route */
    public boolean isRouteComplete() { return itinerary.isComplete(); }

    /**
     * Puts the vehicle onto a new route, stationary at that route's origin.
     * Cargo already on board is kept.
     * @param route the new route to follow
     * @throws NullPointerException if route is null
     */
    public void dispatch(Route route) {
        this.itinerary = new Itinerary(route);
        this.distanceAlong = 0;
        this.speed = 0;
        this.isStopped = true;
    }

    public CargoHold getCargoHold() {
        return cargoHold;
    }

    public void stop() {
        this.isStopped = true;
    }
    
    public void go() {
        this.isStopped = false;
    }

    /**
     * Gets the current X coordinate of the vehicle
     * @return The current X Coordinate
     */
    public double getX(){
        if (itinerary.isComplete()) {
            return itinerary.getEntryNode().getX();
        }
        double t = distanceAlong/itinerary.getCurrentSegment().getLength();
        return lerp(itinerary.getEntryNode().getX(), itinerary.getTargetNode().getX(), t);
    }

    /**
     * Gets the current Y coordinate of the Vehicle
     * @return The current Y coordinate
     */
    public double getY(){
        if (itinerary.isComplete()) {
            return itinerary.getEntryNode().getY();
        }
        double t = distanceAlong/itinerary.getCurrentSegment().getLength();
        return lerp(itinerary.getEntryNode().getY(), itinerary.getTargetNode().getY(), t);
    }

    /**
     * Gets the current X and Y coordinates as a Point2D Object for ease of use
     * @return The current X and Y coordinates
     */
    public Point2D getPos(){
        if (itinerary.isComplete()) {
            return itinerary.getEntryNode().getPos().toPoint2D();
        }
        double t = distanceAlong/itinerary.getCurrentSegment().getLength();
        return lerp(itinerary.getEntryNode().getPos(),itinerary.getTargetNode().getPos(), t);
    }

    public void update(double dt) { // this is just an example of what we should be doing, it needs to be edited.
        if(dt < 0) {
            throw new IllegalArgumentException("Function argument cannot be negative!");
        }

        float initialSpeed = speed;

        if (isStopped) {
            decelerate(dt);
        } else {
            accelerate(dt);
        }

        double distanceTravelled = ((initialSpeed + speed) / 2.0) * dt;
        moveAlongRoute(distanceTravelled);
    }

    /**
     * Accelerates the train until it reaches its max speed value
     */
    private void accelerate(double dt) {
        speed = Math.clamp(speed + acceleration * (float) dt, 0f, maxSpeed);
    }

    /**
     * Decelerates the train until it stops
     */
    private void decelerate(double dt) {
        speed = Math.clamp(speed - acceleration * (float) dt, 0f, maxSpeed);
    }

    /**
     * Moves the train along the route for the number of travel units passed into the function
     * @param distanceToMove
     */
    private void moveAlongRoute(double distanceToMove) {
        if(distanceToMove < 0) {
            throw new IllegalArgumentException("The value of units to move cannot be negative!");
        }

        while(distanceToMove > 0 && !itinerary.isComplete()) {
            PathwaySegment segment = itinerary.getCurrentSegment();
            double remainingOnSegment = segment.getLength() - distanceAlong;

            // Prevents overshooting onto the next segment where the train moves past the node
            if(distanceToMove < remainingOnSegment) {
                distanceAlong += distanceToMove;
                return;
            }

            distanceToMove -= remainingOnSegment;
            distanceAlong = 0;

            Node arrivedAt = itinerary.getTargetNode(); // gets the node the train arrived to
            itinerary.advance();

            // Arrived at the station
            if(arrivedAt instanceof Station station) { // stops the vehicle permanently at the station
                speed = 0;
                isStopped = true;

                unloadPassengers(station); //let passengers off
                
                //only board passengers if vehicle still has somewhere to travel
                if(!itinerary.isComplete()){
                    boardPassengers(station);
                }
                return;
            }

            //if route ends at a node that is not a station, stop vehicle
            if (itinerary.isComplete()){
                speed = 0;
                isStopped = true;
                return;
            }
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

        if (itinerary.getCurrentSegment() == null) {
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


    public String toString() {
        return "Vehicle { " + getPos() + ", " + speed + ", " + itinerary.getDestinationNode() + " }";
    }

    /**
     * Attempts to board passengers from a station onto this vehicle
     * A passenger can board if:
     * - the cargo is passenger
     * -the passenger's destination is still ahead on this's vehicle's route
     * -the vehicle has enough capacity
     * @param station, the station passengers are boarding from
     * @return the number of passengers successfully boarded
     */
    public int boardPassengers(Station station){
        Objects.requireNonNull(station);

        int boarded = 0;

        //check all the cargo currently waiting at the station
        for (Cargo cargo : station.getCargoHold().getContents()){
            if (cargo.getType() != CargoType.PASSENGER){
                continue;
            }

            //do not board the passengers whose destination is not ahead on the route
            if (!itinerary.willVisit(cargo.getDestination())){
            continue;
            }

            //Transfer the passengers from station to vehicle if vehicle has enough capacity
            boolean transferred = CargoTransfer.transferCargo(station.getCargoHold(), cargoHold, cargo);

            if (transferred){
                boarded++;
            }
        }

        return boarded;
    }
    /**
     * Unloads passengers from the vehicle when they reach their destination station
     * @param station, the station at which vehicle has arrived
     * @return number of passengers successfully unloaded
     */
    public int unloadPassengers(Station station){
        Objects.requireNonNull(station);

        int unload = 0;
        //Check each piece of the cargo currently on vehicle
        for (Cargo cargo : cargoHold.getContents()){
            if (cargo.getType() != CargoType.PASSENGER){
                continue;
            }
            //deliverCargo only removes the passenger if this is their destination
            if (CargoTransfer.deliverCargo(cargoHold, cargo, station.getPos())){
                unload++;
            }
        }
        return unload;
    }
}
