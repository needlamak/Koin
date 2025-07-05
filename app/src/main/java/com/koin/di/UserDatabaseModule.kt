package com.koin.di

import android.content.Context
import androidx.room.Room
import com.koin.data.user.UserDao
import com.koin.data.user.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserDatabaseModule {
    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase =
        Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            UserDatabase.DATABASE_NAME
        ).build()

    @Provides
    fun provideUserDao(db: UserDatabase): UserDao = db.userDao()
}
