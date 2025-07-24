package com.koin.data.user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.koin.data.watchlist.WatchlistDao
import com.koin.data.watchlist.WatchlistEntity

@Database(
    entities = [UserEntity::class, WatchlistEntity::class],
    version = 3,
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun watchlistDao(): WatchlistDao

    companion object {
        @Volatile private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            UserDatabase::class.java,
            DATABASE_NAME
        ).addMigrations(MIGRATION_2_3).build()

        const val DATABASE_NAME = "user_database"

        val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                // Create the new table with the updated schema
                database.execSQL("CREATE TABLE users_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, username TEXT NOT NULL, email TEXT NOT NULL, avatarUri TEXT)")
                // Copy data from the old table to the new table, mapping 'name' to 'username'
                database.execSQL("INSERT INTO users_new (id, username, email, avatarUri) SELECT id, name, email, avatarUri FROM users")
                // Remove the old table
                database.execSQL("DROP TABLE users")
                // Rename the new table to the old table's name
                database.execSQL("ALTER TABLE users_new RENAME TO users")
            }
        }
    }
}
