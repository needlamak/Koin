package com.base.features.portfolio.presentation

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.base.features.portfolio.domain.model.Portfolio
import com.base.features.portfolio.presentation.components.AddTransactionDialog
import com.base.features.portfolio.presentation.components.BalanceHeader
import com.base.features.portfolio.presentation.components.ChartPoint
import com.base.features.portfolio.presentation.components.CoinListBottomSheet
import com.base.features.portfolio.presentation.components.CompletePortfolioChart
import com.kyro.ui.theme.presentation.components.home.AddFundsBottomSheetContent
import com.kyro.ui.theme.presentation.components.home.ScannerBottomSheetContent
import com.kyro.ui.theme.presentation.components.home.SendMoneyBottomSheetContent
import androidx.compose.ui.platform.LocalView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    onCoinClick: (String) -> Unit = {},
    viewModel: PortfolioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddFundsSheet by remember { mutableStateOf(false) }
    var showScanQrSheet by remember { mutableStateOf(false) }
    var showSendFundsSheet by remember { mutableStateOf(false) }
    var selectedPortfolio by remember { mutableStateOf<Portfolio?>(null) }

    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
    val pullToRefreshState = rememberPullToRefreshState()
    val view = LocalView.current
    val darkTheme = isSystemInDarkTheme()
    val bottomBarColor = MaterialTheme.colorScheme.surfaceContainer

    SideEffect {
        val window = (view.context as Activity).window
        window.navigationBarColor = bottomBarColor.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
    }



    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            CoinListBottomSheet(
                portfolios = uiState.portfolios,
                onCoinClick = { portfolio ->
                    // Navigate to detail screen when coin is clicked
                    onCoinClick(portfolio.coinId)
                }
            )
        },
        sheetPeekHeight = 250.dp,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                title = {
                },
                actions = {
                    Row(Modifier.padding(horizontal = 16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                        }

                        Spacer(Modifier.weight(1f))
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
                                        showAddFundsSheet = true
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
                                        showScanQrSheet = true
                                        showMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Send Funds") },
                                    onClick = {
                                        showSendFundsSheet = true
                                        showMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Send, contentDescription = null)
                                    }
                                )
                            }

                        }
                    }
                }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {

                BalanceHeader(
                    totalBalance = uiState.totalBalance.toDouble(),
                    gainAmount = uiState.gainAmount,
                    gainPercentage = uiState.gainPercentage,
                    onWalletClick = { /* TODO */ },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Chart section with fixed height
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(16.dp)
                ) {
                    Column {

                        CompletePortfolioChart(
                            data = uiState.chartData.map { ChartPoint(it.first, it.second) },
                            selectedTimeRange = uiState.selectedTimeRange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Time range selector below chart
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            item {
                                TimeRange.values().forEach { range ->
                                    val isSelected = range == uiState.selectedTimeRange
                                    Button(
                                        onClick = { viewModel.selectTimeRange(range) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    ) {
                                        Text(range.name)
                                    }
                                }
                            }
                        }
                    }
                }

            }

            selectedPortfolio?.let { portfolio ->
                AddTransactionDialog(
                    portfolio = portfolio,
                    onDismiss = { selectedPortfolio = null },
                    onConfirm = { type, quantity, price, notes ->
                        viewModel.addTransaction(
                            amount = System.currentTimeMillis(), // or another value
                            coinId = type,
                            quantity = quantity,
                            price = price
                        )
                        selectedPortfolio = null
                    }
                )
            }

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
                    ScannerBottomSheetContent(
                        onDismiss = { showScanQrSheet = false },
                        onScanFromGallery = { /* handle here */ },
                        onShowMyCode = { /* handle here */ }
                    )
                }
            }

            if (showSendFundsSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showSendFundsSheet = false },
                    tonalElevation = 0.dp,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ) {
                    SendMoneyBottomSheetContent(
                        onDismiss = { showSendFundsSheet = false },
                        onContactSelected = { /* handle here */ },
                        onScanQR = { showScanQrSheet = true } // example: open QR from here
                    )
                }
            }

//            if (showAddFundsSheet) {
//                // Implement AddFundsBottomSheet if needed
//                showAddFundsSheet = false
//            }
//
//            if (showScanQrSheet) {
//                // Implement ScanQrSheet if needed
//                showScanQrSheet = false
//            }
//
//            if (showSendFundsSheet) {
//                // Implement SendFundsSheet if needed
//                showSendFundsSheet = false
//            }
        }
    }


}
