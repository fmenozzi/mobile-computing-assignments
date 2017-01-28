package com.example.menozzi.hw1;

import android.support.annotation.NonNull;

import java.util.Random;

public class Grid {
    public class Cell {
        public int row;
        public int col;

        public MainActivity.CellColor color;

        public Cell(int row, int col, boolean isBlack) {
            this.row = row;
            this.col = col;

            this.color = isBlack ? MainActivity.CellColor.BLACK : MainActivity.CellColor.WHITE;
        }

        public boolean isBlack() {
            return this.color == MainActivity.CellColor.BLACK;
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

    public void reset() {
        Random rng = new Random();
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                grid[r][c] = new Cell(r, c, rng.nextBoolean());
            }
        }
    }

    public void toggleCellColor(int r, int c) {
        Cell cell = grid[r][c];
        cell.color = cell.isBlack() ? MainActivity.CellColor.WHITE : MainActivity.CellColor.BLACK;
    }

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

    public boolean allCellColorsAre(MainActivity.CellColor targetState) {
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
