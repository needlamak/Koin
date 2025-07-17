package com.koin.app.pricealert

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.koin.ui.pricealert.WorkerDependenciesEntryPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import timber.log.Timber

@HiltWorker
class PriceAlertWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val startTime = System.currentTimeMillis()
        Timber.tag("PriceAlertWorker").d("🚀 Starting work - Run attempt: ") //${runAttempt}

        return try {
            // Add progress tracking
            setProgress(workDataOf("status" to "initializing"))

            val appContext = applicationContext
            val hiltEntryPoint = EntryPointAccessors.fromApplication(
                appContext,
                WorkerDependenciesEntryPoint::class.java
            )

            val coinRepository = hiltEntryPoint.coinRepository()
            val checkPriceAlertsUseCase = hiltEntryPoint.checkPriceAlertsUseCase()
            val notificationManager = hiltEntryPoint.priceAlertNotificationManager()

            // Step 1: Refresh coins
            Timber.tag("PriceAlertWorker").d("🔄 Refreshing coin data...")
            setProgress(workDataOf("status" to "refreshing_coins"))

            withTimeout(30_000) { // 30 second timeout
                coinRepository.refreshCoins()
            }

            Timber.tag("PriceAlertWorker").d("✅ Coin data refreshed")

            // Step 2: Get coins
            setProgress(workDataOf("status" to "fetching_coins"))
            val coinsResult = coinRepository.getAllCoins().first()

            if (coinsResult.isFailure) {
                Timber.tag("PriceAlertWorker")
                    .e("❌ Failed to get coins: ${coinsResult.exceptionOrNull()?.message}")
                return Result.retry()
            }

            val coins = coinsResult.getOrNull() ?: emptyList()
            Timber.tag("PriceAlertWorker").d("📊 Retrieved ${coins.size} coins")

            // Step 3: Check alerts
            setProgress(workDataOf("status" to "checking_alerts"))
            val triggeredAlerts = checkPriceAlertsUseCase(coins)
            Timber.tag("PriceAlertWorker").d("🔔 Found ${triggeredAlerts.size} triggered alerts")

            // Step 4: Send notifications
            setProgress(workDataOf("status" to "sending_notifications"))
            triggeredAlerts.forEach { trigger ->
                Timber.tag("PriceAlertWorker")
                    .d("📤 Sending notification for ${trigger.alert.coinSymbol}")
                notificationManager.sendPriceAlertNotification(trigger)
            }

            val duration = System.currentTimeMillis() - startTime
            Timber.tag("PriceAlertWorker").d("✅ Work completed successfully in ${duration}ms")

            Result.success(
                workDataOf(
                    "completed_at" to System.currentTimeMillis(),
                    "alerts_checked" to triggeredAlerts.size,
                    "duration_ms" to duration
                )
            )

        } catch (e: TimeoutCancellationException) {
            Timber.tag("PriceAlertWorker").e("⏰ Work timed out: ${e.message}")
            Result.retry()
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            Timber.tag("PriceAlertWorker").e("❌ Work failed after ${duration}ms: ${e.message}")
            Result.failure(workDataOf("error" to e.message))
        }
    }
}