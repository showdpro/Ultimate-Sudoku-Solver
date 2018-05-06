package com.example.john.sudokusolver;

import com.example.john.sudokusolver.SudokuPuzzleSolver.SudokuPuzzleSolution;

/**
 * Created by john on 5/5/18.
 * Class that mocks a repository
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

    int[][] currentPuzzle;
    SudokuPuzzleSolution sudokuPuzzleSolution;

    public SudokuPuzzleRepository() {
        currentPuzzle = DEFAULT_PUZZLE;
    }
}
