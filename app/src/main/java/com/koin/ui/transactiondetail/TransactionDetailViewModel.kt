package com.koin.ui.transactiondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koin.domain.model.Transaction
import com.koin.domain.transaction.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val transactionId: String = checkNotNull(savedStateHandle["transactionId"])

    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState: StateFlow<TransactionDetailUiState> = _uiState

    init {
        loadTransactionDetail()
    }

    private fun loadTransactionDetail() {
        viewModelScope.launch {
            transactionRepository.getAllTransactions()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collectLatest { transactions ->
                    val transaction = transactions.find { it.id == transactionId }
                    _uiState.update { it.copy(transaction = transaction, isLoading = false, error = null) }
                }
        }
    }
}