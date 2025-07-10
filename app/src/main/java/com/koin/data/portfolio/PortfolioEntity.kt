package com.koin.data.portfolio

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "portfolio_holdings")
data class PortfolioHoldingEntity(
    @PrimaryKey val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinImageUrl: String,
    val quantity: Double,
    val averagePurchasePrice: Double,
    val totalTransactionFees: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "portfolio_transactions")
data class PortfolioTransactionEntity(
    @PrimaryKey val id: String,
    val coinId: String,
    val type: String, // BUY or SELL
    val quantity: Double,
    val pricePerCoin: Double,
    val transactionFee: Double,
    val timestamp: Long
)

@Entity(tableName = "portfolio_balance")
data class PortfolioBalanceEntity(
    @PrimaryKey val id: Int = 1, // Single row table
    val balance: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)
