package com.example.menozzi.hw2.drawers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import com.example.menozzi.hw2.util.LTRB;

public class AccelerometerAnimationViewDrawer implements SensorAnimationViewDrawer {
    private float mSensorValue;
    private float mMaxSensorValue;

    private static Paint sPhonePaint  = new Paint();
    private static Paint sScreenPaint = new Paint();
    private static Paint sButtonPaint = new Paint();

    private static Paint sLinesPaint  = new Paint();

    private static Path sPhonePath = new Path();

    private static final LTRB BOUNDS = new LTRB();

    private static final int PHONE_STROKE_WIDTH = 10;
    private static final int PHONE_CORNER_RADIUS = 20;

    private static final int PHONE_WIDTH = 200;
    private static final int PHONE_HEIGHT = 350;

    private static final int SCREEN_OFFSET_L = 20;
    private static final int SCREEN_OFFSET_R = 20;
    private static final int SCREEN_OFFSET_T = 40;
    private static final int SCREEN_OFFSET_B = 60;

    private static final int HOME_BUTTON_RADIUS = 15;

    static {
        sPhonePaint.setColor(Color.BLACK);
        sPhonePaint.setAntiAlias(true);
        sPhonePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        sPhonePaint.setStrokeWidth(PHONE_STROKE_WIDTH);
        sPhonePaint.setStrokeJoin(Paint.Join.ROUND);
        sPhonePaint.setPathEffect(new CornerPathEffect(PHONE_CORNER_RADIUS));

        sScreenPaint.setColor(Color.WHITE);
        sScreenPaint.setAntiAlias(true);

        sButtonPaint.setColor(Color.WHITE);
        sButtonPaint.setAntiAlias(true);

        sLinesPaint.setColor(Color.BLACK);
        sLinesPaint.setAntiAlias(true);
    }

    public void onDraw(Canvas canvas, float sensorValue, float maxSensorValue) {
        mSensorValue = sensorValue;
        mMaxSensorValue = maxSensorValue;

        final int PHONE_CENTER_X = canvas.getWidth()/2;
        final int PHONE_CENTER_Y = canvas.getHeight()/2;

        BOUNDS.set(PHONE_CENTER_X - (PHONE_WIDTH/2),
                   PHONE_CENTER_Y - (PHONE_HEIGHT/2),
                   PHONE_CENTER_X + (PHONE_WIDTH/2),
                   PHONE_CENTER_Y + (PHONE_HEIGHT/2));

        drawPhone(canvas, PHONE_CENTER_X, PHONE_CENTER_Y);
        drawLines(canvas, PHONE_CENTER_X, PHONE_CENTER_Y);
    }

    private void drawPhone(Canvas canvas, int phoneCenterX, int phoneCenterY) {
        // Draw phone body
        sPhonePath.reset();
        sPhonePath.moveTo(BOUNDS.l, BOUNDS.t);
        sPhonePath.lineTo(BOUNDS.r, BOUNDS.t);
        sPhonePath.lineTo(BOUNDS.r, BOUNDS.b);
        sPhonePath.lineTo(BOUNDS.l, BOUNDS.b);
        sPhonePath.close();
        canvas.drawPath(sPhonePath, sPhonePaint);

        // Draw phone screen
        canvas.drawRect(BOUNDS.l + SCREEN_OFFSET_L,
                        BOUNDS.t + SCREEN_OFFSET_T,
                        BOUNDS.r - SCREEN_OFFSET_R,
                        BOUNDS.b - SCREEN_OFFSET_B,
                        sScreenPaint);

        // Draw home button
        canvas.drawCircle(phoneCenterX, BOUNDS.b - SCREEN_OFFSET_B/2, HOME_BUTTON_RADIUS, sButtonPaint);
    }

    private void drawLines(Canvas canvas, int phoneCenterX, int phoneCenterY) {

    }
}
