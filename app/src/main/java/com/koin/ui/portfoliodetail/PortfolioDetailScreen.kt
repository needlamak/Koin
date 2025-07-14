package com.koin.ui.portfoliodetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.koin.data.coin.TimeRange
import com.koin.domain.model.Coin
import com.koin.ui.coindetail.EnhancedPriceChart
import com.koin.ui.pricealert.CreatePriceAlertDialog
import com.koin.ui.pricealert.PriceAlertViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioDetailScreen(
    state: PortfolioDetailUiState,
    onEvent: (PortfolioDetailUiEvent) -> Unit,
    onBackClick: () -> Unit,
    navigateToTransactionSuccess: () -> Unit,
    priceAlertViewModel: PriceAlertViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val portfolioCoin = state.portfolioCoin
    val selectedTimeRange = state.selectedTimeRange
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    val priceAlertState by priceAlertViewModel.uiState.collectAsState()
    val createAlertState by priceAlertViewModel.createAlertState.collectAsState()

    var showSellSuccessSheet by remember { mutableStateOf(false) }

    LaunchedEffect(state.transactionSuccess) {
        if (state.transactionSuccess) {
            showSellSuccessSheet = true
            onEvent(PortfolioDetailUiEvent.ClearToast) // Clear the transaction success state
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { errorMessage ->
            println("Error: $errorMessage")
            onEvent(PortfolioDetailUiEvent.ClearToast) // Clear the error state
        }
    }

    val scrollProgress = (scrollState.value / 500f).coerceIn(0f, 1f)
    val headerAlpha = 1f - scrollProgress
    val topBarContentAlpha = scrollProgress

    fun onTimeRangeSelected(timeRange: TimeRange) {
        onEvent(PortfolioDetailUiEvent.TimeRangeSelected(timeRange))
    }

    val priceHistory = state.historicalData.map { it.price }

    if (priceAlertState.showCreateDialog && priceAlertState.selectedCoin != null) {
        CreatePriceAlertDialog(
            coin = priceAlertState.selectedCoin!!,
            createAlertState = createAlertState,
            onDismiss = { priceAlertViewModel.hideCreateAlertDialog() },
            onTargetPriceChange = { priceAlertViewModel.updateTargetPrice(it) },
            onAlertTypeChange = { priceAlertViewModel.updateAlertType(it) },
            onCreateAlert = { priceAlertViewModel.createAlert() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.alpha(topBarContentAlpha)
                    ) {
                        AnimatedVisibility(
                            visible = scrollProgress > 0.3f,
                            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
                            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                        ) {
                            AsyncImage(
                                model = portfolioCoin?.coinImageUrl,
                                contentDescription = "${portfolioCoin?.coinName} logo",
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(18.dp))
                        }

                        Column {
                            Text(
                                portfolioCoin?.coinName ?: "Loading...",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            AnimatedVisibility(
                                visible = scrollProgress > 0.5f,
                                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                                exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                            ) {
                                Text(
                                    text = portfolioCoin?.formattedCurrentValue ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (portfolioCoin?.unrealizedPnL ?: 0.0 >= 0) Color.Green else Color.Red
                                )
                            }
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
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            portfolioCoin?.let {
                HorizontalFloatingToolBar(
                    onSellClick = {
                        onEvent(
                            PortfolioDetailUiEvent.SellCoin(
                                coinId = it.coinId,
                                quantity = it.quantity,
                                pricePerCoin = it.currentPrice
                            )
                        )
                        navigateToTransactionSuccess()
                    },
                    onCreatePriceAlertClick = {
                        val coin = Coin(
                            id = it.coinId,
                            symbol = it.coinSymbol,
                            name = it.coinName,
                            imageUrl = it.coinImageUrl,
                            currentPrice = it.currentPrice,
                            marketCap = 0,
                            marketCapRank = 0,
                            priceChange24h = 0.0,
                            priceChangePercentage24h = 0.0,
                            priceChangePercentage1h = 0.0,
                            priceChangePercentage7d = 0.0,
                            priceChangePercentage30d = 0.0,
                            sparklineData = null,
                            high24h = 0.0,
                            low24h = 0.0,
                            totalVolume = 0.0,
                            circulatingSupply = 0.0,
                            totalSupply = 0.0,
                            maxSupply = 0.0,
                            ath = 0.0,
                            athDate = "",
                            atl = 0.0,
                            atlDate = ""
                        )
                        priceAlertViewModel.showCreateAlertDialog(coin)
                    },
                    onBuyClick = {
                        // TODO: Navigate to Buy Screen
                    }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading || state.isLoadingHistoricalData, // Assuming isLoading also covers refreshing
            onRefresh = { onEvent(PortfolioDetailUiEvent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                portfolioCoin == null -> {
                    // Handled by error state and Snackbar
                }

                else -> {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(8.dp)
                    ) {
                        // Header Section
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(headerAlpha)
                                .scale(1f - (scrollProgress * 0.1f)),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                AsyncImage(
                                    model = portfolioCoin.coinImageUrl,
                                    contentDescription = "${portfolioCoin.coinName} logo",
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = portfolioCoin.coinSymbol.uppercase(),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = portfolioCoin.formattedCurrentValue,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = portfolioCoin.formattedUnrealizedPnL,
                                    color = if (portfolioCoin.isPositiveUnrealizedPnL) Color.Green else Color.Red,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))

                        // Portfolio Value Chart
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .background(MaterialTheme.colorScheme.background),
                            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
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
                                        EnhancedPriceChart(
                                            prices = priceHistory,
                                            modifier = Modifier.fillMaxSize(),
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
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        // Time Range Selector
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            items(TimeRange.entries) { timeRange ->
                                TimeRangeButton(
                                    text = timeRange.displayName,
                                    isSelected = timeRange == selectedTimeRange,
                                    onClick = { onTimeRangeSelected(timeRange) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        // Holding Details
                        Text(
                            "Holding Details",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.background)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatItem("Quantity", portfolioCoin.formattedQuantity)
                            StatItem(
                                "Average Purchase Price",
                                portfolioCoin.formattedAveragePurchasePrice
                            )
                            StatItem("Total Cost Basis", portfolioCoin.formattedTotalCostBasis)
                            StatItem("Current Price", portfolioCoin.formattedCurrentPrice)
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        // Performance
                        Text(
                            "Performance",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.background)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatItem("Unrealized PnL", portfolioCoin.formattedUnrealizedPnL)
                            StatItem(
                                "Unrealized PnL %",
                                portfolioCoin.formattedUnrealizedPnLPercentage
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        // About Section (Placeholder)
                        Text(
                            "About ${portfolioCoin.coinName}",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "${portfolioCoin.coinName} is a cryptocurrency held in your portfolio. " +
                                    "This section will provide more details about the coin itself.",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(90.dp)) // Add space for the floating action button
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalFloatingToolBar(
    onSellClick: () -> Unit,
    onCreatePriceAlertClick: () -> Unit,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBuyClick) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = "Buy")
                Spacer(Modifier.width(8.dp))
                Text("Buy")
            }
            TextButton(onClick = onCreatePriceAlertClick) {
                Icon(Icons.Default.AddAlert, contentDescription = "Create Price Alert")
                Spacer(Modifier.width(8.dp))
                Text("Price Alert")
            }

            TextButton(onClick = onSellClick) {
                Icon(
                    Icons.Default.Sell,
                    contentDescription = "Sell",
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(8.dp))
                Text("Sell", color = MaterialTheme.colorScheme.error)
            }
        }
    }
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