package com.koin.ui.coinlist

import androidx.lifecycle.viewModelScope
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

@HiltViewModel
class CoinListViewModel @Inject constructor(
    private val repository: CoinRepository
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
        }
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
                                filteredCoins = filterCoins(coins, state.searchQuery),
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
                (repository as? com.koin.data.coin.CoinRepositoryImpl)?.refreshFromNetwork()
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
                filteredCoins = filterCoins(state.coins, query)
            )
        }
    }

    private fun filterCoins(coins: List<Coin>, query: String): List<Coin> {
        return if (query.isBlank()) {
            coins
        } else {
            coins.filter { coin ->
                coin.name.contains(query, ignoreCase = true) ||
                        coin.symbol.contains(query, ignoreCase = true)
            }
        }
    }

    // New function: Resets ALL filters (including search, sort, etc.)
    private fun resetAllFilters() {
        _uiState.update { state ->
            state.copy(
                searchQuery = "", // Clears search
                filteredCoins = state.coins, // Resets filtered list to all coins
                // Add any other filter-related state resets here (e.g., sortType = SortType.NAME_ASC, showOnlyPositiveChange = false)
            )
        }
    }

    // New function: Specifically resets only the search query
    private fun resetSearchQuery() {
        _uiState.update { state ->
            state.copy(
                searchQuery = "",
                filteredCoins = filterCoins(state.coins, "") // Re-filter with an empty query
            )
        }
    }
}

data class CoinListUiState(
    val coins: List<Coin> = emptyList(),
    val filteredCoins: List<Coin> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

sealed class CoinListUiEvent {
    object RefreshData : CoinListUiEvent() // Event for refreshing actual data
    data class OnSearchQueryChange(val query: String) : CoinListUiEvent()
    data class OnCoinClick(val coinId: String) : CoinListUiEvent()
    object ResetFilters : CoinListUiEvent() // Event for resetting all filters
    object ResetSearch : CoinListUiEvent() // Event for resetting just the search query
}


enum class SortType {
    NAME_ASC,
    NAME_DESC,
    PRICE_ASC,
    PRICE_DESC,
    CHANGE_ASC,
    CHANGE_DESC
}