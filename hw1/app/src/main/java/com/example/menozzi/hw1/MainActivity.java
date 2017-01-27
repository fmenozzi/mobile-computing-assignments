package com.example.menozzi.hw1;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    enum TargetState {
        BLACK,
        WHITE,
    }

    Button mBlackButton;
    Button mWhiteButton;

    TargetState targetState = TargetState.BLACK;

    int primaryColor;
    int secondaryColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBlackButton = (Button) findViewById(R.id.target_state_black_button);
        mWhiteButton = (Button) findViewById(R.id.target_state_white_button);

        primaryColor   = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        secondaryColor = ContextCompat.getColor(this, R.color.colorSecondary);
    }

    public void onBlackClick(View view) {
        mBlackButton.setBackgroundColor(primaryColor);
        mBlackButton.setTextColor(secondaryColor);

        mWhiteButton.setBackgroundColor(secondaryColor);
        mWhiteButton.setTextColor(primaryColor);

        targetState = TargetState.BLACK;
    }

    public void onWhiteClick(View view) {
        mWhiteButton.setBackgroundColor(primaryColor);
        mWhiteButton.setTextColor(secondaryColor);

        mBlackButton.setBackgroundColor(secondaryColor);
        mBlackButton.setTextColor(primaryColor);

        targetState = TargetState.WHITE;
    }
}
