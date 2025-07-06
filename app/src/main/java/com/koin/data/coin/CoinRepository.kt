package com.koin.data.coin

import com.koin.data.coin.dto.PriceDataPoint
import com.koin.data.coin.dto.toPriceDataPoints
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinRepository @Inject constructor(
    private val api: CoinGeckoApiService,
    // Add database instance here when implementing caching
) {
    suspend fun getCoinMarketChart(
        coinId: String,
        timeRange: TimeRange,
        vsCurrency: String = "usd"
    ): List<PriceDataPoint> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis / 1000 // Current time in seconds
        
        // Calculate start time based on the selected time range
        timeRange.days?.let { calendar.add(Calendar.DAY_OF_YEAR, -it) }
        val startTime = calendar.timeInMillis / 1000 // Start time in seconds
        
        return try {
            val response = api.getCoinMarketChartRange(
                id = coinId,
                vsCurrency = vsCurrency,
                from = startTime,
                to = endTime
            )
            response.toPriceDataPoints()
        } catch (e: Exception) {
            // Handle error (e.g., log, return empty list, or rethrow)
            emptyList()
        }
    }
    
    // Add other repository methods here (getCoins, getCoinDetails, etc.)
}

// Add this to your existing TimeRange.kt file if it doesn't exist
 enum class TimeRange(val days: Int) {
     DAY(1),
     WEEK(7),
     MONTH(30),
     YEAR(365),
     FIVE_YEARS(5 * 365)
 }
