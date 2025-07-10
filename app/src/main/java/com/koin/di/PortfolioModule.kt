package com.koin.di

import com.koin.data.portfolio.PortfolioRepositoryImpl
import com.koin.domain.portfolio.AddCoinToHoldingForTestUseCase
import com.koin.domain.portfolio.PortfolioRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PortfolioModule {

    @Binds
    @Singleton
    abstract fun bindPortfolioRepository(
        portfolioRepositoryImpl: PortfolioRepositoryImpl
    ): PortfolioRepository

    companion object {
        @Provides
        @Singleton
        fun provideAddCoinToHoldingForTestUseCase(
            portfolioRepository: PortfolioRepository
        ): AddCoinToHoldingForTestUseCase {
            return AddCoinToHoldingForTestUseCase(portfolioRepository)
        }
    }
}
