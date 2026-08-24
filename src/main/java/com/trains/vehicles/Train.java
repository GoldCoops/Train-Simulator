package com.trains.vehicles;

import com.trains.cargo.CargoHold;
import com.trains.network.PathwaySegment;
import com.trains.network.*;

public class Train extends Vehicle {


    public Train(int x, int y, float maxSpeed, float acceleration, PathwaySegment curSegment, CargoHold cargoHold, Node targetNode) {
        super(x, y, maxSpeed, acceleration, curSegment, cargoHold, targetNode);
    }

}
