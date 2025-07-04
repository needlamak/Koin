package com.koin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CryptoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any application-wide components here
    }
}
