package com.koin.di

import com.koin.data.transaction.TransactionRepositoryImpl
import com.koin.domain.transaction.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TransactionModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository
}