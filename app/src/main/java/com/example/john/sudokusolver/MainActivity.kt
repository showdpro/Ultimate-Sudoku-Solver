/*
 * Created by John Masiello. Copyright (c) 2018
 */

package com.example.john.sudokusolver

import android.arch.lifecycle.ViewModelProviders
import android.content.res.ColorStateList
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main entry point for the app
 */

class MainActivity : AppCompatActivity() {
    private var mPuzzleViewCallback: PuzzleViewCallback? = null
    private lateinit var mPuzzleModel: SudokuPuzzleViewModel
    private var mFloatingButton: FloatingActionButton? = null
    private var buttonState: Int = STATE_SOLVER
        set(value) {
            when (value) {
                STATE_CREATOR -> {
                    val backGroundColorTintList: ColorStateList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                    mFloatingButton?.backgroundTintList  = backGroundColorTintList
                    mFloatingButton?.setImageDrawable(resources.getDrawable(R.drawable.button_accept))
                }
                else -> {
                    val backGroundColorTintList: ColorStateList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
                    mFloatingButton?.backgroundTintList  = backGroundColorTintList
                    mFloatingButton?.setImageDrawable(resources.getDrawable(R.drawable.button_solve))
                }
            }
            field = value
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPuzzleViewCallback = (supportFragmentManager.findFragmentById(R.id.puzzleContent)) as? PuzzleViewCallback
        mPuzzleModel = ViewModelProviders.of(this).get(SudokuPuzzleViewModel().javaClass)

        mFloatingButton = my_fab
        mFloatingButton?.setOnClickListener { _ ->
            when (buttonState) {
                STATE_CREATOR -> {
                    buttonState = STATE_SOLVER
                    mPuzzleViewCallback?.capturePuzzleFromView()?.let { mPuzzleModel.currentPuzzle = it }
                }
                else -> {
                    mPuzzleViewCallback?.capturePuzzleFromView()?.let { mPuzzleModel.visiblePuzzle = it }

                    // Access the solving Logic. It is quick, so work loads on background threads are not used here
                    val success = mPuzzleModel.solveCurrentPuzzle()

                    Toast.makeText(this, if (success) R.string.solveSuccess else R.string.solveFail,
                            Toast.LENGTH_SHORT).show()

                    mPuzzleViewCallback?.refreshPuzzleView()
                }
            }
        }

        buttonState = savedInstanceState?.getInt("buttonState") ?: STATE_SOLVER
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("buttonState", buttonState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when (item?.itemId) {
            R.id.new_puzzle -> {
                mPuzzleModel.setNewPuzzle(SudokuPuzzleRepository.emptyPuzzle())
                mPuzzleViewCallback?.refreshPuzzleView()

                Toast.makeText(this, getString(R.string.newPuzzleInstructions),
                        Toast.LENGTH_SHORT).show()
                buttonState = STATE_CREATOR
                true
            }
            R.id.reset_puzzle -> {
                mPuzzleModel.visiblePuzzle = mPuzzleModel.currentPuzzle
                mPuzzleViewCallback?.refreshPuzzleView()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val STATE_SOLVER  = 0
        private const val STATE_CREATOR = 1
    }
}
