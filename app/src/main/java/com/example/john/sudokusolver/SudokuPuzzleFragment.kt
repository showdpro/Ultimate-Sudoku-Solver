package com.example.john.sudokusolver

import android.app.Fragment
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import kotlinx.android.synthetic.main.light_cell_layout.view.largeDigit as light_large_digit
import kotlinx.android.synthetic.main.dark_cell_layout.view.largeDigit as dark_large_digit
import kotlinx.android.synthetic.main.sudoku_puzzle_grid_layout.view.*

/**
 * Created by john on 5/4/18.
 */

class SudokuPuzzleFragment : Fragment() {
    private var mSudokuSolver: SudokuPuzzleSolver = SudokuPuzzleSolver()
    private lateinit var puzzleLayout: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (savedInstanceState != null)
//            mSudokuSolver.restore(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
//        mSudokuSolver.save(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.sudoku_puzzle_grid_layout, container, false)

        // TODO copy puzzle state into puzzleLayout
//        puzzleLayout = view.puzzleLayout
        puzzleLayout = view.findViewById<GridLayout>(R.id.puzzleLayout)
        puzzleLayout.removeAllViews()

        puzzleLayout.columnCount = SudokuPuzzleSolver.MAX_LENGTH
        puzzleLayout.rowCount = SudokuPuzzleSolver.MAX_LENGTH
        puzzleLayout.orientation = GridLayout.HORIZONTAL

        var layoutParams : GridLayout.LayoutParams

        // Populate the cells in the puzzle layout
        var cellView: View
        for (row in 0 until puzzleLayout.rowCount) {
            for (column in 0 until puzzleLayout.columnCount) {
                if ( (column / 3 + row / 3) % 2 == 0) {
                    cellView = inflater.inflate(R.layout.light_cell_layout, puzzleLayout, false)
//                    Log.d("gui", cellView.toString())

                    // TODO copy the contents of puzzle data into cell
//                    Log.d("data", (column+1).toString(10))
                    val digit = (column + 1).toString(10)
//                    cellView.findViewById<EditText>(R.id.largeDigit)?.setText(digit)
                    cellView.dark_large_digit.setText(digit)
                } else {
                    cellView = inflater.inflate(R.layout.dark_cell_layout, puzzleLayout, false)

                    // TODO copy the contents of puzzle data into cell
//                    Log.d("data", (column+1).toString(10))
                    val digit = (column + 1).toString(10)
//                    cellView.findViewById<EditText>(R.id.largeDigit)?.setText(digit)
                    cellView.light_large_digit.setText(digit)
                }

                // Add the cell to the puzzle layout
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && false) {
                    layoutParams = GridLayout.LayoutParams(
                            GridLayout.spec(row, 1, 1f),
                            GridLayout.spec(column, 1, 1f))
                } else {
                    layoutParams = GridLayout.LayoutParams(
                            ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT))
                    layoutParams.setGravity(Gravity.NO_GRAVITY)
                }
                puzzleLayout.addView(cellView, layoutParams)
            }
        }

//        // Resize the GridLayout to wrap contents
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && false) {
//            puzzleLayout.viewTreeObserver
//        }
        return view
    }
}
