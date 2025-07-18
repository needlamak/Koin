package com.koin.app.pricealert

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class PriceAlertServiceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) : DefaultLifecycleObserver {

    companion object {
        private const val PREF_SERVICE_ENABLED = "price_alert_service_enabled"
    }

    var isServiceEnabled: Boolean
        get() = sharedPreferences.getBoolean(PREF_SERVICE_ENABLED, false)
        set(value) {
            sharedPreferences.edit { putBoolean(PREF_SERVICE_ENABLED, value) }
            if (value) {
                startService()
            } else {
                stopService()
            }
        }

    fun startService() {
        try {
            PriceAlertService.start(context)
            Timber.tag("ServiceManager").d("Service started")
        } catch (e: Exception) {
            Timber.tag("ServiceManager").e("Failed to start service: ${e.message}")
        }
    }

    fun stopService() {
        try {
            PriceAlertService.stop(context)
            Timber.tag("ServiceManager").d("Service stopped")
        } catch (e: Exception) {
            Timber.tag("ServiceManager").e("Failed to stop service: ${e.message}")
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        // Auto-start service if enabled
        if (isServiceEnabled) {
            startService()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        // Service will continue running in background
    }
}