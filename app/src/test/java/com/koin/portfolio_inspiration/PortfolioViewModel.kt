package com.base.features.portfolio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.base.core.data.remote.CoinGeckoApi
import com.base.core.data.remote.model.CoinMarket
import com.base.features.portfolio.data.local.entity.TransactionType
import com.base.features.portfolio.domain.model.Portfolio
import com.base.features.portfolio.domain.usecase.AddTransactionUseCase
import com.base.features.portfolio.domain.usecase.GetPortfolioDetailsUseCase
import com.base.features.portfolio.domain.usecase.GetPortfoliosUseCase
import com.base.features.portfolio.domain.repository.PortfolioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject



enum class TimeRange(val days: Int) {
    DAY(1),
    WEEK(7),
    MONTH(30),
    YEAR(365)
}

data class PortfolioUiState(
    val totalBalance: BigDecimal = BigDecimal.ZERO,
    val portfolios: List<Portfolio> = emptyList(),
    val chartData: List<Pair<Long, Double>> = emptyList(),
    val selectedTimeRange: TimeRange = TimeRange.DAY,
    val coins: List<CoinMarket> = emptyList(),
    val isRefreshing: Boolean = false,
    val gainAmount: Double = 0.0,
    val gainPercentage: Double = 0.0,
    val error: String? = null
)

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val getPortfoliosUseCase: GetPortfoliosUseCase,
    private val getPortfolioDetailsUseCase: GetPortfolioDetailsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val coinGeckoApi: CoinGeckoApi,
    private val repository: PortfolioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortfolioUiState())
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()

    private var priceUpdateJob: Job? = null
    private var chartUpdateJob: Job? = null

    init {
        refresh()
        startPriceUpdates()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            try {
                val portfolios = getPortfoliosUseCase().first()

                if (portfolios.isEmpty()) {
                    repository.initializeMockData()
                }

                val updatedPortfolios = getPortfoliosUseCase().first()
                val coins = coinGeckoApi.getCoinMarkets()

                val totalBalance = updatedPortfolios.sumOf { it.totalValue }
                val gainAmount = updatedPortfolios.sumOf { it.profitLoss }
                val gainPercentage = if (totalBalance > 0) {
                    (gainAmount / (totalBalance - gainAmount)) * 100
                } else 0.0

                _uiState.update {
                    it.copy(
                        portfolios = updatedPortfolios,
                        coins = coins,
                        totalBalance = totalBalance.toBigDecimal(),
                        gainAmount = gainAmount,
                        gainPercentage = gainPercentage,
                        isRefreshing = false,
                        error = null
                    )
                }

                // Initialize chart data after setting portfolios
                updateChartData()
            } catch (e: Exception) {
                _uiState.update { it.copy(isRefreshing = false, error = e.message) }
            }
        }
    }

    fun selectTimeRange(range: TimeRange) {
        // Cancel any ongoing chart update
        chartUpdateJob?.cancel()
        
        _uiState.update { it.copy(selectedTimeRange = range) }
        
        // Start new chart update with debounce
        chartUpdateJob = viewModelScope.launch {
            delay(300) // Debounce time range changes
            updateChartData()
        }
    }

    private suspend fun updateChartData() {
        val range = _uiState.value.selectedTimeRange
        val mainPortfolio = _uiState.value.portfolios.firstOrNull() ?: return
        
        try {
            _uiState.update { it.copy(isRefreshing = true) }
            
            val chartData = coinGeckoApi.getMarketChart(
                id = mainPortfolio.coinId,
                days = range.days.toString()
            ).toPriceEntries()
            
            _uiState.update { it.copy(
                chartData = chartData,
                isRefreshing = false,
                error = null
            ) }
        } catch (e: Exception) {
            _uiState.update { it.copy(
                isRefreshing = false,
                error = e.message
            ) }
        }
    }

    private fun startPriceUpdates() {
        priceUpdateJob?.cancel()
        priceUpdateJob = viewModelScope.launch {
            while (isActive) {
                try {
                    val portfolios = _uiState.value.portfolios
                    portfolios.forEach { portfolio ->
                        val price = coinGeckoApi.getCoinPrice(portfolio.coinId)
                        repository.updateCurrentPrice(portfolio.coinId, price)
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = e.message) }
                }
                delay(30000) // Update every 30 seconds
            }
        }
    }

    fun addTransaction(amount: Long, coinId: TransactionType, quantity: Double, price: Double) {
        viewModelScope.launch {
            try {
                addTransactionUseCase(amount, coinId, quantity, price)
                refresh()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun checkPriceAlerts(prices: Map<String, Map<String, Double>>) {
        // TODO: Implement price alert checks and notifications
    }

    override fun onCleared() {
        super.onCleared()
        priceUpdateJob?.cancel()
        chartUpdateJob?.cancel()
    }
}

//sealed class PortfolioUiState {
//    data object Loading : PortfolioUiState()
//    data class Success(val portfolios: List<Portfolio>) : PortfolioUiState()
//    data class Error(val message: String) : PortfolioUiState()
//}

sealed class TransactionUiState {
    data object Idle : TransactionUiState()
    data object Loading : TransactionUiState()
    data object Success : TransactionUiState()
    data class Error(val message: String) : TransactionUiState()
} 