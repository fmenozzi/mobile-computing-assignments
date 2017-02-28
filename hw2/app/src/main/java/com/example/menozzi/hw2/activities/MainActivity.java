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

        String presentStatus = " " + getString(R.string.status_present);
        String absentStatus = " " + getString(R.string.status_absent);

        if (mLightSensor != null) {
            TextView lightStatusView = (TextView) findViewById(R.id.light_status_textview);
            lightStatusView.setText(presentStatus);
            lightStatusView.setTextColor(ContextCompat.getColor(this, R.color.colorPresent));

            String maxRangeString = " " + String.valueOf(mLightSensor.getMaximumRange()) + " lx";
            String resolutionString = " " + String.valueOf(mLightSensor.getResolution()) + " lx";
            String minDelayString = " " + String.valueOf(mLightSensor.getMinDelay()) + " us";

            ((TextView) findViewById(R.id.light_range_textview)).setText(maxRangeString);
            ((TextView) findViewById(R.id.light_resolution_textview)).setText(resolutionString);
            ((TextView) findViewById(R.id.light_delay_textview)).setText(minDelayString);
        } else {
            TextView lightStatusView = (TextView) findViewById(R.id.light_status_textview);
            lightStatusView.setText(absentStatus);
            lightStatusView.setTextColor(ContextCompat.getColor(this, R.color.colorAbsent));

            ((TextView) findViewById(R.id.light_range_textview)).setText(" N/A");
            ((TextView) findViewById(R.id.light_resolution_textview)).setText(" N/A");
            ((TextView) findViewById(R.id.light_delay_textview)).setText(" N/A");
        }

        if (mAccelSensor != null) {
            TextView accelSensorView = (TextView) findViewById(R.id.accel_status_textview);
            accelSensorView.setText(presentStatus);
            accelSensorView.setTextColor(ContextCompat.getColor(this, R.color.colorPresent));

            String resolutionTruncated = String.valueOf(mAccelSensor.getResolution()).substring(0, 7);

            String maxRangeString = " " + String.valueOf(mAccelSensor.getMaximumRange()) + " m/s^2";
            String resolutionString = " " + resolutionTruncated + " m/s^2";
            String minDelayString = " " + String.valueOf(mAccelSensor.getMinDelay()) + " us";

            ((TextView) findViewById(R.id.accel_range_textview)).setText(maxRangeString);
            ((TextView) findViewById(R.id.accel_resolution_textview)).setText(resolutionString);
            ((TextView) findViewById(R.id.accel_delay_textview)).setText(minDelayString);
        } else {
            TextView accelSensorView = (TextView) findViewById(R.id.accel_status_textview);
            accelSensorView.setText(absentStatus);
            accelSensorView.setTextColor(ContextCompat.getColor(this, R.color.colorAbsent));

            ((TextView) findViewById(R.id.accel_range_textview)).setText(" N/A");
            ((TextView) findViewById(R.id.accel_resolution_textview)).setText(" N/A");
            ((TextView) findViewById(R.id.accel_delay_textview)).setText(" N/A");
        }
    }

    /**
     * Callback for selecting a sensor.
     *
     * @param v
     *          View corresponding to the Button pressed
     */
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
