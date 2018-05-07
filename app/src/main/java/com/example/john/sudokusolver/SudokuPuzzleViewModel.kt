/*
 * Created by John Masiello. Copyright (c) 2018
 */

package com.example.john.sudokusolver

import android.arch.lifecycle.ViewModel

class SudokuPuzzleViewModel: ViewModel() {
    private val sudokuPuzzleSolver: SudokuPuzzleSolver = SudokuPuzzleSolver()
    private val sudokuPuzzleRepository: SudokuPuzzleRepository = SudokuPuzzleRepository()

    // Wrapping the repository properties

    var visiblePuzzle: Array<IntArray>
        get() = sudokuPuzzleRepository.visiblePuzzle
        set(value) { sudokuPuzzleRepository.visiblePuzzle = value}

    var currentPuzzle: Array<IntArray>
        get() = sudokuPuzzleRepository.currentPuzzle
        set(value) { sudokuPuzzleRepository.currentPuzzle = value}

    /**
     * @return true iff the puzzle was completely solved
     */
    fun solveCurrentPuzzle(): Boolean {
        sudokuPuzzleRepository.sudokuPuzzleSolution =
                sudokuPuzzleSolver.solve(sudokuPuzzleRepository.visiblePuzzle)

        // When when is finished being solved...
        // Update the repository
        sudokuPuzzleRepository.visiblePuzzle =
                sudokuPuzzleRepository.sudokuPuzzleSolution.partialSolutionSolvedDigits

        return sudokuPuzzleRepository.sudokuPuzzleSolution.isSolved
    }

    /**
     * @param puzzle The puzzle that is set to both visible and current puzzle, via copy
     */
    fun setNewPuzzle(puzzle: Array<IntArray>) {
        sudokuPuzzleRepository.currentPuzzle = puzzle
        sudokuPuzzleRepository.visiblePuzzle = puzzle
    }
}