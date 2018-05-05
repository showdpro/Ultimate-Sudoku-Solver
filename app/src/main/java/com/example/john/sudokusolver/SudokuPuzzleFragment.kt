package com.example.john.sudokusolver

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.GridLayout
import kotlinx.android.synthetic.main.sudoku_puzzle_grid_layout.view.*

/**
 * Created by john on 5/4/18.
 */

class SudokuPuzzleFragment : Fragment() {
    private var mSudokuSolver: SudokuPuzzleSolver = SudokuPuzzleSolver()
    private lateinit var puzzleLayout: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null)
            mSudokuSolver.restore(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mSudokuSolver.save(outState)
    }

    @SuppressLint("NewApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.sudoku_puzzle_grid_layout, container, false)

        puzzleLayout = view.puzzleLayout

        puzzleLayout.columnCount = SudokuPuzzleSolver.MAX_LENGTH
        puzzleLayout.rowCount = SudokuPuzzleSolver.MAX_LENGTH
        puzzleLayout.orientation = GridLayout.HORIZONTAL
        puzzleLayout.alignmentMode = GridLayout.ALIGN_BOUNDS

        val layoutParams: GridLayout.LayoutParams = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                val rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)
                val columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)
                GridLayout.LayoutParams(rowSpec, columnSpec)
            }
            else -> {
                val width = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    val size = Point()
                    activity.windowManager.defaultDisplay.getSize(size)
                    size.x / SudokuPuzzleSolver.MAX_LENGTH
                } else {
                    ViewGroup.LayoutParams.WRAP_CONTENT
                }
                GridLayout.LayoutParams(ViewGroup.LayoutParams(width, width))
            }
        }

        var editText: EditText?
        var previousEditText: EditText? = null

        // Populate the cells in the puzzle layout
        for (row in 0 until puzzleLayout.rowCount) {
            for (column in 0 until puzzleLayout.columnCount) {
                val cellView = inflater.inflate(
                        if ( (column / 3 + row / 3) % 2 == 0) R.layout.light_cell_layout
                        else R.layout.dark_cell_layout,
                        null)
                // TODO copy the contents of puzzle data into cell
                editText = cellView.findViewWithTag(getString(R.string.cell_editable_digit_tag))
                editText?.setText((row * 9 + column + 1).toString())

                // Set the field navigation behavior for the editable digits
                editText?.imeOptions =
                        if (column != puzzleLayout.columnCount - 1 || row != puzzleLayout.rowCount - 1)
                            EditorInfo.IME_ACTION_NEXT
                        else
                            EditorInfo.IME_ACTION_DONE

                editText.id = puzzleLayout.rowCount * row + column + 1
                previousEditText?.id = editText.id
                previousEditText = editText

                // Add the cell to the puzzle layout
                puzzleLayout.addView(cellView, GridLayout.LayoutParams(layoutParams))


            }
        }
//        puzzleLayout.findViewById<EditText>(41)?.setText("99")
        return view
    }
}