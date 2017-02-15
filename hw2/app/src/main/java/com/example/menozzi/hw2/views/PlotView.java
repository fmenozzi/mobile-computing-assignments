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

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        mGridPaint.setColor(Color.GRAY);
        mGridPaint.setAntiAlias(true);

        for (int x = 0; x < w; x += w/8) {
            canvas.drawLine(x, 0.0f, x, h, mGridPaint);
        }

        for (int y = 0; y < h; y += h/8) {
            canvas.drawLine(0.0f, y, w, y, mGridPaint);
        }
    }
}
