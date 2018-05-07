/*
 * Created by John Masiello. Copyright (c) 2018
 */

package com.example.john.sudokusolver

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.widget.GridLayout

class SudokuGridLayout: GridLayout {
    private val foreGroundPaint: Paint = Paint()

    constructor(context: Context?): super(context)

    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)

    init {
        setWillNotDraw(false)
        foreGroundPaint.color = Color.BLACK
    }

    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
        val firstIndexBottomRow = (rowCount - 1) * columnCount

        if (canvas == null || childCount <= firstIndexBottomRow) {
            return
        }
        // Used to either inset or center a thicker stroke
        val strokeOffset = THICK_STROKE_WIDTH / 2.0f
        var nudgedPosition: Float

        var contentHeight = 0f

        val columnCount = columnCount
        for (i in 0..firstIndexBottomRow step columnCount) {
            contentHeight += getChildAt(i).height
        }

        // Draw the vertical lines
        var contentWidth = 0f

        foreGroundPaint.strokeWidth = THICK_STROKE_WIDTH
        // Inset the line drawn on the edge, so the thickness does not fall outside of the viewport
        canvas.drawLine(strokeOffset, 0f, strokeOffset, contentHeight, foreGroundPaint)
        contentWidth += getChildAt(0).width


        // Accumulate child widths at the same time
        for (i in 1 until columnCount) {
            foreGroundPaint.strokeWidth = if (i % 3 == 0) {
                nudgedPosition = contentWidth - strokeOffset
                THICK_STROKE_WIDTH
            } else {
                nudgedPosition = contentWidth
                NORMAL_STROKE_WIDTH
            }

            canvas.drawLine(nudgedPosition, 0f, nudgedPosition, contentHeight, foreGroundPaint)
            contentWidth += getChildAt(i).width
        }

        foreGroundPaint.strokeWidth = THICK_STROKE_WIDTH
        // Inset the line drawn on the edge, so the thickness does not fall outside of the viewport
        canvas.drawLine(contentWidth - strokeOffset, 0f, contentWidth - strokeOffset, contentHeight, foreGroundPaint)

        // Draw the horizontal lines
        contentHeight = 0f

        foreGroundPaint.strokeWidth = THICK_STROKE_WIDTH
        // Inset the line drawn on the edge, so the thickness does not fall outside of the viewport
        canvas.drawLine(0f, strokeOffset, contentWidth, strokeOffset, foreGroundPaint)
        contentHeight += getChildAt(0).height

        foreGroundPaint.strokeWidth = NORMAL_STROKE_WIDTH
        for (i in columnCount..firstIndexBottomRow step columnCount) {
            foreGroundPaint.strokeWidth = if ((i / columnCount) % 3 == 0) {
                nudgedPosition = contentHeight - strokeOffset
                THICK_STROKE_WIDTH
            } else {
                nudgedPosition = contentHeight
                NORMAL_STROKE_WIDTH
            }

            canvas.drawLine(0f, nudgedPosition, contentWidth, nudgedPosition, foreGroundPaint)
            contentHeight += getChildAt(0).height
        }

        foreGroundPaint.strokeWidth = THICK_STROKE_WIDTH
        // Inset the line drawn on the edge, so the thickness does not fall outside of the viewport
        canvas.drawLine(0f, contentHeight - strokeOffset, contentWidth, contentHeight - strokeOffset, foreGroundPaint)
    }

    companion object {
        private const val THICK_STROKE_WIDTH  = 4.0f
        private const val NORMAL_STROKE_WIDTH = 2.0f
    }
}