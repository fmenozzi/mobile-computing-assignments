package com.example.menozzi.hw2.activities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.menozzi.hw2.util.Axis;
import com.example.menozzi.hw2.util.FixedCircularFloatBuffer;
import com.example.menozzi.hw2.drawers.AccelerometerAnimationViewDrawer;
import com.example.menozzi.hw2.drawers.LightSensorAnimationViewDrawer;
import com.example.menozzi.hw2.views.SensorAnimationView;
import com.example.menozzi.hw2.views.PlotView;
import com.example.menozzi.hw2.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class PlotActivity extends AppCompatActivity implements SensorEventListener {

    PlotView mPlotView;
    SensorAnimationView mSensorAnimationView;

    FixedCircularFloatBuffer mSensorData;
    FixedCircularFloatBuffer mRunningMeans;
    FixedCircularFloatBuffer mRunningStdDevs;

    SensorManager mSensorManager;
    Sensor mSensor;

    Timer mTimer = new Timer();

    float mInitialYAxisMax;

    AtomicInteger mCurrentSensorValue = new AtomicInteger();

    static final float LIGHT_AXIS_MIN = 0.0f;
    static final float LIGHT_AXIS_MAX = 100.0f;
    static final float LIGHT_AXIS_RESOLUTION = 20.0f;
    static final String LIGHT_AXIS_LABEL = "Luminance (lx)";
    static final Axis LIGHT_AXIS = new Axis(LIGHT_AXIS_MIN,
                                            LIGHT_AXIS_MAX,
                                            LIGHT_AXIS_RESOLUTION,
                                            LIGHT_AXIS_LABEL);

    static final float ACCEL_AXIS_MIN = 0.0f;
    static final float ACCEL_AXIS_MAX = 15.0f;
    static final float ACCEL_AXIS_RESOLUTION = 5.0f;
    static final String ACCEL_AXIS_LABEL = "Acceleration (m/s^2)";
    static final Axis ACCEL_AXIS = new Axis(ACCEL_AXIS_MIN,
                                            ACCEL_AXIS_MAX,
                                            ACCEL_AXIS_RESOLUTION,
                                            ACCEL_AXIS_LABEL);

    static final float X_AXIS_MIN = 0.0f;
    static final float X_AXIS_MAX = 5.0f;
    static final float X_AXIS_RESOLUTION = 1.0f;
    static final String X_AXIS_LABEL = "Elapsed Time (x100 ms)";
    static final Axis X_AXIS = new Axis(X_AXIS_MIN, X_AXIS_MAX, X_AXIS_RESOLUTION, X_AXIS_LABEL);

    static final Axis Y_AXIS = new Axis(0,0,0,null);

    static final int SENSOR_SAMPLING_RATE = SensorManager.SENSOR_DELAY_NORMAL;

    static final int SENSOR_DATA_PERIOD_MS = 100;

    static final float LIGHT_MAX_VALUE = 100.0f;
    static final float ACCEL_MAX_VALUE = 10.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);

        String sensorName = getIntent().getDataString();
        int sensorType = sensorName.equals("Light") ? Sensor.TYPE_LIGHT : Sensor.TYPE_ACCELEROMETER;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(sensorType);

        int bufsize = X_AXIS.getNumTicks() + 2;
        mSensorData = new FixedCircularFloatBuffer(bufsize);
        mRunningMeans = new FixedCircularFloatBuffer(bufsize);
        mRunningStdDevs = new FixedCircularFloatBuffer(bufsize);

        X_AXIS.reset(X_AXIS_MIN, X_AXIS_MAX, X_AXIS_RESOLUTION, X_AXIS_LABEL);
        Y_AXIS.reset((sensorType == Sensor.TYPE_LIGHT) ? LIGHT_AXIS : ACCEL_AXIS);

        mInitialYAxisMax = Y_AXIS.max;

        mPlotView = (PlotView) findViewById(R.id.plotview);
        mPlotView.setXAxis(X_AXIS);
        mPlotView.setYAxis(Y_AXIS);
        mPlotView.setSensorDataBuffer(mSensorData);
        mPlotView.setRunningMeanBuffer(mRunningMeans);
        mPlotView.setRunningStdDevBuffer(mRunningStdDevs);
        mPlotView.invalidate();

        mSensorAnimationView = (SensorAnimationView) findViewById(R.id.sensor_animation_view);
        if (sensorType == Sensor.TYPE_LIGHT) {
            mSensorAnimationView.setMaxValue(LIGHT_MAX_VALUE);
            mSensorAnimationView.setAnimationViewDrawer(new LightSensorAnimationViewDrawer());
        } else {
            mSensorAnimationView.setMaxValue(ACCEL_MAX_VALUE);
            mSensorAnimationView.setAnimationViewDrawer(new AccelerometerAnimationViewDrawer());
        }

        mSensorManager.registerListener(PlotActivity.this, mSensor, SENSOR_SAMPLING_RATE);

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                PlotActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        float sensorValue = Float.intBitsToFloat(mCurrentSensorValue.get());

                        synchronized (mSensorData) {
                            mSensorData.add(sensorValue);
                            mRunningMeans.add(mSensorData.getMean());
                            mRunningStdDevs.add(mSensorData.getStdDev());

                            mSensorAnimationView.setValue(sensorValue);

                            if (mSensorData.isFull()) {
                                X_AXIS.shiftLeft();
                            }

                            Float dataMax = mSensorData.getMax();
                            Float meanMax = mRunningMeans.getMax();
                            Float stdMax  = mRunningStdDevs.getMax();
                            if (dataMax != null && meanMax != null && stdMax != null) {
                                Float totalMax = Math.max(dataMax, Math.max(meanMax, stdMax));
                                totalMax = (float)(Y_AXIS.resolution*(Math.ceil(Math.abs(totalMax/Y_AXIS.resolution))));
                                if (totalMax >= mInitialYAxisMax) {
                                    Y_AXIS.max = totalMax;
                                }
                            }
                        }

                        mPlotView.invalidate();
                    }
                });
            }
        }, 0, SENSOR_DATA_PERIOD_MS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mTimer.cancel();
        mTimer.purge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SENSOR_SAMPLING_RATE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_LIGHT:
                mCurrentSensorValue.set(Float.floatToIntBits(event.values[0]));
                break;

            case Sensor.TYPE_ACCELEROMETER:
                float ax = event.values[0];
                float ay = event.values[1];
                float az = event.values[2];

                float a = (float)Math.sqrt(ax*ax + ay*ay + az*az);
                mCurrentSensorValue.set(Float.floatToIntBits(a));
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
