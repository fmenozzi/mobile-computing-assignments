package com.example.menozzi.hw1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public enum CellColor {
        BLACK,
        WHITE,
    }

    CellColor mTargetState = CellColor.BLACK;

    Button mBlackButton;
    Button mWhiteButton;

    int mPrimaryColor;
    int mSecondaryColor;

    TableLayout mGridTableLayout;
    TableLayout mSwitchTableLayout;

    TextView mMoveCountTextView;
    TextView mSequenceTextView;

    int mMoveCount = 0;

    Grid mGrid = new Grid();

    static final Map<String, Integer[]> SWITCH_MAP = new HashMap<>();

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

        mMoveCountTextView = (TextView) findViewById(R.id.move_count_textview);
        mMoveCountTextView.append(" 0");

        mSequenceTextView = (TextView) findViewById(R.id.sequence_textview);

        SWITCH_MAP.put("A", new Integer[]{0,1,2});
        SWITCH_MAP.put("B", new Integer[]{3,7,9,11});
        SWITCH_MAP.put("C", new Integer[]{4,10,14,15});
        SWITCH_MAP.put("D", new Integer[]{0,4,5,6,7});
        SWITCH_MAP.put("E", new Integer[]{6,7,8,10,12});
        SWITCH_MAP.put("F", new Integer[]{0,2,14,15});
        SWITCH_MAP.put("G", new Integer[]{3,14,15});
        SWITCH_MAP.put("H", new Integer[]{4,5,7,14,15});
        SWITCH_MAP.put("I", new Integer[]{1,2,3,4,5});
        SWITCH_MAP.put("J", new Integer[]{3,4,5,9,13});
    }

    public void setupGridTable() {
        mGridTableLayout = (TableLayout) findViewById(R.id.grid_table);
        for (int r = 0; r < Grid.GRID_SIZE; r++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                                          TableRow.LayoutParams.WRAP_CONTENT));
            for (int c = 0; c < Grid.GRID_SIZE; c++) {
                int k = r*Grid.GRID_SIZE + c;

                boolean darkSquare = mGrid.getCell(r, c).isBlack();

                int padding = dipToPixels(this, 4);

                int w = dipToPixels(this, 64);
                int h = dipToPixels(this, 64);

                TextView cell = new TextView(this);
                cell.setLayoutParams(new TableRow.LayoutParams(c+1));
                cell.setBackgroundColor(darkSquare ? mPrimaryColor : mSecondaryColor);
                cell.setTextColor(darkSquare ? mSecondaryColor : mPrimaryColor);
                cell.setGravity(Gravity.CENTER);
                cell.setText(String.valueOf(k));
                cell.setTextSize(40);
                cell.setPadding(padding, padding, padding, padding);
                cell.setWidth(w);
                cell.setHeight(h);
                cell.setId(k);

                row.addView(cell);
            }

            mGridTableLayout.addView(row);
        }
    }

    public void setupSwitchTable() {
        mSwitchTableLayout = (TableLayout) findViewById(R.id.switch_table);
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
                        updateMoveCount(mMoveCount+1);

                        toggleCells(((TextView) v).getText().toString());

                        checkForWin();
                    }
                });

                row.addView(button);
            }

            mSwitchTableLayout.addView(row);
        }
    }

    public void onBlackClick(View view) {
        mBlackButton.setBackgroundColor(mPrimaryColor);
        mBlackButton.setTextColor(mSecondaryColor);

        mWhiteButton.setBackgroundColor(mSecondaryColor);
        mWhiteButton.setTextColor(mPrimaryColor);

        mTargetState = CellColor.BLACK;

        checkForWin();
    }

    public void onWhiteClick(View view) {
        mWhiteButton.setBackgroundColor(mPrimaryColor);
        mWhiteButton.setTextColor(mSecondaryColor);

        mBlackButton.setBackgroundColor(mSecondaryColor);
        mBlackButton.setTextColor(mPrimaryColor);

        mTargetState = CellColor.WHITE;

        checkForWin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_restart:
                updateMoveCount(0);
                updateSequence("");
                resetGrid();
                updateGridTable();
                break;
            case R.id.action_auto:
                autoSolve();
                break;
            default:
                Toast.makeText(this, "How did we even get here?", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void resetGrid() {
        mGrid.reset();
    }

    public void updateMoveCount(int newCount) {
        String moveCountText = getResources().getString(R.string.move_count_text);
        mMoveCount = newCount;
        mMoveCountTextView.setText(moveCountText + " " + newCount);
    }

    public void updateSequence(String sequence) {
        String sequenceText = getResources().getString(R.string.sequence_text);
        mSequenceTextView.setText(sequenceText + " " + sequence);
    }

    public void updateGridTable() {
        for (int r = 0; r < Grid.GRID_SIZE; r++) {
            for (int c = 0; c < Grid.GRID_SIZE; c++) {
                int k = r*Grid.GRID_SIZE + c;

                boolean isBlack = mGrid.getCell(r, c).isBlack();

                TextView cellView = (TextView) mGridTableLayout.findViewById(k);
                cellView.setBackgroundColor(isBlack ? mPrimaryColor : mSecondaryColor);
                cellView.setTextColor(isBlack ? mSecondaryColor : mPrimaryColor);
            }
        }
    }

    public void toggleCells(String key) {
        for (Integer i : SWITCH_MAP.get(key)) {
            int r = i / Grid.GRID_SIZE;
            int c = i % Grid.GRID_SIZE;

            mGrid.toggleCellColor(r, c);
        }

        updateGridTable();
    }

    public void checkForWin() {
        boolean won = true;

        for (int r = 0; r < Grid.GRID_SIZE; r++) {
            for (int c = 0; c < Grid.GRID_SIZE; c++) {
                boolean cellIsBlack = mGrid.getCell(r, c).isBlack();
                boolean targetStateIsBlack = mTargetState == CellColor.BLACK;
                if ((cellIsBlack && !targetStateIsBlack) || (!cellIsBlack && targetStateIsBlack)) {
                    won = false;
                }
            }
        }

        if (won) {
            Toast.makeText(this, "You win!", Toast.LENGTH_SHORT).show();
        }
    }

    public void autoSolve() {
        String sequence = AutoSolver.solve(mGrid, mTargetState);
        if (sequence != null) {
            updateSequence(sequence);
        } else {
            Toast.makeText(this, "No solution", Toast.LENGTH_SHORT).show();
        }
    }

    public int dipToPixels(@NonNull Context context, int dip) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics);
    }
}
