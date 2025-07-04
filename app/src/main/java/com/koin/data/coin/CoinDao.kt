package com.koin.data.coin

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {
    @Query("SELECT * FROM coins ORDER BY marketCapRank ASC")
    fun getAllCoins(): Flow<List<CoinEntity>>

    @Query("SELECT * FROM coins WHERE id = :coinId")
    fun getCoinById(coinId: String): Flow<CoinEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coins: List<CoinEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoin(coin: CoinEntity)

    @Query("DELETE FROM coins")
    suspend fun deleteAllCoins()

    @Query("SELECT COUNT(*) FROM coins")
    suspend fun getCoinCount(): Int
} 