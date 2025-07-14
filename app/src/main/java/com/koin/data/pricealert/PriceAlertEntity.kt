package com.koin.data.pricealert

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_alerts")
data class PriceAlertEntity(
    @PrimaryKey val id: String,
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinImageUrl: String,
    val targetPrice: Double,
    val alertType: String, // "ABOVE" or "BELOW"
    val isActive: Boolean = true,
    val isTriggered: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val triggeredAt: Long? = null
)