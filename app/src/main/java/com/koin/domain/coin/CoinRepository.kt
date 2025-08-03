package com.koin.domain.coin

import android.content.Context
import com.koin.data.coin.TimeRange
import com.koin.data.coin.dto.PriceDataPoint
import com.koin.data.pricealert.PriceAlertEntity
import com.koin.domain.model.Coin
import com.koin.domain.pricealert.PriceAlert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

// Repository interface update
interface CoinRepository {
    fun getAllCoins(): Flow<Result<List<Coin>>>
    fun getCoinById(id: String?): Flow<Result<Coin?>>
    suspend fun refreshCoins(): Result<Unit> // Changed return type
    suspend fun getCoinMarketChart(
        coinId: String,
        timeRange: TimeRange,
        vsCurrency: String
    ): List<PriceDataPoint>
    val lastError: StateFlow<String?> // Expose error state
//    fun getAllCoins(): Flow<Result<List<Coin>>>
//    fun getCoinById(id: String?): Flow<Result<Coin?>>
//    suspend fun refreshCoins()
//    suspend fun getCoinMarketChart(
//        coinId: String,
//        timeRange: TimeRange,
//        vsCurrency: String = "usd"
//    ): List<PriceDataPoint>

    // Add to existing CoinRepository interface
    suspend fun createPriceAlert(alert: PriceAlert): Result<Unit>
//    suspend fun updatePriceAlert(alert: PriceAlert): Result<Unit>
    suspend fun deletePriceAlert(alertId: PriceAlertEntity): Result<Unit>
    fun getAllPriceAlerts(): Flow<Result<List<PriceAlert>>>
    fun getActiveAlertsForCoin(coinId: String): Flow<Result<List<PriceAlert>>>
    suspend fun markAlertAsTriggered(alertId: String, triggeredAt: Long): Result<Unit>
//    suspend fun deactivateAlert(alertId: String): Result<Unit>
}
