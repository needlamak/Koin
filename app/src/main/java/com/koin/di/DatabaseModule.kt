package com.koin.di

import android.content.Context
import androidx.room.Room
import com.koin.app.pricealert.PriceAlertDao
import com.koin.app.pricealert.PriceAlertNotificationManager
import com.koin.data.coin.CoinDao
import com.koin.data.coin.CoinDatabase
import com.koin.data.portfolio.PortfolioDao
import com.koin.domain.coin.CoinRepository
import com.koin.domain.pricealert.CheckPriceAlertsUseCase
import com.koin.domain.pricealert.CreatePriceAlertUseCase
import com.koin.domain.pricealert.DeletePriceAlertUseCase
import com.koin.domain.pricealert.GetPriceAlertsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideCoinDatabase(@ApplicationContext context: Context): CoinDatabase {
        return Room.databaseBuilder(
            context,
            CoinDatabase::class.java,
            "coin_database"
        )
            .addMigrations(
                CoinDatabase.Companion.MIGRATION_1_2,
                CoinDatabase.Companion.MIGRATION_2_3,
                CoinDatabase.Companion.MIGRATION_3_4
            )
            .build()
    }

    @Provides
    fun provideCoinDao(database: CoinDatabase): CoinDao {
        return database.coinDao()
    }

    @Provides
    fun providePortfolioDao(database: CoinDatabase): PortfolioDao {
        return database.portfolioDao()
    }

    // Add to DatabaseModule
    @Provides
    fun providePriceAlertDao(database: CoinDatabase): PriceAlertDao {
        return database.priceAlertDao()
    }

    // Add to UseCaseModule
    @Provides
    fun provideCreatePriceAlertUseCase(repository: CoinRepository): CreatePriceAlertUseCase {
        return CreatePriceAlertUseCase(repository)
    }

    @Provides
    fun provideGetPriceAlertsUseCase(repository: CoinRepository): GetPriceAlertsUseCase {
        return GetPriceAlertsUseCase(repository)
    }

    @Provides
    fun provideDeletePriceAlertUseCase(repository: CoinRepository): DeletePriceAlertUseCase {
        return DeletePriceAlertUseCase(repository)
    }

    @Provides
    fun provideCheckPriceAlertsUseCase(repository: CoinRepository): CheckPriceAlertsUseCase {
        return CheckPriceAlertsUseCase(repository)
    }

    // Add new NotificationModule
    @Module
    @InstallIn(SingletonComponent::class)
    object NotificationModule {
        @Provides
        @Singleton
        fun providePriceAlertNotificationManager(@ApplicationContext context: Context): PriceAlertNotificationManager {
            return PriceAlertNotificationManager(context)
        }
    }
}