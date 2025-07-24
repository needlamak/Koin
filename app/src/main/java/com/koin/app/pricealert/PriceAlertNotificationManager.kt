package com.koin.app.pricealert

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.koin.MainActivity
import com.koin.R
import com.koin.domain.pricealert.PriceAlertTrigger
import com.koin.domain.pricealert.PriceAlertType
import timber.log.Timber
import java.util.Locale

class PriceAlertNotificationManager(
    private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PRICE_ALERT_CHANNEL_ID,
                "Price Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for cryptocurrency price alerts"
                enableLights(true)
                enableVibration(true)
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(trigger: PriceAlertTrigger): Notification {
        val alert = trigger.alert
        val title = "${alert.coinSymbol.uppercase()} Price Alert"
        val message = when (alert.alertType) {
            PriceAlertType.ABOVE -> "${alert.coinName} has reached $${
                String.format(Locale.US, "%.6f", trigger.currentPrice)
            } (above your target of $${String.format(Locale.US, "%.6f", alert.targetPrice)})"

            PriceAlertType.BELOW -> "${alert.coinName} has dropped to $${
                String.format(Locale.US, "%.6f", trigger.currentPrice)
            } (below your target of $${String.format(Locale.US, "%.6f", alert.targetPrice)})"
        }

        // Intent to open the app when the notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("coin_id", alert.coinId) // Optional: pass coinId for deep linking
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            alert.id.hashCode(), // Unique request code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, PRICE_ALERT_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.koin)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setGroup("price_alerts_group") // Optional: for grouping notifications
            .build()
    }

    fun sendPriceAlertNotification(trigger: PriceAlertTrigger) {
        // Check if notifications are enabled
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            Timber.tag("PriceAlertNotificationM").w("Notifications are disabled")
            return
        }

        val notification = createNotification(trigger)
        notificationManager.notify(trigger.alert.id.hashCode(), notification)

        Timber.tag("PriceAlertNotificationM")
            .d("Notification sent for ${trigger.alert.coinSymbol}")
    }

    companion object {
        const val PRICE_ALERT_CHANNEL_ID = "price_alerts"
    }
}