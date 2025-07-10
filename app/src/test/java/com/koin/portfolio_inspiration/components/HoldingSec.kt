package com.base.features.portfolio.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.base.core.data.remote.model.CoinMarket
import com.base.features.portfolio.presentation.TimeRange
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UpdatedHoldingItem(
    coin: CoinMarket,
    onClick: () -> Unit,
    shouldAnimate: Boolean,
    animationDelay: Int = 0,
    onAddToFavorites: (() -> Unit)? = null,
    onAddToWatchlist: (() -> Unit)? = null,
    holdingAmount: Double = 0.0, // Add holding amount parameter
    holdingValue: Double = 0.0   // Add holding value parameter
) {
    var visible by remember { mutableStateOf(!shouldAnimate) }
    var showMenu by remember { mutableStateOf(false) }

    if (shouldAnimate) {
        LaunchedEffect(Unit) {
            delay(animationDelay.toLong())
            visible = true
        }
    } else {
        LaunchedEffect(Unit) {
            visible = true
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = if (shouldAnimate) {
            fadeIn(
                animationSpec = tween(300, easing = EaseOutCubic)
            ) + slideInVertically(
                animationSpec = tween(300, easing = EaseOutCubic),
                initialOffsetY = { it / 4 }
            )
        } else {
            fadeIn(animationSpec = tween(0))
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = { showMenu = true }
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Coin Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    AsyncImage(
                        model = coin.image,
                        contentDescription = coin.name,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = coin.name,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Text(
                            text = coin.symbol.uppercase(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Price Info
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = coin.formattedPrice(),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = coin.formattedPriceChange(),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (coin.isPriceChangePositive())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }

                // Holdings Info
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = String.format(Locale.US, "$%.2f", holdingValue),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = String.format(Locale.US, "%.4f %s", holdingAmount, coin.symbol.uppercase()),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Long press menu with scrim
            if (showMenu) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
//                        .background(Color.Black.copy(alpha = 1f))
                        .clickable { showMenu = false }
                )

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Add to Favorites") },
                        onClick = {
                            onAddToFavorites?.invoke()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add to Watchlist") },
                        onClick = {
                            onAddToWatchlist?.invoke()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DropdownMenuButton() {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier
                .background(
                    Color.White.copy(alpha = 0.1f),
                    CircleShape
                )
        ) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = Color.White
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF2A2A2A))
        ) {
            DropdownMenuItem(
                text = { Text("My Meme Coins", color = Color.White) },
                onClick = { expanded = false }
            )
            DropdownMenuItem(
                text = { Text("Settings", color = Color.White) },
                onClick = { expanded = false }
            )
        }
    }
}

// TimeRangeFilterChips.kt
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeRangeFilterChips(
    selectedTimeRange: TimeRange,
    onTimeRangeSelected: (TimeRange) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeRanges = listOf(
        TimeRange.DAY to "1D",
        TimeRange.WEEK to "1W",
        TimeRange.MONTH to "1M",
        TimeRange.YEAR to "1Y",
    )

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(timeRanges) { (timeRange, label) ->
            FilterChip(
                selected = selectedTimeRange == timeRange,
                onClick = { onTimeRangeSelected(timeRange) },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selectedTimeRange == timeRange) Color.White else Color.Gray
                    )
                },
                modifier = Modifier.animateItem(),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.surface,
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            )
        }
    }
}

