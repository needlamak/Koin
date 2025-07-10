package com.koin.di

import android.content.Context
import androidx.room.Room
import com.koin.data.coin.CoinDao
import com.koin.data.coin.CoinDatabase
import com.koin.data.portfolio.PortfolioDao
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
            CoinDatabase.MIGRATION_1_2, 
            CoinDatabase.MIGRATION_2_3, 
            CoinDatabase.MIGRATION_3_4
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
}
