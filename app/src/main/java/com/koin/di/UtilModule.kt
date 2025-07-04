package com.koin.di

import android.content.Context
import com.koin.data.coin.NetworkUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {
    @Provides
    @Singleton
    fun provideNetworkUtil(@ApplicationContext context: Context): NetworkUtil {
        return NetworkUtil(context)
    }
}