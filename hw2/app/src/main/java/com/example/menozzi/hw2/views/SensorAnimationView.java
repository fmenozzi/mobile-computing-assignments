package com.example.menozzi.hw2.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class SensorAnimationView extends View {

    static Paint sBulbPaint = new Paint();
    static Paint sStemPaint = new Paint();

    static {
        sBulbPaint.setColor(Color.YELLOW);
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

    public SensorAnimationView(Context context) {
        super(context);
    }
    public SensorAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SensorAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        final int BULB_CENTER_X = w/2;
        final int BULB_CENTER_Y = BULB_RADIUS;

        final int BULB_LINE_OFFSET = 10;

        // Draw bulb
        canvas.drawCircle(BULB_CENTER_X, BULB_CENTER_Y, BULB_RADIUS, sBulbPaint);
        sBulbPath.reset();
        sBulbPath.moveTo(BULB_CENTER_X - BULB_RADIUS, BULB_CENTER_Y);
        sBulbPath.lineTo(BULB_CENTER_X - (BULB_RADIUS/2 + BULB_LINE_OFFSET), BULB_CENTER_Y + 2*BULB_RADIUS);
        sBulbPath.lineTo(BULB_CENTER_X + (BULB_RADIUS/2 + BULB_LINE_OFFSET), BULB_CENTER_Y + 2*BULB_RADIUS);
        sBulbPath.lineTo(BULB_CENTER_X + BULB_RADIUS, BULB_CENTER_Y);
        sBulbPath.close();
        canvas.drawPath(sBulbPath, sBulbPaint);

        // Draw stem
        float l = BULB_CENTER_X - (BULB_RADIUS/2 + BULB_LINE_OFFSET);
        float r = BULB_CENTER_X + (BULB_RADIUS/2 + BULB_LINE_OFFSET);
        float t = BULB_CENTER_Y + 2*BULB_RADIUS + STEM_OFFSET;
        float b = t + STEM_RECT_HEIGHT;
        for (int i = 1; i <= STEM_NUM_BANDS; i++) {
            canvas.drawRect(l, t, r, b, sStemPaint);
            t = b + STEM_PADDING;
            b = t + STEM_RECT_HEIGHT;
        }
        sStemPath.reset();
        sStemPath.moveTo(BULB_CENTER_X - (r-l)/2, b - STEM_RECT_HEIGHT);
        sStemPath.lineTo(BULB_CENTER_X - 30, b + STEM_RECT_HEIGHT);
        sStemPath.lineTo(BULB_CENTER_X + 30, b + STEM_RECT_HEIGHT);
        sStemPath.lineTo(BULB_CENTER_X + (r-l)/2, b - STEM_RECT_HEIGHT);
        sStemPath.close();
        canvas.drawPath(sStemPath, sStemPaint);
    }
}
