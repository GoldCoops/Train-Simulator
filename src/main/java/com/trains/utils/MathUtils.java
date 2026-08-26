package com.trains.utils;

import java.math.BigDecimal;

public final class MathUtils {

    /**
     * Calculates a value between two numbers based on a progress percentage: {@code t}
     * @param a the starting number, which will be returned when {@code t <= 0}
     * @param b the target number, which will be returned when {@code t => 1}
     * @param t the fraction of the way between the two values
     * @return the interpolated value
     */
    public static float lerp(float a, float b, float t) {
        t = Math.clamp(t, 0.0f, 1.0f);
        return a + (b - a) * t;
    }

    /**
     * Calculates a value between two numbers based on a progress percentage: {@code t}
     * @param a starting number, which will be returned when {@code t <= 0}
     * @param b target number, which will be returned when {@code t => 1}
     * @param t the fraction of the way between the two values
     * @return the interpolated value
     */
    public static double lerp(double a, double b, double t) {
        t = Math.clamp(t, 0.0, 1.0);
        return a + (b - a) * t;
    }

    /**
     * Calculates a value between two numbers based on a progress percentage: {@code t}
     * @param a the starting number, which will be returned when {@code t <= 0}
     * @param b the target number, which will be returned when {@code t => 1}
     * @param t the fraction of the way between the two values
     * @return the interpolated value
     */
    public static int lerp(int a, int b, double t) {
        t = Math.clamp(t, 0.0, 1.0);
        return (int) (a + (b - a) * t);
    }

    /**
     * Calculates a value between two Point2D coordinates based on a progress percentage: {@code t}
     * @param origin the starting number, which will be returned when {@code t <= 0}
     * @param target the target number, which will be returned when {@code t => 1}
     * @param t the fraction of the way between the two values
     * @return the interpolated value
     */
    public static Point2D lerp(Point2D origin, Point2D target, double t) {
        t = Math.clamp(t, 0.0, 1.0);
        double nx = origin.x + t * (target.x - origin.x);
        double ny = origin.y + t * (target.y - origin.y);
        return new Point2D(nx, ny);
    }

    /**
     * Calculates a value between two Point2D coordinates based on a progress percentage: {@code t}
     * @param origin the starting number, which will be returned when {@code t <= 0}
     * @param target the target number, which will be returned when {@code t => 1}
     * @param t the fraction of the way between the two values
     * @return the interpolated value
     */
    public static Point2D lerp(GridPos origin, GridPos target, double t) {
        return lerp(gridPosToPoint2D(origin), gridPosToPoint2D(target), t);
    }

    /**
     * Converts a given GridPos to a Point2D
     * @param pos The GridPos to convert to Point2D
     * @return The created Point2D Object
     */
    public static Point2D gridPosToPoint2D(GridPos pos){
        return new Point2D(pos.x(), pos.y());
    }

    /**
     * Converts a given Point2D to a GridPos
     * This method will round both x and y fields to the nearest int; it will also cast the resulting long to int, potentially losing data.
     * @param pos The Point2D to convert
     * @return The created GridPos Object
     */
    public static GridPos point2DToGridPos(Point2D pos){
        return new GridPos((int) round(pos.x),(int) round(pos.y));
    }


    /**
     * Rounds a double to an integer
     * @param value the value to round
     * @return the rounded value
     */
    public static long round(double value) {
        return (long) Math.round(value);
    }

    /**
     * Rounds a float to an integer
     * @param value the value to round
     * @return the rounded value
     */
    public static int round(float value) {
        return (int) Math.round(value);
    }



}
