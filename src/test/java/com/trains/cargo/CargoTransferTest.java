package com.trains.cargo;

import com.trains.utils.GridPos;
import org.junit.jupiter.api.Test;
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

    @Test
    void movesCargo() {
        GridPos destination = new GridPos(5, 5);
        CargoHold source = CargoHold.forType(10, CargoType.FREIGHT);
        CargoHold dest = CargoHold.forType(10, CargoType.FREIGHT);
        Cargo cargo = Cargo.freight(destination, 5);

        source.addCargo(cargo);
        assertTrue(CargoTransfer.transferCargo(source, dest, cargo));
        assertFalse(source.hasCargo(cargo));
        assertTrue(dest.hasCargo(cargo));
    }

    @Test
    void sameCargoHold() {
        GridPos destination = new GridPos(15, 5);
        CargoHold hold = CargoHold.forType(10, CargoType.FREIGHT);
        Cargo cargo = Cargo.freight(destination, 5);
        hold.addCargo(cargo);

        assertTrue(CargoTransfer.transferCargo(hold,  hold, cargo));
        assertTrue(hold.hasCargo(cargo));
        assertEquals(5, hold.getUsedUnits());
    }

    @Test
    void cargoMissingFromSource() {
        GridPos destination = new GridPos(115, 50);
        CargoHold source = CargoHold.forType(40, CargoType.FREIGHT);
        CargoHold dest = CargoHold.forType(10, CargoType.FREIGHT);
        Cargo cargo = Cargo.freight(destination, 20);

        assertFalse(CargoTransfer.transferCargo(source, dest, cargo));
    }

    @Test
    void rejectedTypeLeavesCargoInSource() {
        GridPos destination = new GridPos(200, 35);
        CargoHold source = CargoHold.forType(40, CargoType.FREIGHT);
        CargoHold dest = CargoHold.forType(3, CargoType.PASSENGER);
        Cargo cargo = Cargo.freight(destination,3);

        source.addCargo(cargo);
        assertFalse(CargoTransfer.transferCargo(source, dest, cargo));
        assertTrue(source.hasCargo(cargo));
    }

    @Test
    void overCapacity() {
        GridPos destination = new GridPos(300, 500);
        CargoHold hold = CargoHold.forType(3, CargoType.FREIGHT);
        Cargo cargo = Cargo.freight(destination, 100);

        assertFalse(CargoTransfer.insertCargo(hold, cargo));
        assertEquals(0, hold.getUsedUnits());
    }

    @Test
    void nullSource() {
        GridPos destination = new GridPos(1000, 445);
        CargoHold dest = CargoHold.forType(30, CargoType.FREIGHT);
        Cargo cargo = Cargo.freight(destination, 35);

        assertThrows(NullPointerException.class, () -> CargoTransfer.transferCargo(null, dest, cargo));
    }

    

    
}
