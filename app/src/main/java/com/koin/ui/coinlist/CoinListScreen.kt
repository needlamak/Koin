package com.koin.ui.coinlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.koin.domain.model.Coin
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun CoinListScreen(
    state: CoinListUiState,
    onEvent: (CoinListUiEvent) -> Unit,
    onCoinClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope() // For launching scroll coroutine

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showFilters by rememberSaveable { mutableStateOf(false) }

    var selectedSortType by rememberSaveable { mutableStateOf(SortType.NAME_ASC) }
    var showOnlyPositiveChange by rememberSaveable { mutableStateOf(false) }

    val filterIconRotation by animateFloatAsState(
        targetValue = if (showFilters) 180f else 0f,
        label = "filterRotation"
    )

    // State to control toolbar visibility (and now FAB visibility)
    var showToolbarAndFab by remember { mutableStateOf(true) }
    // State to control scroll to top FAB visibility (only appears if scrolled down)
    val showScrollToTopButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }

    LaunchedEffect(state.searchQuery) {
        searchQuery = state.searchQuery
    }

    // Effect for toolbar and FAB hide/show on scroll
    LaunchedEffect(listState) {
        var previousOffset = listState.firstVisibleItemScrollOffset
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .map { currentOffset ->
                // Determine if scrolling down. Add a small buffer to avoid flickering
                currentOffset > previousOffset && currentOffset > 0
            }
            .distinctUntilChanged()
            .collect { isScrollingDown ->
                showToolbarAndFab = !isScrollingDown
                previousOffset = listState.firstVisibleItemScrollOffset
            }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        topBar = {
            AnimatedVisibility(
                visible = showToolbarAndFab, // Controls the entire top bar
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                CoinListToolbar(
                    searchQuery = searchQuery,
                    isSearchActive = isSearchActive,
                    onQueryChange = { newQuery ->
                        searchQuery = newQuery
                        onEvent(CoinListUiEvent.OnSearchQueryChange(newQuery))
                    },
                    onSearch = { _ ->
                        isSearchActive = false
                    },
                    onActiveChange = { active -> isSearchActive = active },
                    onResetSearch = {
                        searchQuery = ""
                        onEvent(CoinListUiEvent.ResetSearch)
                    },
                    onToggleFilters = { showFilters = !showFilters },
                    showFiltersActive = showFilters,
                    filterIconRotation = filterIconRotation,
                    onResetAllFilters = {
                        searchQuery = ""
                        selectedSortType = SortType.NAME_ASC
                        showOnlyPositiveChange = false
                        onEvent(CoinListUiEvent.ResetFilters)
                        showFilters = false
                    },
                    showResetFiltersButton = searchQuery.isNotEmpty() || showOnlyPositiveChange || selectedSortType != SortType.NAME_ASC,
                    filteredCoins = state.filteredCoins
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showToolbarAndFab && showScrollToTopButton, // FAB also hides with toolbar
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                SmallFloatingActionButton( // <<< Changed to SmallFloatingActionButton
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0) // <<< Corrected to scroll to top
                        }
                    }
                ) {
                    Icon(Icons.Filled.ArrowUpward, "Scroll to top")
                }
            }
        }
    ) { paddingValues ->
        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter section with animation (remains same logic, applying the 'modifier = Modifier' fix)
            AnimatedVisibility(
                visible = showFilters,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Sort by",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            SortTypeChip(
                                text = "Name",
                                selected = selectedSortType in listOf(
                                    SortType.NAME_ASC,
                                    SortType.NAME_DESC
                                ),
                                ascending = selectedSortType == SortType.NAME_ASC,
                                onClick = {
                                    selectedSortType = if (selectedSortType == SortType.NAME_ASC)
                                        SortType.NAME_DESC else SortType.NAME_ASC
                                    // TODO: Dispatch sort event to ViewModel if needed
                                },
                                modifier = Modifier // Added this for the fix
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            SortTypeChip(
                                text = "Price",
                                selected = selectedSortType in listOf(
                                    SortType.PRICE_ASC,
                                    SortType.PRICE_DESC
                                ),
                                ascending = selectedSortType == SortType.PRICE_ASC,
                                onClick = {
                                    selectedSortType = if (selectedSortType == SortType.PRICE_ASC)
                                        SortType.PRICE_DESC else SortType.PRICE_ASC
                                    // TODO: Dispatch sort event to ViewModel if needed
                                },
                                modifier = Modifier // Added this for the fix
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            SortTypeChip(
                                text = "Change",
                                selected = selectedSortType in listOf(
                                    SortType.CHANGE_ASC,
                                    SortType.CHANGE_DESC
                                ),
                                ascending = selectedSortType == SortType.CHANGE_ASC,
                                onClick = {
                                    selectedSortType = if (selectedSortType == SortType.CHANGE_ASC)
                                        SortType.CHANGE_DESC else SortType.CHANGE_ASC
                                    // TODO: Dispatch sort event to ViewModel if needed
                                },
                                modifier = Modifier // Added this for the fix
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = showOnlyPositiveChange,
                                onCheckedChange = { showOnlyPositiveChange = it })
                            Text(
                                "Show only positive change",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Main content area with PullToRefreshBox
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { onEvent(CoinListUiEvent.RefreshData) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    when {
                        state.isLoading && state.coins.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        state.error != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Spacer(Modifier.height(20.dp))
                                Text(
                                    text = "Error: ${state.error}",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        state.filteredCoins.isEmpty() && !state.isLoading -> {
                            EmptySearchState()
                        }

                        else -> {

                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 28.dp)
                            ) {
                                item { Spacer(Modifier.height(40.dp)) }
                                items(items = state.filteredCoins, key = { it.id }) { coin ->
                                    CoinItem(coin = coin, onClick = { onCoinClick(coin.id) })
                                }
                            }
                        }
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
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
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

@Composable
private fun SortTypeChip(
    text: String,
    selected: Boolean,
    ascending: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text)
                if (selected) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = if (ascending) "Ascending" else "Descending",
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(if (ascending) 0f else 180f)
                    )
                }
            }
        },
        modifier = modifier
    )
}


@Composable
private fun EmptySearchState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No coins found",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Try adjusting your search terms",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
