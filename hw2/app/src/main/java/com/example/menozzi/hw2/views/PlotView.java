package com.example.menozzi.hw2.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.example.menozzi.hw2.Axis;
import com.example.menozzi.hw2.FixedCircularBuffer;

public class PlotView extends View {

    private static class LTRB {
        int l, t, r, b;
        LTRB() {
            this(0,0,0,0);
        }
        LTRB(int l, int t, int r, int b) {
            set(l,t,r,b);
        }
        int width() {
            return r-l;
        }
        int height() {
            return b-t;
        }
        void set(int l, int t, int r, int b) {
            this.l = l;
            this.t = t;
            this.r = r;
            this.b = b;
        }
    }

    static final int STROKE_WIDTH = 4;
    static final int MARGIN = STROKE_WIDTH/2;
    static final LTRB BOUNDS = new LTRB();

    Axis mXAxis;
    Axis mYAxis;

    FixedCircularBuffer<Float> mSensorData;

    static final int POINT_RADIUS = 15;

    static Paint sGridPaint = new Paint();
    static Paint sTickPaint = new Paint();
    static Paint sTextPaint = new Paint();
    static Paint sDataPaint = new Paint();
    static Paint sLinePaint = new Paint();

    static {
        sGridPaint.setColor(Color.GRAY);
        sGridPaint.setAntiAlias(true);
        sGridPaint.setStyle(Paint.Style.STROKE);
        sGridPaint.setStrokeWidth(STROKE_WIDTH);

        sTickPaint.setColor(Color.GRAY);
        sTickPaint.setAntiAlias(true);
        sTickPaint.setTextSize(32);

        sTextPaint.setColor(Color.GRAY);
        sTextPaint.setAntiAlias(true);
        sTextPaint.setTextSize(48);
        sTextPaint.setTextAlign(Paint.Align.CENTER);

        sDataPaint.setAntiAlias(true);

        sLinePaint.setAntiAlias(true);
        sLinePaint.setStrokeWidth(5);
    }

    public PlotView(Context context) {
        super(context);
    }
    public PlotView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public PlotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setXAxis(Axis xAxis) {
        mXAxis = xAxis;
    }
    public void setYAxis(Axis yAxis) {
        mYAxis = yAxis;
    }

    public void setSensorDataBuffer(FixedCircularBuffer<Float> buffer) {
        mSensorData = buffer;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        BOUNDS.set(MARGIN+250, MARGIN+250, w-MARGIN-50, h-MARGIN-300);

        int xInterval = (int)(mXAxis.getNormalizedIntervalLength() * (BOUNDS.width()+MARGIN));
        int yInterval = (int)(mYAxis.getNormalizedIntervalLength() * (BOUNDS.height()+MARGIN));

        drawGrid(canvas, BOUNDS, xInterval, yInterval);
        drawAxes(canvas, BOUNDS, xInterval, yInterval);
        drawData(canvas, BOUNDS, xInterval, yInterval);
    }

    public void drawGrid(Canvas canvas, LTRB bounds, int xInterval, int yInterval) {
        // Draw outer grid borders
        canvas.drawRect(bounds.l, bounds.t, bounds.r, bounds.b, sGridPaint);

        // Draw interior x-axis lines
        for (int i = 1; i <= mXAxis.getNumTicks(); i++) {
            int x = i*xInterval + bounds.l;
            canvas.drawLine(x, bounds.t, x, bounds.b, sGridPaint);
        }

        // Draw interior y-axis lines
        for (int i = 1; i <= mYAxis.getNumTicks(); i++) {
            int y = i*yInterval + bounds.t;
            canvas.drawLine(bounds.l, y, bounds.r, y, sGridPaint);
        }
    }

    public void drawAxes(Canvas canvas, LTRB bounds, int xInterval, int yInterval) {
        int leftAfterPadding = bounds.l - 50;
        int bottomAfterPadding = bounds.b + 60;

        sTickPaint.setTextAlign(Paint.Align.CENTER);

        // Draw x-axis tick values
        canvas.drawText(String.valueOf(mXAxis.min), bounds.l, bottomAfterPadding, sTickPaint);
        for (int i = 1; i <= mXAxis.getNumTicks(); i++) {
            int x = i*xInterval + bounds.l;
            String tickValue = String.valueOf(mXAxis.min + i*mXAxis.resolution);
            canvas.drawText(tickValue, x, bottomAfterPadding, sTickPaint);
        }
        canvas.drawText(String.valueOf(mXAxis.max), bounds.r, bottomAfterPadding, sTickPaint);

        sTickPaint.setTextAlign(Paint.Align.RIGHT);

        Rect textBounds = new Rect();
        sTickPaint.getTextBounds("0", 0, 1, textBounds);
        int halfTextSize = textBounds.height()/2;

        // Draw y-axis tick values
        canvas.drawText(String.valueOf(mYAxis.min), leftAfterPadding, bounds.b + halfTextSize, sTickPaint);
        for (int i = 1; i <= mYAxis.getNumTicks(); i++) {
            int y = bounds.b - i*yInterval + halfTextSize;
            String tickValue = String.valueOf(mYAxis.min + i*mYAxis.resolution);
            canvas.drawText(tickValue, leftAfterPadding, y, sTickPaint);
        }
        canvas.drawText(String.valueOf(mYAxis.max), leftAfterPadding, bounds.t + halfTextSize, sTickPaint);

        // Draw x-axis label
        canvas.drawText(mXAxis.label, (bounds.r+bounds.l)/2, bottomAfterPadding + 100, sTextPaint);

        // Draw y-axis label
        canvas.save();
        int yx = leftAfterPadding - 100;
        int yy = (bounds.t+bounds.b)/2;
        canvas.rotate(-90, yx, yy);
        canvas.drawText(mYAxis.label, yx, yy, sTextPaint);
        canvas.restore();
    }

    public void drawData(Canvas canvas, LTRB bounds, int xInterval, int yInterval) {
        FixedCircularBuffer<Float> currentData = mSensorData.copy();

        sDataPaint.setColor(Color.GREEN);
        sLinePaint.setColor(Color.GREEN);

        // Draw sensor data points and lines
        int size = currentData.getSize();
        for (int i = 0; i < size; i++) {
            float unitHeight = (bounds.b-bounds.t)/(float)(mYAxis.max - mYAxis.min);

            float cx = bounds.l + i*xInterval;
            float cy = bounds.b - (currentData.get(i) * unitHeight);
            canvas.drawCircle(cx, cy, POINT_RADIUS, sDataPaint);

            if (i != size-1) {
                float cx2 = bounds.l + (i+1)*xInterval;
                float cy2 = bounds.b - (currentData.get(i+1) * unitHeight);
                canvas.drawLine(cx, cy, cx2, cy2, sLinePaint);
            }
        }

        Float max = currentData.getMax();
        if (max != null) {
            float unitHeight = (bounds.b-bounds.t)/(float)(mYAxis.max - mYAxis.min);
            float cx = (bounds.l + bounds.r)/2;
            float cy = bounds.b - (max*unitHeight);
            sDataPaint.setColor(Color.RED);
            canvas.drawCircle(cx, cy, POINT_RADIUS, sDataPaint);
        }
    }
}
