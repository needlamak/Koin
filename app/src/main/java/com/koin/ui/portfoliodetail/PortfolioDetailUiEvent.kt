package com.koin.ui.portfoliodetail

import com.koin.data.coin.TimeRange
import com.koin.domain.model.Coin
import com.koin.domain.pricealert.PriceAlertType

sealed class PortfolioDetailUiEvent {
    object Refresh : PortfolioDetailUiEvent()
    data class TimeRangeSelected(val timeRange: com.koin.data.coin.TimeRange) : PortfolioDetailUiEvent()
    object ClearToast : PortfolioDetailUiEvent()
    data class SellCoin(val coinId: String, val quantity: Double, val pricePerCoin: Double) :
        PortfolioDetailUiEvent()
    object ShowCreateAlertDialog : PortfolioDetailUiEvent()
    object HideCreateAlertDialog : PortfolioDetailUiEvent()
    data class UpdateTargetPrice(val price: String) : PortfolioDetailUiEvent()
    data class UpdateAlertType(val alertType: com.koin.domain.pricealert.PriceAlertType) : PortfolioDetailUiEvent()
    object CreateAlert : PortfolioDetailUiEvent()
    object ShowBuyDialog : PortfolioDetailUiEvent()
    object HideBuyDialog : PortfolioDetailUiEvent()
    data class BuyCoin(val amount: Double) : PortfolioDetailUiEvent()
    object HideBuySuccessBottomSheet : PortfolioDetailUiEvent()
}