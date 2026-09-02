package com.trains.cargo;

import com.trains.utils.GridPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class CargoHoldTest {
    
    @Test
    void nullCargoType() {
        assertThrows(NullPointerException.class, () -> CargoHold.forType(10, null));
        assertThrows(NullPointerException.class, () -> CargoHold.forType(-1, null));
        assertThrows(NullPointerException.class, () -> CargoHold.forType(0, null));
    }

    @Test
    void acceptsOnlySpecifiedType() {
        GridPos destination = new GridPos(15, 30);
        CargoHold passengerHold = CargoHold.forType(10, CargoType.PASSENGER);
        assertTrue(passengerHold.canAccept(Cargo.passenger(destination)));
        assertFalse(passengerHold.canAccept(Cargo.freight(destination, 205)));

        CargoHold freightHold = CargoHold.forType(100, CargoType.FREIGHT);
        assertTrue(freightHold.canAccept(Cargo.freight(destination, 25)));
        assertFalse(freightHold.canAccept(Cargo.passenger(destination)));
        
        CargoHold exact = CargoHold.forType(10, CargoType.FREIGHT);
        assertTrue(exact.canAccept(Cargo.freight(destination, 10)));

        CargoHold bigCapacity = CargoHold.forType(10000, CargoType.PASSENGER);
        assertFalse(bigCapacity.canAccept(Cargo.freight(destination, 1)));
    }

}
 