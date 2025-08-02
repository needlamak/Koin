package com.koin.ui.portfoliodetail

import com.koin.data.coin.TimeRange
import com.koin.data.coin.dto.PriceDataPoint
import com.koin.domain.portfolio.PortfolioHolding
import com.koin.ui.portfoliodetail.SellTransactionDetails

data class PortfolioDetailUiState(
    val isLoading: Boolean = false,
    val portfolioCoin: PortfolioHolding? = null,
    val error: String? = null,
    val selectedTimeRange: TimeRange = TimeRange.ONE_DAY,
    val historicalData: List<PriceDataPoint> = emptyList(),
    val isLoadingHistoricalData: Boolean = false,
    val transactionSuccess: Boolean = false,
    val soldCoinDetails: SellTransactionDetails? = null,
    val showBuyDialog: Boolean = false,
    val showBuySuccessBottomSheet: Boolean = false,
    val buyTransactionDetails: com.koin.ui.coinlist.BuyTransactionDetails? = null
)