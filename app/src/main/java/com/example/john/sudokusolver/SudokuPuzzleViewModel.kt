package com.example.john.sudokusolver

import android.arch.lifecycle.ViewModel

class SudokuPuzzleViewModel: ViewModel() {
    val sudokuPuzzleSolver: SudokuPuzzleSolver = SudokuPuzzleSolver()
    val sudokuPuzzleRepository: SudokuPuzzleRepository = SudokuPuzzleRepository()
}