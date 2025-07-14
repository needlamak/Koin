package com.koin.ui.portfoliodetail

import com.koin.data.coin.TimeRange
import com.koin.domain.model.Coin
import com.koin.domain.pricealert.PriceAlertType

sealed class PortfolioDetailUiEvent {
    data object Refresh : PortfolioDetailUiEvent()
    data class TimeRangeSelected(val timeRange: TimeRange) : PortfolioDetailUiEvent()
    data class SellCoin(
        val coinId: String,
        val quantity: Double,
        val pricePerCoin: Double
    ) : PortfolioDetailUiEvent()
    data object ClearToast : PortfolioDetailUiEvent()
    data class ShowCreateAlertDialog(val coin: Coin) : PortfolioDetailUiEvent()
    data object HideCreateAlertDialog : PortfolioDetailUiEvent()
    data class UpdateTargetPrice(val price: String) : PortfolioDetailUiEvent()
    data class UpdateAlertType(val type: PriceAlertType) : PortfolioDetailUiEvent()
    data object CreateAlert : PortfolioDetailUiEvent()

}