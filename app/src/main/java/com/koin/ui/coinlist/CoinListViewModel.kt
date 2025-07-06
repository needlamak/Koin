package com.koin.ui.coinlist

import androidx.lifecycle.viewModelScope
import com.koin.domain.coin.Coin
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
            is CoinListUiEvent.Refresh -> refreshCoins()
            is CoinListUiEvent.OnSearchQueryChange -> updateSearchQuery(event.query)
            is CoinListUiEvent.OnCoinClick -> { /* Handle navigation to detail */ }
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

    private fun refreshCoins() {
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            try {
                // Assuming repository has a refresh method
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
    object Refresh : CoinListUiEvent()
    data class OnSearchQueryChange(val query: String) : CoinListUiEvent()
    data class OnCoinClick(val coinId: String) : CoinListUiEvent()
}
