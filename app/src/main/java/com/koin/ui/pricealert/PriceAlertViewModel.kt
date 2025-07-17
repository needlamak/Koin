package com.koin.ui.pricealert

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.koin.app.pricealert.PriceAlertWorker
import com.koin.domain.model.Coin
import com.koin.domain.pricealert.CreatePriceAlertUseCase
import com.koin.domain.pricealert.DeletePriceAlertUseCase
import com.koin.domain.pricealert.GetPriceAlertsUseCase
import com.koin.domain.pricealert.PriceAlertType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
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

    fun createAlert(context: Context) {
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

                // ✅ 1. Log message
                Timber.tag("PriceAlert")
                    .d("Price alert created for ${selectedCoin.symbol.uppercase()} at $$targetPrice")

                // ✅ 2. Toast message
                Toast.makeText(
                    context,
                    "${selectedCoin.symbol.uppercase()} alert activated at $$targetPrice",
                    Toast.LENGTH_SHORT
                ).show()

                // ✅ 3. Schedule periodic worker
                // 🔁 Schedule periodic worker (if not already done)
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val periodicRequest =
                    PeriodicWorkRequestBuilder<PriceAlertWorker>(15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build()

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "price_alert_checker",
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicRequest
                )

                // ✅ Trigger immediate one-time work
                val oneTimeRequest = OneTimeWorkRequestBuilder<PriceAlertWorker>()
                    .setConstraints(constraints)
                    .build()

                WorkManager.getInstance(context).enqueue(oneTimeRequest)

                Timber.tag("PriceAlert").d("One-time worker triggered immediately")

            } else {
                _createAlertState.value = _createAlertState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
}