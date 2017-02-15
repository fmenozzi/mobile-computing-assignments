package com.example.menozzi.hw2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PlotActivity extends AppCompatActivity {

    Axis mXAxis;
    Axis mYAxis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
    }
}
