package com.koin.data.coin.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoinMarketChartResponse(
    @Json(name = "prices")
    val prices: List<List<Double>>,
    @Json(name = "market_caps")
    val marketCaps: List<List<Double>>,
    @Json(name = "total_volumes")
    val totalVolumes: List<List<Double>>
)

data class PriceDataPoint(
    val timestamp: Long,
    val price: Double
)

fun CoinMarketChartResponse.toPriceDataPoints(): List<PriceDataPoint> {
    return prices.map { point ->
        PriceDataPoint(
            timestamp = point[0].toLong(),
            price = point[1]
        )
    }
}
