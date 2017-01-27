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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static final int GRID_SIZE = 4;

    enum TargetState {
        BLACK,
        WHITE,
    }

    TargetState mTargetState = TargetState.BLACK;

    Button mBlackButton;
    Button mWhiteButton;

    int mPrimaryColor;
    int mSecondaryColor;

    TableLayout mGridTable;
    TableLayout mSwitchTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBlackButton = (Button) findViewById(R.id.target_state_black_button);
        mWhiteButton = (Button) findViewById(R.id.target_state_white_button);

        mPrimaryColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        mSecondaryColor = ContextCompat.getColor(this, R.color.colorSecondary);

        setupGridTable();

        setupSwitchTable();
    }

    public void setupGridTable() {
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
                cell.setBackgroundColor(darkSquare ? mPrimaryColor : mSecondaryColor);
                cell.setTextColor(darkSquare ? mSecondaryColor : mPrimaryColor);
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

    public void setupSwitchTable() {
        mSwitchTable = (TableLayout) findViewById(R.id.switch_table);
        for (int r = 0; r < 2; r++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                                          TableRow.LayoutParams.WRAP_CONTENT));
            for (int c = 0; c < 5; c++) {
                int ascii = 65 + (r*5 + c);

                int paddingTopBottom = dipToPixels(this, 4);
                int paddingLeftRight = dipToPixels(this, 16);

                int margin = dipToPixels(this, 4);

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                                                         TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(margin, margin, margin, margin);

                TextView button = new TextView(this);
                button.setLayoutParams(params);
                button.setBackgroundColor(mPrimaryColor);
                button.setTextColor(mSecondaryColor);
                button.setGravity(Gravity.CENTER);
                button.setText(Character.toString((char) ascii));
                button.setTextSize(32);
                button.setPadding(paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView view = (TextView) v;
                        Toast.makeText(v.getContext(), "Pressed " + view.getText(), Toast.LENGTH_SHORT).show();
                    }
                });

                row.addView(button);
            }

            mSwitchTable.addView(row);
        }
    }

    public void onBlackClick(View view) {
        mBlackButton.setBackgroundColor(mPrimaryColor);
        mBlackButton.setTextColor(mSecondaryColor);

        mWhiteButton.setBackgroundColor(mSecondaryColor);
        mWhiteButton.setTextColor(mPrimaryColor);

        mTargetState = TargetState.BLACK;
    }

    public void onWhiteClick(View view) {
        mWhiteButton.setBackgroundColor(mPrimaryColor);
        mWhiteButton.setTextColor(mSecondaryColor);

        mBlackButton.setBackgroundColor(mSecondaryColor);
        mBlackButton.setTextColor(mPrimaryColor);

        mTargetState = TargetState.WHITE;
    }

    public int dipToPixels(Context context, int dip) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics);
    }
}
