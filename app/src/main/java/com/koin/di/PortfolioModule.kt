package com.koin.di

import com.koin.data.portfolio.PortfolioRepositoryImpl
import com.koin.domain.portfolio.PortfolioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PortfolioModule {

}
