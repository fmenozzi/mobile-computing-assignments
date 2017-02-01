package com.example.menozzi.hw1;

import android.support.annotation.NonNull;

import java.util.Random;

public class Grid {
    public class Cell {
        public int row;
        public int col;

        public CellColor color;

        public Cell(int row, int col, boolean isBlack) {
            this.row = row;
            this.col = col;

            this.color = isBlack ? CellColor.BLACK : CellColor.WHITE;
        }

        public boolean isBlack() {
            return this.color == CellColor.BLACK;
        }
    }

    public static final int GRID_SIZE = 4;

    private Cell[][] grid = new Cell[GRID_SIZE][GRID_SIZE];

    public Grid() {
        reset();
    }

    public Cell getCell(int r, int c) {
        return grid[r][c];
    }

    public void setCell(int r, int c, @NonNull Cell cell) {
        grid[r][c] = cell;
    }

    /**
     * Reset board to a pseudorandom configuration
     */
    public void reset() {
        Random rng = new Random();
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                grid[r][c] = new Cell(r, c, rng.nextBoolean());
            }
        }
    }

    /**
     * Toggle individual cell color
     *
     * @param r
     *          Cell row index
     * @param c
     *          Cell column index
     */
    public void toggleCellColor(int r, int c) {
        Cell cell = grid[r][c];
        cell.color = cell.isBlack() ? CellColor.WHITE : CellColor.BLACK;
    }

    /**
     * Create a deep copy of the Grid for use in the autosolver
     *
     * @return A deep copy of the Grid
     */
    @NonNull
    public Grid copy() {
        Grid clone = new Grid();
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                clone.setCell(r, c, new Cell(r, c, getCell(r, c).isBlack()));
            }
        }
        return clone;
    }

    /**
     * Check whether all cells in the board match the target state
     *
     * @param targetState
     *          State to match
     *
     * @return Whether all cell colors match the target state
     */
    public boolean allCellColorsAre(CellColor targetState) {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (targetState != grid[r][c].color) {
                    return false;
                }
            }
        }
        return true;
    }
}
