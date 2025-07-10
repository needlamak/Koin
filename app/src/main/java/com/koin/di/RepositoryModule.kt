package com.koin.di

import com.koin.data.coin.CoinRepositoryImpl
import com.koin.data.portfolio.PortfolioRepositoryImpl
import com.koin.data.user.LocalUserRepository
import com.koin.domain.coin.CoinRepository
import com.koin.domain.portfolio.PortfolioRepository
import com.koin.domain.user.UserRepository
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
    abstract fun bindUserRepository(
        localUserRepository: LocalUserRepository
    ): UserRepository
    @Binds
    @Singleton
    abstract fun bindCoinRepository(
        coinRepositoryImpl: CoinRepositoryImpl
    ): CoinRepository

    @Binds
    @Singleton
    abstract fun bindPortfolioRepository(
        portfolioRepositoryImpl: PortfolioRepositoryImpl
    ): PortfolioRepository
} 