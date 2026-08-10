package com.trains.network;

public record GridPos(int x, int y) {
    public GridPos {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Grid position cannot be negative");
        }
    }
}
