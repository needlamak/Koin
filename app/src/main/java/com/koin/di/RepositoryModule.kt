package com.koin.di

import com.koin.data.coin.CoinRepositoryImpl
import com.koin.domain.coin.CoinRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCoinRepository(
        coinRepositoryImpl: CoinRepositoryImpl
    ): CoinRepository
} 