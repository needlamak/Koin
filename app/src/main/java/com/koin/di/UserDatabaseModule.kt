package com.koin.di

import android.content.Context
import androidx.room.Room
import com.koin.data.user.UserDao
import com.koin.data.user.UserDatabase
import com.koin.data.watchlist.WatchlistDao
import com.koin.data.watchlist.WatchlistRepositoryImpl
import com.koin.domain.watchlist.WatchlistRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserDatabaseModule {
    
    @Binds
    abstract fun bindWatchlistRepository(
        watchlistRepositoryImpl: WatchlistRepositoryImpl
    ): WatchlistRepository
    
    companion object {
        @Provides
        @Singleton
        fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase =
            Room.databaseBuilder(
                context,
                UserDatabase::class.java,
                UserDatabase.DATABASE_NAME
            ).fallbackToDestructiveMigration() // Added for version change
            .build()

        @Provides
        fun provideUserDao(db: UserDatabase): UserDao = db.userDao()
        
        @Provides
        fun provideWatchlistDao(db: UserDatabase): WatchlistDao = db.watchlistDao()
    }
}
