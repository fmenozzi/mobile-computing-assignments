package com.example.menozzi.hw1;

import java.util.Random;

public class Grid {
    public class Cell {
        public int row;
        public int col;

        public boolean isBlack;

        public Cell(int row, int col, boolean isBlack) {
            this.row = row;
            this.col = col;

            this.isBlack = isBlack;
        }
    }

    public static final int GRID_SIZE = 4;

    private Cell[][] grid = new Cell[GRID_SIZE][GRID_SIZE];

    public Grid() {
        Random rng = new Random();
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                grid[r][c] = new Cell(r, c, rng.nextBoolean());
            }
        }
    }

    public Cell getCell(int r, int c) {
        return grid[r][c];
    }

    public void setCell(int r, int c, Cell cell) {
        grid[r][c] = cell;
    }

    public void toggleCellColor(int r, int c) {
        grid[r][c].isBlack = !grid[r][c].isBlack;
    }

    public Grid copy() {
        Grid clone = new Grid();
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                clone.setCell(r, c, new Cell(r, c, getCell(r, c).isBlack));
            }
        }
        return clone;
    }
}
