package com.koin.data.coin

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(
    entities = [CoinEntity::class, CoinChartEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CoinDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao

    companion object {
        @Volatile
        private var INSTANCE: CoinDatabase? = null

        fun getDatabase(context: Context): CoinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CoinDatabase::class.java,
                    "coin_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a new table with the composite primary key
                database.execSQL("""
                    CREATE TABLE `coin_chart_new` (
                        `coinId` TEXT NOT NULL, 
                        `timeRange` TEXT NOT NULL, 
                        `timestamp` INTEGER NOT NULL, 
                        `priceDataJson` TEXT NOT NULL, 
                        PRIMARY KEY(`coinId`, `timeRange`)
                    )
                """)
                // Copy the data from the old table to the new table
                // This handles potential duplicates by taking the most recent entry for each coin/timerange pair
                database.execSQL("""
                    INSERT INTO `coin_chart_new` (coinId, timeRange, timestamp, priceDataJson)
                    SELECT coinId, timeRange, timestamp, priceDataJson FROM `coin_chart`
                    GROUP BY coinId, timeRange
                    HAVING timestamp = MAX(timestamp)
                """)
                // Remove the old table
                database.execSQL("DROP TABLE `coin_chart`")
                // Rename the new table to the original table name
                database.execSQL("ALTER TABLE `coin_chart_new` RENAME TO `coin_chart`")
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `coin_chart` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `coinId` TEXT NOT NULL,
                        `timeRange` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `priceDataJson` TEXT NOT NULL
                    )
                """)
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_coin_chart_coinId_timeRange` ON `coin_chart` (`coinId`, `timeRange`)")
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromStringList(value: List<Double>?): String? {
        return value?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<Double>? {
        return value?.let {
            Gson().fromJson(it, object : TypeToken<List<Double>>() {}.type)
        }
    }

    // For chart data: List<Pair<Long, Double>>
    @TypeConverter
    fun fromChartList(value: List<Pair<Long, Double>>?): String? {
        return value?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toChartList(value: String?): List<Pair<Long, Double>>? {
        return value?.let {
            Gson().fromJson(it, object : TypeToken<List<Pair<Long, Double>>>() {}.type)
        }
    }
}