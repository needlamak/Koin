package com.koin.ui.coinlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.koin.domain.coin.Coin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinListScreen(
    state: CoinListUiState,
    onEvent: (CoinListUiEvent) -> Unit,
    onCoinClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    
    LaunchedEffect(Unit) {
        searchQuery = state.searchQuery
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crypto Tracker") },
                actions = {
                    IconButton(
                        onClick = { onEvent(CoinListUiEvent.Refresh) },
                        enabled = !state.isRefreshing
                    ) {
                        if (state.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onEvent(CoinListUiEvent.OnSearchQueryChange(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search coins...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { focusManager.clearFocus() }
                )
            )

            // Loading state
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } 
            // Empty state
            else if (state.filteredCoins.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No coins found")
                }
            } 
            // Success state
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.filteredCoins) { coin ->
                        CoinItem(
                            coin = coin,
                            onClick = { onCoinClick(coin.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoinItem(
    coin: Coin,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coin icon
            AsyncImage(
                model = coin.imageUrl,
                contentDescription = "${coin.name} logo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Coin info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = coin.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = coin.symbol.uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Price info
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = coin.formattedPrice,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = coin.formattedPriceChange,
                    color = if (coin.isPositive24h) Color.Green else Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CoinListPreview() {
    MaterialTheme {
        CoinListScreen(
            state = CoinListUiState(
                coins = listOf(
                    Coin(
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
                    )
                ),
                filteredCoins = emptyList(),
                isLoading = false,
                isRefreshing = false,
                error = null
            ),
            onEvent = {},
            onCoinClick = {}
        )
    }
}
