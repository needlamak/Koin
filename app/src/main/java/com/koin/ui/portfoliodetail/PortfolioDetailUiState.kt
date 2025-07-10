package com.koin.ui.portfoliodetail

import com.koin.data.coin.TimeRange
import com.koin.data.coin.dto.PriceDataPoint
import com.koin.domain.portfolio.PortfolioHolding

data class PortfolioDetailUiState(
    val isLoading: Boolean = false,
    val portfolioCoin: PortfolioHolding? = null,
    val error: String? = null,
    val selectedTimeRange: TimeRange = TimeRange.ONE_DAY,
    val historicalData: List<PriceDataPoint> = emptyList(),
    val isLoadingHistoricalData: Boolean = false
)