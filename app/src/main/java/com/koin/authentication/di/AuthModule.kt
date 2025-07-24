package com.koin.authentication.di // Or your appropriate package name for dependency injection modules

import com.koin.authentication.data.AuthRepository
import com.koin.authentication.data.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing authentication-related dependencies.
 * This module tells Hilt how to create instances of AuthRepository.
 */
@Module
@InstallIn(SingletonComponent::class) // This makes the bindings available throughout the application's lifecycle
abstract class AuthModule {

    /**
     * Binds AuthRepositoryImpl to AuthRepository.
     * When AuthRepository is requested, Hilt will provide an instance of AuthRepositoryImpl.
     * The @Singleton annotation ensures that only one instance of AuthRepositoryImpl is created
     * and reused across the application.
     *
     * @param authRepositoryImpl The concrete implementation of AuthRepository.
     * @return An instance of AuthRepository.
     */
    @Binds
    @Singleton // Ensures a single instance of AuthRepository is provided throughout the app
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}
