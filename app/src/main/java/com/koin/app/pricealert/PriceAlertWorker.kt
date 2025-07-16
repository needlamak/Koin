package com.koin.app.pricealert

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.koin.ui.pricealert.WorkerDependenciesEntryPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import timber.log.Timber

@HiltWorker
class PriceAlertWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    @dagger.assisted.AssistedFactory
    interface Factory {
        fun create(context: Context, params: WorkerParameters): PriceAlertWorker
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        Timber.tag("PriceAlertWorker").d("Starting work")

        // 1. Get the application context
        val appContext = applicationContext
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            appContext,
            WorkerDependenciesEntryPoint::class.java
        )

        // 3. Retrieve the dependencies
        val coinRepository = hiltEntryPoint.coinRepository()
        val checkPriceAlertsUseCase = hiltEntryPoint.checkPriceAlertsUseCase()
        val notificationManager = hiltEntryPoint.priceAlertNotificationManager()

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
            Timber.tag("PriceAlertWorker").d("Checking for triggered alerts")
            val triggeredAlerts = checkPriceAlertsUseCase(coins)

            Timber.tag("PriceAlertWorker").d("Found ${triggeredAlerts.size} triggered alerts")
            // Send notifications for triggered alerts
            triggeredAlerts.forEach { trigger ->
                Timber.tag("PriceAlertWorker")
                    .d("Sending notification for ${trigger.alert.coinSymbol}")
                notificationManager.sendPriceAlertNotification(trigger)
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}