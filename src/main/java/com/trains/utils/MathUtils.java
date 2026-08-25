package com.trains.utils;

import java.math.BigDecimal;

public class MathUtils {
    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    public static int lerp(int a, int b, double t) {
        return (int) (a + (b - a) * t);
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static long round(double value) {
        return (long) Math.round(value);
    }

    public static int round(float value) {
        return (int) Math.round(value);
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
