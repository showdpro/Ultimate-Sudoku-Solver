package com.example.john.sudokusolver

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Answer by ban-geoengineering on Sep 22 '17 at 23:15, in response to:
 * https://stackoverflow.com/questions/46370836/android-movable-draggable-floating-action-button-fab
 */
class MovableFloatingActionButton : FloatingActionButton, View.OnTouchListener {

    private var downRawX: Float = 0.toFloat()
    private var downRawY: Float = 0.toFloat()
    private var dX: Float = 0.toFloat()
    private var dY: Float = 0.toFloat()
    private var dragThreshold2 = 0F

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setOnTouchListener(this)
        dragThreshold2 = CLICK_DRAG_TOLERANCE * resources.displayMetrics.density
        dragThreshold2 *= dragThreshold2
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        val action = motionEvent.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {

                downRawX = motionEvent.rawX
                downRawY = motionEvent.rawY
                dX = view.x - downRawX
                dY = view.y - downRawY

                return true // Consumed

            }
            MotionEvent.ACTION_MOVE -> {

                val viewWidth = view.width
                val viewHeight = view.height

                val viewParent = view.parent as View
                val parentWidth = viewParent.width
                val parentHeight = viewParent.height

                var newX = motionEvent.rawX + dX
                newX = Math.max(0f, newX) // Don't allow the FAB past the left hand side of the parent
                newX = Math.min((parentWidth - viewWidth).toFloat(), newX) // Don't allow the FAB past the right hand side of the parent

                var newY = motionEvent.rawY + dY
                newY = Math.max(0f, newY) // Don't allow the FAB past the top of the parent
                newY = Math.min((parentHeight - viewHeight).toFloat(), newY) // Don't allow the FAB past the bottom of the parent

                view.animate()
                        .x(newX)
                        .y(newY)
                        .setDuration(0)
                        .start()

                return true // Consumed

            }
            MotionEvent.ACTION_UP -> {

                val upRawX = motionEvent.rawX
                val upRawY = motionEvent.rawY

                val upDX = upRawX - downRawX
                val upDY = upRawY - downRawY

                return if (upDX * upDX + upDY * upDY < dragThreshold2) { // A click
                    performClick()
                } else { // A drag
                    true // Consumed
                }

            }
            else -> return super.onTouchEvent(motionEvent)
        }

    }

    companion object {

        // Units in dp
        private const val CLICK_DRAG_TOLERANCE = 20f // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.
    }
}
