package com.trains.utils;

import java.math.BigDecimal;

public class MathUtils {

    /**
     * Linear interpolation between two float values
     * @param a The first value
     * @param b The second value
     * @param t The fraction of the way between the two values
     * @return The interpolated value
     */
    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    /**
     * Linear interpolation between two double values
     * @param a the first value
     * @param b the second value
     * @param t the fraction of the way between the two values
     * @return the interpolated value
     */
    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    /**
     * Linear interpolation between two int values
     * @param a First value
     * @param b second value
     * @param t the fraction of the way between the two values
     * @return the interpolated value
     */
    public static int lerp(int a, int b, double t) {
        return (int) (a + (b - a) * t);
    }

    /**
     * Linear interpolation between two Point2D values
     * @param origin the origin point
     * @param target the target point
     * @param t the fraction of the way between the two points
     * @return the interpolated point
     */
    public static Point2D lerp(Point2D origin, Point2D target, double t) {
        t = Math.clamp(t, 0.0, 1.0);
        double nx = origin.x + t * (target.x - origin.x);
        double ny = origin.y + t * (target.y - origin.y);
        return new Point2D(nx, ny);
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


    /**
     * A 2D point class
     */
    public static class Point2D {
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
    }
}
