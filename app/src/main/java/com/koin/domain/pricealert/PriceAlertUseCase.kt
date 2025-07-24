package com.koin.domain.pricealert

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.koin.app.pricealert.PriceAlertWorker
import com.koin.data.pricealert.PriceAlertEntity
import com.koin.domain.coin.CoinRepository
import com.koin.domain.model.Coin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SchedulePriceAlertWorkerUseCase @Inject constructor(
    private val workManager: WorkManager
) {
    operator fun invoke() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Schedule periodic worker
        val periodicRequest = PeriodicWorkRequestBuilder<PriceAlertWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "price_alert_checker",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )

        // Trigger immediate one-time work
        val oneTimeRequest = OneTimeWorkRequestBuilder<PriceAlertWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(oneTimeRequest)

        Timber.tag("PriceAlert").d("Worker scheduled and triggered immediately")
    }
}
class CreatePriceAlertUseCase(
    private val repository: CoinRepository
) {
    suspend operator fun invoke(
        coinId: String,
        coinName: String,
        coinSymbol: String,
        coinImageUrl: String,
        targetPrice: Double,
        alertType: PriceAlertType
    ): Result<Unit> {
        val alert = PriceAlert(
            id = generateAlertId(),
            coinId = coinId,
            coinName = coinName,
            coinSymbol = coinSymbol,
            coinImageUrl = coinImageUrl,
            targetPrice = targetPrice,
            alertType = alertType
        )
        return repository.createPriceAlert(alert)
    }
    
    private fun generateAlertId(): String {
        return "alert_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}

class GetPriceAlertsUseCase(
    private val repository: CoinRepository
) {
    operator fun invoke(): Flow<Result<List<PriceAlert>>> {
        return repository.getAllPriceAlerts()
    }
}
class CheckPriceAlertsUseCase(
    private val repository: CoinRepository
) {
    suspend operator fun invoke(coins: List<Coin>): List<PriceAlertTrigger> {
        val triggers = mutableListOf<PriceAlertTrigger>()

        for (coin in coins) {
            val alertsResult = repository.getActiveAlertsForCoin(coin.id).first()
            if (alertsResult.isSuccess) {
                val alerts = alertsResult.getOrNull() ?: emptyList()
                val triggeredAt = System.currentTimeMillis()

                for (alert in alerts) {
                    if (shouldTriggerAlert(alert, coin.currentPrice)) {
                        triggers.add(
                            PriceAlertTrigger(
                                alert = alert,
                                currentPrice = coin.currentPrice,
                                priceChange = coin.priceChangePercentage24h,
                                triggeredAt = triggeredAt
                            )
                        )
                        repository.markAlertAsTriggered(alert.id, triggeredAt)
                    }
                }
            }
        }

        return triggers
    }

    private fun shouldTriggerAlert(alert: PriceAlert, currentPrice: Double): Boolean {
        return when (alert.alertType) {
            PriceAlertType.ABOVE -> currentPrice >= alert.targetPrice
            PriceAlertType.BELOW -> currentPrice <= alert.targetPrice
        }
    }
}

class DeletePriceAlertUseCase(
    private val repository: CoinRepository
) {
    suspend operator fun invoke(alertId: PriceAlertEntity): Result<Unit> {
        return repository.deletePriceAlert(alertId)
    }
}

