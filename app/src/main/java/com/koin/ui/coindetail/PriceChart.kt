package com.koin.ui.coindetail

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun EnhancedPriceChart(
    prices: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Blue,
    gradientColors: List<Color> = listOf(
        Color.Blue.copy(alpha = 0.3f),
        Color.Blue.copy(alpha = 0.1f)
    )
) {
    val maxPrice = remember(prices) { prices.maxOrNull() ?: 0.0 }
    val minPrice = remember(prices) { prices.minOrNull() ?: 0.0 }
    val priceRange = remember(prices) { maxPrice - minPrice }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var showTooltip by remember { mutableStateOf(false) }

    // Animation for line drawing
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(2000, easing = EaseOutCubic),
        label = "line_animation"
    )

    // Animation for gradient
    val gradientProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(2500, delayMillis = 500, easing = EaseOutCubic),
        label = "gradient_animation"
    )

    // Animation for selected point
    val selectedPointScale by animateFloatAsState(
        targetValue = if (selectedIndex != null) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "selected_point_scale"
    )

    val pointerModifier = Modifier.pointerInput(prices) {
        detectDragGestures(
            onDragStart = { position ->
                showTooltip = true
                val width = size.width
                val step = width / (prices.size - 1).coerceAtLeast(1)
                val index = (position.x / step).toInt().coerceIn(0, prices.lastIndex)
                selectedIndex = index
            },
            onDragEnd = {
                // Keep the tooltip and selected point visible after drag ends
                // Remove these lines if you want them to disappear
                // showTooltip = false
                // selectedIndex = null
            },
            onDragCancel = {
                showTooltip = false
                selectedIndex = null
            }
        ) { change, _ ->
            val width = size.width
            val step = width / (prices.size - 1).coerceAtLeast(1)
            val xPosition = change.position.x
            val index = (xPosition / step).toInt().coerceIn(0, prices.lastIndex)
            selectedIndex = index
        }
    }

    Canvas(
        modifier = modifier
            .then(pointerModifier)
            .padding(vertical = 8.dp)
    ) {
        if (prices.isEmpty() || priceRange == 0.0) return@Canvas

        val width = size.width
        val height = size.height
        val step = width / (prices.size - 1).coerceAtLeast(1)

        // Create path for the price line
        val linePath = androidx.compose.ui.graphics.Path().apply {
            val animatedSize = (prices.size * animationProgress).toInt().coerceAtLeast(1)
            prices.take(animatedSize).forEachIndexed { index, price ->
                val x = index * step
                val y = height - ((price - minPrice) / priceRange * height).toFloat()

                if (index == 0) {
                    moveTo(x, y)
                } else {
                    lineTo(x, y)
                }
            }
        }

        // Create path for gradient area (under the line)
        val gradientPath = androidx.compose.ui.graphics.Path().apply {
            val animatedSize = (prices.size * gradientProgress).toInt().coerceAtLeast(1)

            // Start from bottom-left
            moveTo(0f, height)

            // Follow the price line
            prices.take(animatedSize).forEachIndexed { index, price ->
                val x = index * step
                val y = height - ((price - minPrice) / priceRange * height).toFloat()
                lineTo(x, y)
            }

            // Close the path by going to bottom-right and back to start
            if (animatedSize > 0) {
                lineTo((animatedSize - 1) * step, height)
            }
            close()
        }

        // Draw gradient area under the line
        drawPath(
            path = gradientPath,
            brush = Brush.verticalGradient(
                colors = gradientColors,
                startY = 0f,
                endY = height
            )
        )

        // Draw dotted grid lines
        val gridColor = Color.Blue.copy(alpha = 0.2f)
        val dashWidth = 8.dp.toPx()
        val dashGap = 8.dp.toPx()

        for (i in 1..4) {
            val y = height * i / 5
            var x = 0f
            while (x < width) {
                val endX = minOf(x + dashWidth, width)
                drawLine(
                    color = gridColor,
                    start = Offset(x, y),
                    end = Offset(endX, y),
                    strokeWidth = 1.dp.toPx()
                )
                x += dashWidth + dashGap
            }
        }

        // Draw price line with smooth styling
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw selected point with enhanced styling and animation
        selectedIndex?.let { index ->
            if (index in prices.indices) {
                val x = index * step
                val y = height - ((prices[index] - minPrice) / priceRange * height).toFloat()

                // Draw outer ring with pulse animation
                val pulseRadius = 16.dp.toPx() * selectedPointScale
                drawCircle(
                    color = lineColor.copy(alpha = 0.3f),
                    radius = pulseRadius,
                    center = Offset(x, y)
                )

                // Draw main point
                drawCircle(
                    color = lineColor,
                    radius = 8.dp.toPx() * selectedPointScale,
                    center = Offset(x, y)
                )

                // Draw inner highlight
                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    radius = 4.dp.toPx() * selectedPointScale,
                    center = Offset(x, y)
                )

                // Draw vertical indicator line
                drawLine(
                    color = lineColor.copy(alpha = 0.5f),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(dashWidth, dashGap)
                    )
                )

                // Draw price tooltip
                if (showTooltip) {
                    drawContext.canvas.nativeCanvas.apply {
                        val priceText = "$${String.format(Locale.US, "%.4f", prices[index])}"
                        val textPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 32f
                            textAlign = android.graphics.Paint.Align.CENTER
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                            setShadowLayer(8f, 0f, 2f, android.graphics.Color.BLACK)
                        }

                        val textBounds = android.graphics.Rect()
                        textPaint.getTextBounds(priceText, 0, priceText.length, textBounds)

                        val padding = 12.dp.toPx()
                        val tooltipWidth = textBounds.width() + padding * 2
                        val tooltipHeight = textBounds.height() + padding * 2

                        val tooltipX = (x - tooltipWidth / 2).coerceIn(0f, width - tooltipWidth)
                        val tooltipY = (y - tooltipHeight - 16.dp.toPx()).coerceAtLeast(0f)

                        // Draw tooltip background with blue theme
                        drawRoundRect(
                            color = lineColor.copy(alpha = 0.9f),
                            topLeft = Offset(tooltipX, tooltipY),
                            size = Size(tooltipWidth, tooltipHeight),
                            cornerRadius = CornerRadius(8.dp.toPx())
                        )

                        // Draw tooltip text
                        drawText(
                            priceText,
                            tooltipX + tooltipWidth / 2,
                            tooltipY + tooltipHeight / 2 + textBounds.height() / 2,
                            textPaint
                        )
                    }
                }
            }
        }

        // Draw min/max indicators with blue theme
        if (prices.isNotEmpty()) {
            val maxIndex = prices.indexOfFirst { it == maxPrice }
            val minIndex = prices.indexOfFirst { it == minPrice }

            // Max indicator (lighter blue)
            val maxX = maxIndex * step
            val maxY = height - ((maxPrice - minPrice) / priceRange * height).toFloat()
            drawCircle(
                color = Color.Blue.copy(alpha = 0.8f),
                radius = 6.dp.toPx(),
                center = Offset(maxX, maxY)
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = Offset(maxX, maxY)
            )

            // Min indicator (darker blue)
            val minX = minIndex * step
            val minY = height - ((minPrice - minPrice) / priceRange * height).toFloat()
            drawCircle(
                color = Color.Blue.copy(alpha = 0.6f),
                radius = 6.dp.toPx(),
                center = Offset(minX, minY)
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = Offset(minX, minY)
            )
        }
    }
}