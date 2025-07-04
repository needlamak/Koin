package com.koin.data.coin

import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoApiService {
    @GET("coins/markets")
    suspend fun getCoinsWithFullDetails(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = true,
        @Query("price_change_percentage") priceChange: String = "1h,24h,7d,30d"
    ): List<CoinDetailDto>
} 