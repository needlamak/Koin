package com.koin.domain.pricealert

data class PriceAlert(
    val id: String,
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinImageUrl: String,
    val targetPrice: Double,
    val alertType: PriceAlertType,
    val isActive: Boolean = true,
    val isTriggered: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val triggeredAt: Long? = null
)

enum class PriceAlertType {
    ABOVE, BELOW
}

data class PriceAlertTrigger(
    val alert: PriceAlert,
    val currentPrice: Double,
    val priceChange: Double,
    val triggeredAt: Long?
)