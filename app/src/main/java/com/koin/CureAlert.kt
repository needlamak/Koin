package com.koin

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.koin.app.pricealert.PriceAlertWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


//@HiltAndroidApp
class CureAlertApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Initialize worker for testing
        initializeCureAlertWorker()
        portfolioInitializer.initialize()
    }

    @Inject
    lateinit var portfolioInitializer: com.koin.data.portfolio.PortfolioInitializer

    private fun initializeCureAlertWorker() {
        val workManager = WorkManager.getInstance(this)

        val workRequest = PeriodicWorkRequestBuilder<PriceAlertWorker>(
            15, TimeUnit.MINUTES
        ).setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "Cure_alert_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        Timber.d("CureAlertWorker initialized and scheduled")
    }
}

@HiltWorker
class CureAlertWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @AssistedFactory
    interface Factory {
        fun create(context: Context, workerParams: WorkerParameters): CureAlertWorker
    }

    override suspend fun doWork(): Result {
        return try {
            Timber.d("CureAlertWorker started executing")

            // Simulate Cure checking work
            delay(2000)

            Timber.d("CureAlertWorker completed successfully")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "CureAlertWorker failed")
            Result.failure()
        }
    }
}



data class CureAlert(
    val id: String,
    val productName: String,
    val targetCure: Double,
    val currentCure: Double,
    val isActive: Boolean = true
)
