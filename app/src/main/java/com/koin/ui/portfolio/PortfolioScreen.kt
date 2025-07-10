package com.koin.ui.portfolio

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.koin.components.BottomNavBar
import com.koin.domain.model.Coin
import com.koin.domain.portfolio.Portfolio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    state: PortfolioUiState,
    onEvent: (PortfolioUiEvent) -> Unit,
    selectedCoin: Coin?,
    navController: NavController,
    onPortfolioCoinClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddFundsSheet by remember { mutableStateOf(false) }
    var showScanQrSheet by remember { mutableStateOf(false) }
    var showSendFundsSheet by remember { mutableStateOf(false) }

    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
    val pullToRefreshState = rememberPullToRefreshState()

    // Main Scaffold for bottom navigation
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController as NavHostController)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                PortfolioHoldingsBottomSheet(
                    holdings = state.portfolio.holdings,
                    onBuyCoin = { coinId -> onEvent(PortfolioUiEvent.ShowBuyDialog(coinId)) },
                    onPortfolioCoinClick = onPortfolioCoinClick
                )
            },
            sheetPeekHeight = 250.dp,
            topBar = {
                PortfolioTopBar(
                    onAddFunds = { showAddFundsSheet = true },
                    onScanQr = { showScanQrSheet = true },
                    onSendFunds = { showSendFundsSheet = true }
                )
            }
        ) {
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = state.isRefreshing,
                onRefresh = { onEvent(PortfolioUiEvent.RefreshData) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Balance Header
                    BalanceHeader(
                        portfolio = state.portfolio,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    PortfolioChart(
                        portfolio = state.portfolio,
                        selectedTimeRange = state.selectedTimeRange,
                        modifier = Modifier
                            .fillMaxWidth(),
                        onEvent = onEvent
                    )
                    Spacer(modifier = Modifier.height(500.dp))
                }
            }
        }

        // Modal Bottom Sheets
        if (showAddFundsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddFundsSheet = false },
                tonalElevation = 0.dp,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                AddFundsBottomSheetContent(
                    onDismiss = { showAddFundsSheet = false }
                )
            }
        }

        if (showScanQrSheet) {
            ModalBottomSheet(
                onDismissRequest = { showScanQrSheet = false },
                tonalElevation = 0.dp,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                ScanQrBottomSheetContent(
                    onDismiss = { showScanQrSheet = false }
                )
            }
        }

        if (showSendFundsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSendFundsSheet = false },
                tonalElevation = 0.dp,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                SendFundsBottomSheetContent(
                    onDismiss = { showSendFundsSheet = false }
                )
            }
        }

        // Buy Coin Dialog
        if (state.showBuyDialog && selectedCoin != null) {
            BuyCoinDialog(
                coin = selectedCoin,
                availableBalance = state.portfolio.balance,
                onDismiss = { onEvent(PortfolioUiEvent.HideBuyDialog) },
                onConfirm = { quantity, _ ->
                    onEvent(
                        PortfolioUiEvent.BuyCoin(
                            coinId = selectedCoin.id,
                            quantity = quantity,
                            pricePerCoin = selectedCoin.currentPrice
                        )
                    )
                }
            )
        }

        // Handle errors
        state.error?.let { error ->
            LaunchedEffect(error) {
                snackbarHostState.showSnackbar(error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortfolioTopBar(
    onAddFunds: () -> Unit,
    onScanQr: () -> Unit,
    onSendFunds: () -> Unit
) {
    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        title = { },
        actions = {
            Row(Modifier.padding(horizontal = 16.dp)) {
                // Profile button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile"
                    )
                }

                Spacer(Modifier.weight(1f))

                // Menu button
                var showMenu by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Add Funds") },
                            onClick = {
                                onAddFunds()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.AccountBalanceWallet,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Scan QR") },
                            onClick = {
                                onScanQr()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Send Funds") },
                            onClick = {
                                onSendFunds()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun BalanceHeader(
    portfolio: Portfolio,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Total balance
        Text(
            text = portfolio.formattedTotalPortfolioValue,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Gain/Loss information
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = portfolio.formattedUnrealizedPnL,
                style = MaterialTheme.typography.titleMedium,
                color = if (portfolio.isPositiveUnrealizedPnL) Color.Green else Color.Red,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "(${portfolio.formattedPortfolioPerformancePercentage})",
                style = MaterialTheme.typography.titleMedium,
                color = if (portfolio.isPositivePerformance) Color.Green else Color.Red,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Available balance
        Text(
            text = "Available: ${portfolio.formattedBalance}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Placeholder bottom sheet contents - you can implement these based on your needs
@Composable
private fun AddFundsBottomSheetContent(
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Funds",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Feature coming soon!",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onDismiss) {
            Text("Close")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ScanQrBottomSheetContent(
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Scan QR Code",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "QR Scanner feature coming soon!",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onDismiss) {
            Text("Close")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SendFundsBottomSheetContent(
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Send Funds",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Send funds feature coming soon!",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onDismiss) {
            Text("Close")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
