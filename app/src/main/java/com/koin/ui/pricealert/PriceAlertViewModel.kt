package com.koin.ui.pricealert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koin.data.pricealert.PriceAlertEntity
import com.koin.domain.model.Coin
import com.koin.domain.pricealert.CreatePriceAlertUseCase
import com.koin.domain.pricealert.DeletePriceAlertUseCase
import com.koin.domain.pricealert.GetPriceAlertsUseCase
import com.koin.domain.pricealert.PriceAlertType
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PriceAlertViewModel @Inject constructor(
    private val getPriceAlertsUseCase: GetPriceAlertsUseCase,
    private val createPriceAlertUseCase: CreatePriceAlertUseCase,
    private val deletePriceAlertUseCase: DeletePriceAlertUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PriceAlertUiState())
    val uiState: StateFlow<PriceAlertUiState> = _uiState.asStateFlow()
    
    private val _createAlertState = MutableStateFlow(CreateAlertUiState())
    val createAlertState: StateFlow<CreateAlertUiState> = _createAlertState.asStateFlow()
    
    init {
        loadPriceAlerts()
    }
    
    private fun loadPriceAlerts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            getPriceAlertsUseCase()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
                .collect { result ->
                    _uiState.value = if (result.isSuccess) {
                        _uiState.value.copy(
                            alerts = result.getOrNull() ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    } else {
                        _uiState.value.copy(
                            isLoading = false,
                            error = result.exceptionOrNull()?.message
                        )
                    }
                }
        }
    }
    
    fun showCreateAlertDialog(coin: Coin) {
        _uiState.value = _uiState.value.copy(
            showCreateDialog = true,
            selectedCoin = coin
        )
    }
    
    fun hideCreateAlertDialog() {
        _uiState.value = _uiState.value.copy(
            showCreateDialog = false,
            selectedCoin = null
        )
        _createAlertState.value = CreateAlertUiState()
    }
    
    fun updateTargetPrice(price: String) {
        _createAlertState.value = _createAlertState.value.copy(targetPrice = price)
    }
    
    fun updateAlertType(type: PriceAlertType) {
        _createAlertState.value = _createAlertState.value.copy(alertType = type)
    }
    
    fun createAlert() {
        val selectedCoin = _uiState.value.selectedCoin ?: return
        val targetPrice = _createAlertState.value.targetPrice.toDoubleOrNull() ?: return
        
        viewModelScope.launch {
            _createAlertState.value = _createAlertState.value.copy(isLoading = true)
            
            val result = createPriceAlertUseCase(
                coinId = selectedCoin.id,
                coinName = selectedCoin.name,
                coinSymbol = selectedCoin.symbol,
                coinImageUrl = selectedCoin.imageUrl,
                targetPrice = targetPrice,
                alertType = _createAlertState.value.alertType
            )
            
            if (result.isSuccess) {
                hideCreateAlertDialog()
                loadPriceAlerts()
            } else {
                _createAlertState.value = _createAlertState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    fun deleteAlert(alertId: PriceAlertEntity) {
        viewModelScope.launch {
            deletePriceAlertUseCase(alertId)
            loadPriceAlerts()
        }
    }
}