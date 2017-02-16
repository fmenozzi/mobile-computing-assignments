package com.example.menozzi.hw2.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.example.menozzi.hw2.Axis;

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

    Paint mGridPaint = new Paint();
    Paint mTickPaint = new Paint();

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawGrid(canvas);
    }

    public void drawGrid(Canvas canvas) {
        int strokeWidth = 4;

        mGridPaint.setColor(Color.GRAY);
        mGridPaint.setAntiAlias(true);
        mGridPaint.setStrokeWidth(strokeWidth);
        mGridPaint.setStyle(Paint.Style.STROKE);

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        int margin = strokeWidth/2;

        LTRB bounds = new LTRB(margin+150, margin+50, w-margin-50, h-margin-100);

        // Draw outer grid borders
        canvas.drawRect(bounds.l, bounds.t, bounds.r, bounds.b, mGridPaint);

        // Draw interior x-axis lines
        int xInterval = (int)(mXAxis.getNormalizedIntervalLength() * (bounds.width()+margin));
        for (int i = 1; i <= mXAxis.getNumTicks(); i++) {
            int x = i*xInterval + bounds.l;
            canvas.drawLine(x, bounds.t, x, bounds.b, mGridPaint);
        }

        // Draw interior y-axis lines
        int yInterval = (int)(mYAxis.getNormalizedIntervalLength() * (bounds.height()+margin));
        for (int i = 1; i <= mYAxis.getNumTicks(); i++) {
            int y = i*yInterval + bounds.t;
            canvas.drawLine(bounds.l, y, bounds.r, y, mGridPaint);
        }

        mTickPaint.setColor(Color.GRAY);
        mTickPaint.setAntiAlias(true);
        mTickPaint.setTextSize(32);
        mTickPaint.setTextAlign(Paint.Align.CENTER);

        int leftAfterPadding = bounds.l - 50;
        int bottomAfterPadding = bounds.b + 60;

        // Draw x-axis tick values
        canvas.drawText(String.valueOf(mXAxis.min), bounds.l, bottomAfterPadding, mTickPaint);
        for (int i = 1; i <= mXAxis.getNumTicks(); i++) {
            int x = i*xInterval + bounds.l;
            String tickValue = String.valueOf(mXAxis.min + i*mXAxis.resolution);
            canvas.drawText(tickValue, x, bottomAfterPadding, mTickPaint);
        }
        canvas.drawText(String.valueOf(mXAxis.max), bounds.r, bottomAfterPadding, mTickPaint);

        mTickPaint.setTextAlign(Paint.Align.RIGHT);

        Rect textBounds = new Rect();
        mTickPaint.getTextBounds("0", 0, 1, textBounds);
        int halfTextSize = textBounds.height()/2;

        // Draw y-axis tick values
        canvas.drawText(String.valueOf(mYAxis.min), leftAfterPadding, bounds.b + halfTextSize, mTickPaint);
        for (int i = 1; i <= mYAxis.getNumTicks(); i++) {
            int y = i*yInterval + bounds.t + halfTextSize;
            String tickValue = String.valueOf(mYAxis.min + (mYAxis.max-i)*mYAxis.resolution);
            canvas.drawText(tickValue, leftAfterPadding, y, mTickPaint);
        }
        canvas.drawText(String.valueOf(mYAxis.max), leftAfterPadding, bounds.t + halfTextSize, mTickPaint);
    }
}
