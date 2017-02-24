package com.example.menozzi.hw2.drawers;

import android.graphics.Canvas;

public interface SensorAnimationViewDrawer {
    void onDraw(Canvas canvas, float sensorValue, float maxSensorValue);
}
