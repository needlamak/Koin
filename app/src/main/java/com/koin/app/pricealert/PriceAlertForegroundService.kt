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
import com.koin.MainActivity
import com.koin.R
import com.koin.domain.coin.CoinRepository
import com.koin.domain.pricealert.CheckPriceAlertsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PriceAlertForegroundService : Service() {

    companion object {
        private const val SERVICE_ID = 1001
        private const val CHANNEL_ID = "price_alert_service"
        private const val CHECK_INTERVAL_MS = 5 * 60 * 1000L // 5 minutes

        fun start(context: Context) {
            val intent = Intent(context, PriceAlertForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, PriceAlertForegroundService::class.java)
            context.stopService(intent)
        }
    }

    @Inject
    lateinit var coinRepository: CoinRepository

    @Inject
    lateinit var checkPriceAlertsUseCase: CheckPriceAlertsUseCase

    @Inject
    lateinit var notificationManager: PriceAlertNotificationManager

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var monitoringJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(SERVICE_ID, createServiceNotification())
        startPriceMonitoring()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        monitoringJob?.cancel()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startPriceMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = serviceScope.launch {
            while (isActive) {
                checkPriceAlerts()
                delay(CHECK_INTERVAL_MS)
            }
        }
    }

    private suspend fun checkPriceAlerts() {
        try {
            coinRepository.refreshCoins()
            val coinsResult = coinRepository.getAllCoins().first()

            if (coinsResult.isSuccess) {
                val coins = coinsResult.getOrNull() ?: emptyList()
                val triggeredAlerts = checkPriceAlertsUseCase(coins)

                triggeredAlerts.forEach { trigger ->
                    notificationManager.sendPriceAlertNotification(trigger)
                }

                Timber.tag("PriceAlertService")
                    .d("Checked alerts, found ${triggeredAlerts.size} triggers")
            }
        } catch (e: Exception) {
            Timber.tag("PriceAlertService").e("Failed to check alerts: ${e.message}")
        }
    }

    private fun createServiceNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Price Alerts Active")
            .setContentText("Monitoring cryptocurrency prices")
            .setSmallIcon(R.drawable.koin)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Price Alert Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background price monitoring service"
                setShowBadge(false)
                setSound(null, null)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}