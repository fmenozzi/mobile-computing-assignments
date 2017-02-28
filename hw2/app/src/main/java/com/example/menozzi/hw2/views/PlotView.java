package com.example.menozzi.hw2.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.example.menozzi.hw2.util.Axis;
import com.example.menozzi.hw2.util.FixedCircularFloatBuffer;
import com.example.menozzi.hw2.util.LTRB;

public class PlotView extends View {

    static final int STROKE_WIDTH = 4;
    static final int MARGIN = STROKE_WIDTH/2;
    static final LTRB BOUNDS = new LTRB();

    Axis mXAxis;
    Axis mYAxis;

    FixedCircularFloatBuffer mSensorData;
    FixedCircularFloatBuffer mRunningMeans;
    FixedCircularFloatBuffer mRunningStdDevs;

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

    public void setSensorDataBuffer(FixedCircularFloatBuffer buffer) {
        mSensorData = buffer;
    }
    public void setRunningMeanBuffer(FixedCircularFloatBuffer buffer) {
        mRunningMeans = buffer;
    }
    public void setRunningStdDevBuffer(FixedCircularFloatBuffer buffer) {
        mRunningStdDevs = buffer;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        BOUNDS.set(MARGIN+250, MARGIN+250, w-MARGIN-50, h-MARGIN-300);

        int xInterval = (int)(mXAxis.getNormalizedIntervalLength() * (BOUNDS.width()+MARGIN));
        int yInterval = (int)(mYAxis.getNormalizedIntervalLength() * (BOUNDS.height()+MARGIN));

        drawLegend(canvas, BOUNDS);
        drawGrid(canvas, BOUNDS, xInterval, yInterval);
        drawAxes(canvas, BOUNDS, xInterval, yInterval);
        drawData(canvas, BOUNDS, xInterval, yInterval);
    }

    public void drawLegend(Canvas canvas, LTRB bounds) {
        final int LINE_LEN = 100;
        final int LINE_PAD = 50;

        final int[] COLORS = new int[]{Color.GREEN, Color.BLUE, Color.MAGENTA};
        final String[] TEXTS = new String[]{"Value", "Mean", "Std Dev"};

        sTextPaint.setTextAlign(Paint.Align.LEFT);

        Rect textBounds = new Rect();
        sTextPaint.getTextBounds("M", 0, 1, textBounds);
        int halfTextSize = textBounds.height()/2;

        int x = 350;
        int y = 50;
        for (int i = 0; i < TEXTS.length; i++) {
            sLinePaint.setColor(COLORS[i]);
            sDataPaint.setColor(COLORS[i]);

            canvas.drawLine(x, y, x+LINE_LEN, y, sLinePaint);
            canvas.drawCircle(x + LINE_LEN/2, y, POINT_RADIUS, sDataPaint);
            canvas.drawText(TEXTS[i], x + LINE_LEN + 50, y + halfTextSize, sTextPaint);

            y += LINE_PAD;
        }
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

        sTextPaint.setTextAlign(Paint.Align.CENTER);

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
        FixedCircularFloatBuffer[] buffers = new FixedCircularFloatBuffer[] {
                mRunningStdDevs.copy(), mRunningMeans.copy(), mSensorData.copy(),
        };

        int [] colors = new int[] {
                Color.MAGENTA, Color.BLUE, Color.GREEN,
        };

        for (int i = 0; i < buffers.length; i++) {
            sDataPaint.setColor(colors[i]);
            sLinePaint.setColor(colors[i]);

            int size = buffers[i].getSize();
            for (int j = 0; j < size; j++) {
                float unitHeight = (bounds.b-bounds.t)/(mYAxis.max - mYAxis.min);

                float cx = bounds.l + j*xInterval;
                float cy = bounds.b - (buffers[i].get(j) * unitHeight);
                canvas.drawCircle(cx, cy, POINT_RADIUS, sDataPaint);

                if (j != size-1) {
                    float cx2 = bounds.l + (j+1)*xInterval;
                    float cy2 = bounds.b - (buffers[i].get(j+1) * unitHeight);
                    canvas.drawLine(cx, cy, cx2, cy2, sLinePaint);
                }
            }
        }
    }
}
