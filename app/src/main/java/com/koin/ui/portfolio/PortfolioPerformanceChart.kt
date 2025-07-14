package com.koin.ui.portfolio

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.koin.data.coin.TimeRange
import com.koin.domain.portfolio.Portfolio
import java.text.NumberFormat
import java.util.Locale
@Composable
fun PortfolioPerformanceChart(
    portfolio: Portfolio,
    selectedTimeRange: TimeRange,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Blue,
    gradientColors: List<Color> = listOf(
        Color.Blue.copy(alpha = 0.3f),
        Color.Blue.copy(alpha = 0.1f)
    )
) {
    // Generate mock portfolio value history based on current state
    val portfolioHistory = remember(portfolio, selectedTimeRange) {
        generatePortfolioHistory(portfolio, selectedTimeRange)
    }

    val maxValue =
        remember(portfolioHistory) { portfolioHistory.maxOrNull() ?: Portfolio.INITIAL_BALANCE }
    val minValue =
        remember(portfolioHistory) { portfolioHistory.minOrNull() ?: Portfolio.INITIAL_BALANCE }
    val valueRange = remember(portfolioHistory) { maxValue - minValue }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var showTooltip by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }

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

    // Smooth pointer animation
    val pointerAnimationProgress by animateFloatAsState(
        targetValue = if (showTooltip) 1f else 0f,
        animationSpec = tween(300, easing = EaseOutCubic),
        label = "pointer_animation"
    )

    // Fixed blue color scheme
    val chartLineColor = Color.Blue
    val chartGradientColors = listOf(
        Color.Blue.copy(alpha = 0.3f),
        Color.Blue.copy(alpha = 0.1f)
    )

    val pointerModifier = Modifier.pointerInput(portfolioHistory) {
        detectTapGestures(
            onTap = { offset ->
                val width = size.width
                val step = width / (portfolioHistory.size - 1).coerceAtLeast(1)
                val index = (offset.x / step).toInt().coerceIn(0, portfolioHistory.lastIndex)
                selectedIndex = index
                showTooltip = true
            }
        )

        detectDragGestures(
            onDragStart = { position ->
                isDragging = true
                showTooltip = true
                val width = size.width
                val step = width / (portfolioHistory.size - 1).coerceAtLeast(1)
                val index = (position.x / step).toInt().coerceIn(0, portfolioHistory.lastIndex)
                selectedIndex = index
            },
            onDragEnd = {
                isDragging = false
                // Keep tooltip visible after drag ends
            },
            onDragCancel = {
                isDragging = false
                showTooltip = false
                selectedIndex = null
            }
        ) { change, _ ->
            val width = size.width
            val step = width / (portfolioHistory.size - 1).coerceAtLeast(1)
            val xPosition = change.position.x
            val index = (xPosition / step).toInt().coerceIn(0, portfolioHistory.lastIndex)
            selectedIndex = index
        }
    }

    Canvas(
        modifier = modifier
            .then(pointerModifier)
            .padding(vertical = 8.dp)
    ) {
        if (portfolioHistory.isEmpty() || valueRange == 0.0) {
            // Draw flat line at initial balance if no data
            val y = size.height / 2
            drawLine(
                color = Color.Gray,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 2.dp.toPx()
            )
            return@Canvas
        }

        val width = size.width
        val height = size.height
        val step = width / (portfolioHistory.size - 1).coerceAtLeast(1)

        // Draw dotted grid lines
        val gridColor = Color.Gray.copy(alpha = 0.3f)
        val dottedPathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
            floatArrayOf(4f, 8f)
        )

        // Horizontal dotted grid lines
        for (i in 1..4) {
            val y = height * i / 5
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dottedPathEffect
            )
        }

        // Vertical dotted grid lines
        for (i in 1..4) {
            val x = width * i / 5
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dottedPathEffect
            )
        }

        // Create path for the portfolio value line
        val linePath = androidx.compose.ui.graphics.Path().apply {
            val animatedSize = (portfolioHistory.size * animationProgress).toInt().coerceAtLeast(1)
            portfolioHistory.take(animatedSize).forEachIndexed { index, value ->
                val x = index * step
                val y = if (valueRange > 0) {
                    height - ((value - minValue) / valueRange * height).toFloat()
                } else {
                    height / 2 // Center line if no range
                }

                if (index == 0) {
                    moveTo(x, y)
                } else {
                    lineTo(x, y)
                }
            }
        }

        // Create path for gradient area (under the line)
        val gradientPath = androidx.compose.ui.graphics.Path().apply {
            val animatedSize = (portfolioHistory.size * gradientProgress).toInt().coerceAtLeast(1)

            // Start from bottom-left
            moveTo(0f, height)

            // Follow the portfolio value line
            portfolioHistory.take(animatedSize).forEachIndexed { index, value ->
                val x = index * step
                val y = if (valueRange > 0) {
                    height - ((value - minValue) / valueRange * height).toFloat()
                } else {
                    height / 2
                }
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
                colors = chartGradientColors,
                startY = 0f,
                endY = height
            )
        )

        // Draw portfolio value line (single color, no glow)
        drawPath(
            path = linePath,
            color = chartLineColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw selected point with smooth animation
        selectedIndex?.let { index ->
            if (index in portfolioHistory.indices) {
                val x = index * step
                val y = if (valueRange > 0) {
                    height - ((portfolioHistory[index] - minValue) / valueRange * height).toFloat()
                } else {
                    height / 2
                }

                // Animated pointer size
                val pointerRadius = 8.dp.toPx() * pointerAnimationProgress
                val outerRadius = 12.dp.toPx() * pointerAnimationProgress

                // Draw outer circle with subtle animation
                drawCircle(
                    color = chartLineColor.copy(alpha = 0.2f * pointerAnimationProgress),
                    radius = outerRadius,
                    center = Offset(x, y)
                )

                // Draw main point
                drawCircle(
                    color = chartLineColor,
                    radius = pointerRadius,
                    center = Offset(x, y)
                )

                // Draw inner highlight
                drawCircle(
                    color = Color.White.copy(alpha = 0.9f * pointerAnimationProgress),
                    radius = pointerRadius * 0.5f,
                    center = Offset(x, y)
                )

                // Draw vertical line from point to bottom
                drawLine(
                    color = chartLineColor.copy(alpha = 0.3f * pointerAnimationProgress),
                    start = Offset(x, y),
                    end = Offset(x, height),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                        floatArrayOf(6f, 6f)
                    )
                )

                // Draw value tooltip with smooth animation
                if (showTooltip) {
                    drawContext.canvas.nativeCanvas.apply {
                        val valueText = NumberFormat.getCurrencyInstance(Locale.US)
                            .format(portfolioHistory[index])
                        val textPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 32f
                            textAlign = android.graphics.Paint.Align.CENTER
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                            setShadowLayer(8f, 0f, 2f, android.graphics.Color.BLACK)
                        }

                        val textBounds = android.graphics.Rect()
                        textPaint.getTextBounds(valueText, 0, valueText.length, textBounds)

                        val padding = 12.dp.toPx()
                        val tooltipWidth = textBounds.width() + padding * 2
                        val tooltipHeight = textBounds.height() + padding * 2

                        val tooltipX = (x - tooltipWidth / 2).coerceIn(0f, width - tooltipWidth)
                        val tooltipY = (y - tooltipHeight - 16.dp.toPx()).coerceAtLeast(0f)

                        // Animate tooltip appearance
                        val tooltipAlpha = pointerAnimationProgress

                        // Draw tooltip background
                        drawRoundRect(
                            color = Color.Black.copy(alpha = 0.8f * tooltipAlpha),
                            topLeft = Offset(tooltipX, tooltipY),
                            size = Size(tooltipWidth, tooltipHeight),
                            cornerRadius = CornerRadius(8.dp.toPx())
                        )

                        // Draw tooltip text
                        textPaint.alpha = (255 * tooltipAlpha).toInt()
                        drawText(
                            valueText,
                            tooltipX + tooltipWidth / 2,
                            tooltipY + tooltipHeight / 2 + textBounds.height() / 2,
                            textPaint
                        )
                    }
                }
            }
        }

        // Draw initial balance line for reference (dotted)
        if (valueRange > 0) {
            val initialBalanceY =
                height - ((Portfolio.INITIAL_BALANCE - minValue) / valueRange * height).toFloat()
            drawLine(
                color = Color.Gray.copy(alpha = 0.5f),
                start = Offset(0f, initialBalanceY),
                end = Offset(width, initialBalanceY),
                strokeWidth = 1.dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(4f, 8f)
                )
            )
        }
    }
}

