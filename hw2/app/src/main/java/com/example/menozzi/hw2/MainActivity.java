package com.example.menozzi.hw2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    void tempClickCallback(View v) {
        Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
    }
}
