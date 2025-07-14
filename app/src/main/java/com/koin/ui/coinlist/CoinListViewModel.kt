package com.koin.ui.coinlist

import androidx.lifecycle.viewModelScope
import com.koin.data.coin.CoinRepositoryImpl
import com.koin.domain.coin.CoinRepository
import com.koin.domain.model.Coin
import com.koin.domain.portfolio.PortfolioRepository
import com.koin.domain.user.UserRepository
import com.koin.domain.watchlist.WatchlistItem
import com.koin.domain.watchlist.WatchlistRepository
import com.koin.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel @Inject constructor(
    private val repository: CoinRepository,
    private val userRepository: UserRepository,
    private val watchlistRepository: WatchlistRepository,
    private val portfolioRepository: PortfolioRepository
) : BaseViewModel<CoinListUiState, CoinListUiEvent>() {

    override val _uiState: MutableStateFlow<CoinListUiState> = MutableStateFlow(CoinListUiState())

    init {
        loadCoins()
    }

    override fun handleEvent(event: CoinListUiEvent) {
        when (event) {
            is CoinListUiEvent.RefreshData -> refreshCoinsData() // For fetching new data
            is CoinListUiEvent.OnSearchQueryChange -> updateSearchQuery(event.query)
            is CoinListUiEvent.OnCoinClick -> { /* Handle navigation to detail */ }
            is CoinListUiEvent.ResetFilters -> resetAllFilters() // For resetting all active filters
            is CoinListUiEvent.ResetSearch -> resetSearchQuery() // For clearing just the search query
            is CoinListUiEvent.ToggleWatchlist -> toggleWatchlist(event.coin)
            is CoinListUiEvent.BuyCoin -> buyCoin(event.coin, event.amount)
            is CoinListUiEvent.HideBuySuccessBottomSheet -> hideBuySuccessBottomSheet()
            is CoinListUiEvent.ShowBuyDialog -> showBuyDialog(event.coin)
            is CoinListUiEvent.UpdateFilters -> updateFilters(event.sortType, event.showOnlyPositiveChange)
            CoinListUiEvent.HideBuyDialog -> hideBuyDialog()
        }
    }

    private fun hideBuySuccessBottomSheet() {
        _uiState.update { it.copy(showBuySuccessBottomSheet = false, buyTransactionDetails = null) }
    }

    private fun loadCoins() {
        viewModelScope.launch {
            repository.getAllCoins()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collectLatest { result ->
                    result.onSuccess { coins ->
                        _uiState.update { state ->
                            state.copy(
                                coins = coins,
                                filteredCoins = filterCoins(coins, state.searchQuery, state.sortType, state.showOnlyPositiveChange),
                                isLoading = false,
                                error = null
                            )
                        }
                    }.onFailure { e ->
                        _uiState.update { it.copy(error = e.message, isLoading = false) }
                    }
                }
        }
    }

    // Renamed for clarity: This is for refreshing the actual data from the network
    private fun refreshCoinsData() {
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            try {
                (repository as? CoinRepositoryImpl)?.refreshFromNetwork()
                _uiState.update { it.copy(isRefreshing = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isRefreshing = false) }
            }
        }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredCoins = filterCoins(state.coins, query, state.sortType, state.showOnlyPositiveChange)
            )
        }
    }

    private fun filterCoins(coins: List<Coin>, query: String, sortType: SortType, showOnlyPositiveChange: Boolean): List<Coin> {
        val filtered = if (query.isBlank()) {
            coins
        } else {
            coins.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.symbol.contains(query, ignoreCase = true)
            }
        }

        val positiveChangeFiltered = if (showOnlyPositiveChange) {
            filtered.filter { it.priceChangePercentage24h > 0 }
        } else {
            filtered
        }

        return when (sortType) {
            SortType.NAME_ASC -> positiveChangeFiltered.sortedBy { it.name }
            SortType.NAME_DESC -> positiveChangeFiltered.sortedByDescending { it.name }
            SortType.PRICE_ASC -> positiveChangeFiltered.sortedBy { it.currentPrice }
            SortType.PRICE_DESC -> positiveChangeFiltered.sortedByDescending { it.currentPrice }
            SortType.CHANGE_ASC -> positiveChangeFiltered.sortedBy { it.priceChangePercentage24h }
            SortType.CHANGE_DESC -> positiveChangeFiltered.sortedByDescending { it.priceChangePercentage24h }
        }
    }

    private fun updateFilters(sortType: SortType? = null, showOnlyPositiveChange: Boolean? = null) {
        _uiState.update { state ->
            val newSortType = sortType ?: state.sortType
            val newShowOnlyPositiveChange = showOnlyPositiveChange ?: state.showOnlyPositiveChange
            state.copy(
                sortType = newSortType,
                showOnlyPositiveChange = newShowOnlyPositiveChange,
                filteredCoins = filterCoins(state.coins, state.searchQuery, newSortType, newShowOnlyPositiveChange)
            )
        }
    }

    // New function: Resets ALL filters (including search, sort, etc.)
    private fun resetAllFilters() {
        _uiState.update { state ->
            state.copy(
                searchQuery = "",
                sortType = SortType.NAME_ASC,
                showOnlyPositiveChange = false,
                filteredCoins = filterCoins(state.coins, "", SortType.NAME_ASC, false)
            )
        }
    }

    // New function: Specifically resets only the search query
    private fun resetSearchQuery() {
        _uiState.update { state ->
            state.copy(
                searchQuery = "",
                filteredCoins = filterCoins(state.coins, "", state.sortType, state.showOnlyPositiveChange) // Re-filter with an empty query
            )
        }
    }

    private fun toggleWatchlist(coin: Coin) {
        viewModelScope.launch {
            try {
                val userId = 1L // Assume single user for now
                val watchlistItem = WatchlistItem(
                    userId = userId,
                    coinId = coin.id,
                    coinName = coin.name,
                    coinSymbol = coin.symbol,
                    coinImageUrl = coin.imageUrl,
                    addedAt = System.currentTimeMillis()
                )
                watchlistRepository.addToWatchlist(watchlistItem)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update watchlist: ${e.message}") }
            }
        }
    }

    private fun buyCoin(coin: Coin, amount: Double) {
        viewModelScope.launch {
            try {
                portfolioRepository.buyCoin(coin, amount)
                val totalPrice = amount * coin.currentPrice
                _uiState.update {
                    it.copy(
                        showBuySuccessBottomSheet = true,
                        buyTransactionDetails = BuyTransactionDetails(
                            coinName = coin.name,
                            quantity = amount,
                            totalPrice = totalPrice
                        ),
                        showBuyDialog = false // Hide the buy dialog after successful purchase
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to buy coin: ${e.message}") }
            }
        }
    }

    private fun showBuyDialog(coin: Coin) {
        _uiState.update { it.copy(showBuyDialog = true, selectedCoinForBuy = coin) }
    }

    private fun hideBuyDialog() {
        _uiState.update { it.copy(showBuyDialog = false, selectedCoinForBuy = null) }
    }
}


data class BuyTransactionDetails(
    val coinName: String,
    val quantity: Double,
    val totalPrice: Double
)

data class CoinListUiState(
    val coins: List<Coin> = emptyList(),
    val filteredCoins: List<Coin> = emptyList(),
    val searchQuery: String = "",
    val sortType: SortType = SortType.NAME_ASC,
    val showOnlyPositiveChange: Boolean = false,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val showBuyDialog: Boolean = false,
    val selectedCoinForBuy: Coin? = null,
    val showBuySuccessBottomSheet: Boolean = false,
    val buyTransactionDetails: BuyTransactionDetails? = null
)

sealed class CoinListUiEvent {
    object RefreshData : CoinListUiEvent() // Event for refreshing actual data
    data class OnSearchQueryChange(val query: String) : CoinListUiEvent()
    data class OnCoinClick(val coinId: String) : CoinListUiEvent()
    object ResetFilters : CoinListUiEvent() // Event for resetting all filters
    object ResetSearch : CoinListUiEvent() // Event for resetting just the search query
    data class ToggleWatchlist(val coin: Coin) : CoinListUiEvent()
    data class BuyCoin(val coin: Coin, val amount: Double) : CoinListUiEvent()
    object HideBuySuccessBottomSheet : CoinListUiEvent()
    data class ShowBuyDialog(val coin: Coin) : CoinListUiEvent()
    object HideBuyDialog : CoinListUiEvent()
    data class UpdateFilters(val sortType: SortType? = null, val showOnlyPositiveChange: Boolean? = null) : CoinListUiEvent()
}



enum class SortType {
    NAME_ASC,
    NAME_DESC,
    PRICE_ASC,
    PRICE_DESC,
    CHANGE_ASC,
    CHANGE_DESC
}