// Generate mock portfolio history based on current portfolio state and time range
private fun generatePortfolioHistory(portfolio: Portfolio, timeRange: TimeRange): List<Double> {
    val dataPoints = when (timeRange) {
        TimeRange.ONE_DAY -> 24
        TimeRange.ONE_WEEK -> 7
        TimeRange.ONE_YEAR -> 365
        TimeRange.ALL -> 365 // Using 365 data points for 'ALL' for consistency with 1Y
    }

    val currentValue = portfolio.totalPortfolioValue
    val performancePercentage = portfolio.portfolioPerformancePercentage / 100.0

    // If no holdings, return flat line at initial balance
    if (portfolio.holdings.isEmpty()) {
        return (0 until dataPoints).map { Portfolio.INITIAL_BALANCE }
    }

    // Generate realistic-looking portfolio growth curve
    return (0 until dataPoints).map { index ->
        val progress = index.toDouble() / (dataPoints - 1)

        // Add some realistic volatility using Random.nextDouble()
        val volatility =
            kotlin.math.sin(progress * 20) * (currentValue * 0.05) * kotlin.random.Random.nextDouble(
                -1.0,
                1.0
            )

        // Linear interpolation from initial balance to current value
        val baseValue =
            Portfolio.INITIAL_BALANCE + (currentValue - Portfolio.INITIAL_BALANCE) * progress

        // Add volatility but ensure end value matches current
        if (index == dataPoints - 1) {
            currentValue // Ensure last point is exactly current value
        } else {
            (baseValue + volatility).coerceAtLeast(Portfolio.INITIAL_BALANCE * 0.5) // Don't go below 50% of initial
        }
    }
}