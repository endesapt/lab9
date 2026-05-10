package com.example.mycalc.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.max

@Composable
fun GrowthChart(
    points: List<com.example.mycalc.model.GraphPoint>,
    modifier: Modifier,
    scale: Float,
    useGradient: Boolean,
    strokeWidth: Float
) {
    val progress by animateFloatAsState(targetValue = if (points.isEmpty()) 0f else 1f, label = "chart")

    Canvas(modifier = modifier.fillMaxSize()) {
        if (points.isEmpty()) return@Canvas

        val minX = points.minOf { it.index }
        val maxX = points.maxOf { it.index }
        val minY = points.minOf { it.amount }
        val maxY = points.maxOf { it.amount }
        val xRange = max(1, maxX - minX)
        val yRange = max(1.0, maxY - minY)

        val padding = 12f
        val width = size.width - padding * 2
        val height = size.height - padding * 2

        val path = Path()
        points.forEachIndexed { index, point ->
            val x = padding + (point.index - minX) / xRange.toFloat() * width
            val y = padding + (1f - ((point.amount - minY) / yRange).toFloat()) * height
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        val lineColor = Color(0xFF2E7D32).copy(alpha = progress)

        withTransform({
            val pivot = Offset(size.width / 2f, size.height / 2f)
            scale(scaleX = scale, scaleY = scale, pivot = pivot)
        }) {
            if (useGradient) {
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(padding + width, padding + height)
                    lineTo(padding, padding + height)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(lineColor.copy(alpha = 0.35f * progress), Color.Transparent)
                    )
                )
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}
