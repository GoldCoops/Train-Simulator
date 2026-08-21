package com.trains.network;

public record GridPos(int x, int y) {
    public GridPos {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Grid position cannot be negative");
        }
        if (x%5 != 0 || y%5 != 0) {
            throw new IllegalArgumentException("Grid position must be a multiple of 5");
        }
    }
}
