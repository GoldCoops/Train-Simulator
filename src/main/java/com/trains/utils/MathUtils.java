package com.trains.utils;

import java.math.BigDecimal;

public final class MathUtils {


    private MathUtils() {
    }

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
        return lerp(origin.toPoint2D(), target.toPoint2D(), t);
    }



}
