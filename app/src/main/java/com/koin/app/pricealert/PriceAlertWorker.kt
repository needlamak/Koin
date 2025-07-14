package com.koin.app.pricealert

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.koin.domain.coin.CoinRepository
import com.koin.domain.pricealert.CheckPriceAlertsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first



@HiltWorker
class PriceAlertWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val coinRepository: CoinRepository,
    private val checkPriceAlertsUseCase: CheckPriceAlertsUseCase,
    private val notificationManager: PriceAlertNotificationManager
) : CoroutineWorker(context, workerParams) {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        return try {
            // Refresh coin data
            coinRepository.refreshCoins()
            
            // Get current coins
            val coinsResult = coinRepository.getAllCoins().first()
            if (coinsResult.isFailure) {
                return Result.retry()
            }
            
            val coins = coinsResult.getOrNull() ?: emptyList()
            
            // Check for triggered alerts
            val triggeredAlerts = checkPriceAlertsUseCase(coins)
            
            // Send notifications for triggered alerts
            triggeredAlerts.forEach { trigger ->
                notificationManager.sendPriceAlertNotification(trigger)
            }
            
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
    
    @AssistedFactory
    interface Factory {
        fun create(context: Context, params: WorkerParameters): PriceAlertWorker
    }
}