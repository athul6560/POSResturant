package com.zeezaglobal.posresturant.ui.designElememnts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DottedLines @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint().apply {
        color = 0xFFB7B7B7.toInt()
        strokeWidth = 5f // Width of the line
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 5f), 0f) // Dash and gap lengths
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw the dotted line horizontally across the view
        canvas.drawLine(0f, height / 2f, width.toFloat(), height / 2f, paint)
    }
}