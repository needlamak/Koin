package com.koin

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.koin.app.pricealert.PriceAlertForegroundService
import com.koin.app.pricealert.PriceAlertWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class CryptoApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var portfolioInitializer: com.koin.data.portfolio.PortfolioInitializer

    override fun onCreate() {
        super.onCreate()
        portfolioInitializer.initialize()
        // To start monitoring
        PriceAlertForegroundService.start(this)
        // To stop monitoring
        PriceAlertForegroundService.stop(this)
        setupPriceAlertWorker()

        Amplify.addPlugin(AWSCognitoAuthPlugin())
        Amplify.configure(applicationContext)
        Timber.tag("KoinApp").i("Initialized Amplify")
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG) // ✅ Add this line
            .setMaxSchedulerLimit(50) // Increase scheduler limit
            .build()

    private fun setupPriceAlertWorker() {
        // Cancel existing work first
        WorkManager.getInstance(this).cancelUniqueWork("price_alert_checker")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false) // Allow when battery is low
            .setRequiresDeviceIdle(false)    // Don't wait for idle
            .build()

        // Increase interval - Android 14 throttles frequent work
        val workRequest = PeriodicWorkRequestBuilder<PriceAlertWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag("price_alerts")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "price_alert_checker",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

        WorkManager.getInstance(this)
            .getWorkInfosForUniqueWorkLiveData("price_alert_checker")
            .observeForever { workInfos ->
                workInfos?.forEach { workInfo ->
                    Timber.tag("WorkManager").d("Work status: ${workInfo.state}")
                }
            }
    }
}
