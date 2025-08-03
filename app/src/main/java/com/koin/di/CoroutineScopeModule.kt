package com.koin.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

// DI Module for CoroutineScope
@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeModule {
    
    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}

// Qualifier annotation
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope