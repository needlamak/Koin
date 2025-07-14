package com.koin.ui.pricealert

import com.koin.domain.model.Coin
import com.koin.domain.pricealert.PriceAlert
import com.koin.domain.pricealert.PriceAlertType

data class PriceAlertUiState(
    val alerts: List<PriceAlert> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCreateDialog: Boolean = false,
    val selectedCoin: Coin? = null
)

data class CreateAlertUiState(
    val targetPrice: String = "",
    val alertType: PriceAlertType = PriceAlertType.ABOVE,
    val isLoading: Boolean = false,
    val error: String? = null
)