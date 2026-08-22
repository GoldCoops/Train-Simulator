package com.trains.network;

/**
 * Immutable record for storing Node positions, nodes should not be able to move after creation.
 * neither x or y can be negative, and they must both be a multiple of 5 to combat overcrowding
 * @param x the x position
 * @param y the y position
 */
record GridPos(int x, int y) {
    public GridPos {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Grid position cannot be negative");
        }
        if (x%5 != 0 || y%5 != 0) {
            throw new IllegalArgumentException("Grid position must be a multiple of 5");
        }
    }
}
