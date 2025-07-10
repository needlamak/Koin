package com.koin.ui.portfolio

import com.koin.domain.portfolio.Portfolio
import com.koin.domain.portfolio.PortfolioHolding

data class PortfolioUiState(
    val isLoading: Boolean = false,
    val portfolio: Portfolio = Portfolio.empty(),
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val showBuyDialog: Boolean = false,
    val selectedCoinForBuy: String? = null,
    val showBottomSheet: Boolean = false,
    val selectedTimeRange: TimeRange = TimeRange.ALL
)

sealed class PortfolioUiEvent {
    object RefreshData : PortfolioUiEvent()
    object ShowBottomSheet : PortfolioUiEvent()
    object HideBottomSheet : PortfolioUiEvent()
    object HideBuyDialog : PortfolioUiEvent()
    data class ShowBuyDialog(val coinId: String) : PortfolioUiEvent()
    data class BuyCoin(
        val coinId: String,
        val quantity: Double,
        val pricePerCoin: Double
    ) : PortfolioUiEvent()
    data class SellCoin(
        val coinId: String,
        val quantity: Double,
        val pricePerCoin: Double
    ) : PortfolioUiEvent()
    object ResetPortfolio : PortfolioUiEvent()
    data class SelectTimeRange(val timeRange: TimeRange) : PortfolioUiEvent()
}

// Time range enum for chart selection
enum class TimeRange {
    DAY,
    WEEK,
    MONTH,
    YEAR,
    ALL
}

// States for buy dialog
data class BuyDialogState(
    val isLoading: Boolean = false,
    val quantity: String = "",
    val estimatedCost: Double = 0.0,
    val error: String? = null
)
