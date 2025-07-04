package com.koin.ui.coindetail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.koin.domain.coin.Coin
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
                    if (state.isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(
                            onClick = { onEvent(CoinDetailUiEvent.Refresh) },
                            enabled = !state.isLoading
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
                        StatItem("Circulating Supply", "${coin.formattedSupply} ${coin.symbol.uppercase()}")
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

@Preview(showBackground = true)
@Composable
private fun CoinDetailPreview() {
    MaterialTheme {
        CoinDetailScreen(
            state = CoinDetailUiState(
                coinId = "bitcoin",
                coin = Coin(
                    id = "bitcoin",
                    name = "Bitcoin",
                    symbol = "btc",
                    imageUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png",
                    currentPrice = 50000.0,
                    marketCap = 950000000000,
                    marketCapRank = 1,
                    priceChange24h = 1000.0,
                    priceChangePercentage24h = 2.5,
                    priceChangePercentage1h = 0.5,
                    priceChangePercentage7d = 5.0,
                    priceChangePercentage30d = 15.0,
                    sparklineData = null,
                    high24h = 51000.0,
                    low24h = 49000.0,
                    totalVolume = 30000000000.0,
                    circulatingSupply = 18900000.0,
                    totalSupply = 21000000.0,
                    maxSupply = 21000000.0,
                    ath = 69000.0,
                    athDate = "2021-11-10T14:24:11.849Z",
                    atl = 67.81,
                    atlDate = "2013-07-06T00:00:00.000Z"
                ),
                isLoading = false,
                isRefreshing = false,
                error = null
            ),
            onEvent = {},
            onBackClick = {}
        )
    }
}
