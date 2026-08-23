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

    public static CargoHold forType(int capacity, CargoType accepted) {
        return new CargoHold(capacity, EnumSet.of(accepted));
    }

    public static CargoHold mixed(int capacity) {
        return new CargoHold(capacity, EnumSet.allOf(CargoType.class));
    }


    public int getCapacity() {
        return capacity;
    }



    public boolean canAccept(Cargo cargo) {
        return usedUnits + cargo.getUnits() <= capacity;
    }

}
