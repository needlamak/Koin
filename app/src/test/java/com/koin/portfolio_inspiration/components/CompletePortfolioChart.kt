package com.base.features.portfolio.presentation.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.features.portfolio.presentation.TimeRange
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

data class ChartPoint(
    val timestamp: Long,
    val value: Double
)

@Composable
fun CompletePortfolioChart(
    data: List<ChartPoint>,
    selectedTimeRange: TimeRange,
    modifier: Modifier = Modifier
) {
    var touchPoint by remember { mutableStateOf<Offset?>(null) }
    var selectedValue by remember { mutableStateOf<Double?>(null) }
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1500, easing = EaseOutCubic),
        label = "chart_animation"
    )

    val gridState = remember(data) {
        calculateGridIntervals(data)
    }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        touchPoint = offset
                        // Find nearest data point
                        selectedValue = findNearestValue(offset, data, size)
                    }
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            touchPoint = offset
                            selectedValue = findNearestValue(offset, data, size)
                        },
                        onDragEnd = {
                            touchPoint = null
                            selectedValue = null
                        },
                        onDragCancel = {
                            touchPoint = null
                            selectedValue = null
                        },
                        onHorizontalDrag = { change, _ ->
                            touchPoint = change.position
                            selectedValue = findNearestValue(change.position, data, size)
                        }
                    )
                }
        ) {
            if (data.isEmpty()) return@Canvas

            val canvasWidth = size.width
            val canvasHeight = size.height
            val padding = 12f
            val rightPadding = 60f
            val gradientExtraHeight = 15.dp.toPx()

            val minValue = data.minOf { it.value }
            val maxValue = data.maxOf { it.value }
            val valueRange = maxValue - minValue

            val points = data.mapIndexed { index, point ->
                val x = padding + (index.toFloat() / (data.size - 1)) * (canvasWidth - rightPadding - padding)
                val y = canvasHeight - padding - ((point.value - minValue) / valueRange * (canvasHeight - 2 * padding)).toFloat()
                Offset(x, y)
            }

            // Draw grid lines and labels
            val gridYPositions = gridState.gridValues.map { value ->
                canvasHeight - padding - ((value - minValue) / valueRange * (canvasHeight - 2 * padding)).toFloat()
            }

            // Draw dotted grid lines
            gridYPositions.forEach { yPos ->
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 10f), 0f)
                drawLine(
                    color = Color.Gray.copy(alpha = 0.3f),
                    start = Offset(padding, yPos),
                    end = Offset(canvasWidth - rightPadding, yPos),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = pathEffect
                )
            }

            // Draw amount labels
            gridState.gridValues.forEachIndexed { index, value ->
                val yPos = gridYPositions[index]
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 12.sp.toPx()
                        textAlign = android.graphics.Paint.Align.RIGHT
                        isAntiAlias = true
                    }
                    drawText(
                        formatGridValue(value),
                        canvasWidth - 8f,
                        yPos + 4.dp.toPx(),
                        paint
                    )
                }
            }

            // Create smooth curve path
            val smoothCurvePath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, points.first().y)

                    for (i in 1 until points.size) {
                        val prevPoint = points[i - 1]
                        val currentPoint = points[i]

                        val controlPoint1X = prevPoint.x + (currentPoint.x - prevPoint.x) * 0.3f
                        val controlPoint1Y = prevPoint.y
                        val controlPoint2X = currentPoint.x - (currentPoint.x - prevPoint.x) * 0.3f
                        val controlPoint2Y = currentPoint.y

                        cubicTo(
                            controlPoint1X, controlPoint1Y,
                            controlPoint2X, controlPoint2Y,
                            currentPoint.x * animatedProgress, currentPoint.y
                        )
                    }
                }
            }

            // Create gradient fill path
            val gradientPath = Path().apply {
                if (points.isNotEmpty()) {
                    val startX = points.first().x
                    val bottomY = canvasHeight - padding + gradientExtraHeight

                    moveTo(startX, bottomY - 8.dp.toPx())
                    quadraticBezierTo(
                        startX, bottomY,
                        startX + 8.dp.toPx(), bottomY
                    )

                    val endX = points.last().x * animatedProgress
                    lineTo(endX - 8.dp.toPx(), bottomY)
                    quadraticBezierTo(
                        endX, bottomY,
                        endX, bottomY - 8.dp.toPx()
                    )

                    lineTo(endX, points.last().y)

                    for (i in points.size - 2 downTo 0) {
                        val currentPoint = points[i]
                        val nextPoint = points[i + 1]

                        val controlPoint1X = nextPoint.x - (nextPoint.x - currentPoint.x) * 0.3f
                        val controlPoint1Y = nextPoint.y
                        val controlPoint2X = currentPoint.x + (nextPoint.x - currentPoint.x) * 0.3f
                        val controlPoint2Y = currentPoint.y

                        cubicTo(
                            controlPoint1X, controlPoint1Y,
                            controlPoint2X, controlPoint2Y,
                            currentPoint.x * animatedProgress, currentPoint.y
                        )
                    }

                    close()
                }
            }

            // Draw gradient fill
            drawPath(
                path = gradientPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Green.copy(alpha = 0.3f),
                       Color.LightGray.copy(alpha = 0.05f)
                    ),
                    startY = points.minOfOrNull { it.y } ?: 0f,
                    endY = canvasHeight - padding + gradientExtraHeight
                )
            )

            // Draw smooth line
            drawPath(
                path = smoothCurvePath,
                color = Color.DarkGray,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw touch indicator
            touchPoint?.let { offset ->
                if (offset.x in padding..(canvasWidth - rightPadding)) {
                    // Draw vertical line
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(offset.x, padding),
                        end = Offset(offset.x, canvasHeight - padding),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                    )

                    // Draw touch point
                    drawCircle(
                        color = Color.Blue,
                        radius = 6.dp.toPx(),
                        center = Offset(offset.x, findYForX(offset.x, points))
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = Offset(offset.x, findYForX(offset.x, points))
                    )
                }
            }

            // Draw current value indicator when not touching
            if (touchPoint == null && points.isNotEmpty() && animatedProgress > 0.8f) {
                val lastPoint = points.last()
                drawCircle(
                    color = Color.White,
                    radius = 6.dp.toPx(),
                    center = Offset(lastPoint.x * animatedProgress, lastPoint.y)
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = Offset(lastPoint.x * animatedProgress, lastPoint.y)
                )
            }
        }

        // Show value tooltip
        selectedValue?.let { value ->
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(8.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = formatter.format(value),
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun findNearestValue(offset: Offset, data: List<ChartPoint>, size: IntSize): Double? {
    if (data.isEmpty()) return null
    
    val padding = 12f
    val rightPadding = 60f
    val xRatio = (offset.x - padding) / (size.width - rightPadding - padding)
    val index = (xRatio * (data.size - 1)).toInt().coerceIn(0, data.size - 1)
    return data.getOrNull(index)?.value
}

private fun findYForX(x: Float, points: List<Offset>): Float {
    if (points.isEmpty()) return 0f
    
    // Find the two points that x falls between
    val index = points.indexOfFirst { it.x >= x }
    if (index <= 0) return points.first().y
    if (index >= points.size) return points.last().y
    
    val startPoint = points[index - 1]
    val endPoint = points[index]
    
    // Linear interpolation
    val progress = (x - startPoint.x) / (endPoint.x - startPoint.x)
    return startPoint.y + (endPoint.y - startPoint.y) * progress
}

// Your existing helper classes and functions...
data class GridState(
    val gridValues: List<Double>,
    val interval: Double
)


private fun calculateGridIntervals(data: List<ChartPoint>): GridState {
    if (data.isEmpty()) return GridState(emptyList(), 100.0)

    val minValue = data.minOf { it.value }
    val maxValue = data.maxOf { it.value }
    val range = maxValue - minValue

    // Determine a suitable base interval for the data range
    val unroundedInterval = range / ( /* Desired number of grid lines - 1 */ 4.0) // For 5 grid lines
    val p = 10.0.pow(floor(log10(unroundedInterval)))
    val roundedInterval = ceil(unroundedInterval / p) * p

    val gridValues = mutableListOf<Double>()
    var currentValue = floor(minValue / roundedInterval) * roundedInterval
    while (currentValue <= maxValue + roundedInterval * 0.1) { // Add a small buffer
        gridValues.add(currentValue)
        currentValue += roundedInterval
    }

    // Ensure we have at least a few grid lines, and not too many
    // You can adjust these numbers to control density
    val minGridLines = 3
    val maxGridLines = 7

    if (gridValues.size < minGridLines && roundedInterval > 0) {
        // If too few, try halving the interval to get more lines
        return calculateGridIntervals(data.map { it.copy(value = it.value) }, roundedInterval / 2)
    } else if (gridValues.size > maxGridLines) {
        // If too many, try doubling the interval to get fewer lines
        return calculateGridIntervals(data.map { it.copy(value = it.value) }, roundedInterval * 2)
    }

    return GridState(gridValues.distinct().sorted(), roundedInterval)
}

// Overload to allow recursive calls with a suggested interval
private fun calculateGridIntervals(data: List<ChartPoint>, suggestedInterval: Double): GridState {
    val minValue = data.minOf { it.value }
    val maxValue = data.maxOf { it.value }

    val gridValues = mutableListOf<Double>()
    var currentValue = floor(minValue / suggestedInterval) * suggestedInterval
    while (currentValue <= maxValue + suggestedInterval * 0.1) {
        gridValues.add(currentValue)
        currentValue += suggestedInterval
    }
    return GridState(gridValues.distinct().sorted(), suggestedInterval)
}

private fun formatGridValue(value: Double): String {
    return when {
        value >= 1000000 -> "${(value / 1000000).toInt()}M"
        value >= 1000 -> "${(value / 1000).toInt()}K"
        else -> value.toInt().toString()
    }
}