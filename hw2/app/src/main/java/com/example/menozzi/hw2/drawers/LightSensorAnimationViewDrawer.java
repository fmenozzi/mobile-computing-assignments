package com.example.menozzi.hw2.drawers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class LightSensorAnimationViewDrawer implements SensorAnimationViewDrawer {
    float mSensorValue;
    float mMaxSensorValue;

    static Paint sBulbPaint = new Paint();
    static Paint sStemPaint = new Paint();

    static {
        sBulbPaint.setColor(Color.rgb(0,0,0));
        sBulbPaint.setAntiAlias(true);

        sStemPaint.setColor(Color.BLACK);
        sStemPaint.setAntiAlias(true);
    }

    static Path sBulbPath = new Path();
    static Path sStemPath = new Path();

    static final int BULB_RADIUS = 90;

    static final int STEM_OFFSET = 5;
    static final int STEM_RECT_HEIGHT = 10;
    static final int STEM_PADDING = 5;
    static final int STEM_NUM_BANDS = 3;

    public void onDraw(Canvas canvas, float sensorValue, float maxSensorValue) {
        final int BULB_CENTER_X = canvas.getWidth()/2;
        final int BULB_CENTER_Y = BULB_RADIUS;
        final int BULB_LINE_OFFSET = 10;

        mSensorValue = sensorValue;
        mMaxSensorValue = maxSensorValue;

        drawBulb(canvas, BULB_CENTER_X, BULB_CENTER_Y, BULB_LINE_OFFSET);
        drawStem(canvas, BULB_CENTER_X, BULB_CENTER_Y, BULB_LINE_OFFSET);
    }

    private void drawBulb(Canvas canvas, int bulbCenterX, int bulbCenterY, int bulbLineOffset) {
        // Set color based on sensor value
        int y = (int)Math.min((mSensorValue/mMaxSensorValue) * 255, 255);
        sBulbPaint.setColor(Color.rgb(y, y, 0));
        canvas.drawCircle(bulbCenterX, bulbCenterY, BULB_RADIUS, sBulbPaint);

        sBulbPath.reset();
        sBulbPath.moveTo(bulbCenterX - BULB_RADIUS, bulbCenterY);
        sBulbPath.lineTo(bulbCenterX - (BULB_RADIUS/2 + bulbLineOffset), bulbCenterY + 2*BULB_RADIUS);
        sBulbPath.lineTo(bulbCenterX + (BULB_RADIUS/2 + bulbLineOffset), bulbCenterY + 2*BULB_RADIUS);
        sBulbPath.lineTo(bulbCenterX + BULB_RADIUS, bulbCenterY);
        sBulbPath.close();
        canvas.drawPath(sBulbPath, sBulbPaint);
    }

    private void drawStem(Canvas canvas, int bulbCenterX, int bulbCenterY, int bulbLineOffset) {
        float l = bulbCenterX - (BULB_RADIUS/2 + bulbLineOffset);
        float r = bulbCenterX + (BULB_RADIUS/2 + bulbLineOffset);
        float t = bulbCenterY + 2*BULB_RADIUS + STEM_OFFSET;
        float b = t + STEM_RECT_HEIGHT;
        for (int i = 1; i <= STEM_NUM_BANDS; i++) {
            canvas.drawRect(l, t, r, b, sStemPaint);
            t = b + STEM_PADDING;
            b = t + STEM_RECT_HEIGHT;
        }
        sStemPath.reset();
        sStemPath.moveTo(bulbCenterX - (r-l)/2, b - STEM_RECT_HEIGHT);
        sStemPath.lineTo(bulbCenterX - 30, b + STEM_RECT_HEIGHT);
        sStemPath.lineTo(bulbCenterX + 30, b + STEM_RECT_HEIGHT);
        sStemPath.lineTo(bulbCenterX + (r-l)/2, b - STEM_RECT_HEIGHT);
        sStemPath.close();
        canvas.drawPath(sStemPath, sStemPaint);
    }
}
