/*
 * Created by John Masiello. Copyright (c) 2018
 */

package com.example.john.sudokusolver;

import com.example.john.sudokusolver.SudokuPuzzleSolver.SudokuPuzzleSolution;

import java.util.Arrays;

/**
 * Mocks a repository
 */
public class SudokuPuzzleRepository {
    public static final int[][ ] DEFAULT_PUZZLE =
    {
            {5, 0, 0, 0, 0, 9, 0, 2, 6},
            {0, 0, 9, 0, 8, 0, 0, 0, 0},
            {0, 6, 0, 0, 0, 0, 9, 0, 1},
            {0, 0, 0, 0, 5, 4, 0, 0, 3},
            {7, 0, 1, 3, 6, 8, 2, 0, 5},
            {3, 0, 0, 7, 2, 0, 0, 0, 0},
            {1, 0, 3, 0, 0, 0, 0, 4, 0},
            {0, 0, 0, 0, 7, 0, 1, 0, 0},
            {9, 8, 0, 6, 0, 0, 0, 0, 7}
    };

    public static int[][] emptyPuzzle() {
        return new int[SudokuPuzzleSolver.MAX_LENGTH][SudokuPuzzleSolver.MAX_LENGTH];
    }

    private int[][] currentPuzzle;
    /**
     * The puzzle that will appear in the view;
     */
    private int[][] visiblePuzzle;
    private SudokuPuzzleSolution sudokuPuzzleSolution;

    public SudokuPuzzleRepository() {
        setCurrentPuzzle(DEFAULT_PUZZLE);
    }

    /**
     *
     * @return The current Puzzle
     */
    public int[][] getCurrentPuzzle() {
        if (currentPuzzle == null) {
            // Create a blank puzzle
            currentPuzzle = new int[SudokuPuzzleSolver.MAX_LENGTH][SudokuPuzzleSolver.MAX_LENGTH];
        }
        return currentPuzzle;
    }

    /**
     *
     * @param puzzle Sets a copy of puzzle to the current puzzle
     */
    public void setCurrentPuzzle(int[][] puzzle) {
        currentPuzzle = new int[SudokuPuzzleSolver.MAX_LENGTH][SudokuPuzzleSolver.MAX_LENGTH];

        for (int i = 0; i < puzzle.length; i++) {
            currentPuzzle[i] = Arrays.copyOf(puzzle[i],
                    puzzle[i].length);
        }
    }

    public SudokuPuzzleSolution getSudokuPuzzleSolution() {
        return sudokuPuzzleSolution;
    }

    /**
     *
     * @param sudokuPuzzleSolution Sets the puzzle solution without copy
     */
    public void setSudokuPuzzleSolution(SudokuPuzzleSolution sudokuPuzzleSolution) {
        this.sudokuPuzzleSolution = sudokuPuzzleSolution;
    }

    public int[][] getVisiblePuzzle() {
        if (visiblePuzzle == null) {
            visiblePuzzle = getCurrentPuzzle();
        }
        return visiblePuzzle;
    }

    /**
     *
     * @param puzzle Sets the visible puzzle via copy
     */
    public void setVisiblePuzzle(int[][] puzzle) {
        visiblePuzzle = new int[SudokuPuzzleSolver.MAX_LENGTH][SudokuPuzzleSolver.MAX_LENGTH];

        for (int i = 0; i < puzzle.length; i++) {
            visiblePuzzle[i] = Arrays.copyOf(puzzle[i],
                    puzzle[i].length);
        }
    }
}
