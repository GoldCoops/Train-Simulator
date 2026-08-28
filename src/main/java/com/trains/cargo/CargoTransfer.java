package com.trains.cargo;

import java.util.Objects;

public final class CargoTransfer {


    /**
     * Tramsfers cargo from origin to destination CargoHold
     * @param source the CargoHold from which the cargo is being transferred
     * @param destination CargoHold destination to where the cargo is being transferred
     * @param cargo being transferred
     * @return returns true is cargo was successfully transferred
     * @throws RuntimeException if the cargo failed to transfer
    */
    public static boolean transferCargo(CargoHold source, CargoHold destination, Cargo cargo) throws RuntimeException {
        Objects.requireNonNull(source);
        Objects.requireNonNull(destination);
        Objects.requireNonNull(cargo);
        if (source == destination) return true; // no-op
        if (!source.hasCargo(cargo)) return false; // if the source doesnt have the given cargo
        if (!destination.canAccept(cargo)) return false; // if  the destination cant accept the given cargo
        if (!source.removeCargo(cargo)) { // i was wrong, we should remove first, we have a copy of cargo, and can add it back if needed.
            return false;
        }
        try {
            destination.addCargo(cargo); // try to add the cargo to the destination
        } catch (RuntimeException e) { // this catches both NullPointerException and IllegalStateException potentially thrown by CargoHold.addCargo()
            source.addCargo(cargo); // If the exception is thrown, we want to add the cargo back to the original list so that it is not lost
            throw e; // And throw the exception so the caller knows this process failed
        }
        return true;

    }


    /*
    All of this is already done by CargoHold on each instance, we don't need these static methods


    Adds cargo to CargoHold destination
    @param destination to where the cargo is being transferred to
    @param cargo being transferred

    private static void addCargo(CargoHold destination, Cargo cargo) {
        destination.addCargo(cargo);
    }


    Checks whether destination contains the cargo that was transferred from source
    Helper method for addCargo method
    @param destination of the CargoHold being checked
    @param cargo to be found
    @return true if destination contains the cargo

    private static boolean verifyCargo(CargoHold destination, Cargo cargo) {
        return destination.hasCargo(cargo);
    }


    Removes cargo from the source
    Should only be used once verifyCargo() has been called
    @param origin CargoHold origin that the cargo is being removed from
    @param cargo being removed

    private static void removeCargo(CargoHold origin, Cargo cargo) {
        boolean cargoIsRemoved = origin.removeCargo(cargo);

        if (!cargoIsRemoved) {
            throw new IllegalStateException("Cargo was not found");
        }
    }


     */





    /**
     * Adds newly created cargo directly to a destination CargoHold
     * This is used when cargo is first spawned
     * @deprecated
     * @param destination, the cargoHold where cargo will be added
     * @param cargo, the newly created cargo to add
     * @return true if the cargo was successfully added
     */ // This should be done in CargoSpawner with CargoHold.addCargo(Cargo), it already checks if Cargo is null and if it can be accepted.
    @Deprecated
    public static boolean insertCargo(CargoHold destination, Cargo cargo){
        Objects.requireNonNull(destination);
        Objects.requireNonNull(cargo);

        if(!destination.canAccept(cargo)){
            return false;
        }

        destination.addCargo(cargo);

        return destination.hasCargo(cargo);
    }
}
