package com.example.menozzi.hw2.activities;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.menozzi.hw2.Axis;
import com.example.menozzi.hw2.FixedCircularBuffer;
import com.example.menozzi.hw2.views.PlotView;
import com.example.menozzi.hw2.R;

public class PlotActivity extends AppCompatActivity {

    PlotView mPlotView;
    TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);

        mPlotView = (PlotView) findViewById(R.id.plotview);
        mPlotView.setXAxis(new Axis(0.0, 5.0, 1.0, "X Axis"));
        mPlotView.setYAxis(new Axis(0.0, 5.0, 1.0, "Y Axis"));
        mPlotView.invalidate();

        mTextView = (TextView) findViewById(R.id.textview);
        mTextView.setText(getIntent().getData().toString());

        new CountDownTimer(10000, 500) {
            private FixedCircularBuffer<Integer> buffer = new FixedCircularBuffer<>(4);
            int i = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                buffer.add(i++);
                mTextView.setText(buffer.toString());
            }

            @Override
            public void onFinish() {
                Toast.makeText(PlotActivity.this, "Done!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }
}
