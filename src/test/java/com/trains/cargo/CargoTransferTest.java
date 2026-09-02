package com.trains.cargo;

import com.trains.utils.GridPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CargoTransferTest {
    // InsertCargo
    @Test
    void insertCargoAddPassengersToHold(){
        CargoHold hold = CargoHold.forType(10, CargoType.PASSENGER);
        Cargo passenger = Cargo.passenger(new GridPos(5, 5));

         boolean inserted = CargoTransfer.insertCargo(hold, passenger);

        assertTrue(inserted);
        assertEquals(1, hold.getUsedUnits());
    }
    
    @Test  
    void insertFreightIntoPassengerHold() {
        GridPos destination = new GridPos(125, 5);
        CargoHold hold = CargoHold.forType(230, CargoType.PASSENGER);
        Cargo freight = Cargo.freight(destination, 95);

        assertFalse(CargoTransfer.insertCargo(hold, freight));
        assertEquals(0, hold.getUsedUnits());
    }

    @Test
    void insertOverCapacity() {
        GridPos destination = new GridPos(150, 35);
        CargoHold hold = CargoHold.forType(3, CargoType.FREIGHT);
        Cargo cargo = Cargo.freight(destination, 5);

        assertFalse(CargoTransfer.insertCargo(hold, cargo));
        assertEquals(0, hold.getUsedUnits());
    }

    @Test
    void multipleInsertCargo() {
        GridPos destination = new GridPos(110, 35);
        CargoHold hold = CargoHold.forType(230, CargoType.FREIGHT);
        hold.addCargo(Cargo.freight(destination, 3));
        Cargo newCargo = Cargo.freight(destination, 4);

        assertTrue(CargoTransfer.insertCargo(hold, newCargo));
        assertEquals(7, hold.getUsedUnits());
    }

    @Test
    void insertCargoNullDestination() {
        GridPos destination = new GridPos(295, 65);
        Cargo cargo = Cargo.passenger(destination);
        assertThrows(NullPointerException.class, () -> CargoTransfer.insertCargo(null, cargo));
    }

    @Test
    void insertCargoNullCargo() {
        GridPos destination = new GridPos(295, 65);
        CargoHold hold = CargoHold.mixed(40);
        assertThrows(NullPointerException.class, () -> CargoTransfer.insertCargo(hold, null));
    }

    


    //transferCargo
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
    void cargoTransferNullSource() {
        GridPos destination = new GridPos(1000, 445);
        CargoHold dest = CargoHold.forType(30, CargoType.FREIGHT);
        Cargo cargo = Cargo.freight(destination, 35);

        assertThrows(NullPointerException.class, () -> CargoTransfer.transferCargo(null, dest, cargo));
    }

    @Test
    void cargoTransferNullDestination() {
        GridPos destination = new GridPos(500, 800);
        CargoHold source = CargoHold.forType(300, CargoType.FREIGHT);
        Cargo cargo = Cargo.freight(destination, 15);

        assertThrows(NullPointerException.class, () -> CargoTransfer.transferCargo(source, null, cargo));
    }

    @Test
    void cargoTransferNullCargo() {
        CargoHold source = CargoHold.forType(240, CargoType.FREIGHT);
        CargoHold dest = CargoHold.forType(305, CargoType.FREIGHT);
        assertThrows(NullPointerException.class, () -> CargoTransfer.transferCargo(source, dest, null));
    }


    


    
}
