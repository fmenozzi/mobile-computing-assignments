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

import com.example.menozzi.hw2.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        Sensor accelSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (lightSensor != null) {
            TextView lightStatusView = (TextView) findViewById(R.id.light_status_textview);
            lightStatusView.setText("Present");
            lightStatusView.setTextColor(ContextCompat.getColor(this, R.color.colorPresent));

            float rng = lightSensor.getMaximumRange();
            float res = lightSensor.getResolution();
            float dly = lightSensor.getMinDelay();

            ((TextView) findViewById(R.id.light_range_textview)).setText(String.valueOf(rng));
            ((TextView) findViewById(R.id.light_resolution_textview)).setText(String.valueOf(res));
            ((TextView) findViewById(R.id.light_delay_textview)).setText(String.valueOf(dly));
        } else {
            TextView lightStatusView = (TextView) findViewById(R.id.light_status_textview);
            lightStatusView.setText("Not Present");
            lightStatusView.setTextColor(ContextCompat.getColor(this, R.color.colorAbsent));
        }

        if (accelSensor != null) {
            TextView accelSensorView = (TextView) findViewById(R.id.accel_status_textview);
            accelSensorView.setText("Present");
            accelSensorView.setTextColor(ContextCompat.getColor(this, R.color.colorPresent));

            float rng = accelSensor.getMaximumRange();
            float res = accelSensor.getResolution();
            float dly = accelSensor.getMinDelay();

            ((TextView) findViewById(R.id.accel_range_textview)).setText(String.valueOf(rng));
            ((TextView) findViewById(R.id.accel_resolution_textview)).setText(String.valueOf(res));
            ((TextView) findViewById(R.id.accel_delay_textview)).setText(String.valueOf(dly));
        } else {
            TextView accelSensorView = (TextView) findViewById(R.id.accel_status_textview);
            accelSensorView.setText("Not Present");
            accelSensorView.setTextColor(ContextCompat.getColor(this, R.color.colorAbsent));
        }
    }

    void buttonCallback(View v) {
        Intent intent = new Intent(this, PlotActivity.class);
        intent.setData(Uri.parse((v.getId() == R.id.light_sensor_button) ? "Light" : "Accelerometer"));
        startActivity(intent);
    }
}
