package com.koin.di

import android.content.Context
import androidx.room.Room
import com.koin.data.coin.CoinDao
import com.koin.data.coin.CoinDatabase
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
        ).build()
    }

    @Provides
    fun provideCoinDao(database: CoinDatabase): CoinDao {
        return database.coinDao()
    }
} 