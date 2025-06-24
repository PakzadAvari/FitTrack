package com.example.fitnessapp

// DiagonalSplitView.kt


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class DiagonalSplitView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val redPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFB71C1C.toInt() // Dark red color
        style = Paint.Style.FILL
    }

    private val blueBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF2196F3.toInt() // Blue color
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val diagonalPath = Path()
    private val borderPath = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createDiagonalPaths(w, h)
    }

    private fun createDiagonalPaths(w: Int, h: Int) {
        // Clear previous paths
        diagonalPath.reset()
        borderPath.reset()

        // Create diagonal fill area (right side)
        diagonalPath.apply {
            moveTo(w * 0.55f, 0f) // Start from top, slightly left of center
            lineTo(w.toFloat(), 0f) // Top right corner
            lineTo(w.toFloat(), h.toFloat()) // Bottom right corner
            lineTo(w * 0.15f, h.toFloat()) // Bottom left, more diagonal
            close() // Close the path
        }

        // Create diagonal border line
        borderPath.apply {
            moveTo(w * 0.55f, 0f)
            lineTo(w * 0.15f, h.toFloat())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the diagonal red section
        canvas.drawPath(diagonalPath, redPaint)

        // Draw the blue diagonal border
        canvas.drawPath(borderPath, blueBorderPaint)
    }
}

