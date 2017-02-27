package com.example.menozzi.hw2.activities;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
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

        TextView light_view_1 = (TextView) findViewById(R.id.light_text_1);
        TextView light_view_2 = (TextView) findViewById(R.id.light_text_2);
        TextView accel_view_1 = (TextView) findViewById(R.id.accel_text_1);
        TextView accel_view_2 = (TextView) findViewById(R.id.accel_text_2);

        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        Sensor accelSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (lightSensor != null) {
            light_view_1.setText("Status: Present");

            float rng = lightSensor.getMaximumRange();
            float res = lightSensor.getResolution();
            float dly = lightSensor.getMinDelay();

            light_view_2.setText(String.valueOf(rng) + ", " + String.valueOf(res) + ", " + String.valueOf(dly));
        } else {
            light_view_1.setText("Status: Not Present");
            light_view_2.setText("N/A");
        }

        if (accelSensor != null) {
            accel_view_1.setText("Status: Present");
            float rng = accelSensor.getMaximumRange();
            float res = accelSensor.getResolution();
            float dly = accelSensor.getMinDelay();

            accel_view_2.setText(String.valueOf(rng) + ", " + String.valueOf(res) + ", " + String.valueOf(dly));
        } else {
            accel_view_1.setText("Status: Not Present");
            accel_view_2.setText("N/A");
        }
    }

    void buttonCallback(View v) {
        Intent intent = new Intent(this, PlotActivity.class);
        intent.setData(Uri.parse((v.getId() == R.id.light_sensor_button) ? "Light" : "Accelerometer"));
        startActivity(intent);
    }
}
