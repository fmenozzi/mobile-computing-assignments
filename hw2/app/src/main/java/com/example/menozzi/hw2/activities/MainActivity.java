package com.example.menozzi.hw2.activities;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.menozzi.hw2.R;

public class MainActivity extends AppCompatActivity {

    SensorManager mSensorManager;
    Sensor mLightSensor;
    Sensor mAccelSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (mLightSensor != null) {
            TextView lightStatusView = (TextView) findViewById(R.id.light_status_textview);
            lightStatusView.setText("Present");
            lightStatusView.setTextColor(ContextCompat.getColor(this, R.color.colorPresent));

            float rng = mLightSensor.getMaximumRange();
            float res = mLightSensor.getResolution();
            float dly = mLightSensor.getMinDelay();

            ((TextView) findViewById(R.id.light_range_textview)).setText(String.valueOf(rng));
            ((TextView) findViewById(R.id.light_resolution_textview)).setText(String.valueOf(res));
            ((TextView) findViewById(R.id.light_delay_textview)).setText(String.valueOf(dly));
        } else {
            TextView lightStatusView = (TextView) findViewById(R.id.light_status_textview);
            lightStatusView.setText("Not Present");
            lightStatusView.setTextColor(ContextCompat.getColor(this, R.color.colorAbsent));

            ((TextView) findViewById(R.id.light_range_textview)).setText("N/A");
            ((TextView) findViewById(R.id.light_resolution_textview)).setText("N/A");
            ((TextView) findViewById(R.id.light_delay_textview)).setText("N/A");
        }

        if (mAccelSensor != null) {
            TextView accelSensorView = (TextView) findViewById(R.id.accel_status_textview);
            accelSensorView.setText("Present");
            accelSensorView.setTextColor(ContextCompat.getColor(this, R.color.colorPresent));

            float rng = mAccelSensor.getMaximumRange();
            float res = mAccelSensor.getResolution();
            float dly = mAccelSensor.getMinDelay();

            ((TextView) findViewById(R.id.accel_range_textview)).setText(String.valueOf(rng));
            ((TextView) findViewById(R.id.accel_resolution_textview)).setText(String.valueOf(res));
            ((TextView) findViewById(R.id.accel_delay_textview)).setText(String.valueOf(dly));
        } else {
            TextView accelSensorView = (TextView) findViewById(R.id.accel_status_textview);
            accelSensorView.setText("Not Present");
            accelSensorView.setTextColor(ContextCompat.getColor(this, R.color.colorAbsent));

            ((TextView) findViewById(R.id.accel_range_textview)).setText("N/A");
            ((TextView) findViewById(R.id.accel_resolution_textview)).setText("N/A");
            ((TextView) findViewById(R.id.accel_delay_textview)).setText("N/A");
        }
    }

    void buttonCallback(View v) {
        boolean isLightButton = (v.getId() == R.id.light_sensor_button);
        if ((isLightButton && mLightSensor == null) || (!isLightButton && mAccelSensor == null)) {
            Toast.makeText(this, "Sensor unavailable", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, PlotActivity.class);
            intent.setData(Uri.parse(isLightButton ? "Light" : "Accelerometer"));
            startActivity(intent);
        }
    }
}
