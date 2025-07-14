package com.koin.app.pricealert

import android.Manifest
import android.app.Notification
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.koin.R
import com.koin.domain.pricealert.PriceAlertTrigger
import com.koin.domain.pricealert.PriceAlertType

class PriceAlertNotificationManager(
    private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)
    
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendPriceAlertNotification(trigger: PriceAlertTrigger) {
        val notification: Notification = createNotification(trigger)
        notificationManager.notify(trigger.alert.id.hashCode(), notification)
    }
    
    private fun createNotification(trigger: PriceAlertTrigger): Notification {
        val alert = trigger.alert
        val title = "${alert.coinSymbol.uppercase()} Price Alert"
        val message = when (alert.alertType) {
            PriceAlertType.ABOVE -> "${alert.coinName} has reached $${trigger.currentPrice} (above your target of $${alert.targetPrice})"
            PriceAlertType.BELOW -> "${alert.coinName} has dropped to $${trigger.currentPrice} (below your target of $${alert.targetPrice})"
        }
        
        return NotificationCompat.Builder(context, PRICE_ALERT_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.koin)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
    }
    
    companion object {
        const val PRICE_ALERT_CHANNEL_ID = "price_alerts"
    }
}