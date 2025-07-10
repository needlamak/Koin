package com.koin.ui.portfoliodetail

import com.koin.data.coin.TimeRange

sealed class PortfolioDetailUiEvent {
    data object Refresh : PortfolioDetailUiEvent()
    data class TimeRangeSelected(val timeRange: TimeRange) : PortfolioDetailUiEvent()
    data object ClearToast : PortfolioDetailUiEvent()
}