package com.trains.vehicles;

import com.trains.network.PathwaySegment;

public class Train extends Vehicle {
    private float acceleration;
    private float currentSpeed;

    public Train(PathwaySegment curSegment, float maxSpeed) {
        super(0,0,curSegment);
        this.acceleration = .2f + (super.capacity / 1000);
        super.speed = maxSpeed;
        this.currentSpeed = 0;
    }

    /**
     * Accelerates the train until it reaches its max speed value
     */
    private void accelerateTrain() {
        while(super.speed > currentSpeed) {
            currentSpeed += acceleration;
        }
    }
    
    /**
     * Decelerates the train until it stops
     */
    private void decelerateTrain() {
        while(currentSpeed > 0) {
            currentSpeed -= acceleration;
        }

        currentSpeed = 0; // Sets speed to zero in case it goes to the negatives
    }
}
