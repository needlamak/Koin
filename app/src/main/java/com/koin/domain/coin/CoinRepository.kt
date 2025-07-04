package com.koin.domain.coin

import kotlinx.coroutines.flow.Flow

interface CoinRepository {
    fun getAllCoins(): Flow<Result<List<Coin>>>
    fun getCoinById(id: String): Flow<Result<Coin?>>
    suspend fun refreshCoins()
} 