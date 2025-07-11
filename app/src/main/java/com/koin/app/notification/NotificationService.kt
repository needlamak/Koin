package com.koin.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.koin.R
import javax.inject.Inject

class NotificationService @Inject constructor(
    private val context: Context
) {

    companion object {
        const val CHANNEL_ID = "coin_purchase_channel"
        const val CHANNEL_NAME = "Coin Purchase Notifications"
        const val COIN_PURCHASE_NOTIFICATION_ID = 1
        const val WATCHLIST_NOTIFICATION_ID = 2
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for new coin purchases"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showCoinPurchaseNotification(coinName: String, amount: Double) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.koin) // Replace with your app's icon
            .setContentTitle("Coin Purchased!")
            .setContentText("You have successfully purchased $amount of $coinName.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(COIN_PURCHASE_NOTIFICATION_ID, notification)
    }

    fun showWatchlistNotification(coinName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app's icon
            .setContentTitle("Watchlist Updated!")
            .setContentText("$coinName has been added to your watchlist.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(WATCHLIST_NOTIFICATION_ID, notification)
    }
}
