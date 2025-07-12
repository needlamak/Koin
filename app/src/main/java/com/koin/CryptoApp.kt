package com.koin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
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
}
