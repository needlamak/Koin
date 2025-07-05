package com.koin.ui.coindetail

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun PriceChart(
    prices: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Blue,
    gradientColors: List<Color> = listOf(
        Color.Blue.copy(alpha = 0.3f),
        Color.Blue.copy(alpha = 0.1f)
    ),
    onTouch: (Int) -> Unit = {}
) {
    if (prices.isEmpty()) return

    var touchX by remember { mutableFloatStateOf(-1f) }
    val animatedTouchX by animateFloatAsState(touchX, label = "touchAnimation")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    touchX = offset.x
                    val index = (offset.x / size.width * (prices.size - 1)).toInt().coerceIn(0, prices.size - 1)
                    onTouch(index)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    touchX = change.position.x
                    val index = (change.position.x / size.width * (prices.size - 1)).toInt().coerceIn(0, prices.size - 1)
                    onTouch(index)
                }
            }
    ) {
        val maxPrice = remember(prices) { prices.maxOrNull() ?: 0.0 }
        val minPrice = remember(prices) { prices.minOrNull() ?: 0.0 }
        val priceRange = maxPrice - minPrice

        val path = remember { Path() }
        val gradientPath = remember { Path() }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val graphWidth = size.width
            val graphHeight = size.height - 32.dp.toPx()
            val padding = 16.dp.toPx()

            val points = prices.mapIndexed { index, price ->
                val x = (index.toFloat() / (prices.size - 1)) * (graphWidth - padding * 2) + padding
                val y = graphHeight - ((price - minPrice) / priceRange * graphHeight).toFloat()
                Offset(x, y)
            }

            if (points.isNotEmpty()) {
                gradientPath.reset()
                gradientPath.moveTo(points.first().x, graphHeight + padding)
                points.forEach { gradientPath.lineTo(it.x, it.y) }
                gradientPath.lineTo(points.last().x, graphHeight + padding)
                gradientPath.close()

                drawPath(
                    path = gradientPath,
                    brush = Brush.verticalGradient(gradientColors, 0f, graphHeight + padding)
                )
            }

            if (points.isNotEmpty()) {
                path.reset()
                path.moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    path.lineTo(points[i].x, points[i].y)
                }

                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            if (animatedTouchX > 0) {
                val index = (animatedTouchX / graphWidth * (prices.size - 1)).toInt().coerceIn(0, prices.size - 1)
                val point = points[index]

                drawLine(
                    color = Color.Gray.copy(alpha = 0.5f),
                    start = Offset(point.x, 0f),
                    end = Offset(point.x, graphHeight),
                    strokeWidth = 1.dp.toPx()
                )

                drawCircle(
                    color = lineColor,
                    radius = 6.dp.toPx(),
                    center = point
                )

                val tooltipWidth = 100.dp.toPx()
                val tooltipHeight = 40.dp.toPx()
                val tooltipX = point.x - tooltipWidth / 2
                val tooltipY = point.y - 50.dp.toPx()

                drawRoundRect(
                    color = Color.LightGray,
                    topLeft = Offset(tooltipX, tooltipY),
                    size = Size(tooltipWidth, tooltipHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                drawContext.canvas.nativeCanvas.drawText(
                    "$${String.format("%.2f", prices[index])}",
                    point.x,
                    tooltipY + tooltipHeight / 2 + 5.dp.toPx() / 2,
                    Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 12.dp.toPx()
                        textAlign = Paint.Align.CENTER
                    }
                )
            }
        }
    }
}
