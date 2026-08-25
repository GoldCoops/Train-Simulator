package com.trains.cargo;

public class CargoTransfer {
    // This class should manage the boarding removal of cargo from vehicles and stations to ensure no desync between their separate cargo tracking lists
    // This class should ensure that no cargo is lost, when moving cargo between two holds, we should first add the cargo to the new list
    // then confirm it exists in the new list, then remove it from the old list.
}
