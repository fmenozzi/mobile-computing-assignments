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
        mGridPaint.setStrokeWidth(strokeWidth);
        mGridPaint.setStrokeCap(Paint.Cap.ROUND);

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        int margin = strokeWidth/2;
        canvas.drawLine(  margin,   margin,   margin, h-margin, mGridPaint);
        canvas.drawLine(  margin,   margin, w-margin,   margin, mGridPaint);
        canvas.drawLine(  margin, h-margin, w-margin, h-margin, mGridPaint);
        canvas.drawLine(w-margin,   margin, w-margin, h-margin, mGridPaint);

        int xInterval = (int)(mXAxis.getNormalizedIntervalLength() * w);
        for (int i = 1; i <= mXAxis.getNumTicks(); i++) {
            int x = i*xInterval + margin;
            canvas.drawLine(x, 0.0f, x, h, mGridPaint);
        }

        int yInterval = (int)(mYAxis.getNormalizedIntervalLength() * h);
        for (int i = 1; i <= mYAxis.getNumTicks(); i++) {
            int y = i*yInterval + margin;
            canvas.drawLine(0.0f, y, w, y, mGridPaint);
        }
    }
}
