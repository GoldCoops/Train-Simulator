package com.trains.cargo;

import java.util.*;

public final class CargoHold {
    private final int capacity;
    private final Set<CargoType> accepted;
    private final List<Cargo> contents = new ArrayList<>();
    private int usedUnits;

    private CargoHold(int capacity, Set<CargoType> accepted) {
        if (accepted.isEmpty()) {
            throw new IllegalArgumentException("At least one cargo type must be accepted");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be greater than or equal to 0");
        }
        this.accepted = EnumSet.copyOf(accepted);
        this.capacity = capacity;
    }

    /**
     * Creates a cargo hold for one specific type of cargo
     * @param capacity The capacity of the hold
     * @param accepted The accepted type of cargo
     * @return The CargoHold Object
     */
    public static CargoHold forType(int capacity, CargoType accepted) {
        Objects.requireNonNull(accepted);
        return new CargoHold(capacity, EnumSet.of(accepted));
    }

    /**
     * Creates a mixed cargo hold that accepts any of the supplied CargoType's
     * @param capacity The capacity of the hold
     * @param accepted The cargo types to accept
     * @return The CargoHold Object
     */
    public static CargoHold mixed(int capacity, CargoType... accepted) {
        Objects.requireNonNull(accepted);
        if (accepted.length == 0) {
            throw new IllegalArgumentException("At least one cargo type must be accepted");
        }
        return new CargoHold(capacity, EnumSet.copyOf(Arrays.asList(accepted)));
    }

    /**
     * Creates a mixed cargo hold that accepts any of the cargo types in the enum
     * @param capacity The capacity of the hold
     * @return The CargoHold Object
     */
    public static CargoHold mixed(int capacity) {
        return new CargoHold(capacity, EnumSet.allOf(CargoType.class));
    }

    /**
     * Gets the capacity of the hold
     * @return the capacity of the hold
     */
    public int getCapacity() {
        return capacity;
    }

    /**
    checks whether the hold contains the cargo
    @param cargo the cargo being checked
    @return true if the cargo exists in the contents
    */
    private boolean contains(Cargo cargo) {
        return contents.contains(cargo);
    }

    /**
     Adds cargo to the contents list and updates the capacity
    */
    private void addCargo(Cargo cargo) {
        contents.add(cargo)
        usedUnits += cargo.getUnits();
    }

    


    public boolean canAccept(Cargo cargo) { // this needs to consult the accepted types set, I will leave that up to the person who takes this package
        return usedUnits + cargo.getUnits() <= capacity;
    }

    

}
