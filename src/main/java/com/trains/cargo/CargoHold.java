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
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be greater than or equal to 1");
        }
        this.accepted = EnumSet.copyOf(accepted);
        this.capacity = capacity;
    }

    /**
     * Returns an unmodifiable copy of the contents of the CargoHold
     * @return an unmodifiable list of contents
     */
    public List<Cargo> getContents() {
        return List.copyOf(contents);
    }

    /**
     * Creates a cargo hold for one specific type of cargo
     * @param capacity The capacity of the hold
     * @param accepted The accepted type of cargo
     * @return The CargoHold Object
     * @throws IllegalArgumentException if capacity is less than 1
     * @throws NullPointerException if accepted is null
     */
    public static CargoHold forType(int capacity, CargoType accepted) throws IllegalArgumentException, NullPointerException{
        Objects.requireNonNull(accepted);
        return new CargoHold(capacity, EnumSet.of(accepted));
    }

    /**
     * Creates a mixed cargo hold that accepts any of the supplied CargoType's
     * @param capacity The capacity of the hold
     * @param accepted The cargo types to accept
     * @return The CargoHold Object
     * @throws IllegalArgumentException if accepted is supplied, or if capacity is less than 1
     * @throws NullPointerException if accepted is null
     */
    public static CargoHold mixed(int capacity, CargoType... accepted) throws IllegalArgumentException, NullPointerException{
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
     * @throws IllegalArgumentException if capacity is less than 1
     */
    public static CargoHold mixed(int capacity) throws IllegalArgumentException{
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
     * Gets the number of used units of the hold
     * @return the number of used units
     */
    public int getUsedUnits() {
        return usedUnits;
    }

    /** 
     * gets the remaining unused capacity of the hold
     * @return the number of unused capacity 
     * 
    */
    public int getRemainingCapacity() {
        return capacity - usedUnits;
    }

    /**
    checks whether the cargo is held in the contents
    @param cargo the cargo being checked
    @return true if the cargo exists in the contents
     @throws NullPointerException if cargo is null
    */
    boolean hasCargo(Cargo cargo) throws NullPointerException{
        Objects.requireNonNull(cargo);
        return contents.contains(cargo);
    }

    /**
     Adds cargo to the contents list and updates the capacity
     @param cargo being added to contents
     @throws IllegalStateException if the hold can't accept the cargo
     @throws NullPointerException if cargo is null
    */
    void addCargo(Cargo cargo) throws NullPointerException, IllegalStateException{
        Objects.requireNonNull(cargo);
         if (!canAccept(cargo)) {
            throw new IllegalStateException("Cargo not accepted: " + cargo);
        }
        contents.add(cargo);
        usedUnits += cargo.getUnits();
    }

    /**
     * removes the cargo from the hold if it is there
     * @param cargo being removed
     * @return true if cargo was found and removed
     * @throws NullPointerException if cargo is null
    */
    boolean removeCargo(Cargo cargo) throws NullPointerException {
        Objects.requireNonNull(cargo);
        boolean removed = contents.remove(cargo);
        if (removed) {
            usedUnits -= cargo.getUnits();
        }
        return removed;
    }


    /**
     * If the calling cargoHold can accept a given piece of cargo
     * @param cargo the cargo to check
     * @return true if the cargo can be accepted, false otherwise
     * @throws NullPointerException if cargo is null
     */
    public boolean canAccept(Cargo cargo) throws NullPointerException{
        Objects.requireNonNull(cargo);
        return accepted.contains(cargo.getType()) && usedUnits + cargo.getUnits() <= capacity;
    }

    /**
     * Verifies that the used units and the sum of the cargo units in the contents are equal
     * <p>
     *     Uses a stream to sum the cargo units and ensure that it matches the usedUnits variable
     *     If this method returns false, the cargo hold is in an inconsistent state and should be reset or discarded.
     * </p>
     * @return true if the used units and the sum of the cargo units in the contents are equal, false otherwise
     */
    public boolean verifyIntegrity() {
        return usedUnits == contents.stream().mapToInt(Cargo::getUnits).sum();
    }

    

}
