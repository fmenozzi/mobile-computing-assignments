package com.example.menozzi.hw1;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static final int GRID_SIZE = 4;

    enum TargetState {
        BLACK,
        WHITE,
    }

    TargetState targetState = TargetState.BLACK;

    Button mBlackButton;
    Button mWhiteButton;

    int primaryColor;
    int secondaryColor;

    TableLayout mGridTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBlackButton = (Button) findViewById(R.id.target_state_black_button);
        mWhiteButton = (Button) findViewById(R.id.target_state_white_button);

        primaryColor   = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        secondaryColor = ContextCompat.getColor(this, R.color.colorSecondary);

        mGridTable = (TableLayout) findViewById(R.id.grid_table);
        for (int r = 0; r < GRID_SIZE; r++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                                          TableRow.LayoutParams.WRAP_CONTENT));
            for (int c = 0; c < GRID_SIZE; c++) {
                boolean rowEven = r % 2 == 0;
                boolean colEven = c % 2 == 0;

                boolean darkSquare = rowEven == colEven;

                int padding = dipToPixels(this, 4);

                int w = dipToPixels(this, 64);
                int h = dipToPixels(this, 64);

                TextView cell = new TextView(this);
                cell.setLayoutParams(new TableRow.LayoutParams(c+1));
                cell.setBackgroundColor(darkSquare ? primaryColor : secondaryColor);
                cell.setTextColor(darkSquare ? secondaryColor : primaryColor);
                cell.setGravity(Gravity.CENTER);
                cell.setText(String.valueOf(r*GRID_SIZE + c));
                cell.setTextSize(40);
                cell.setPadding(padding, padding, padding, padding);
                cell.setWidth(w);
                cell.setHeight(h);

                row.addView(cell);
            }

            mGridTable.addView(row);
        }
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

    public int dipToPixels(Context context, int dip) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics);
    }
}
