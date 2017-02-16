package com.example.menozzi.hw2.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

        LTRB bounds = new LTRB(margin+100, margin+200, w-margin-100, h-margin-300);

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
    }
}
