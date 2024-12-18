package com.example.tablecount

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

@SuppressLint("ClickableViewAccessibility")
class DraggableView : AppCompatTextView {
    private var lastX: Float = 0f
    private var lastY: Float = 0f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val parent = parent as? View ?: return false // Get the parent view

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX - x
                lastY = event.rawY - y
            }
            MotionEvent.ACTION_MOVE -> {
                val newX = event.rawX - lastX
                val newY = event.rawY - lastY

                // Calculate the boundaries
                val maxX = parent.width - width
                val maxY = parent.height - height

                // Constrain the movement within the boundaries
                x = newX.coerceIn(0f, maxX.toFloat())
                y = newY.coerceIn(0f, maxY.toFloat())
            }
        }
        return true
    }

}
