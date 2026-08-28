package com.trains.cargo;

import com.trains.utils.GridPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class CargoTransferTest {
    
    @Test
    void insertCargoAddPassengersToHold(){
        CargoHold hold = CargoHold.forType(10, CargoType.PASSENGER);
        Cargo passenger = Cargo.passenger(new GridPos(5, 5));

         boolean inserted = CargoTransfer.insertCargo(hold, passenger);

        assertTrue(inserted);
        assertEquals(1, hold.getUsedUnits());
    }
}
