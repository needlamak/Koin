package com.koin.ui.coindetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.koin.data.coin.TimeRange
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailScreen(
    state: CoinDetailUiState,
    onEvent: (CoinDetailUiEvent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coin = state.coin

    // Sync selected time range with state
    val selectedTimeRange = state.selectedTimeRange

    // Handle time range changes
    fun onTimeRangeSelected(timeRange: TimeRange) {
        onEvent(CoinDetailUiEvent.TimeRangeSelected(timeRange))
    }

    // Extract prices from historical data
    val priceHistory = state.historicalData.map { it.price }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        coin?.name ?: "Loading...",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Refresh button
                    IconButton(
                        onClick = { onEvent(CoinDetailUiEvent.Refresh) },
                        enabled = !state.isRefreshing
                    ) {
                        if (state.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            coin == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Coin not found")
                }
            }

            else -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Header Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Coin icon and symbol
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            AsyncImage(
                                model = coin.imageUrl,
                                contentDescription = "${coin.name} logo",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = coin.symbol.uppercase(),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        // Price and change
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = coin.formattedPrice,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = coin.formattedPriceChange,
                                color = if (coin.isPositive24h) Color.Green else Color.Red,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    // Price Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            //.padding(8.dp)
                    ) {
                        when {
                            state.isLoadingHistoricalData -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            priceHistory.isNotEmpty() -> {
                                val isPositive = priceHistory.last() > priceHistory.first()
                                PriceChart(
                                    prices = priceHistory,
                                    modifier = Modifier.fillMaxSize(),
                                    lineColor = if (isPositive) Color.Green else Color.Red,
                                    gradientColors = listOf(
                                        if (isPositive)
                                            Color.Green.copy(alpha = 0.3f)
                                        else
                                            Color.Red.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            }

                            !coin.sparklineData.isNullOrEmpty() -> {
                                PriceChart(
                                    prices = coin.sparklineData,
                                    modifier = Modifier.fillMaxSize(),
                                    lineColor = if (coin.isPositive24h) Color.Green else Color.Red,
                                    gradientColors = listOf(
                                        if (coin.isPositive24h)
                                            Color.Green.copy(alpha = 0.2f)
                                        else
                                            Color.Red.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            }

                            else -> {
                                Text(
                                    "No price data available",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Time Range Selector
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        item {

                            TimeRange.entries.forEach { timeRange ->
                                TimeRangeButton(
                                    text = timeRange.name,
                                    isSelected = selectedTimeRange == timeRange,
                                    onClick = { onTimeRangeSelected(timeRange) }
                                )
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(24.dp))

                    // Market Stats
                    Text(
                        "Market Stats",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Stats Grid
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatItem("Market Cap", coin.formattedMarketCap)
                        StatItem("24h Trading Volume", coin.formattedVolume)
                        StatItem(
                            "Circulating Supply",
                            "${coin.formattedSupply} ${coin.symbol.uppercase()}"
                        )
                        coin.supplyPercentage?.let { percentage ->
                            StatItem("Supply Percentage", "${String.format("%.2f", percentage)}%")
                        }
                        coin.high24h?.let { high ->
                            StatItem("24h High", formatCurrency(high))
                        }
                        coin.low24h?.let { low ->
                            StatItem("24h Low", formatCurrency(low))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // About Section
                    Text(
                        "About ${coin.name}",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Additional details can be added here
                    // For now, just show some placeholder text
                    Text(
                        "${coin.name} is a decentralized digital currency, without a central bank or single administrator, " +
                                "that can be sent from user to user on the peer-to-peer bitcoin network without the need for intermediaries.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(amount)
}

@Composable
private fun TimeRangeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        ),
        modifier = modifier
            .height(32.dp)
            .padding(horizontal = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}

//
@Composable
private fun PriceChart(
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

    val selectedIndex = remember { mutableStateOf<Int?>(null) }

    // Touch responsiveness: update selectedIndex on tap or drag
    val pointerModifier = Modifier.pointerInput(prices) {
        detectTapGestures { offset ->
            val width = size.width
            val step = width / (prices.size - 1).coerceAtLeast(1)
            val index = (offset.x / step).toInt().coerceIn(0, prices.lastIndex)
            selectedIndex.value = index
        }
        detectDragGestures { change, _ ->
            val width = size.width
            val step = width / (prices.size - 1).coerceAtLeast(1)
            val index = (change.position.x / step).toInt().coerceIn(0, prices.lastIndex)
            selectedIndex.value = index
        }
    }

    Canvas(
        modifier = modifier
            .then(pointerModifier)
            .padding(vertical = 8.dp)
            .clip(MaterialTheme.shapes.medium)
    ) {
        if (prices.isEmpty() || priceRange == 0.0) return@Canvas

        val width = size.width
        val height = size.height
        val step = width / (prices.size - 1).coerceAtLeast(1)

        // Draw gradient background
        drawRect(
            brush = Brush.verticalGradient(
                colors = gradientColors,
                startY = 0f,
                endY = height
            ),
            size = size
        )

        // Draw price line
        val path = Path().apply {
            prices.forEachIndexed { index, price ->
                val x = index * step
                val y = height - ((price - minPrice) / priceRange * height).toFloat()

                if (index == 0) {
                    moveTo(x, y)
                } else {
                    lineTo(x, y)
                }
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw selected point
        selectedIndex.value?.let { index ->
            if (index in prices.indices) {
                val x = index * step
                val y = height - ((prices[index] - minPrice) / priceRange * height).toFloat()

                drawCircle(
                    color = lineColor,
                    radius = 8.dp.toPx(),
                    center = Offset(x, y)
                )

                // Draw price label
                drawContext.canvas.nativeCanvas.apply {
                    val priceText = "$${String.format("%.4f", prices[index])}"
                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.CENTER
                        setShadowLayer(4f, 0f, 0f, android.graphics.Color.BLACK)
                    }

                    val textBounds = android.graphics.Rect()
                    textPaint.getTextBounds(priceText, 0, priceText.length, textBounds)

                    val textX = x.coerceIn(0f, width - textBounds.width() - 16.dp.toPx())
                    val textY = (y - 12.dp.toPx()).coerceAtLeast(
                        textBounds.height().toFloat() + 8.dp.toPx()
                    )

                    drawText(
                        priceText,
                        textX,
                        textY,
                        textPaint
                    )
                }
            }
        }
    }
}