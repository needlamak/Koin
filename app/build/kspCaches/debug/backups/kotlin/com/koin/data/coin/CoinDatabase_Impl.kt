package com.koin.`data`.coin

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class CoinDatabase_Impl : CoinDatabase() {
  private val _coinDao: Lazy<CoinDao> = lazy {
    CoinDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(3,
        "fcf5ed15af6111b32e0254d1adeb2b36", "aff2c927ed2655dea813f906cbba4071") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `coins` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `symbol` TEXT NOT NULL, `imageUrl` TEXT NOT NULL, `currentPrice` REAL NOT NULL, `marketCap` INTEGER NOT NULL, `marketCapRank` INTEGER NOT NULL, `priceChange24h` REAL NOT NULL, `priceChangePercentage24h` REAL NOT NULL, `priceChangePercentage1h` REAL, `priceChangePercentage7d` REAL, `priceChangePercentage30d` REAL, `sparkline_data` TEXT, `high24h` REAL, `low24h` REAL, `totalVolume` REAL, `circulatingSupply` REAL, `totalSupply` REAL, `maxSupply` REAL, `ath` REAL, `athDate` TEXT, `atl` REAL, `atlDate` TEXT, `lastUpdated` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `coin_chart` (`coinId` TEXT NOT NULL, `timeRange` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `priceDataJson` TEXT NOT NULL, PRIMARY KEY(`coinId`, `timeRange`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'fcf5ed15af6111b32e0254d1adeb2b36')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `coins`")
        connection.execSQL("DROP TABLE IF EXISTS `coin_chart`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsCoins: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCoins.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("symbol", TableInfo.Column("symbol", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("imageUrl", TableInfo.Column("imageUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("currentPrice", TableInfo.Column("currentPrice", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("marketCap", TableInfo.Column("marketCap", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("marketCapRank", TableInfo.Column("marketCapRank", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("priceChange24h", TableInfo.Column("priceChange24h", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("priceChangePercentage24h", TableInfo.Column("priceChangePercentage24h",
            "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("priceChangePercentage1h", TableInfo.Column("priceChangePercentage1h",
            "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("priceChangePercentage7d", TableInfo.Column("priceChangePercentage7d",
            "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("priceChangePercentage30d", TableInfo.Column("priceChangePercentage30d",
            "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("sparkline_data", TableInfo.Column("sparkline_data", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("high24h", TableInfo.Column("high24h", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("low24h", TableInfo.Column("low24h", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("totalVolume", TableInfo.Column("totalVolume", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("circulatingSupply", TableInfo.Column("circulatingSupply", "REAL", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("totalSupply", TableInfo.Column("totalSupply", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("maxSupply", TableInfo.Column("maxSupply", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("ath", TableInfo.Column("ath", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("athDate", TableInfo.Column("athDate", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("atl", TableInfo.Column("atl", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("atlDate", TableInfo.Column("atlDate", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("lastUpdated", TableInfo.Column("lastUpdated", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCoins: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCoins: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCoins: TableInfo = TableInfo("coins", _columnsCoins, _foreignKeysCoins,
            _indicesCoins)
        val _existingCoins: TableInfo = read(connection, "coins")
        if (!_infoCoins.equals(_existingCoins)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |coins(com.koin.data.coin.CoinEntity).
              | Expected:
              |""".trimMargin() + _infoCoins + """
              |
              | Found:
              |""".trimMargin() + _existingCoins)
        }
        val _columnsCoinChart: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCoinChart.put("coinId", TableInfo.Column("coinId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoinChart.put("timeRange", TableInfo.Column("timeRange", "TEXT", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoinChart.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoinChart.put("priceDataJson", TableInfo.Column("priceDataJson", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCoinChart: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCoinChart: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCoinChart: TableInfo = TableInfo("coin_chart", _columnsCoinChart,
            _foreignKeysCoinChart, _indicesCoinChart)
        val _existingCoinChart: TableInfo = read(connection, "coin_chart")
        if (!_infoCoinChart.equals(_existingCoinChart)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |coin_chart(com.koin.data.coin.CoinChartEntity).
              | Expected:
              |""".trimMargin() + _infoCoinChart + """
              |
              | Found:
              |""".trimMargin() + _existingCoinChart)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "coins", "coin_chart")
  }

  public override fun clearAllTables() {
    super.performClear(false, "coins", "coin_chart")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(CoinDao::class, CoinDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun coinDao(): CoinDao = _coinDao.value
}
