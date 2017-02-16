package com.example.menozzi.hw2.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.menozzi.hw2.Axis;

public class PlotView extends View {

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

        // Draw outer grid borders
        int left = margin + 100;
        int right = w-margin - 100;
        int top = margin + 200;
        int bottom = h-margin - 300;
        canvas.drawRect(left, top, right, bottom, mGridPaint);

        // Draw interior x-axis lines
        int xInterval = (int)(mXAxis.getNormalizedIntervalLength() * (right-left+margin));
        for (int i = 1; i <= mXAxis.getNumTicks(); i++) {
            int x = i*xInterval + left;
            canvas.drawLine(x, top, x, bottom, mGridPaint);
        }

        // Draw interior y-axis lines
        int yInterval = (int)(mYAxis.getNormalizedIntervalLength() * (bottom-top+margin));
        for (int i = 1; i <= mYAxis.getNumTicks(); i++) {
            int y = i*yInterval + top;
            canvas.drawLine(left, y, right, y, mGridPaint);
        }
    }
}
