package com.example.john.sudokusolver;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static com.example.john.sudokusolver.SudokuPuzzleSolver.*;

/**
 * Created by john on 5/3/18.
 * Checks iterators and solvers for the Sudoku Puzzle Solber
 */

public class PuzzleSolveUnitTest {
    final int[][] puzzle1 = {
            {0, 0, 0, 0, 3, 4, 0, 0, 0},
            {4, 0, 2, 0, 0, 0, 3, 1, 0},
            {0, 0, 0, 1, 0, 0, 5, 0, 0},
            {8, 0, 0, 6, 0, 0, 0, 3, 0},
            {2, 0, 0, 0, 1, 0, 0, 0, 9},
            {0, 3, 0, 0, 0, 7, 0, 0, 2},
            {0, 0, 3, 0, 0, 6, 0, 0, 0},
            {0, 7, 4, 0, 0, 0, 6, 0, 8},
            {0, 0, 0, 8, 9, 0, 0, 0, 0}
    };

    final int[][] puzzle2 = {
            {0, 7, 0, 0, 5, 0, 0, 0, 0},
            {0, 5, 1, 6, 0, 9, 2, 0, 0},
            {4, 0, 6, 2, 0, 7, 0, 0, 1},
            {0, 8, 7, 0, 0, 0, 1, 0, 0},
            {3, 0, 0, 8, 0, 4, 0, 0, 2},
            {0, 0, 4, 0, 0, 0, 5, 9, 0},
            {1, 0, 0, 7, 0, 5, 8, 0, 9},
            {0, 0, 8, 3, 0, 2, 4, 7, 0},
            {0, 0, 0, 0, 4, 0, 0, 1, 0}
    };

    final int[][] puzzle3 = {
            {9, 0, 2, 0, 0, 0, 0, 0, 8},
            {0, 0, 0, 0, 8, 5, 0, 0, 9},
            {4, 0, 0, 2, 0, 0, 0, 0, 0},
            {0, 5, 0, 0, 0, 6, 3, 0, 0},
            {0, 1, 0, 0, 3, 0, 0, 2, 0},
            {0, 0, 6, 4, 0, 0, 0, 9, 0},
            {0, 0, 0, 0, 0, 2, 0, 0, 3},
            {5, 0, 0, 8, 1, 0, 0, 0, 0},
            {6, 0, 0, 0, 0, 0, 7, 0, 2}
    };

    final int[][] empty9x9Grid = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    private SudokuPuzzleSolver mSudokuPuzzleSolver;
    private int[][] mPuzzle;

    @Before
    public void beginPuzzleSolver() {
        mSudokuPuzzleSolver = new SudokuPuzzleSolver();
        mPuzzle = puzzle1;
    }

    @Test
    public void showValues() {
        System.out.println(Arrays.toString(VALUE));
    }

    @Test
    public void testSolvingTools() {
        mSudokuPuzzleSolver.initializePuzzle(mPuzzle);


        System.out.println(Arrays.toString(VALUE));

        System.out.println();
        mSudokuPuzzleSolver.printPartialSolution();

        // Tests iterators based on the already printed data
        System.out.println();
        testIterator();

        System.out.println();
        testPartitionIterator();
    }

    @Test
    public void testInitialSteps() {
        mSudokuPuzzleSolver.initializePuzzle(mPuzzle);
        mSudokuPuzzleSolver.makeInitialDeductions();

        System.out.println();
        mSudokuPuzzleSolver.printPartialSolution();
    }

    @Test
    public void testSolver() throws Exception {
        mSudokuPuzzleSolver.initializePuzzle(mPuzzle);
        mSudokuPuzzleSolver.makeInitialDeductions();

        System.out.println();
        mSudokuPuzzleSolver.printPartialSolution();
        System.out.println();

        System.out.println("Solving puzzle...");
        mSudokuPuzzleSolver.makeGeneralDeductions();
        System.out.println("All Done!");
        System.out.println();

        mSudokuPuzzleSolver.printPartialSolution();

        System.out.println();
        mSudokuPuzzleSolver.printPartialSolution("0", " ");
    }

    @Test
    public void testSolverOpaque() throws Exception {
        assertTrue(mSudokuPuzzleSolver.solve(mPuzzle).isSolved);
    }

    @Test
    public void testIterator() {
        SudokuPuzzleSolver.SudokuIterator it = mSudokuPuzzleSolver.getIterator(GRID);

        it.resetSlice();

        while (it.nextSlice()) {
            it.resetElement();
            while (it.nextElement()) {
                System.out.printf("%02d ", it.i * MAX_LENGTH + it.j);
            }
            System.out.println();
        }
    }

    @Test
    public void testPartitionIterator() {
        SudokuPuzzleSolver.SudokuIterator it = mSudokuPuzzleSolver.getIterator(GRID);

        float dummy;
        int i, j;

        it.resetSlice();

        while (it.nextSlice()) {
            it.resetPartition();
            i = it.i;
            j = it.j;

            System.out.printf("Next Slice starting on %02d...\n", i * MAX_LENGTH + j - 8);

            while (it.nextPartition()) {

                System.out.print("Next Partition...\n");

                while (it.nextPartitionElement()) {
                    i = it.i;
                    j = it.j;
                    dummy = mPuzzle[i][j] != 0 ? -mPuzzle[i][j]
                            : mSudokuPuzzleSolver.getDigit(
                                    mSudokuPuzzleSolver.getPartiallySolvedPuzzle()[i][j]);

                    System.out.printf("%02d  %+.3f\n", i * MAX_LENGTH + j, dummy);
                }
                System.out.println();
            }
        }
    }
}
