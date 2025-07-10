package com.koin.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koin.data.coin.TimeRange
import com.koin.domain.coin.CoinRepository
import com.koin.domain.portfolio.BuyCoinUseCase
import com.koin.domain.portfolio.GetPortfolioUseCase
import com.koin.domain.portfolio.Portfolio
import com.koin.domain.portfolio.RefreshPortfolioUseCase
import com.koin.domain.portfolio.ResetPortfolioUseCase
import com.koin.domain.portfolio.SellCoinUseCase
import com.koin.domain.portfolio.AddCoinToHoldingForTestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    getPortfolioUseCase: GetPortfolioUseCase,
    private val buyCoinUseCase: BuyCoinUseCase,
    private val sellCoinUseCase: SellCoinUseCase,
    private val refreshPortfolioUseCase: RefreshPortfolioUseCase,
    private val resetPortfolioUseCase: ResetPortfolioUseCase,
    private val addCoinToHoldingForTestUseCase: AddCoinToHoldingForTestUseCase,
    coinRepository: CoinRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _showBuyDialog = MutableStateFlow(false)
    private val _selectedCoinForBuy = MutableStateFlow<String?>(null)
    private val _showBottomSheet = MutableStateFlow(false)
    private val _selectedTimeRange = MutableStateFlow(TimeRange.ALL)

    // Single data source for portfolio
    private val portfolioResult = getPortfolioUseCase()
        .catch { emit(Result.failure(it)) }
        .onStart { _isLoading.value = true }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Result.success(Portfolio.empty())
        )

    val uiState = combine(
        _isLoading,
        portfolioResult,
        _error,
        _isRefreshing,
        _showBuyDialog,
        _selectedCoinForBuy,
        _showBottomSheet,
        _selectedTimeRange  // Added this missing flow
    ) { flows ->
        val isLoading = flows[0] as Boolean
        val portfolioResult = flows[1] as Result<Portfolio>
        val error = flows[2] as String?
        val isRefreshing = flows[3] as Boolean
        val showBuyDialog = flows[4] as Boolean
        val selectedCoinForBuy = flows[5] as String?
        val showBottomSheet = flows[6] as Boolean
        val selectedTimeRange = flows[7] as TimeRange

        portfolioResult.fold(
            onSuccess = { portfolio ->
                _isLoading.value = false
                _error.value = null
                PortfolioUiState(
                    isLoading = isLoading && portfolio.holdings.isEmpty(),
                    portfolio = portfolio,
                    error = error,
                    isRefreshing = isRefreshing,
                    showBuyDialog = showBuyDialog,
                    selectedCoinForBuy = selectedCoinForBuy,
                    showBottomSheet = showBottomSheet,
                    selectedTimeRange = selectedTimeRange
                )
            },
            onFailure = { exception ->
                _isLoading.value = false
                PortfolioUiState(
                    isLoading = false,
                    error = exception.message ?: "Unknown error",
                    isRefreshing = isRefreshing,
                    showBuyDialog = showBuyDialog,
                    selectedCoinForBuy = selectedCoinForBuy,
                    showBottomSheet = showBottomSheet,
                    selectedTimeRange = selectedTimeRange
                )
            }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PortfolioUiState(isLoading = true)
    )

    fun onEvent(event: PortfolioUiEvent) {
        when (event) {
            PortfolioUiEvent.RefreshData -> refreshPortfolio()
            PortfolioUiEvent.ShowBottomSheet -> _showBottomSheet.value = true
            PortfolioUiEvent.HideBottomSheet -> _showBottomSheet.value = false
            PortfolioUiEvent.HideBuyDialog -> {
                _showBuyDialog.value = false
                _selectedCoinForBuy.value = null
            }
            is PortfolioUiEvent.ShowBuyDialog -> {
                _selectedCoinForBuy.value = event.coinId
                _showBuyDialog.value = true
            }
            is PortfolioUiEvent.BuyCoin -> buyCoin(
                event.coinId,
                event.quantity,
                event.pricePerCoin
            )
            is PortfolioUiEvent.SellCoin -> sellCoin(
                event.coinId,
                event.quantity,
                event.pricePerCoin
            )
            PortfolioUiEvent.ResetPortfolio -> resetPortfolio()
            is PortfolioUiEvent.SelectTimeRange -> {
                _selectedTimeRange.value = event.timeRange
            }
            is PortfolioUiEvent.AddCoinForTest -> addCoinForTest(
                event.coinId,
                event.quantity,
                event.pricePerCoin
            )
        }
    }

    // Property to get the selected coin for the buy dialog
    val selectedCoin = combine(
        _selectedCoinForBuy,
        coinRepository.getAllCoins()
    ) { coinId, coinsResult ->
        coinId?.let { id ->
            coinsResult.getOrNull()?.find { it.id == id }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    private fun refreshPortfolio() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                refreshPortfolioUseCase()
            } catch (e: Exception) {
                _error.value = "Failed to refresh portfolio: ${e.localizedMessage}"
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun buyCoin(coinId: String, quantity: Double, pricePerCoin: Double) {
        viewModelScope.launch {
            try {
                val result = buyCoinUseCase(coinId, quantity, pricePerCoin)
                result.fold(
                    onSuccess = {
                        _showBuyDialog.value = false
                        _selectedCoinForBuy.value = null
                        _error.value = null
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Failed to buy coin"
                    }
                )
            } catch (e: Exception) {
                _error.value = "Failed to buy coin: ${e.localizedMessage}"
            }
        }
    }

    private fun sellCoin(coinId: String, quantity: Double, pricePerCoin: Double) {
        viewModelScope.launch {
            try {
                val result = sellCoinUseCase(coinId, quantity, pricePerCoin)
                result.fold(
                    onSuccess = {
                        _error.value = null
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Failed to sell coin"
                    }
                )
            } catch (e: Exception) {
                _error.value = "Failed to sell coin: ${e.localizedMessage}"
            }
        }
    }

    private fun resetPortfolio() {
        viewModelScope.launch {
            try {
                resetPortfolioUseCase()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to reset portfolio: ${e.localizedMessage}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    private fun addCoinForTest(coinId: String, quantity: Double, pricePerCoin: Double) {
        viewModelScope.launch {
            try {
                val result = addCoinToHoldingForTestUseCase(coinId, quantity, pricePerCoin)
                result.fold(
                    onSuccess = {
                        _error.value = null
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Failed to add coin for test"
                    }
                )
            } catch (e: Exception) {
                _error.value = "Failed to add coin for test: ${e.localizedMessage}"
            }
        }
    }
}