package com.trains.cargo;

public final class CargoTransfer {
    // This class should manage the boarding removal of cargo from vehicles and stations to ensure no desync between their separate cargo tracking lists
    // This class should ensure that no cargo is lost, when moving cargo between two holds, we should first add the cargo to the new list
    // then confirm it exists in the new list, then remove it from the old list.
    // If the cargo is not in the new list, then we do not remove it from the old list, bail and throw an exception.

    /**
    Tramsfers cargo from origin to destination CargoHold
    @param source the CargoHold from which the cargo is being transferred
    @param destination CargoHold destination to where the cargo is being transferred
    @param cargo being transferred 
    @return returns true is cargo was successfully transferred
    */
    public static boolean transferCargo(CargoHold source, CargoHold destination, Cargo cargo) {
        addCargo(destination, cargo);

        if (!verifyCargo(destination, cargo)) {
            throw new IllegalStateException("Cargo was not added to destination");
        }

        removeCargo(source, cargo);
        return true;
    }

    /**
    Adds cargo to CargoHold destination
    @param destination to where the cargo is being transferred to
    @param cargo being transferred
    */
    private static void addCargo(CargoHold destination, Cargo cargo) {
        destination.addCargo(cargo);
    }

    /*
    Checks whether destination contains the cargo that was transferred from source
    Helper method for addCargo method
    @param destination of the CargoHold being checked
    @param cargo to be found
    @return true if destination contains the cargo
    */
    private static boolean verifyCargo(CargoHold destination, Cargo cargo) {
        return destination.hasCargo(cargo);
    }

    /*
    Removes cargo from the source
    Should only be used once verifyCargo() has been called
    @param origin CargoHold origin that the cargo is being removed from
    @param cargo being removed
    */
    private static void removeCargo(CargoHold origin, Cargo cargo) {
        boolean cargoIsRemoved = origin.removeCargo(cargo);

        if (!cargoIsRemoved) {
            throw new IllegalStateException("Cargo was not found");
        }
    }
}
