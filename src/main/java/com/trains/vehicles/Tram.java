package com.trains.vehicles;

import com.trains.cargo.CargoHold;
import com.trains.network.*;

public class Tram extends Vehicle {
        // This class of a vehicle that only carries passengers and not freight 
        
    public Tram(int x, int y, float maxSpeed, float acceleration, PathwaySegment curSegment, CargoHold cargoHold, Node targetNode) {
        super(x, y, maxSpeed, acceleration, curSegment, cargoHold, targetNode);
    }
 
}