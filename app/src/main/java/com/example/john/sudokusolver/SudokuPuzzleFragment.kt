/*
 * Created by John Masiello. Copyright (c) 2018
 */

package com.example.john.sudokusolver

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.GridLayout
import kotlinx.android.synthetic.main.sudoku_puzzle_grid_layout.view.*

import com.example.john.sudokusolver.SudokuPuzzleSolver.MAX_LENGTH

/**
 * The fragment containing the puzzle
 */
class SudokuPuzzleFragment : Fragment(), PuzzleViewCallback {
    private lateinit var mSudokuPuzzleViewModel: SudokuPuzzleViewModel
    private lateinit var puzzleLayout: GridLayout

    override fun onPause() {
        super.onPause()
        mSudokuPuzzleViewModel.setVisiblePuzzle(capturePuzzleFromView())
    }

    @SuppressLint("NewApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.sudoku_puzzle_grid_layout, container, false)

        puzzleLayout = view.puzzleLayout

        puzzleLayout.columnCount = MAX_LENGTH
        puzzleLayout.rowCount = MAX_LENGTH
        puzzleLayout.orientation = GridLayout.HORIZONTAL
        puzzleLayout.alignmentMode = GridLayout.ALIGN_BOUNDS

        val layoutParams: GridLayout.LayoutParams = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                val rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)
                val columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)
                GridLayout.LayoutParams(rowSpec, columnSpec)
            }
            else -> {
                val activity = activity
                val width = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                && activity != null) {
                    val size = Point()
                    activity.windowManager.defaultDisplay.getSize(size)
                    size.x / MAX_LENGTH
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

                editText = cellView.findViewWithTag(getString(R.string.cell_editable_digit_tag))

                // Set the field navigation behavior for the editable digits
                editText?.imeOptions =
                        if (column != puzzleLayout.columnCount - 1 || row != puzzleLayout.rowCount - 1)
                            EditorInfo.IME_ACTION_NEXT
                        else
                            EditorInfo.IME_ACTION_DONE

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    editText.id = View.generateViewId()
                    previousEditText?.id = editText.id
                    previousEditText = editText
                }

                // Add the cell to the puzzle layout
                puzzleLayout.addView(cellView, GridLayout.LayoutParams(layoutParams))


            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mSudokuPuzzleViewModel = ViewModelProviders.of(activity!!).get(SudokuPuzzleViewModel().javaClass)

        // Copy the values of the puzzle into the puzzle layout
        refreshPuzzleView()
    }

    override fun capturePuzzleFromView(): Array<IntArray> {
        val result = Array(MAX_LENGTH, {_ -> IntArray(MAX_LENGTH, {_ -> 0})})

        applyToLargeDigits(
                {editText, row, column ->
                    editText.editableText.toString().toIntOrNull()?.let {
                        result[row][column] = SudokuPuzzleSolver.clipDigit(it)}})
        return result
    }

    override fun refreshPuzzleView() {
        val puzzle = mSudokuPuzzleViewModel.getVisiblePuzzle()

        applyToLargeDigits(
                {editText, row, column ->
                    editText.setText(digitToString(puzzle[row][column]))})
    }

    /**
     * @return (row, column) coordinates
     */
    private fun getCellCoordinates(childIndex: Int): Pair<Int, Int> {
        return Pair(childIndex / MAX_LENGTH, childIndex % MAX_LENGTH)
    }

    private fun digitToString(digit: Int): CharSequence {
        return if (digit == 0) "" else digit.toString()
    }

    /**
     * Inline function to handle repetitive iteration code over the cells
     * @param f Lambda function to be applied to each digit in each cell
     */
    private inline fun applyToLargeDigits(f: (EditText, row: Int, column: Int) -> Unit) {
        val childCount = puzzleLayout.childCount
        val largeDigitTag = getString(R.string.cell_editable_digit_tag)
        var pair: Pair<Int, Int>

        for (i in 0 until childCount) {
            pair = getCellCoordinates(i)
            puzzleLayout.getChildAt(i).findViewWithTag<EditText>(largeDigitTag)?.
                    let { f(it, pair.first, pair.second) }
        }
    }
}