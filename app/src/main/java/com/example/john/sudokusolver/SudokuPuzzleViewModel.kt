/*
 * Created by John Masiello. Copyright (c) 2018
 */

package com.example.john.sudokusolver

import android.arch.lifecycle.ViewModel

class SudokuPuzzleViewModel: ViewModel() {
    private val sudokuPuzzleSolver: SudokuPuzzleSolver = SudokuPuzzleSolver()
    private val sudokuPuzzleRepository: SudokuPuzzleRepository = SudokuPuzzleRepository()

    /**
     * @return true iff the puzzle was completely solved
     */
    fun solveCurrentPuzzle(): Boolean {
        sudokuPuzzleRepository.sudokuPuzzleSolution =
                sudokuPuzzleSolver.solve(sudokuPuzzleRepository.currentPuzzle)

        // When when is finished being solved...
        // Update the repository
        sudokuPuzzleRepository.visiblePuzzle =
                sudokuPuzzleRepository.sudokuPuzzleSolution.partialSolutionSolvedDigits

        return sudokuPuzzleRepository.sudokuPuzzleSolution.isSolved
    }

    // Repository Methods

    fun getVisiblePuzzle(): Array<IntArray> {
        return sudokuPuzzleRepository.visiblePuzzle
    }

    fun setVisiblePuzzle(puzzle: Array<IntArray>) {
        sudokuPuzzleRepository.visiblePuzzle = puzzle
    }

    fun setCurrentPuzzle(puzzle: Array<IntArray>) {
        sudokuPuzzleRepository.currentPuzzle = puzzle
    }
}