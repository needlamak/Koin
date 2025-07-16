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
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.koin.components.BottomNavBar
import com.koin.domain.model.Coin
import com.koin.util.NetworkMonitor
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinListScreen(
    state: CoinListUiState,
    onEvent: (CoinListUiEvent) -> Unit,
    onCoinClick: (String) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var isSearchActive by remember { mutableStateOf(false) }
    var showFilters by rememberSaveable { mutableStateOf(false) }

    // Animate filter icon rotation
    val filterIconRotation by animateFloatAsState(
        targetValue = if (showFilters) 180f else 0f,
        label = "filterRotation"
    )

    // Optimize toolbar visibility logic
    var showToolbarAndFab by remember { mutableStateOf(true) }
    val showScrollToTopButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    // Network monitoring
    val networkMonitor = remember { NetworkMonitor(context) }
    val isNetworkAvailable by networkMonitor.isNetworkAvailable.collectAsState()

    // Optimize scroll detection
    LaunchedEffect(listState) {
        var lastScrollOffset = 0
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }
            .distinctUntilChanged()
            .collect { (index, offset) ->
                val currentScrollOffset = index * 100 + offset // Approximate
                showToolbarAndFab = currentScrollOffset <= lastScrollOffset || currentScrollOffset < 100
                lastScrollOffset = currentScrollOffset
            }
    }

    val sheetState = rememberModalBottomSheetState()

    // Modal bottom sheets
    if (state.showBuyDialog) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(CoinListUiEvent.HideBuyDialog) },
            sheetState = sheetState
        ) {
            state.selectedCoinForBuy?.let { coin ->
                BuyBottomSheet(
                    coin = coin,
                    onConfirm = { amount ->
                        onEvent(CoinListUiEvent.BuyCoin(coin, amount))
                    }
                )
            }
        }
    }

    if (state.showBuySuccessBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(CoinListUiEvent.HideBuySuccessBottomSheet) },
            sheetState = sheetState
        ) {
            state.buyTransactionDetails?.let { details ->
                BuySuccessBottomSheet(
                    coinName = details.coinName,
                    coinSymbol = details.coinSymbol,
                    coinImage = details.coinImage,
                    quantity = details.quantity,
                    totalPrice = details.totalPrice,
                    onDismiss = { onEvent(CoinListUiEvent.HideBuySuccessBottomSheet) }
                )
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            AnimatedVisibility(
                visible = showToolbarAndFab,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                CoinListToolbar(
                    searchQuery = state.searchQuery,
                    isSearchActive = isSearchActive,
                    onQueryChange = { newQuery ->
                        onEvent(CoinListUiEvent.OnSearchQueryChange(newQuery))
                    },
                    onSearch = { isSearchActive = false },
                    onActiveChange = { active -> isSearchActive = active },
                    onResetSearch = {
                        onEvent(CoinListUiEvent.ResetSearch)
                    },
                    onToggleFilters = { showFilters = !showFilters },
                    showFiltersActive = showFilters,
                    filterIconRotation = filterIconRotation,
                    onResetAllFilters = {
                        onEvent(CoinListUiEvent.ResetFilters)
                        showFilters = false
                    },
                    showResetFiltersButton = state.searchQuery.isNotEmpty() ||
                            state.showOnlyPositiveChange ||
                            state.sortType != SortType.NAME_ASC,
                    filteredCoins = state.filteredCoins
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showToolbarAndFab && showScrollToTopButton,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
                ) {
                    Icon(Icons.Filled.ArrowUpward, "Scroll to top")
                }
            }
        },
        bottomBar = {
            BottomNavBar(navController = navController as NavHostController)
        }
    ) { paddingValues ->
        val paddingVal = paddingValues
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp)
        ) {
            // Filter section
            AnimatedVisibility(
                visible = showFilters,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                FilterSection(
                    state = state,
                    onEvent = onEvent
                )
            }

            // Main content area
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
                        state.isLoading -> {
                            LoadingState()
                        }
                        state.error != null && state.coins.isEmpty() -> {
                            ErrorEmptyState(
                                error = state.error,
                                isNetworkAvailable = isNetworkAvailable,
                                onRetry = { onEvent(CoinListUiEvent.RefreshData) }
                            )
                        }
                        state.coins.isEmpty() -> {
                            DataEmptyState(
                                isNetworkAvailable = isNetworkAvailable,
                                onRetry = { onEvent(CoinListUiEvent.RefreshData) }
                            )
                        }
                        state.filteredCoins.isEmpty() -> {
                            EmptySearchState()
                        }
                        else -> {
                            OptimizedCoinList(
                                coins = state.filteredCoins,
                                listState = listState,
                                onCoinClick = onCoinClick,
                                onEvent = onEvent
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    state: CoinListUiState,
    onEvent: (CoinListUiEvent) -> Unit
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
                    selected = state.sortType in listOf(SortType.NAME_ASC, SortType.NAME_DESC),
                    ascending = state.sortType == SortType.NAME_ASC,
                    onClick = {
                        val newSortType = if (state.sortType == SortType.NAME_ASC)
                            SortType.NAME_DESC else SortType.NAME_ASC
                        onEvent(CoinListUiEvent.UpdateFilters(sortType = newSortType))
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                SortTypeChip(
                    text = "Price",
                    selected = state.sortType in listOf(SortType.PRICE_ASC, SortType.PRICE_DESC),
                    ascending = state.sortType == SortType.PRICE_ASC,
                    onClick = {
                        val newSortType = if (state.sortType == SortType.PRICE_ASC)
                            SortType.PRICE_DESC else SortType.PRICE_ASC
                        onEvent(CoinListUiEvent.UpdateFilters(sortType = newSortType))
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                SortTypeChip(
                    text = "Change",
                    selected = state.sortType in listOf(SortType.CHANGE_ASC, SortType.CHANGE_DESC),
                    ascending = state.sortType == SortType.CHANGE_ASC,
                    onClick = {
                        val newSortType = if (state.sortType == SortType.CHANGE_ASC)
                            SortType.CHANGE_DESC else SortType.CHANGE_ASC
                        onEvent(CoinListUiEvent.UpdateFilters(sortType = newSortType))
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.showOnlyPositiveChange,
                    onCheckedChange = {
                        onEvent(CoinListUiEvent.UpdateFilters(showOnlyPositiveChange = it))
                    }
                )
                Text(
                    "Show only positive change",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun OptimizedCoinList(
    coins: List<Coin>,
    listState: LazyListState,
    onCoinClick: (String) -> Unit,
    onEvent: (CoinListUiEvent) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 28.dp)
    ) {
        item { Spacer(Modifier.height(20.dp)) }
        items(
            items = coins,
            key = { coin -> coin.id }
        ) { coin ->
            OptimizedCoinItem(
                coin = coin,
                onClick = { onCoinClick(coin.id) },
                onToggleWatchlist = { onEvent(CoinListUiEvent.ToggleWatchlist(coin)) },
                onBuyClick = { onEvent(CoinListUiEvent.ShowBuyDialog(coin)) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalWearMaterialApi::class)
@Composable
private fun OptimizedCoinItem(
    coin: Coin,
    onClick: () -> Unit,
    onToggleWatchlist: () -> Unit,
    onBuyClick: () -> Unit
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val sizePx = with(LocalDensity.current) { 160.dp.toPx() }
    val anchors = mapOf(0f to 0, -sizePx to 1)

    // Memoize expensive calculations
    val priceColor = remember(coin.isPositive24h) {
        if (coin.isPositive24h) Color.Green else Color.Red
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        // Background with actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleWatchlist,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.StarBorder,
                    contentDescription = "Add to watchlist",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(
                onClick = onBuyClick,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Buy Coin",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Main card content
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
                    orientation = Orientation.Horizontal
                ),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Coin icon with better loading
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(coin.imageUrl)
                        .memoryCacheKey(coin.id)
                        .diskCacheKey(coin.id)
                        .build(),
                    contentDescription = "${coin.name} logo",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Coin info
                Column(modifier = Modifier.weight(1f)) {
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
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = coin.formattedPrice,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = coin.formattedPriceChange,
                        color = priceColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CoinListScreen(
//    state: CoinListUiState,
//    onEvent: (CoinListUiEvent) -> Unit,
//    onCoinClick: (String) -> Unit,
//    navController: NavController,
//    modifier: Modifier = Modifier
//) {
//
//    val context = LocalContext.current
//    val listState = rememberLazyListState()
//    val coroutineScope = rememberCoroutineScope()
//
//    var isSearchActive by remember { mutableStateOf(false) }
//    var showFilters by rememberSaveable { mutableStateOf(false) }
//    var showOnlyPositiveChange by rememberSaveable { mutableStateOf(false) }
//
//    val filterIconRotation by animateFloatAsState(
//        targetValue = if (showFilters) 180f else 0f,
//        label = "filterRotation"
//    )
//
//    var showToolbarAndFab by remember { mutableStateOf(true) }
//    val showScrollToTopButton by remember {
//        derivedStateOf {
//            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
//        }
//    }
//
//    // Network state
//    val networkMonitor = remember { NetworkMonitor(context) }
//    val isNetworkAvailable by networkMonitor.isNetworkAvailable.collectAsState()
//
//    // Hide toolbar and FAB on scroll down
//    LaunchedEffect(listState) {
//        var previousOffset = listState.firstVisibleItemScrollOffset
//        snapshotFlow { listState.firstVisibleItemScrollOffset }
//            .map { currentOffset ->
//                currentOffset > previousOffset && currentOffset > 0
//            }
//            .distinctUntilChanged()
//            .collect { isScrollingDown ->
//                showToolbarAndFab = !isScrollingDown
//                previousOffset = listState.firstVisibleItemScrollOffset
//            }
//    }
//
//
//    val sheetState = rememberModalBottomSheetState()
//
//    if (state.showBuyDialog) {
//        ModalBottomSheet(
//            onDismissRequest = { onEvent(CoinListUiEvent.HideBuyDialog) },
//            sheetState = sheetState
//        ) {
//            state.selectedCoinForBuy?.let { coin ->
//                BuyBottomSheet(
//                    coin = coin,
//                    onConfirm = { amount ->
//                        onEvent(CoinListUiEvent.BuyCoin(coin, amount))
//                    }
//                )
//            }
//        }
//    }
//
//    if (state.showBuySuccessBottomSheet) {
//        ModalBottomSheet(
//            onDismissRequest = { onEvent(CoinListUiEvent.HideBuySuccessBottomSheet) },
//            sheetState = sheetState
//        ) {
//            state.buyTransactionDetails?.let { details ->
//                BuySuccessBottomSheet(
//                    coinName = details.coinName,
//                    coinSymbol = details.coinSymbol,
//                    coinImage = details.coinImage,
//                    quantity = details.quantity,
//                    totalPrice = details.totalPrice,
//                    onDismiss = { onEvent(CoinListUiEvent.HideBuySuccessBottomSheet) }
//                )
//            }
//        }
//    }
//
//    Scaffold(
//        modifier = modifier
//            .fillMaxSize()
//            .statusBarsPadding(),
//        topBar = {
//            AnimatedVisibility(
//                visible = showToolbarAndFab,
//                enter = slideInVertically() + fadeIn(),
//                exit = slideOutVertically() + fadeOut()
//            ) {
//                CoinListToolbar(
//                    searchQuery = state.searchQuery,
//                    isSearchActive = isSearchActive,
//                    onQueryChange = { newQuery ->
//                        onEvent(CoinListUiEvent.OnSearchQueryChange(newQuery))
//                    },
//                    onSearch = { isSearchActive = false },
//                    onActiveChange = { active -> isSearchActive = active },
//                    onResetSearch = {
//                        onEvent(CoinListUiEvent.ResetSearch)
//                    },
//                    onToggleFilters = { showFilters = !showFilters },
//                    showFiltersActive = showFilters,
//                    filterIconRotation = filterIconRotation,
//                    onResetAllFilters = {
//                        onEvent(CoinListUiEvent.ResetFilters)
//                        showFilters = false
//                    },
//                    showResetFiltersButton = state.searchQuery.isNotEmpty() || state.showOnlyPositiveChange || state.sortType != SortType.NAME_ASC,
//                    filteredCoins = state.filteredCoins
//                )
//            }
//        },
//        floatingActionButton = {
//            AnimatedVisibility(
//                visible = showToolbarAndFab && showScrollToTopButton,
//                enter = fadeIn() + scaleIn(),
//                exit = fadeOut() + scaleOut()
//            ) {
//                SmallFloatingActionButton(
//                    onClick = {
//                        coroutineScope.launch {
//                            listState.animateScrollToItem(0)
//                        }
//                    }
//                ) {
//                    Icon(Icons.Filled.ArrowUpward, "Scroll to top")
//                }
//            }
//        },
//        bottomBar = {
//            BottomNavBar(navController = navController as NavHostController)
//        }
//    ) { paddingValues ->
//        val paddingValues = paddingValues
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(vertical = 10.dp)
//        ) {
//            // Filter section
//            AnimatedVisibility(
//                visible = showFilters,
//                enter = expandVertically() + fadeIn(),
//                exit = shrinkVertically() + fadeOut()
//            ) {
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp),
//                    colors = CardDefaults.cardColors(
//                        containerColor = MaterialTheme.colorScheme.surfaceVariant
//                    )
//                ) {
//                    Column(modifier = Modifier.padding(16.dp)) {
//                        Text(
//                            "Sort by",
//                            style = MaterialTheme.typography.titleMedium,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 8.dp)
//                        ) {
//                            SortTypeChip(
//                                text = "Name",
//                                selected = state.sortType in listOf(
//                                    SortType.NAME_ASC,
//                                    SortType.NAME_DESC
//                                ),
//                                ascending = state.sortType == SortType.NAME_ASC,
//                                onClick = {
//                                    val newSortType =
//                                        if (state.sortType == SortType.NAME_ASC) SortType.NAME_DESC else SortType.NAME_ASC
//                                    onEvent(CoinListUiEvent.UpdateFilters(sortType = newSortType))
//                                }
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            SortTypeChip(
//                                text = "Price",
//                                selected = state.sortType in listOf(
//                                    SortType.PRICE_ASC,
//                                    SortType.PRICE_DESC
//                                ),
//                                ascending = state.sortType == SortType.PRICE_ASC,
//                                onClick = {
//                                    val newSortType =
//                                        if (state.sortType == SortType.PRICE_ASC) SortType.PRICE_DESC else SortType.PRICE_ASC
//                                    onEvent(CoinListUiEvent.UpdateFilters(sortType = newSortType))
//                                }
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            SortTypeChip(
//                                text = "Change",
//                                selected = state.sortType in listOf(
//                                    SortType.CHANGE_ASC,
//                                    SortType.CHANGE_DESC
//                                ),
//                                ascending = state.sortType == SortType.CHANGE_ASC,
//                                onClick = {
//                                    val newSortType =
//                                        if (state.sortType == SortType.CHANGE_ASC) SortType.CHANGE_DESC else SortType.CHANGE_ASC
//                                    onEvent(CoinListUiEvent.UpdateFilters(sortType = newSortType))
//                                }
//                            )
//                        }
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 8.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Checkbox(
//                                checked = state.showOnlyPositiveChange,
//                                onCheckedChange = {
//                                    onEvent(CoinListUiEvent.UpdateFilters(showOnlyPositiveChange = it))
//                                }
//                            )
//                            Text(
//                                "Show only positive change",
//                                style = MaterialTheme.typography.bodyMedium,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                        }
//                    }
//                }
//            }
//
//            // Main content area
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .weight(1f)
//            ) {
//                PullToRefreshBox(
//                    isRefreshing = state.isRefreshing,
//                    onRefresh = { onEvent(CoinListUiEvent.RefreshData) },
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    when {
//                        // Show loading if loading is in progress and no data
//                        state.isLoading -> {
//                            LoadingState()
//                        }
//
//                        // Show error if there's an error and no coins
//                        state.error != null && state.coins.isEmpty() -> {
//                            ErrorEmptyState(
//                                error = state.error,
//                                isNetworkAvailable = isNetworkAvailable,
//                                onRetry = { onEvent(CoinListUiEvent.RefreshData) }
//                            )
//                        }
//
//                        // Show empty data state only if data is empty and there is no error
//                        state.coins.isEmpty() -> {
//                            DataEmptyState(
//                                isNetworkAvailable = isNetworkAvailable,
//                                onRetry = { onEvent(CoinListUiEvent.RefreshData) }
//                            )
//                        }
//
//                        // Show empty filtered state if the full list isn't empty, but search returns nothing
//                        state.filteredCoins.isEmpty() -> {
//                            EmptySearchState()
//                        }
//
//                        // Finally, show list if all above are false
//                        else -> {
//                            LazyColumn(
//                                state = listState,
//                                modifier = Modifier.fillMaxSize(),
//                                contentPadding = PaddingValues(vertical = 28.dp)
//                            ) {
//                                item { Spacer(Modifier.height(20.dp)) }
//                                items(items = state.filteredCoins, key = { it.id }) { coin ->
//                                    CoinItem(
//                                        coin = coin,
//                                        onClick = { onCoinClick(coin.id) },
//                                        onToggleWatchlist = {
//                                            onEvent(
//                                                CoinListUiEvent.ToggleWatchlist(
//                                                    it
//                                                )
//                                            )
//                                        },
//                                        onBuyClick = {
//                                            onEvent(CoinListUiEvent.ShowBuyDialog(coin))
//                                        }
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalWearMaterialApi::class)
//@Composable
//private fun CoinItem(
//    coin: Coin,
//    onClick: () -> Unit,
//    onToggleWatchlist: (Coin) -> Unit,
//    onBuyClick: () -> Unit
//) {
//    val swipeableState = rememberSwipeableState(initialValue = 0)
//    val sizePx = with(LocalDensity.current) { 160.dp.toPx() } // Increased size for two icons
//    val anchors = mapOf(0f to 0, -sizePx to 1) // 0 = closed, 1 = swiped
//
//    Box(
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        // Background with star and buy buttons (revealed when swiped)
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(72.dp),
//            horizontalArrangement = Arrangement.End,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            IconButton(
//                onClick = { onToggleWatchlist(coin) },
//                modifier = Modifier
//                    .size(80.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Outlined.StarBorder,
//                    contentDescription = "Add to watchlist",
//                    tint = Color(0xFFFFD700),
//                    modifier = Modifier.size(24.dp)
//                )
//            }
//            IconButton(
//                onClick = onBuyClick,
//                modifier = Modifier
//                    .size(80.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.ShoppingCart,
//                    contentDescription = "Buy Coin",
//                    tint = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.size(24.dp)
//                )
//            }
//        }
//
//        // Main card content
//        Card(
//            onClick = onClick,
//            modifier = Modifier
//                .fillMaxWidth()
//                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
//                .swipeable(
//                    state = swipeableState,
//                    anchors = anchors,
//                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
//                    orientation = Orientation.Horizontal
//                ),
//            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // Coin icon
//                AsyncImage(
//                    model = coin.imageUrl,
//                    contentDescription = "${coin.name} logo",
//                    modifier = Modifier
//                        .size(40.dp)
//                        .clip(CircleShape)
//                )
//
//                Spacer(modifier = Modifier.width(16.dp))
//
//                // Coin info
//                Column(
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text(
//                        text = coin.name,
//                        style = MaterialTheme.typography.titleMedium,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                    Text(
//                        text = coin.symbol.uppercase(),
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//
//                // Price info
//                Column(
//                    horizontalAlignment = Alignment.End
//                ) {
//                    Text(
//                        text = coin.formattedPrice,
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                    Text(
//                        text = coin.formattedPriceChange,
//                        color = if (coin.isPositive24h) Color.Green else Color.Red,
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            }
//        }
//    }
//}


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
                        Icons.AutoMirrored.Filled.Sort,
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