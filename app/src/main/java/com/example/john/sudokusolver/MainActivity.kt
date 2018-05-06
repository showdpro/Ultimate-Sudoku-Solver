/*
 * Created by John Masiello. Copyright (c) 2018
 */

package com.example.john.sudokusolver

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main entry point for the app
 */
class MainActivity : AppCompatActivity() {
    private var mPuzzleViewCallback: PuzzleViewCallback? = null
    private lateinit var mPuzzleModel: SudokuPuzzleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPuzzleViewCallback = (supportFragmentManager.findFragmentById(R.id.puzzleContent)) as? PuzzleViewCallback
        mPuzzleModel = ViewModelProviders.of(this).get(SudokuPuzzleViewModel().javaClass)

        my_fab?.setOnClickListener { _ ->
            mPuzzleViewCallback?.capturePuzzleFromView()?.let { mPuzzleModel.setCurrentPuzzle(it) }

            // Access the solving Logic. It is quick, so work loads on background threads are not used here
            val success = mPuzzleModel.solveCurrentPuzzle()

            Toast.makeText(this, if (success) R.string.solveSuccess else R.string.solveFail,
                    Toast.LENGTH_SHORT).show()

            mPuzzleViewCallback?.refreshPuzzleView()
        }
    }

    // TODO menu options to 1) Blank Puzzle 2) Reset Puzzle
}
