package com.koin.ui.coindetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.koin.data.coin.TimeRange
import com.koin.data.coin.dto.PriceDataPoint
import com.koin.domain.model.Coin
import com.koin.domain.coin.CoinRepository
import com.koin.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CoinDetailUiEvent {
    object Refresh : CoinDetailUiEvent()
    data class TimeRangeSelected(val timeRange: TimeRange) : CoinDetailUiEvent()
}

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val repository: CoinRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CoinDetailUiState, CoinDetailUiEvent>() {

    private val coinId: String = checkNotNull(savedStateHandle["coinId"])

    override val _uiState: MutableStateFlow<CoinDetailUiState> =
        MutableStateFlow(CoinDetailUiState(coinId = coinId))

    init {
        // Initial load should not set isRefreshing to true for a 'pull' action,
        // but rather isLoading. isRefreshing is specifically for pull-to-refresh.
        loadCoin(isRefresh = false)
        loadHistoricalData(isRefresh = false)
    }

    override fun handleEvent(event: CoinDetailUiEvent) {
        when (event) {
            is CoinDetailUiEvent.Refresh -> {
                // When a Refresh event comes from pull-to-refresh, set isRefreshing to true
                _uiState.update { it.copy(isRefreshing = true) }
                loadCoin(isRefresh = true)
                loadHistoricalData(isRefresh = true)
            }
            is CoinDetailUiEvent.TimeRangeSelected -> {
                _uiState.update { it.copy(selectedTimeRange = event.timeRange) }
                loadHistoricalData(isRefresh = false) // Time range change is not a "refresh"
            }
        }
    }

    private fun loadCoin(isRefresh: Boolean = false) {
        viewModelScope.launch {
            // Only set isLoading if it's not a refresh (initial load or explicit non-refresh)
            // isRefreshing handles the loading state for pull-to-refresh
            if (!isRefresh) {
                _uiState.update { it.copy(isLoading = true) }
            }

            repository.getCoinById(coinId)
                .catch { e ->
                    _uiState.update { state ->
                        state.copy(
                            error = e.message,
                            isLoading = false,
                            isRefreshing = false // Always reset isRefreshing on completion
                        )
                    }
                    //Timber.e(e, "Error loading coin details")
                }
                .collectLatest { result ->
                    result.onSuccess { coin ->
                        _uiState.update { state ->
                            state.copy(
                                coin = coin,
                                isLoading = false,
                                isRefreshing = false, // Always reset isRefreshing on completion
                                error = null
                            )
                        }
                    }.onFailure { e ->
                        _uiState.update { state ->
                            state.copy(
                                error = e.message,
                                isLoading = false,
                                isRefreshing = false // Always reset isRefreshing on completion
                            )
                        }
                    }
                }
        }
    }

    private fun loadHistoricalData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            // Only set isLoadingHistoricalData if it's not a refresh
            if (!isRefresh) {
                _uiState.update { it.copy(isLoadingHistoricalData = true) }
            }

            try {
                val timeRange = _uiState.value.selectedTimeRange
                val historicalData = repository.getCoinMarketChart(
                    coinId = coinId,
                    timeRange = timeRange,
                    vsCurrency = "usd"
                )

                _uiState.update { state ->
                    state.copy(
                        historicalData = historicalData,
                        isLoadingHistoricalData = false,
                        isRefreshing = false, // Always reset isRefreshing on completion
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        error = e.message,
                        isLoadingHistoricalData = false,
                        historicalData = emptyList(), // Clear any previous data on error
                        isRefreshing = false // Always reset isRefreshing on completion
                    )
                }
//                Timber.e(e, "Error loading historical data")
            }
        }
    }
}

data class CoinDetailUiState(
    val coinId: String,
    val coin: Coin? = null,
    val historicalData: List<PriceDataPoint> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingHistoricalData: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedTimeRange: TimeRange = TimeRange.ONE_DAY
)

//@HiltViewModel
//class CoinDetailViewModel @Inject constructor(
//    private val repository: CoinRepository,
//    savedStateHandle: SavedStateHandle
//) : BaseViewModel<CoinDetailUiState, CoinDetailUiEvent>() {
//
//    private val coinId: String = checkNotNull(savedStateHandle["coinId"])
//
//    override val _uiState: MutableStateFlow<CoinDetailUiState> =
//        MutableStateFlow(CoinDetailUiState(coinId = coinId))
//
//    init {
//        loadCoin()
//        loadHistoricalData()
//    }
//
//    override fun handleEvent(event: CoinDetailUiEvent) {
//        when (event) {
//            is CoinDetailUiEvent.Refresh -> {
//                loadCoin()
//                loadHistoricalData()
//            }
//            is CoinDetailUiEvent.TimeRangeSelected -> {
//                _uiState.update { it.copy(selectedTimeRange = event.timeRange) }
//                loadHistoricalData()
//            }
//        }
//    }
//
//    private fun loadCoin() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
//
//            repository.getCoinById(coinId)
//                .catch { e ->
//                    _uiState.update { state ->
//                        state.copy(
//                            error = e.message,
//                            isLoading = false,
//                            isRefreshing = false
//                        )
//                    }
//                    //Timber.e(e, "Error loading coin details")
//                }
//                .collectLatest { result ->
//                    result.onSuccess { coin ->
//                        _uiState.update { state ->
//                            state.copy(
//                                coin = coin,
//                                isLoading = false,
//                                isRefreshing = false,
//                                error = null
//                            )
//                        }
//                    }.onFailure { e ->
//                        _uiState.update { state ->
//                            state.copy(
//                                error = e.message,
//                                isLoading = false,
//                                isRefreshing = false
//                            )
//                        }
//                    }
//                }
//        }
//    }
//
//    private fun loadHistoricalData() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoadingHistoricalData = true) }
//
//            try {
//                val timeRange = _uiState.value.selectedTimeRange
//                val historicalData = repository.getCoinMarketChart(
//                    coinId = coinId,
//                    timeRange = timeRange,
//                    vsCurrency = "usd"
//                )
//
//                _uiState.update { state ->
//                    state.copy(
//                        historicalData = historicalData,
//                        isLoadingHistoricalData = false,
//                        error = null
//                    )
//                }
//            } catch (e: Exception) {
//                _uiState.update { state ->
//                    state.copy(
//                        error = e.message,
//                        isLoadingHistoricalData = false,
//                        historicalData = emptyList() // Clear any previous data on error
//                    )
//                }
////                Timber.e(e, "Error loading historical data")
//            }
//        }
//    }
//}

