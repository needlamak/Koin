//package com.kyro.ui.theme.presentation.screens
//
//import android.R
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.expandVertically
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.animation.scaleIn
//import androidx.compose.animation.scaleOut
//import androidx.compose.animation.shrinkVertically
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Clear
//import androidx.compose.material.icons.filled.FilterList
//import androidx.compose.material.icons.filled.History
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material.icons.filled.Sort
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Checkbox
//import androidx.compose.material3.DockedSearchBar
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.FilterChip
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.ListItem
//import androidx.compose.material3.ListItemDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.SearchBarDefaults
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.derivedStateOf
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.rotate
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.kyro.ui.theme.CryptoColors
//import com.kyro.ui.theme.discover.DiscoverViewModel
//import com.kyro.ui.theme.discover.SortType
//import com.kyro.ui.theme.home.UpdatedHoldingItem
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DiscoverScreen(
//    onCoinClick: (String) -> Unit,
//    viewModel: DiscoverViewModel = hiltViewModel(),
//    modifier: Modifier = Modifier
//) {
//    val listState = rememberLazyListState()
//    val filteredCoins by viewModel.filteredCoins.collectAsStateWithLifecycle()
//    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
//    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
//    var expanded by rememberSaveable { mutableStateOf(false) }
//    var showFilters by rememberSaveable { mutableStateOf(false) }
//
//    // Filter states
//    var selectedSortType by rememberSaveable { mutableStateOf(SortType.NAME_ASC) }
//    var showOnlyPositiveChange by rememberSaveable { mutableStateOf(false) }
//
//    // Animation for filter icon rotation
//    val filterIconRotation by animateFloatAsState(
//        targetValue = if (showFilters) 180f else 0f,
//        label = "filterRotation"
//    )
//
//    // Calculate which items should be animated based on scroll state
//    val shouldAnimateItems by remember {
//        derivedStateOf {
//            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        viewModel.loadCoins()
//    }
//
//    Column(modifier = modifier.fillMaxSize()
//        .background(Color.Black)) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Modern M3 SearchBar with proper semantics
//            DockedSearchBar(
//                query = searchQuery,
//                onQueryChange = viewModel::updateSearchQuery,
//                onSearch = {
//                    expanded = false
//                    viewModel.performSearch(it)
//                },
//                active = expanded,
//                onActiveChange = { expanded = it },
//                placeholder = {
//                    Text(
//                        "Search coins...",
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//                },
//                leadingIcon = {
//                    Icon(
//                        Icons.Default.Search,
//                        contentDescription = "Search",
//                        tint = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                },
//                trailingIcon = {
//                    Row {
//                        AnimatedVisibility(
//                            visible = searchQuery.isNotEmpty(),
//                            enter = fadeIn() + scaleIn(),
//                            exit = fadeOut() + scaleOut()
//                        ) {
//                            IconButton(
//                                onClick = {
//                                    viewModel.clearSearch()
//                                    if (expanded) expanded = false
//                                }
//                            ) {
//                                Icon(
//                                    Icons.Default.Clear,
//                                    contentDescription = "Clear search",
//                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
//                                )
//                            }
//                        }
//                        IconButton(
//                            onClick = { showFilters = !showFilters }
//                        ) {
//                            Icon(
//                                Icons.Default.FilterList,
//                                contentDescription = "Filter",
//                                modifier = Modifier.rotate(filterIconRotation),
//                                tint = if (showFilters)
//                                    MaterialTheme.colorScheme.primary
//                                else
//                                    MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                        }
//                    }
//                },
//                colors = SearchBarDefaults.colors(
//                    containerColor = CryptoColors.cardBackgroundDark,
//                    dividerColor = MaterialTheme.colorScheme.outline
//                ),
//                modifier = Modifier
//                    .weight(1f)
//            ) {
//                // Show recent searches even when query is empty
//                LazyColumn {
//                    if (searchQuery.isEmpty()) {
//                        // Recent searches
//                        items(viewModel.getRecentSearches()) { recentSearch ->
//                            ListItem(
//                                headlineContent = { Text(recentSearch)},
//                                leadingContent = {
//                                    Icon(
//                                        Icons.Default.History,
//                                        contentDescription = null,
//                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
//                                    )
//                                },
//                                modifier = Modifier.clickable {
//                                    viewModel.updateSearchQuery(recentSearch)
//                                    expanded = false
//                                }
//                            )
//                        }
//                    } else {
//                        // Search suggestions based on input
//                        items(viewModel.getSearchSuggestions(searchQuery)) { suggestion ->
//                            ListItem(
//                                headlineContent = { Text(suggestion) },
//                                leadingContent = {
//                                    Icon(
//                                        Icons.Default.Search,
//                                        contentDescription = null,
//                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
//                                    )
//                                },
//                                modifier = Modifier.clickable {
//                                    viewModel.updateSearchQuery(suggestion)
//                                    expanded = false
//                                },
//                                colors = ListItemDefaults.colors(CryptoColors.cardBackgroundDark)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//
//        // Filter section with animation
//        AnimatedVisibility(
//            visible = showFilters,
//            enter = expandVertically() + fadeIn(),
//            exit = shrinkVertically() + fadeOut()
//        ) {
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                colors = CardDefaults.cardColors(
//                    containerColor = CryptoColors.cardBackgroundDark
//                )
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    Text(
//                        "Sort by",
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 8.dp)
//                    ) {
//                        SortTypeChip(
//                            text = "Name",
//                            selected = selectedSortType in listOf(SortType.NAME_ASC, SortType.NAME_DESC),
//                            ascending = selectedSortType == SortType.NAME_ASC,
//                            onClick = {
//                                selectedSortType = if (selectedSortType == SortType.NAME_ASC)
//                                    SortType.NAME_DESC else SortType.NAME_ASC
//                                viewModel.sortCoins(selectedSortType)
//                            }
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        SortTypeChip(
//                            text = "Price",
//                            selected = selectedSortType in listOf(SortType.PRICE_ASC, SortType.PRICE_DESC),
//                            ascending = selectedSortType == SortType.PRICE_ASC,
//                            onClick = {
//                                selectedSortType = if (selectedSortType == SortType.PRICE_ASC)
//                                    SortType.PRICE_DESC else SortType.PRICE_ASC
//                                viewModel.sortCoins(selectedSortType)
//                            }
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        SortTypeChip(
//                            text = "Change",
//                            selected = selectedSortType in listOf(SortType.CHANGE_ASC, SortType.CHANGE_DESC),
//                            ascending = selectedSortType == SortType.CHANGE_ASC,
//                            onClick = {
//                                selectedSortType = if (selectedSortType == SortType.CHANGE_ASC)
//                                    SortType.CHANGE_DESC else SortType.CHANGE_ASC
//                                viewModel.sortCoins(selectedSortType)
//                            }
//                        )
//                    }
//
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 8.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Checkbox(
//                            checked = showOnlyPositiveChange,
//                            onCheckedChange = {
//                                showOnlyPositiveChange = it
//                                viewModel.filterByPositiveChange(it)
//                            }
//                        )
//                        Text(
//                            "Show only positive change",
//                            style = MaterialTheme.typography.bodyMedium
//                        )
//                    }
//                }
//            }
//        }
//
//        // Main content area
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .weight(1f)
//        ) {
//            when {
//                filteredCoins.isEmpty() && isSearching -> {
//                    EmptySearchState()
//                }
//                else -> {
//                    LazyColumn(
//                        state = listState,
//                        modifier = Modifier.fillMaxSize(),
//                        contentPadding = PaddingValues(vertical = 8.dp)
//                    ) {
//                        items(
//                            items = filteredCoins,
//                            key = { it.id }
//                        ) { coin ->
//                            UpdatedHoldingItem(
//                                coin = coin,
//                                onClick = { onCoinClick(coin.id) },
//                                shouldAnimate = shouldAnimateItems && !isSearching,
//                                animationDelay = filteredCoins.indexOf(coin) * 100,
//                                onAddToFavorites = {
//                                    // In a real app, this would call the ViewModel
//                                    // to update the favorite status in the repository
//                                },
//                                onAddToWatchlist = {
//                                    // In a real app, this would call the ViewModel
//                                    // to update the watchlist status in the repository
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun SortTypeChip(
//    text: String,
//    selected: Boolean,
//    ascending: Boolean,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    FilterChip(
//        selected = selected,
//        onClick = onClick,
//        label = {
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(text)
//                if (selected) {
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Icon(
//                        Icons.Default.Sort,
//                        contentDescription = if (ascending) "Ascending" else "Descending",
//                        modifier = Modifier
//                            .size(16.dp)
//                            .rotate(if (ascending) 0f else 180f)
//                    )
//                }
//            }
//        },
//        modifier = modifier
//    )
//}
//
//@Composable
//private fun EmptySearchState() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(32.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Icon(
//            Icons.Default.Search,
//            contentDescription = null,
//            modifier = Modifier.size(64.dp),
//            tint = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Text(
//            text = "No coins found",
//            style = MaterialTheme.typography.headlineSmall,
//            color = MaterialTheme.colorScheme.onSurface
//        )
//        Text(
//            text = "Try adjusting your search terms",
//            style = MaterialTheme.typography.bodyMedium,
//            color = MaterialTheme.colorScheme.onSurfaceVariant,
//            modifier = Modifier.padding(top = 8.dp)
//        )
//    }
//}
