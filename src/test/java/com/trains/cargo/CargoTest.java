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
        //test type, unit and destination of passenger()
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

    @Test
    void testFreight()  {
        //test destination and units of freight()
        Cargo cargo = Cargo.freight(destination, 10);
        assertEquals(CargoType.FREIGHT, cargo.getType());
        assertEquals(10, cargo.getUnits());
        assertEquals(destination, cargo.getDestination());

        //zero units
        assertThrows(IllegalArgumentException.class, () -> Cargo.freight(destination, 0));

        //negative units
        assertThrows(IllegalArgumentException.class, () -> Cargo.freight(destination, -10));

        //one unit
        Cargo min = Cargo.freight(destination, 1);
        assertEquals(1, min.getUnits());

        //null destination
        assertThrows(NullPointerException.class, () -> Cargo.freight(null, 10));

        // unit counts
        assertEquals(100, Cargo.freight(destination, 100).getUnits());
        assertEquals(100000, Cargo.freight(destination, 100000).getUnits());

        //multiple calls of freight
        Cargo cargoA = Cargo.freight(destination, 5);
        Cargo cargoB = Cargo.freight(destination, 5);
        assertNotSame(cargoA, cargoB);
    }


}
