package com.example.menozzi.hw2.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.menozzi.hw2.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    void tempClickCallback(View v) {
        Intent intent = new Intent(this, PlotActivity.class);
        intent.setData(Uri.parse((v.getId() == R.id.sensor1_button) ? "Sensor 1" : "Sensor 2"));
        startActivity(intent);
    }
}
