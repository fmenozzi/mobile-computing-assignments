package com.example.menozzi.hw1;

import android.content.Context;
import android.os.CountDownTimer;
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

    CellColor mTargetState = CellColor.BLACK;

    Button mBlackButton;
    Button mWhiteButton;

    int mPrimaryColor;
    int mSecondaryColor;
    int mTertiaryColor;

    TableLayout mGridTableLayout;
    TableLayout mSwitchTableLayout;

    TextView mMoveCountContentTextView;
    TextView mSequenceContentTextView;

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
        mTertiaryColor = ContextCompat.getColor(this, R.color.colorAccent);

        setupGridTable();

        setupSwitchTable();

        mMoveCountContentTextView = (TextView) findViewById(R.id.move_count_content_textview);
        mMoveCountContentTextView.append("0");

        mSequenceContentTextView = (TextView) findViewById(R.id.sequence_content_textview);

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

    /**
     * Generates grid table views
     */
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
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleCellById(v.getId());
                    }
                });

                row.addView(cell);
            }

            mGridTableLayout.addView(row);
        }
    }

    /**
     * Generates switch table views
     */
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
                button.setId(ascii);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateMoveCount(mMoveCount+1);

                        toggleCellsBySequence(((TextView) v).getText().toString());
                    }
                });

                row.addView(button);
            }

            mSwitchTableLayout.addView(row);
        }
    }

    /**
     * Callback function for black target state button
     *
     * @param view
     *          View corresponding to clicked button
     */
    public void onBlackClick(View view) {
        mBlackButton.setBackgroundColor(mPrimaryColor);
        mBlackButton.setTextColor(mSecondaryColor);

        mWhiteButton.setBackgroundColor(mSecondaryColor);
        mWhiteButton.setTextColor(mPrimaryColor);

        mTargetState = CellColor.BLACK;

        checkForWin();
    }

    /**
     * Callback function for white target state button
     *
     * @param view
     *          View corresponding to clicked button
     */
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
                resetSwitches();
                resetGrid();
                break;
            case R.id.action_auto:
                updateSequence("");
                resetSwitches();
                autoSolve();
                break;
            default:
                Toast.makeText(this, "How did we even get here?", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Resets underlying grid data structure and updates grid table UI
     */
    public void resetGrid() {
        mGrid.reset();

        updateGridTable();
    }

    /**
     * Reset switch table UI
     */
    public void resetSwitches() {
        for (int i = (int)'A'; i <= (int)'J'; i++) {
            TextView switchView = (TextView) mSwitchTableLayout.findViewById(i);
            switchView.setBackgroundColor(mPrimaryColor);
            switchView.setTextColor(mSecondaryColor);
        }
    }

    /**
     * Set new move count and update UI
     *
     * @param newCount
     *          New move count to display
     */
    public void updateMoveCount(int newCount) {
        mMoveCount = newCount;
        mMoveCountContentTextView.setText(String.valueOf(newCount));
    }

    /**
     * Set sequence string and update UI
     *
     * @param sequence
     *          New sequence string to display
     */
    public void updateSequence(String sequence) {
        mSequenceContentTextView.setText(String.valueOf(sequence));
    }

    /**
     * Update grid table UI from the underlying data structure
     */
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

    /**
     * Toggle cells that are touched by switches
     *
     * @param key
     *          String representing characters corresponding to all
     *          switches that were pressed
     */
    public void toggleCellsBySequence(String key) {
        for (Integer i : SWITCH_MAP.get(key)) {
            toggleCellById(i);
        }
    }

    /**
     * Toggle cell according to its numeric ID
     *
     * @param id
     *          Linear 1D grid index corresponding to the ID of the
     *          cell View
     */
    public void toggleCellById(int id) {
        int r = id / Grid.GRID_SIZE;
        int c = id % Grid.GRID_SIZE;

        mGrid.toggleCellColor(r, c);

        updateGridTable();

        checkForWin();
    }

    /**
     * Check for win condition on the current grid and announce via Toast
     * if applicable
     */
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

    /**
     * Run the autosolver on the current grid configuration. If the board
     * is solvable, animate the solution by highlighting the switches that
     * correspond to the minimum solution using a CountDownTimer. If the
     * board is not solvable, notify with a Toast.
     */
    public void autoSolve() {
        final String sequence = AutoSolver.solve(mGrid, mTargetState);
        if (sequence != null) {
            int delay = 500;
            int totalInterval = delay * (sequence.length() + 1);

            new CountDownTimer(totalInterval, delay) {
                private int animationIdx = 0;

                @Override
                public void onTick(long millisUntilFinished) {
                    String subsequence = sequence.substring(0, animationIdx+1);

                    String switchStr = subsequence.substring(animationIdx, animationIdx+1);

                    int id = (int) switchStr.charAt(0);

                    TextView switchView = (TextView) mSwitchTableLayout.findViewById(id);
                    switchView.setBackgroundColor(mTertiaryColor);
                    switchView.setTextColor(mSecondaryColor);

                    toggleCellsBySequence(switchStr);
                    updateSequence(subsequence);
                    updateMoveCount(mMoveCount+1);

                    animationIdx += 1;
                }

                @Override
                public void onFinish() {

                }
            }.start();
        } else {
            final Toast t = Toast.makeText(this, "No solution", Toast.LENGTH_SHORT);

            new CountDownTimer(400, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    t.show();
                }

                @Override
                public void onFinish() {
                    t.cancel();
                }
            }.start();

            updateSequence("");
        }
    }

    /**
     * Helper function to convert dip to pixels
     *
     * @param context
     *          Context from which to extract display metrics
     * @param dip
     *          Number of dips to convert
     *
     * @return The number of equivalent pixels
     */
    public int dipToPixels(@NonNull Context context, int dip) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics);
    }
}
