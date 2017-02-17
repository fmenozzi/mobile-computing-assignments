package com.example.menozzi.hw2;

public class Axis {
    public double min = 0.0;
    public double max = 1.0;

    public double resolution = 0.2;

    public String label;

    public Axis(double min, double max, double resolution, String label) {
        reset(min, max, resolution, label);
    }

    public double getNormalizedIntervalLength() {
        return resolution / (max-min);
    }

    public int getNumTicks() {
        return (int)(((max-min)/resolution) - 1);
    }

    public void reset(double min, double max, double resolution, String label) {
        this.min = min;
        this.max = max;

        this.resolution = resolution;

        this.label = label;
    }

    public void shiftLeft() {
        min += resolution;
        max += resolution;
    }
}
