package com.koin.ui.coindetail

import androidx.lifecycle.SavedStateHandle
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
class CoinDetailViewModel @Inject constructor(
    private val repository: CoinRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CoinDetailUiState, CoinDetailUiEvent>() {

    private val coinId: String = checkNotNull(savedStateHandle["coinId"])
    
    override val _uiState: MutableStateFlow<CoinDetailUiState> = 
        MutableStateFlow(CoinDetailUiState(coinId = coinId))

    init {
        loadCoin()
    }

    override fun handleEvent(event: CoinDetailUiEvent) {
        when (event) {
            is CoinDetailUiEvent.Refresh -> loadCoin()
        }
    }

    private fun loadCoin() {
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            repository.getCoinById(coinId)
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            error = e.message,
                            isLoading = false,
                            isRefreshing = false
                        ) 
                    }
                }
                .collectLatest { result ->
                    result.onSuccess { coin ->
                        _uiState.update {
                            it.copy(
                                coin = coin,
                                isLoading = false,
                                isRefreshing = false,
                                error = null
                            )
                        }
                    }.onFailure { e ->
                        _uiState.update { 
                            it.copy(
                                error = e.message,
                                isLoading = false,
                                isRefreshing = false
                            ) 
                        }
                    }
                }
        }
    }
}

data class CoinDetailUiState(
    val coinId: String,
    val coin: Coin? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

sealed class CoinDetailUiEvent {
    object Refresh : CoinDetailUiEvent()
}
