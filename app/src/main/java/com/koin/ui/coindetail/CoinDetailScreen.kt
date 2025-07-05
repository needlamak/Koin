package com.koin.ui.coindetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var selectedTimeRange by remember { mutableStateOf(TimeRange.ONE_DAY) }
    val coin = state.coin
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = coin?.name ?: "Loading...",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        coin?.marketCapRank?.let { rank ->
                            Text(
                                text = "#$rank",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
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
                    IconButton(
                        onClick = { /* TODO: Add to watchlist */ },
                        enabled = coin != null
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Add to watchlist",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    if (state.isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(
                            onClick = { onEvent(CoinDetailUiEvent.Refresh) },
                            enabled = !state.isLoading && coin != null
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
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
                    // Price Section
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = coin.formattedPrice,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${if (coin.isPositive24h) "+" else ""}${String.format("Locale.US", "%.2f", coin.priceChangePercentage24h)}% (24H)",
                                color = if (coin.isPositive24h) Color.Green else Color.Red,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                    
                    // Price Chart
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Generate some sample price data for the chart
                    val priceHistory = remember(coin) {
                        val basePrice = coin.currentPrice
                        List(30) { index ->
                            basePrice * (1 + (Math.random() * 0.1 - 0.05).toFloat())
                        }
                    }
                    
                    PriceChart(
                        prices = priceHistory,
                        lineColor = if (coin.isPositive24h) Color.Green else Color.Red,
                        gradientColors = if (coin.isPositive24h) {
                            listOf(
                                Color.Green.copy(alpha = 0.3f),
                                Color.Green.copy(alpha = 0.05f)
                            )
                        } else {
                            listOf(
                                Color.Red.copy(alpha = 0.3f),
                                Color.Red.copy(alpha = 0.05f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    
                    // Time Range Selector
                    Spacer(modifier = Modifier.height(8.dp))
                    TimeRangeSelector(
                        selectedRange = selectedTimeRange,
                        onRangeSelected = { selectedTimeRange = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Stats Section
                    Text(
                        "Statistics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Stats Grid
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // First row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatItem("Market Cap", coin.formattedMarketCap, Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(16.dp))
                            StatItem("Volume (24h)", coin.formattedVolume, Modifier.weight(1f))
                        }
                        
                        // Second row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            coin.maxSupply?.let { maxSupply ->
                                StatItem("Max Supply", "${formatLargeNumber(maxSupply.toLong())} ${coin.symbol.uppercase()}", 
                                    Modifier.weight(1f))
                            } ?: StatItem("Circulating Supply", "${coin.formattedSupply} ${coin.symbol.uppercase()}", 
                                Modifier.weight(1f))
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            coin.high24h?.let { high ->
                                StatItem("24h High", formatCurrency(high), Modifier.weight(1f))
                            } ?: coin.low24h?.let { low ->
                                StatItem("24h Low", formatCurrency(low), Modifier.weight(1f))
                            } ?: Spacer(modifier = Modifier.weight(1f))
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
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TimeRangeSelector(
    selectedRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeRanges = TimeRange.values()
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        timeRanges.forEach { range ->
            val isSelected = range == selectedRange
            val buttonColors = ButtonDefaults.buttonColors(
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
            )
            
            Button(
                onClick = { onRangeSelected(range) },
                colors = buttonColors,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = range.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

enum class TimeRange(val displayName: String) {
    ONE_HOUR("1H"),
    ONE_DAY("1D"),
    ONE_WEEK("1W"),
    ONE_MONTH("1M"),
    ONE_YEAR("1Y"),
    ALL("ALL")
}

private fun formatLargeNumber(number: Long): String {
    return when {
        number >= 1_000_000_000_000 -> "${String.format("%.2f", number / 1_000_000_000_000.0)}T"
        number >= 1_000_000_000 -> "${String.format("%.2f", number / 1_000_000_000.0)}B"
        number >= 1_000_000 -> "${String.format("%.2f", number / 1_000_000.0)}M"
        number >= 1_000 -> "${String.format("%.1f", number / 1_000.0)}K"
        else -> number.toString()
    }
}

private fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(amount)
}
