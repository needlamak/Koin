package com.koin.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koin.data.coin.TimeRange
import com.koin.domain.coin.CoinRepository
import com.koin.domain.portfolio.BuyCoinUseCase
import com.koin.domain.portfolio.GetBalanceUseCase
import com.koin.domain.portfolio.GetPortfolioUseCase
import com.koin.domain.portfolio.Portfolio
import com.koin.domain.portfolio.PortfolioBalance
import com.koin.domain.portfolio.PortfolioHolding
import com.koin.domain.portfolio.RefreshPortfolioUseCase
import com.koin.domain.portfolio.ResetPortfolioUseCase
import com.koin.domain.portfolio.SellCoinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
    private val getBalanceUseCase: GetBalanceUseCase,
    coinRepository: CoinRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _showBuyDialog = MutableStateFlow(false)
    private val _selectedCoinForBuy = MutableStateFlow<String?>(null)
    private val _showBottomSheet = MutableStateFlow(false)
    private val _selectedTimeRange = MutableStateFlow(TimeRange.ALL)

    // Create the balance flow
    private val balanceFlow = flow {
        emitAll(getBalanceUseCase())
    }.flowOn(Dispatchers.IO)

    // Single data source for portfolio using combineTransform
    val uiState = combineTransform(
        getPortfolioUseCase(), // Flow<List<PortfolioHolding>>
        balanceFlow,           // Flow<PortfolioBalance?>
        _isLoading,
        _error,
        _isRefreshing,
        _showBuyDialog,
        _selectedCoinForBuy,
        _showBottomSheet,
        _selectedTimeRange
    ) { flows ->
        val holdings = flows[0] as List<PortfolioHolding>
        val balance = flows[1] as PortfolioBalance?
        val isLoading = flows[2] as Boolean
        val error = flows[3] as String?
        val isRefreshing = flows[4] as Boolean
        val showBuyDialog = flows[5] as Boolean
        val selectedCoinForBuy = flows[6] as String?
        val showBottomSheet = flows[7] as Boolean
        val selectedTimeRange = flows[8] as TimeRange

        val portfolio = Portfolio(
            balance = balance?.balance ?: Portfolio.Companion.INITIAL_BALANCE,
            holdings = holdings,
            transactions = emptyList()
        )

        val uiState = PortfolioUiState(
            isLoading = isLoading && portfolio.holdings.isEmpty(),
            portfolio = portfolio,
            error = error,
            isRefreshing = isRefreshing,
            showBuyDialog = showBuyDialog,
            selectedCoinForBuy = selectedCoinForBuy,
            showBottomSheet = showBottomSheet,
            selectedTimeRange = selectedTimeRange
        )

        emit(uiState)
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

            is PortfolioUiEvent.SelectTimeRange -> _selectedTimeRange.value = event.timeRange
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
                val coin = selectedCoin.first()
                if (coin != null) {
                    buyCoinUseCase(coin, quantity)
                    _showBuyDialog.value = false
                    _selectedCoinForBuy.value = null
                    _error.value = null
                } else {
                    _error.value = "Could not find coin to buy"
                }
            } catch (e: Exception) {
                _error.value = "Failed to buy coin: ${e.localizedMessage}"
            }
        }
    }

    private fun sellCoin(coinId: String, quantity: Double, pricePerCoin: Double) {
        viewModelScope.launch {
            try {
                sellCoinUseCase(coinId, quantity, pricePerCoin)
                _error.value = null
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
}
