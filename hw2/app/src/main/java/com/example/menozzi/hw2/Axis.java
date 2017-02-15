package com.example.menozzi.hw2;

public class Axis {
    public double xMin = 0.0, xMax = 10.0;
    public double yMin = 0.0, yMax = 10.0;

    public double xResolution = 2.0;
    public double yResulution = 2.0;

    public String label;

    public Axis(double xMin, double xMax, double xResolution,
                double yMin, double yMax, double yResulution,
                String label) {

        this.xMin = xMin;
        this.xMax = xMax;
        this.xResolution = xResolution;

        this.yMin = yMin;
        this.yMax = yMax;
        this.yResulution = yResulution;

        this.label = label;
    }
}
