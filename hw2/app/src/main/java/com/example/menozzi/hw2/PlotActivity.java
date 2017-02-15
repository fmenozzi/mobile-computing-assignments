package com.example.menozzi.hw2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PlotActivity extends AppCompatActivity {

    PlotView mPlotView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);

        mPlotView = (PlotView) findViewById(R.id.plotview);
        mPlotView.setXAxis(new Axis(0.0, 5.0, 1.0, 0.0, 5.0, 0.0, "X Axis"));
        mPlotView.setYAxis(new Axis(0.0, 5.0, 1.0, 0.0, 5.0, 0.0, "Y Axis"));
        mPlotView.invalidate();
    }
}
