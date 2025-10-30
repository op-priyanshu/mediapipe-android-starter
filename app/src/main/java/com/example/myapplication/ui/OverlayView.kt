package com.example.myapplication.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarksConnections
import kotlin.math.min

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var results: PoseLandmarkerResult? = null
    private val pointPaint = Paint()
    private val linePaint = Paint()

    private var imageWidth: Int = 1
    private var imageHeight: Int = 1
    private var scaleFactor: Float = 1f

    init {
        pointPaint.color = Color.GREEN
        pointPaint.style = Paint.Style.FILL
        pointPaint.strokeWidth = 8f

        linePaint.color = Color.WHITE
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = 4f
    }

    fun setResults(
        poseLandmarkerResult: PoseLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int
    ) {
        results = poseLandmarkerResult
        this.imageHeight = imageHeight
        this.imageWidth = imageWidth
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        results?.let { poseLandmarkerResult ->
            // Calculate scaling factor
            val viewWidth = width.toFloat()
            val viewHeight = height.toFloat()
            scaleFactor = min(viewWidth / imageWidth, viewHeight / imageHeight)

            val (offsetX, offsetY) = calculateOffset(viewWidth, viewHeight)

            for (landmark in poseLandmarkerResult.landmarks()) {
                // Draw landmarks
                for (normalizedLandmark in landmark) {
                    canvas.drawPoint(
                        normalizedLandmark.x() * imageWidth * scaleFactor + offsetX,
                        normalizedLandmark.y() * imageHeight * scaleFactor + offsetY,
                        pointPaint
                    )
                }

                // Draw connections
                PoseLandmarksConnections.POSE_LANDMARKS.forEach { connection ->
                    canvas.drawLine(
                        landmark[connection.start()].x() * imageWidth * scaleFactor + offsetX,
                        landmark[connection.start()].y() * imageHeight * scaleFactor + offsetY,
                        landmark[connection.end()].x() * imageWidth * scaleFactor + offsetX,
                        landmark[connection.end()].y() * imageHeight * scaleFactor + offsetY,
                        linePaint
                    )
                }
            }
        }
    }

    private fun calculateOffset(viewWidth: Float, viewHeight: Float): Pair<Float, Float> {
        val scaledWidth = imageWidth * scaleFactor
        val scaledHeight = imageHeight * scaleFactor
        val offsetX = (viewWidth - scaledWidth) / 2
        val offsetY = (viewHeight - scaledHeight) / 2
        return Pair(offsetX, offsetY)
    }
}
