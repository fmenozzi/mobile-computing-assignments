package com.example.menozzi.hw2.activities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.menozzi.hw2.Axis;
import com.example.menozzi.hw2.FixedCircularBuffer;
import com.example.menozzi.hw2.views.PlotView;
import com.example.menozzi.hw2.R;

import java.util.Timer;
import java.util.TimerTask;

public class PlotActivity extends AppCompatActivity implements SensorEventListener {

    PlotView mPlotView;
    TextView mTextView;

    FixedCircularBuffer<Float> mSensorData;

    SensorManager mSensorManager;
    Sensor mSensor;

    static final int SENSOR_SAMPLING_RATE = SensorManager.SENSOR_DELAY_NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);

        String sensorName = getIntent().getDataString();
        int sensorType = sensorName.equals("Light") ? Sensor.TYPE_LIGHT : Sensor.TYPE_ACCELEROMETER;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(sensorType);

        Axis xAxis = new Axis(0.0, 5.0, 1.0, "Time");
        Axis yAxis = new Axis(0.0, 5.0, 1.0, "Data");

        mSensorData = new FixedCircularBuffer<>(xAxis.getNumTicks() + 2);

        mPlotView = (PlotView) findViewById(R.id.plotview);
        mPlotView.setXAxis(xAxis);
        mPlotView.setYAxis(yAxis);
        mPlotView.setSensorDataBuffer(mSensorData);
        mPlotView.invalidate();

        mTextView = (TextView) findViewById(R.id.textview);
        mTextView.setText(getIntent().getData().toString());

        if (mSensor == null) {
            String msg = "No " + sensorName.toLowerCase() + " sensor detected";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mSensorManager.registerListener(PlotActivity.this, mSensor, SENSOR_SAMPLING_RATE);
                }
            }, 0, 500);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SENSOR_SAMPLING_RATE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mSensorManager.unregisterListener(this);

        switch (event.sensor.getType()) {
            case Sensor.TYPE_LIGHT:
                float lux = event.values[0];

                mSensorData.add(lux);

                break;
            case Sensor.TYPE_ACCELEROMETER:
                float ax = event.values[0];
                float ay = event.values[1];
                float az = event.values[2];

                float a = (float)Math.sqrt(ax*ax + ay*ay + az*az);

                mSensorData.add(a);

                break;
        }

        mTextView.setText(mSensorData.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
