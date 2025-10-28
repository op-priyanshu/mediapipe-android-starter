package com.example.myapplication.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarksConnections

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var results: PoseLandmarkerResult? = null
    private val pointPaint = Paint()
    private val linePaint = Paint()

    init {
        pointPaint.color = Color.GREEN
        pointPaint.style = Paint.Style.FILL
        pointPaint.strokeWidth = 8f

        linePaint.color = Color.WHITE
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = 4f
    }

    fun setResults(poseLandmarkerResult: PoseLandmarkerResult) {
        results = poseLandmarkerResult
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        results?.let {
            for (landmark in it.landmarks()) {
                for (normalizedLandmark in landmark) {
                    canvas.drawPoint(
                        normalizedLandmark.x() * width,
                        normalizedLandmark.y() * height,
                        pointPaint
                    )
                }

                PoseLandmarksConnections.POSE_LANDMARKS.forEach { connection ->
                    canvas.drawLine(
                        landmark[connection.start()].x() * width,
                        landmark[connection.start()].y() * height,
                        landmark[connection.end()].x() * width,
                        landmark[connection.end()].y() * height,
                        linePaint
                    )
                }
            }
        }
    }
}
