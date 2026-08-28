package com.trains.cargo;

import com.trains.utils.GridPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class CargoTest {

    private final GridPos destination = new GridPos(5, 125);
    @Test
    void testCargo() {

        //Constructor test
        Cargo cargo = new Cargo(destination, CargoType.FREIGHT, 100);
        assertEquals(destination, cargo.getDestination());
        assertEquals(CargoType.FREIGHT, cargo.getType());
        assertEquals(100, cargo.getUnits());

        // zero units
        assertThrows(IllegalArgumentException.class, () -> new Cargo(destination, CargoType.FREIGHT, 0));

        //negative units
        assertThrows(IllegalArgumentException.class, () -> new Cargo(destination, CargoType.FREIGHT, -5));

        //one unit
        Cargo min = new Cargo(destination, CargoType.FREIGHT, 1);
        assertEquals(1, min.getUnits());
        
        //null destination
        assertThrows(NullPointerException.class, () -> new Cargo(null, CargoType.FREIGHT, 5));

        //null type
        assertThrows(NullPointerException.class, () -> new Cargo(destination, null, 5));

        //two instances with identical fields
        Cargo cargoA = new Cargo(destination, CargoType.FREIGHT, 5);
        Cargo cargoB = new Cargo(destination, CargoType.FREIGHT, 5);
        assertNotSame(cargoA, cargoB);  
    }

    @Test
    void testPassenger() {
        //constructor test
        Cargo cargo = Cargo.passenger(destination);
        assertEquals(CargoType.PASSENGER, cargo.getType());
        assertEquals(1, cargo.getUnits());
        assertEquals(destination, cargo.getDestination());

        //different destinations
        GridPos otherDestination = new GridPos(5, 10);
        Cargo other = Cargo.passenger(otherDestination);
        assertEquals(otherDestination, other.getDestination());

        //null destination 
        assertThrows(NullPointerException.class, () -> Cargo.passenger(null));
        
        //multiple calls of passenger
        Cargo cargoA = Cargo.passenger(destination);
        Cargo cargoB = Cargo.passenger(destination);
        assertNotSame(cargoA, cargoB);
    }


}
