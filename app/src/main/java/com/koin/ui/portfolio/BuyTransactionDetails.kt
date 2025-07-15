package com.koin.ui.portfolio

data class BuyTransactionDetails(
    val coinName: String,
    val coinSymbol: String,
    val coinImage: String?,
    val quantity: Double,
    val totalPrice: Double
)