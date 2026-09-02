package com.trains.utils;

/**
 * Immutable record for storing Node positions, nodes should not be able to move after creation.
 * neither x or y can be negative, and they must both be a multiple of 5 to combat overcrowding
 * @param x the x position
 * @param y the y position
 */
public record GridPos(int x, int y) {
    /**
     * GridPos constructor, x and y must be greater than or equal to 0, and must be a multiple of 5
     * @param x the x position
     * @param y the y position
     */
    public GridPos {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Grid position cannot be negative");
        }
        if (x%5 != 0 || y%5 != 0) {
            throw new IllegalArgumentException("Grid position must be a multiple of 5");
        }
    }

    /**
     * Calculates the distance between this GridPos and the supplied GridPos
     * @param other The other GridPos with which to calculate distance
     * @return The distance between the two GridPos
     */
    public double distanceTo(GridPos other) {
        return Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
    }

    /**
     * Converts this GridPos record to a mutable Point2D
     * @return The created Point2D Object
     */
    public Point2D toPoint2D() {
        return new Point2D(x, y);
    }
}
