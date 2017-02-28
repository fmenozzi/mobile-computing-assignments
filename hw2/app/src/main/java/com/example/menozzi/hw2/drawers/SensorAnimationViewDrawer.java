package com.example.menozzi.hw2.drawers;

import android.graphics.Canvas;

/**
 * Interface for drawing sensor animations in a SensorAnimationView
 * based on current and max sensor values.
 */
public interface SensorAnimationViewDrawer {
    void onDraw(Canvas canvas, float sensorValue, float maxSensorValue);
}
