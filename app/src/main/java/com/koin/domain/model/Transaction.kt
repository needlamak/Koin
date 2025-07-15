package com.koin.domain.model

data class Transaction(
    val id: String,
    val coinId: String,
    val type: String, // "BUY" or "SELL"
    val quantity: Double,
    val pricePerCoin: Double,
    val transactionFee: Double,
    val timestamp: Long,
    val coinName: String,
    val coinSymbol: String,
    val coinImage: String?
)