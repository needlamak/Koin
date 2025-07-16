package com.koin.ui.totalbalance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koin.domain.model.Transaction
import com.koin.domain.portfolio.PortfolioRepository
import com.koin.domain.transaction.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TotalBalanceViewModel @Inject constructor(
    private val portfolioRepository: PortfolioRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TotalBalanceUiState())
    val uiState: StateFlow<TotalBalanceUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                portfolioRepository.getBalance(),
                transactionRepository.getAllTransactions()
            ) { balance, transactions ->
                _uiState.update {
                    it.copy(
                        totalBalance = balance?.balance ?: 0.0,
                        recentTransactions = transactions.take(5), // Take top 5 recent transactions
                        isLoading = false,
                        error = null
                    )
                }
            }.catch { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }.collect { /* Collect to trigger the flow */ }
        }
    }
}

data class TotalBalanceUiState(
    val totalBalance: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
