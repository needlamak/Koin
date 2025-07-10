package com.koin.portfolio_inspiration//@file:Suppress("FunctionName")
//
//package com.base.features.portfolio.presentation
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.BottomSheetScaffold
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.SnackbarHost
//import androidx.compose.material3.SnackbarHostState
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.rememberBottomSheetScaffoldState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.ViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlin.random.Random
//
//// ----------------- MOCK MODELS -----------------
//
//data class Portfolio(
//    val id: Long,
//    val name: String,
//    val totalValue: Double,
//    val profitLoss: Double
//)
//
////enum class TimeRange {
////    DAY, WEEK, MONTH, YEAR, ALL
////}
//
//enum class TransactionType {
//    BUY, SELL
//}
//
//sealed class PortfolioUiState {
//    data object Loading : PortfolioUiState()
//    data class Success(val portfolios: List<Portfolio>) : PortfolioUiState()
//    data class Error(val message: String) : PortfolioUiState()
//}
//
//sealed class TransactionUiState {
//    data object Idle : TransactionUiState()
//    data object Loading : TransactionUiState()
//    data object Success : TransactionUiState()
//    data class Error(val message: String) : TransactionUiState()
//}
//
//// ----------------- MOCK VIEWMODEL -----------------
//
//class PortfolioViewModelPreview : ViewModel() {
//    private val _uiState = MutableStateFlow<PortfolioUiState>(
//        PortfolioUiState.Success(
//            listOf(
//                Portfolio(1, "Bitcoin", 25000.0, 3000.0),
//                Portfolio(2, "Ethereum", 10000.0, 1500.0)
//            )
//        )
//    )
//    val uiState: StateFlow<PortfolioUiState> = _uiState
//
//    private val _selectedTimeRange = MutableStateFlow(TimeRange.DAY)
//    val selectedTimeRange: StateFlow<TimeRange> = _selectedTimeRange
//
//    private val _chartData = MutableStateFlow(generateFakeChartData(TimeRange.DAY))
//    val chartData: StateFlow<List<Pair<Long, Double>>> = _chartData
//
//    private val _transactionState = MutableStateFlow<TransactionUiState>(TransactionUiState.Idle)
//    val transactionState: StateFlow<TransactionUiState> = _transactionState
//
//    fun setTimeRange(range: TimeRange) {
//        _selectedTimeRange.value = range
//        _chartData.value = generateFakeChartData(range)
//    }
//
//    fun resetTransactionState() {
//        _transactionState.value = TransactionUiState.Idle
//    }
//
//    fun addTransaction(
//        portfolioId: Long,
//        type: TransactionType,
//        quantity: Double,
//        price: Double,
//        notes: String?
//    ) {
//        _transactionState.value = TransactionUiState.Success
//    }
//
//    private fun generateFakeChartData(range: TimeRange): List<Pair<Long, Double>> {
//        val now = System.currentTimeMillis()
//        val count = when (range) {
//            TimeRange.DAY -> 24
//            TimeRange.WEEK -> 7
//            TimeRange.MONTH -> 30
//            TimeRange.YEAR, TimeRange.ALL -> 12
//        }
//        val interval = when (range) {
//            TimeRange.DAY -> 3600000L
//            TimeRange.WEEK, TimeRange.MONTH -> 86400000L
//            else -> 2592000000L
//        }
//
//        return List(count) { i ->
//            val timestamp = now - ((count - i) * interval)
//            val value = 50000.0 * (1 + Random.nextDouble(-0.1, 0.1))
//            timestamp to value
//        }
//    }
//}
//
//// ----------------- PREVIEW COMPONENTS -----------------
//
//@Composable
//fun BalanceHeaderPreview(totalBalance: Double, gainPercentage: Double, gainAmount: Double) {
//    Column(Modifier.padding(16.dp)) {
//        Text("Total Balance: $${String.format("%.2f", totalBalance)}", fontWeight = FontWeight.Bold)
//        Text("Gain: $${String.format("%.2f", gainAmount)} (${String.format("%.1f", gainPercentage)}%)")
//    }
//}
//
//@Composable
//fun PortfolioChartPreview(
//    selectedRange: TimeRange,
//    onRangeSelected: (TimeRange) -> Unit,
//    chartData: List<Pair<Long, Double>>
//) {
//    Column(Modifier.padding(16.dp)) {
//        Text("Chart - ${selectedRange.name}")
//    }
//}
//
//@Composable
//fun CoinListBottomSheetPreview(portfolios: List<Portfolio>, onCoinClick: (Portfolio) -> Unit) {
//    Column(Modifier.padding(16.dp)) {
//        portfolios.forEach {
//            Text(it.name, Modifier.clickable { onCoinClick(it) }.padding(8.dp))
//        }
//    }
//}
//
//@Composable
//fun AddTransactionDialogPreview(
//    portfolio: Portfolio,
//    onDismiss: () -> Unit,
//    onConfirm: (TransactionType, Double, Double, String?) -> Unit
//) {
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Add Transaction - ${portfolio.name}") },
//        text = { Text("Mock transaction dialog...") },
//        confirmButton = {
//            TextButton(onClick = {
//                onConfirm(TransactionType.BUY, 1.0, 50000.0, "Sample")
//                onDismiss()
//            }) {
//                Text("Confirm")
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismiss) { Text("Cancel") }
//        }
//    )
//}
//
//// ----------------- SCREEN CONTENT -----------------
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PortfolioScreenPreviewContent(viewModel: PortfolioViewModelPreview) {
//    val uiState by viewModel.uiState.collectAsState()
//    val transactionState by viewModel.transactionState.collectAsState()
//    val selectedTimeRange by viewModel.selectedTimeRange.collectAsState()
//    val chartData by viewModel.chartData.collectAsState()
//
//    val snackbarHostState = remember { SnackbarHostState() }
//    var selectedPortfolio by remember { mutableStateOf<Portfolio?>(null) }
//
//    val bottomSheetState = rememberBottomSheetScaffoldState()
//    LaunchedEffect(transactionState) {
//        when (transactionState) {
//            is TransactionUiState.Success -> {
//                snackbarHostState.showSnackbar("Transaction added")
//                viewModel.resetTransactionState()
//                selectedPortfolio = null
//            }
//
//            is TransactionUiState.Error -> {
//                snackbarHostState.showSnackbar((transactionState as TransactionUiState.Error).message)
//                viewModel.resetTransactionState()
//            }
//
//            else -> {}
//        }
//    }
//
//    BottomSheetScaffold(
//        scaffoldState = bottomSheetState,
//        snackbarHost = { SnackbarHost(snackbarHostState) },
//        sheetContent = {
//            if (uiState is PortfolioUiState.Success) {
//                CoinListBottomSheetPreview(
//                    portfolios = (uiState as PortfolioUiState.Success).portfolios,
//                    onCoinClick = { selectedPortfolio = it }
//                )
//            }
//        },
//        sheetPeekHeight = 200.dp,
//        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
//        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
//    ) { padding ->
//        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
//            Row(
//                Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(40.dp)
//                        .clip(CircleShape)
//                        .background(MaterialTheme.colorScheme.surfaceVariant),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
//                }
//                Spacer(modifier = Modifier.weight(1f))
//                IconButton(onClick = {}) {
//                    Icon(Icons.Default.Add, contentDescription = "Add")
//                }
//            }
//
//            if (uiState is PortfolioUiState.Success) {
//                val portfolios = (uiState as PortfolioUiState.Success).portfolios
//                val total = portfolios.sumOf { it.totalValue }
//                val gain = portfolios.sumOf { it.profitLoss }
//                val gainPercent = if (total > 0) (gain / (total - gain)) * 100 else 0.0
//
//                BalanceHeaderPreview(total, gainPercent, gain)
//                PortfolioChartPreview(selectedTimeRange, viewModel::setTimeRange, chartData)
//            }
//        }
//
//        selectedPortfolio?.let { portfolio ->
//            AddTransactionDialogPreview(
//                portfolio = portfolio,
//                onDismiss = { selectedPortfolio = null },
//                onConfirm = { type, qty, price, notes ->
//                    viewModel.addTransaction(portfolio.id, type, qty, price, notes)
//                }
//            )
//        }
//    }
//}
//
//// ----------------- FINAL PREVIEW ENTRY POINT -----------------
//
//@Preview(showBackground = true)
//@Composable
//fun PortfolioScreenPreview() {
//    val previewViewModel = remember { PortfolioViewModelPreview() }
//    MaterialTheme {
//        PortfolioScreenPreviewContent(viewModel = previewViewModel)
//    }
//}
