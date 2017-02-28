package com.example.menozzi.hw2.util;

public class Axis {
    public float min = 0.0f;
    public float max = 1.0f;

    public float resolution = 0.2f;

    public String label;

    public Axis(float min, float max, float resolution, String label) {
        reset(min, max, resolution, label);
    }

    public float getNormalizedIntervalLength() {
        return resolution / (max-min);
    }

    public int getNumTicks() {
        return (int)(((max-min)/resolution) - 1);
    }

    public void reset(float min, float max, float resolution, String label) {
        this.min = min;
        this.max = max;

        this.resolution = resolution;

        this.label = label;
    }

    public void reset(Axis other) {
        this.min = other.min;
        this.max = other.max;

        this.resolution = other.resolution;

        this.label = other.label;
    }

    public void shiftLeft() {
        min += resolution;
        max += resolution;
    }
}
