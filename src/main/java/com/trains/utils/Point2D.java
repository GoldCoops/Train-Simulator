package com.trains.utils;

/**
 * The Point2D Class
 * A continuous interpolated position for rendering
 */
public final class Point2D {
    public double x;
    public double y;

    /**
     * Constructor for a 2D Point
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Converts this Point2D into an immutable GridPos record, automatically casts to int
     * @return The created GridPos record
     */
    public GridPos toGridPos() {
        return new GridPos((int) x, (int) y);
    }
}
