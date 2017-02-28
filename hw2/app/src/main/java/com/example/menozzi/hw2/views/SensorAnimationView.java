package com.example.menozzi.hw2.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.example.menozzi.hw2.drawers.SensorAnimationViewDrawer;

public class SensorAnimationView extends View {
    private float mSensorValue;
    private float mMaxSensorValue;
    private SensorAnimationViewDrawer mDrawer;

    public SensorAnimationView(Context context) {
        super(context);
    }
    public SensorAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SensorAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setValue(float value) {
        mSensorValue = value;
        invalidate();
    }

    public void setMaxValue(float maxValue) {
        mMaxSensorValue = maxValue;
    }

    public void setAnimationViewDrawer(SensorAnimationViewDrawer drawer) {
        mDrawer = drawer;
    }

    /**
     * Delegates the drawing to the SensorAnimationViewDrawer, passing
     * it the necessary sensor data.
     *
     * @param canvas
     *          Canvas on which to draw
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawer.onDraw(canvas, mSensorValue, mMaxSensorValue);
    }
}
