package com.koin

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.koin.app.pricealert.PriceAlertWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class CryptoApp : Application() {

    @Inject
    lateinit var portfolioInitializer: com.koin.data.portfolio.PortfolioInitializer

    override fun onCreate() {
        super.onCreate()
        portfolioInitializer.initialize()
        // Initialize any application-wide components here
    }
    private fun setupPriceAlertWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<PriceAlertWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "price_alert_checker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

}
