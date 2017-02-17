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

    private class LTRB {
        int l, t, r, b;
        LTRB(int l, int t, int r, int b) {
            this.l = l;
            this.t = t;
            this.r = r;
            this.b = b;
        }
        int width() {
            return r-l;
        }
        int height() {
            return b-t;
        }
    }

    Axis mXAxis;
    Axis mYAxis;

    FixedCircularBuffer<Float> mSensorData;

    static Paint sGridPaint = new Paint();
    static Paint sTickPaint = new Paint();
    static Paint sTextPaint = new Paint();

    static {
        sGridPaint.setColor(Color.GRAY);
        sGridPaint.setAntiAlias(true);
        sGridPaint.setStyle(Paint.Style.STROKE);

        sTickPaint.setColor(Color.GRAY);
        sTickPaint.setAntiAlias(true);
        sTickPaint.setTextSize(32);

        sTextPaint.setColor(Color.GRAY);
        sTextPaint.setAntiAlias(true);
        sTextPaint.setTextSize(48);
        sTextPaint.setTextAlign(Paint.Align.CENTER);
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

        drawGrid(canvas);
    }

    public void drawGrid(Canvas canvas) {
        int strokeWidth = 4;

        sGridPaint.setStrokeWidth(strokeWidth);

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        int margin = strokeWidth/2;

        LTRB bounds = new LTRB(margin+250, margin+250, w-margin-50, h-margin-300);

        // Draw outer grid borders
        canvas.drawRect(bounds.l, bounds.t, bounds.r, bounds.b, sGridPaint);

        // Draw interior x-axis lines
        int xInterval = (int)(mXAxis.getNormalizedIntervalLength() * (bounds.width()+margin));
        for (int i = 1; i <= mXAxis.getNumTicks(); i++) {
            int x = i*xInterval + bounds.l;
            canvas.drawLine(x, bounds.t, x, bounds.b, sGridPaint);
        }

        // Draw interior y-axis lines
        int yInterval = (int)(mYAxis.getNormalizedIntervalLength() * (bounds.height()+margin));
        for (int i = 1; i <= mYAxis.getNumTicks(); i++) {
            int y = i*yInterval + bounds.t;
            canvas.drawLine(bounds.l, y, bounds.r, y, sGridPaint);
        }

        sTickPaint.setTextAlign(Paint.Align.CENTER);

        int leftAfterPadding = bounds.l - 50;
        int bottomAfterPadding = bounds.b + 60;

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
            int y = i*yInterval + bounds.t + halfTextSize;
            String tickValue = String.valueOf(mYAxis.min + (mYAxis.max-i)*mYAxis.resolution);
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
}
