package com.koin.app.pricealert

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.koin.MainActivity
import com.koin.R
import com.koin.ui.pricealert.WorkerDependenciesEntryPoint
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PriceAlertService : Service() {

    companion object {
        const val FOREGROUND_SERVICE_ID = 1001
        const val CHANNEL_ID = "price_alert_service"
        const val WORK_NAME = "price_alert_periodic_work"
        
        fun start(context: Context) {
            val intent = Intent(context, PriceAlertService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stop(context: Context) {
            val intent = Intent(context, PriceAlertService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Timber.tag("PriceAlertService").d("Service created")
    }
    private suspend fun checkAndNotify() {
        try {
            val hiltEntryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                WorkerDependenciesEntryPoint::class.java
            )
            val coinRepository = hiltEntryPoint.coinRepository()
            val checkPriceAlertsUseCase = hiltEntryPoint.checkPriceAlertsUseCase()
            val notificationManager = hiltEntryPoint.priceAlertNotificationManager()

            // Refresh coins
            coinRepository.refreshCoins()

            // Fetch coin list
            val coinsResult = coinRepository.getAllCoins().first()
            if (coinsResult.isFailure) {
                Timber.tag("PriceAlertService")
                    .e("❌ Failed to get coins: ${coinsResult.exceptionOrNull()?.message}")
                return // Just exit the function, no retry
            }

            val coins = coinsResult.getOrNull() ?: emptyList()
            val alerts = checkPriceAlertsUseCase(coins)

            alerts.forEach { alert ->
                notificationManager.sendPriceAlertNotification(alert)
                Timber.tag("PriceAlertService")
                    .d("🔔 Sent notification for ${alert.alert.coinSymbol}")
            }

        } catch (e: Exception) {
            Timber.tag("PriceAlertService").e("❌ Error checking alerts: ${e.message}")
        }
    }



    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(FOREGROUND_SERVICE_ID, createServiceNotification())

        // Start your own repeating task
        serviceScope.launch {
            while (isActive) {
                checkAndNotify()
                delay(TimeUnit.SECONDS.toMillis(10)) // configurable
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }


    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Price Alert Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps price alerts running in background"
                setShowBadge(false)
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createServiceNotification(): Notification {
        // Create intent to open your main activity
        val intent = Intent(this, MainActivity::class.java) // Replace with your main activity
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Price Alerts Active")
            .setContentText("Monitoring cryptocurrency prices")
            .setSmallIcon(R.drawable.koin) // Use your app icon
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun schedulePeriodicWork() {
        val workRequest = PeriodicWorkRequestBuilder<PriceAlertWorker>(
            15, TimeUnit.MINUTES // Minimum interval for periodic work
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
        
        Timber.tag("PriceAlertService").d("Periodic work scheduled")
    }
}