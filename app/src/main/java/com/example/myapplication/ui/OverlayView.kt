package com.example.myapplication.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
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
            val viewWidth = width.toFloat()
            val viewHeight = height.toFloat()
            scaleFactor = min(viewWidth / imageWidth, viewHeight / imageHeight)

            val (offsetX, offsetY) = calculateOffset(viewWidth, viewHeight)

            for (landmark in poseLandmarkerResult.landmarks()) {
                for (normalizedLandmark in landmark) {
                    canvas.drawPoint(
                        normalizedLandmark.x() * imageWidth * scaleFactor + offsetX,
                        normalizedLandmark.y() * imageHeight * scaleFactor + offsetY,
                        pointPaint
                    )
                }

                // Note: The official PoseLandmarksConnections is not public.
                // We are using a local copy defined in the companion object.
                POSE_CONNECTIONS.forEach { connection: Pair<PoseLandmarker.PoseLandmark, PoseLandmarker.PoseLandmark> ->
                    canvas.drawLine(
                        landmark[connection.first.ordinal].x() * imageWidth * scaleFactor + offsetX,
                        landmark[connection.first.ordinal].y() * imageHeight * scaleFactor + offsetY,
                        landmark[connection.second.ordinal].x() * imageWidth * scaleFactor + offsetX,
                        landmark[connection.second.ordinal].y() * imageHeight * scaleFactor + offsetY,
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

    companion object {
        val POSE_CONNECTIONS = listOf(
            Pair(PoseLandmarker.PoseLandmark.NOSE, PoseLandmarker.PoseLandmark.LEFT_EYE_INNER),
            Pair(PoseLandmarker.PoseLandmark.LEFT_EYE_INNER, PoseLandmarker.PoseLandmark.LEFT_EYE),
            Pair(PoseLandmarker.PoseLandmark.LEFT_EYE, PoseLandmarker.PoseLandmark.LEFT_EYE_OUTER),
            Pair(PoseLandmarker.PoseLandmark.LEFT_EYE_OUTER, PoseLandmarker.PoseLandmark.LEFT_EAR),
            Pair(PoseLandmarker.PoseLandmark.NOSE, PoseLandmarker.PoseLandmark.RIGHT_EYE_INNER),
            Pair(PoseLandmarker.PoseLandmark.RIGHT_EYE_INNER, PoseLandmarker.PoseLandmark.RIGHT_EYE),
            Pair(PoseLandmarker.PoseLandmark.RIGHT_EYE, PoseLandmarker.PoseLandmark.RIGHT_EYE_OUTER),
            Pair(PoseLandmarker.PoseLandmark.RIGHT_EYE_OUTER, PoseLandmarker.PoseLandmark.RIGHT_EAR),
            Pair(PoseLandmarker.PoseLandmark.MOUTH_LEFT, PoseLandmarker.PoseLandmark.MOUTH_RIGHT),
            Pair(PoseLandmarker.PoseLandmark.LEFT_SHOULDER, PoseLandmarker.PoseLandmark.RIGHT_SHOULDER),
            Pair(PoseLandmarker.PoseLandmark.LEFT_SHOULDER, PoseLandmarker.PoseLandmark.LEFT_ELBOW),
            Pair(PoseLandmarker.PoseLandmark.LEFT_ELBOW, PoseLandmarker.PoseLandmark.LEFT_WRIST),
            Pair(PoseLandmarker.PoseLandmark.LEFT_WRIST, PoseLandmarker.PoseLandmark.LEFT_PINKY),
            Pair(PoseLandmarker.PoseLandmark.LEFT_WRIST, PoseLandmarker.PoseLandmark.LEFT_INDEX),
            Pair(PoseLandmarker.PoseLandmark.LEFT_WRIST, PoseLandmarker.PoseLandmark.LEFT_THUMB),
            Pair(PoseLandmarker.PoseLandmark.LEFT_SHOULDER, PoseLandmarker.PoseLandmark.LEFT_HIP),
            Pair(PoseLandmarker.PoseLandmark.RIGHT_SHOULDER, PoseLandmarker.PoseLandmark.RIGHT_ELBOW),
            Pair(PoseLandmarker.PoseLandmark.RIGHT_ELBOW, PoseLandmarker.PoseLandmark.RIGHT_WRIST),
            Pair(PoseLandmarker.PoseLandmark.RIGHT_WRIST, PoseLandmarker.PoseLandmark.RIGHT_PINKY),
            Pair(PoseLandmarker.PoseLandmark.RIGHT_WRIST, PoseLandmarker.PoseLandmark.RIGHT_INDEX),
            Pair(PoseLandmarker.PoseLandmark.RIGHT_WRIST, PoseLandmarker.PoseLandmark.RIGHT_THUMB),
            Pair(PoseLandmarker.PoseLandmark.RIGHT_SHOULDER, PoseLandmarker.PoseLandmark.RIGHT_HIP),
            Pair(PoseLandmarker.PoseLandmark.LEFT_HIP, PoseLandmarker.PoseLandmark.RIGHT_HIP),
            Pair(PoseLandmarker.PoseLandmark.LEFT_HIP, PoseLandmarker.PoseLandmark.LEFT_KNEE),
            Pair(PoseLandmarker.PoseLandmark.RIGHT_HIP, PoseLandmarker.PoseLandmark.RIGHT_KNEE),
            Pair(PoseLandmarker.PoseLandmark.LEFT_KNEE, PoseLandmarker.PoseLandmark.LEFT_ANKLE),
            Pair(PoseLandmarker.PoseLandmark.RIGHT_KNEE, PoseLandmarker.PoseLandmark.RIGHT_ANKLE),
            Pair(PoseLandmarker.PoseLandmark.LEFT_ANKLE, PoseLandmarker.PoseLandmark.LEFT_HEEL),
            Pair(PoseLandmarker.PoseLandmark.RIGHT_ANKLE, PoseLandmarker.PoseLandmark.RIGHT_HEEL),
            Pair(PoseLandmarker.PoseLandmark.LEFT_ANKLE, PoseLandmarker.PoseLandmark.LEFT_FOOT_INDEX),
            Pair(PoseLandmarker.PoseLandmark.RIGHT_ANKLE, PoseLandmarker.PoseLandmark.RIGHT_FOOT_INDEX),
            Pair(PoseLandmarker.PoseLandmark.LEFT_HEEL, PoseLandmarker.PoseLandmark.LEFT_FOOT_INDEX),
            Pair(PoseLandmarker.PoseLandmark.RIGHT_HEEL, PoseLandmarker.PoseLandmark.RIGHT_FOOT_INDEX),
        )
    }
}
