package com.trains.cargo;

import com.trains.utils.GridPos;

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
    /**
     * Removes cargo from a hold, but only once it has reached its destination.
     * @param source the hold the cargo is leaving
     * @param cargo the cargo being delivered
     * @param location where the delivery is being attempted
     * @return true if the cargo was at its destination and has been removed,
     *         false if it is not yet there or was not in the hold
     */
    public static boolean deliverCargo(CargoHold source, Cargo cargo, GridPos location) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(cargo);
        Objects.requireNonNull(location);
        if (!cargo.getDestination().equals(location)) {
            return false; // not there yet, so it stays on board
        }
        return source.removeCargo(cargo);
    }

    /**
     * Adds newly created cargo directly to a destination CargoHold
     * This is used when cargo is first spawned
     * @param destination, the cargoHold where cargo will be added
     * @param cargo, the newly created cargo to add
     * @return true if the cargo was successfully added
     */
    public static boolean insertCargo(CargoHold destination, Cargo cargo){
        Objects.requireNonNull(destination);
        Objects.requireNonNull(cargo);

        if(!destination.canAccept(cargo)){
            return false;
        }

        destination.addCargo(cargo);

        return destination.hasCargo(cargo);
    }
    /**
     * Removes cargo from a cargo hold
     * @param source, the cargo hold containing the cargo
     * @param cargo, the cargo to be removed
     * @return true if the cargo was successfully removed
     */
    public static boolean removeCargo(CargoHold source, Cargo cargo){
        Objects.requireNonNull(source);
        Objects.requireNonNull(cargo);

        if (!source.hasCargo(cargo)){
            return false;
        }

        return source.removeCargo(cargo);
    }
}
