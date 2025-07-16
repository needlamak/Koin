// In a separate file, e.g., WorkerDependenciesEntryPoint.kt
package com.koin.ui.pricealert

import com.koin.app.pricealert.PriceAlertNotificationManager
import com.koin.domain.coin.CoinRepository
import com.koin.domain.pricealert.CheckPriceAlertsUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Define an interface for the dependencies you want to expose
@EntryPoint
@InstallIn(SingletonComponent::class) // Or whichever component scope these dependencies live in
interface WorkerDependenciesEntryPoint {
    fun coinRepository(): CoinRepository
    fun checkPriceAlertsUseCase(): CheckPriceAlertsUseCase
    fun priceAlertNotificationManager(): PriceAlertNotificationManager
}