/*
 * Created by John Masiello. Copyright (c) 2018
 */

package com.example.john.sudokusolver

interface PuzzleViewCallback {
    fun capturePuzzleFromView(): Array<IntArray>
    fun refreshPuzzleView()
}