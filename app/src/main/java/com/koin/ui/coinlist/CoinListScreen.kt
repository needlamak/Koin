package com.koin.ui.coinlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
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
import androidx.compose.ui.text.style.TextOverflow
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
    var isSearchActive by remember { mutableStateOf(false) }
    
    // Sample search suggestions
    val searchSuggestions = remember {
        listOf("Bitcoin", "Ethereum", "Solana", "Cardano", "Polkadot")
    }
    
    LaunchedEffect(Unit) {
        searchQuery = state.searchQuery
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Koin") },
                actions = {
                    // Filter chip
                    FilterChip(
                        selected = false,
                        onClick = { /* TODO: Implement filter */ },
                        label = { Text("Filters") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Modern Search Bar
            DockedSearchBar(
                query = searchQuery,
                onQueryChange = { 
                    searchQuery = it
                    onEvent(CoinListUiEvent.OnSearchQueryChange(it))
                },
                onSearch = { isSearchActive = false },
                active = isSearchActive,
                onActiveChange = { isSearchActive = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                ),
                placeholder = { Text("Search coins...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            ) {
                // Search suggestions
                searchSuggestions.forEach { suggestion ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                searchQuery = suggestion
                                onEvent(CoinListUiEvent.OnSearchQueryChange(suggestion))
                                isSearchActive = false
                            },
                        headlineContent = { Text(suggestion) },
                        leadingContent = {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }


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
