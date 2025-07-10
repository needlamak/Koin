package com.koin.domain.portfolio

import kotlinx.coroutines.flow.Flow

interface PortfolioRepository {
    fun getPortfolio(): Flow<Result<Portfolio>>
    
    suspend fun buyCoin(
        coinId: String,
        quantity: Double,
        pricePerCoin: Double,
        transactionFee: Double = 0.0
    ): Result<Unit>
    
    suspend fun sellCoin(
        coinId: String,
        quantity: Double,
        pricePerCoin: Double,
        transactionFee: Double = 0.0
    ): Result<Unit>
    
    suspend fun refreshPortfolio()
    
    suspend fun getTransactionHistory(): List<Transaction>
    
    suspend fun getBalance(): Double
    
    suspend fun resetPortfolio()

    suspend fun addCoinToHoldingForTest(coinId: String, quantity: Double, pricePerCoin: Double): Result<Unit>
}
