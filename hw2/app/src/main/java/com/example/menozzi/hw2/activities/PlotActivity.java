package com.example.menozzi.hw2.activities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.menozzi.hw2.Axis;
import com.example.menozzi.hw2.FixedCircularFloatBuffer;
import com.example.menozzi.hw2.views.SensorAnimationView;
import com.example.menozzi.hw2.views.PlotView;
import com.example.menozzi.hw2.R;

import java.util.Random;
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

    Random rnd = new Random();

    AtomicInteger mCurrentSensorValue = new AtomicInteger();

    static final double LIGHT_AXIS_MIN = 0.0;
    static final double LIGHT_AXIS_MAX = 100.0;
    static final double LIGHT_AXIS_RESOLUTION = 20.0;
    static final String LIGHT_AXIS_LABEL = "Luminance (lx)";
    static final Axis LIGHT_AXIS = new Axis(LIGHT_AXIS_MIN,
                                            LIGHT_AXIS_MAX,
                                            LIGHT_AXIS_RESOLUTION,
                                            LIGHT_AXIS_LABEL);

    static final double ACCEL_AXIS_MIN = 0.0;
    static final double ACCEL_AXIS_MAX = 15.0;
    static final double ACCEL_AXIS_RESOLUTION = 5.0;
    static final String ACCEL_AXIS_LABEL = "Acceleration (m/s^2)";
    static final Axis ACCEL_AXIS = new Axis(ACCEL_AXIS_MIN,
                                            ACCEL_AXIS_MAX,
                                            ACCEL_AXIS_RESOLUTION,
                                            ACCEL_AXIS_LABEL);

    static final double X_AXIS_MIN = 0.0;
    static final double X_AXIS_MAX = 5.0;
    static final double X_AXIS_RESOLUTION = 1.0;
    static final String X_AXIS_LABEL = "Elapsed Time (x100 ms)";
    static final Axis X_AXIS = new Axis(X_AXIS_MIN, X_AXIS_MAX, X_AXIS_RESOLUTION, X_AXIS_LABEL);

    static final Axis Y_AXIS = new Axis(0,0,0,null);

    static final int SENSOR_SAMPLING_RATE = SensorManager.SENSOR_DELAY_NORMAL;

    static final int SENSOR_DATA_PERIOD_MS = 100;

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

        mPlotView = (PlotView) findViewById(R.id.plotview);
        mPlotView.setXAxis(X_AXIS);
        mPlotView.setYAxis(Y_AXIS);
        mPlotView.setSensorDataBuffer(mSensorData);
        mPlotView.setRunningMeanBuffer(mRunningMeans);
        mPlotView.setRunningStdDevBuffer(mRunningStdDevs);
        mPlotView.invalidate();

        mSensorAnimationView = (SensorAnimationView) findViewById(R.id.sensor_animation_view);
        mSensorAnimationView.setMaxValue(100);

        if (mSensor == null) {
            String msg = "No " + sensorName.toLowerCase() + " sensor detected";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
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
                                mSensorAnimationView.setValue(sensorValue);

                                mRunningMeans.add(mSensorData.getMean());
                                mRunningStdDevs.add(mSensorData.getStdDev());

                                if (mSensorData.isFull()) {
                                    X_AXIS.shiftLeft();
                                }

                                Float dataMax = mSensorData.getMax();
                                if (dataMax != null && dataMax > Y_AXIS.max) {
                                    dataMax = (float)(Y_AXIS.resolution*(Math.ceil(Math.abs(dataMax/Y_AXIS.resolution))));
                                    Y_AXIS.max = dataMax;
                                }
                            }

                            mPlotView.invalidate();
                        }
                    });
                }
            }, 0, SENSOR_DATA_PERIOD_MS);
        }
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
