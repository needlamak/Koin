package com.koin.`data`.coin

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class CoinDao_Impl(
  __db: RoomDatabase,
) : CoinDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCoinEntity: EntityInsertAdapter<CoinEntity>

  private val __converters: Converters = Converters()

  private val __insertAdapterOfCoinChartEntity: EntityInsertAdapter<CoinChartEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCoinEntity = object : EntityInsertAdapter<CoinEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `coins` (`id`,`name`,`symbol`,`imageUrl`,`currentPrice`,`marketCap`,`marketCapRank`,`priceChange24h`,`priceChangePercentage24h`,`priceChangePercentage1h`,`priceChangePercentage7d`,`priceChangePercentage30d`,`sparkline_data`,`high24h`,`low24h`,`totalVolume`,`circulatingSupply`,`totalSupply`,`maxSupply`,`ath`,`athDate`,`atl`,`atlDate`,`lastUpdated`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CoinEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.symbol)
        statement.bindText(4, entity.imageUrl)
        statement.bindDouble(5, entity.currentPrice)
        statement.bindLong(6, entity.marketCap)
        statement.bindLong(7, entity.marketCapRank.toLong())
        statement.bindDouble(8, entity.priceChange24h)
        statement.bindDouble(9, entity.priceChangePercentage24h)
        val _tmpPriceChangePercentage1h: Double? = entity.priceChangePercentage1h
        if (_tmpPriceChangePercentage1h == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpPriceChangePercentage1h)
        }
        val _tmpPriceChangePercentage7d: Double? = entity.priceChangePercentage7d
        if (_tmpPriceChangePercentage7d == null) {
          statement.bindNull(11)
        } else {
          statement.bindDouble(11, _tmpPriceChangePercentage7d)
        }
        val _tmpPriceChangePercentage30d: Double? = entity.priceChangePercentage30d
        if (_tmpPriceChangePercentage30d == null) {
          statement.bindNull(12)
        } else {
          statement.bindDouble(12, _tmpPriceChangePercentage30d)
        }
        val _tmpSparklineData: List<Double>? = entity.sparklineData
        val _tmp: String? = __converters.fromStringList(_tmpSparklineData)
        if (_tmp == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmp)
        }
        val _tmpHigh24h: Double? = entity.high24h
        if (_tmpHigh24h == null) {
          statement.bindNull(14)
        } else {
          statement.bindDouble(14, _tmpHigh24h)
        }
        val _tmpLow24h: Double? = entity.low24h
        if (_tmpLow24h == null) {
          statement.bindNull(15)
        } else {
          statement.bindDouble(15, _tmpLow24h)
        }
        val _tmpTotalVolume: Double? = entity.totalVolume
        if (_tmpTotalVolume == null) {
          statement.bindNull(16)
        } else {
          statement.bindDouble(16, _tmpTotalVolume)
        }
        val _tmpCirculatingSupply: Double? = entity.circulatingSupply
        if (_tmpCirculatingSupply == null) {
          statement.bindNull(17)
        } else {
          statement.bindDouble(17, _tmpCirculatingSupply)
        }
        val _tmpTotalSupply: Double? = entity.totalSupply
        if (_tmpTotalSupply == null) {
          statement.bindNull(18)
        } else {
          statement.bindDouble(18, _tmpTotalSupply)
        }
        val _tmpMaxSupply: Double? = entity.maxSupply
        if (_tmpMaxSupply == null) {
          statement.bindNull(19)
        } else {
          statement.bindDouble(19, _tmpMaxSupply)
        }
        val _tmpAth: Double? = entity.ath
        if (_tmpAth == null) {
          statement.bindNull(20)
        } else {
          statement.bindDouble(20, _tmpAth)
        }
        val _tmpAthDate: String? = entity.athDate
        if (_tmpAthDate == null) {
          statement.bindNull(21)
        } else {
          statement.bindText(21, _tmpAthDate)
        }
        val _tmpAtl: Double? = entity.atl
        if (_tmpAtl == null) {
          statement.bindNull(22)
        } else {
          statement.bindDouble(22, _tmpAtl)
        }
        val _tmpAtlDate: String? = entity.atlDate
        if (_tmpAtlDate == null) {
          statement.bindNull(23)
        } else {
          statement.bindText(23, _tmpAtlDate)
        }
        statement.bindLong(24, entity.lastUpdated)
      }
    }
    this.__insertAdapterOfCoinChartEntity = object : EntityInsertAdapter<CoinChartEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `coin_chart` (`coinId`,`timeRange`,`timestamp`,`priceDataJson`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CoinChartEntity) {
        statement.bindText(1, entity.coinId)
        statement.bindText(2, entity.timeRange)
        statement.bindLong(3, entity.timestamp)
        statement.bindText(4, entity.priceDataJson)
      }
    }
  }

  public override suspend fun insertAll(coins: List<CoinEntity>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfCoinEntity.insert(_connection, coins)
  }

  public override suspend fun insertCoin(coin: CoinEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfCoinEntity.insert(_connection, coin)
  }

  public override suspend fun insertCoinChart(chart: CoinChartEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCoinChartEntity.insert(_connection, chart)
  }

  public override fun getAllCoins(): Flow<List<CoinEntity>> {
    val _sql: String = "SELECT * FROM coins ORDER BY marketCapRank ASC"
    return createFlow(__db, false, arrayOf("coins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfSymbol: Int = getColumnIndexOrThrow(_stmt, "symbol")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfCurrentPrice: Int = getColumnIndexOrThrow(_stmt, "currentPrice")
        val _columnIndexOfMarketCap: Int = getColumnIndexOrThrow(_stmt, "marketCap")
        val _columnIndexOfMarketCapRank: Int = getColumnIndexOrThrow(_stmt, "marketCapRank")
        val _columnIndexOfPriceChange24h: Int = getColumnIndexOrThrow(_stmt, "priceChange24h")
        val _columnIndexOfPriceChangePercentage24h: Int = getColumnIndexOrThrow(_stmt,
            "priceChangePercentage24h")
        val _columnIndexOfPriceChangePercentage1h: Int = getColumnIndexOrThrow(_stmt,
            "priceChangePercentage1h")
        val _columnIndexOfPriceChangePercentage7d: Int = getColumnIndexOrThrow(_stmt,
            "priceChangePercentage7d")
        val _columnIndexOfPriceChangePercentage30d: Int = getColumnIndexOrThrow(_stmt,
            "priceChangePercentage30d")
        val _columnIndexOfSparklineData: Int = getColumnIndexOrThrow(_stmt, "sparkline_data")
        val _columnIndexOfHigh24h: Int = getColumnIndexOrThrow(_stmt, "high24h")
        val _columnIndexOfLow24h: Int = getColumnIndexOrThrow(_stmt, "low24h")
        val _columnIndexOfTotalVolume: Int = getColumnIndexOrThrow(_stmt, "totalVolume")
        val _columnIndexOfCirculatingSupply: Int = getColumnIndexOrThrow(_stmt, "circulatingSupply")
        val _columnIndexOfTotalSupply: Int = getColumnIndexOrThrow(_stmt, "totalSupply")
        val _columnIndexOfMaxSupply: Int = getColumnIndexOrThrow(_stmt, "maxSupply")
        val _columnIndexOfAth: Int = getColumnIndexOrThrow(_stmt, "ath")
        val _columnIndexOfAthDate: Int = getColumnIndexOrThrow(_stmt, "athDate")
        val _columnIndexOfAtl: Int = getColumnIndexOrThrow(_stmt, "atl")
        val _columnIndexOfAtlDate: Int = getColumnIndexOrThrow(_stmt, "atlDate")
        val _columnIndexOfLastUpdated: Int = getColumnIndexOrThrow(_stmt, "lastUpdated")
        val _result: MutableList<CoinEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CoinEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpSymbol: String
          _tmpSymbol = _stmt.getText(_columnIndexOfSymbol)
          val _tmpImageUrl: String
          _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          val _tmpCurrentPrice: Double
          _tmpCurrentPrice = _stmt.getDouble(_columnIndexOfCurrentPrice)
          val _tmpMarketCap: Long
          _tmpMarketCap = _stmt.getLong(_columnIndexOfMarketCap)
          val _tmpMarketCapRank: Int
          _tmpMarketCapRank = _stmt.getLong(_columnIndexOfMarketCapRank).toInt()
          val _tmpPriceChange24h: Double
          _tmpPriceChange24h = _stmt.getDouble(_columnIndexOfPriceChange24h)
          val _tmpPriceChangePercentage24h: Double
          _tmpPriceChangePercentage24h = _stmt.getDouble(_columnIndexOfPriceChangePercentage24h)
          val _tmpPriceChangePercentage1h: Double?
          if (_stmt.isNull(_columnIndexOfPriceChangePercentage1h)) {
            _tmpPriceChangePercentage1h = null
          } else {
            _tmpPriceChangePercentage1h = _stmt.getDouble(_columnIndexOfPriceChangePercentage1h)
          }
          val _tmpPriceChangePercentage7d: Double?
          if (_stmt.isNull(_columnIndexOfPriceChangePercentage7d)) {
            _tmpPriceChangePercentage7d = null
          } else {
            _tmpPriceChangePercentage7d = _stmt.getDouble(_columnIndexOfPriceChangePercentage7d)
          }
          val _tmpPriceChangePercentage30d: Double?
          if (_stmt.isNull(_columnIndexOfPriceChangePercentage30d)) {
            _tmpPriceChangePercentage30d = null
          } else {
            _tmpPriceChangePercentage30d = _stmt.getDouble(_columnIndexOfPriceChangePercentage30d)
          }
          val _tmpSparklineData: List<Double>?
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfSparklineData)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfSparklineData)
          }
          _tmpSparklineData = __converters.toStringList(_tmp)
          val _tmpHigh24h: Double?
          if (_stmt.isNull(_columnIndexOfHigh24h)) {
            _tmpHigh24h = null
          } else {
            _tmpHigh24h = _stmt.getDouble(_columnIndexOfHigh24h)
          }
          val _tmpLow24h: Double?
          if (_stmt.isNull(_columnIndexOfLow24h)) {
            _tmpLow24h = null
          } else {
            _tmpLow24h = _stmt.getDouble(_columnIndexOfLow24h)
          }
          val _tmpTotalVolume: Double?
          if (_stmt.isNull(_columnIndexOfTotalVolume)) {
            _tmpTotalVolume = null
          } else {
            _tmpTotalVolume = _stmt.getDouble(_columnIndexOfTotalVolume)
          }
          val _tmpCirculatingSupply: Double?
          if (_stmt.isNull(_columnIndexOfCirculatingSupply)) {
            _tmpCirculatingSupply = null
          } else {
            _tmpCirculatingSupply = _stmt.getDouble(_columnIndexOfCirculatingSupply)
          }
          val _tmpTotalSupply: Double?
          if (_stmt.isNull(_columnIndexOfTotalSupply)) {
            _tmpTotalSupply = null
          } else {
            _tmpTotalSupply = _stmt.getDouble(_columnIndexOfTotalSupply)
          }
          val _tmpMaxSupply: Double?
          if (_stmt.isNull(_columnIndexOfMaxSupply)) {
            _tmpMaxSupply = null
          } else {
            _tmpMaxSupply = _stmt.getDouble(_columnIndexOfMaxSupply)
          }
          val _tmpAth: Double?
          if (_stmt.isNull(_columnIndexOfAth)) {
            _tmpAth = null
          } else {
            _tmpAth = _stmt.getDouble(_columnIndexOfAth)
          }
          val _tmpAthDate: String?
          if (_stmt.isNull(_columnIndexOfAthDate)) {
            _tmpAthDate = null
          } else {
            _tmpAthDate = _stmt.getText(_columnIndexOfAthDate)
          }
          val _tmpAtl: Double?
          if (_stmt.isNull(_columnIndexOfAtl)) {
            _tmpAtl = null
          } else {
            _tmpAtl = _stmt.getDouble(_columnIndexOfAtl)
          }
          val _tmpAtlDate: String?
          if (_stmt.isNull(_columnIndexOfAtlDate)) {
            _tmpAtlDate = null
          } else {
            _tmpAtlDate = _stmt.getText(_columnIndexOfAtlDate)
          }
          val _tmpLastUpdated: Long
          _tmpLastUpdated = _stmt.getLong(_columnIndexOfLastUpdated)
          _item =
              CoinEntity(_tmpId,_tmpName,_tmpSymbol,_tmpImageUrl,_tmpCurrentPrice,_tmpMarketCap,_tmpMarketCapRank,_tmpPriceChange24h,_tmpPriceChangePercentage24h,_tmpPriceChangePercentage1h,_tmpPriceChangePercentage7d,_tmpPriceChangePercentage30d,_tmpSparklineData,_tmpHigh24h,_tmpLow24h,_tmpTotalVolume,_tmpCirculatingSupply,_tmpTotalSupply,_tmpMaxSupply,_tmpAth,_tmpAthDate,_tmpAtl,_tmpAtlDate,_tmpLastUpdated)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getCoinById(coinId: String): Flow<CoinEntity?> {
    val _sql: String = "SELECT * FROM coins WHERE id = ?"
    return createFlow(__db, false, arrayOf("coins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, coinId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfSymbol: Int = getColumnIndexOrThrow(_stmt, "symbol")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfCurrentPrice: Int = getColumnIndexOrThrow(_stmt, "currentPrice")
        val _columnIndexOfMarketCap: Int = getColumnIndexOrThrow(_stmt, "marketCap")
        val _columnIndexOfMarketCapRank: Int = getColumnIndexOrThrow(_stmt, "marketCapRank")
        val _columnIndexOfPriceChange24h: Int = getColumnIndexOrThrow(_stmt, "priceChange24h")
        val _columnIndexOfPriceChangePercentage24h: Int = getColumnIndexOrThrow(_stmt,
            "priceChangePercentage24h")
        val _columnIndexOfPriceChangePercentage1h: Int = getColumnIndexOrThrow(_stmt,
            "priceChangePercentage1h")
        val _columnIndexOfPriceChangePercentage7d: Int = getColumnIndexOrThrow(_stmt,
            "priceChangePercentage7d")
        val _columnIndexOfPriceChangePercentage30d: Int = getColumnIndexOrThrow(_stmt,
            "priceChangePercentage30d")
        val _columnIndexOfSparklineData: Int = getColumnIndexOrThrow(_stmt, "sparkline_data")
        val _columnIndexOfHigh24h: Int = getColumnIndexOrThrow(_stmt, "high24h")
        val _columnIndexOfLow24h: Int = getColumnIndexOrThrow(_stmt, "low24h")
        val _columnIndexOfTotalVolume: Int = getColumnIndexOrThrow(_stmt, "totalVolume")
        val _columnIndexOfCirculatingSupply: Int = getColumnIndexOrThrow(_stmt, "circulatingSupply")
        val _columnIndexOfTotalSupply: Int = getColumnIndexOrThrow(_stmt, "totalSupply")
        val _columnIndexOfMaxSupply: Int = getColumnIndexOrThrow(_stmt, "maxSupply")
        val _columnIndexOfAth: Int = getColumnIndexOrThrow(_stmt, "ath")
        val _columnIndexOfAthDate: Int = getColumnIndexOrThrow(_stmt, "athDate")
        val _columnIndexOfAtl: Int = getColumnIndexOrThrow(_stmt, "atl")
        val _columnIndexOfAtlDate: Int = getColumnIndexOrThrow(_stmt, "atlDate")
        val _columnIndexOfLastUpdated: Int = getColumnIndexOrThrow(_stmt, "lastUpdated")
        val _result: CoinEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpSymbol: String
          _tmpSymbol = _stmt.getText(_columnIndexOfSymbol)
          val _tmpImageUrl: String
          _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          val _tmpCurrentPrice: Double
          _tmpCurrentPrice = _stmt.getDouble(_columnIndexOfCurrentPrice)
          val _tmpMarketCap: Long
          _tmpMarketCap = _stmt.getLong(_columnIndexOfMarketCap)
          val _tmpMarketCapRank: Int
          _tmpMarketCapRank = _stmt.getLong(_columnIndexOfMarketCapRank).toInt()
          val _tmpPriceChange24h: Double
          _tmpPriceChange24h = _stmt.getDouble(_columnIndexOfPriceChange24h)
          val _tmpPriceChangePercentage24h: Double
          _tmpPriceChangePercentage24h = _stmt.getDouble(_columnIndexOfPriceChangePercentage24h)
          val _tmpPriceChangePercentage1h: Double?
          if (_stmt.isNull(_columnIndexOfPriceChangePercentage1h)) {
            _tmpPriceChangePercentage1h = null
          } else {
            _tmpPriceChangePercentage1h = _stmt.getDouble(_columnIndexOfPriceChangePercentage1h)
          }
          val _tmpPriceChangePercentage7d: Double?
          if (_stmt.isNull(_columnIndexOfPriceChangePercentage7d)) {
            _tmpPriceChangePercentage7d = null
          } else {
            _tmpPriceChangePercentage7d = _stmt.getDouble(_columnIndexOfPriceChangePercentage7d)
          }
          val _tmpPriceChangePercentage30d: Double?
          if (_stmt.isNull(_columnIndexOfPriceChangePercentage30d)) {
            _tmpPriceChangePercentage30d = null
          } else {
            _tmpPriceChangePercentage30d = _stmt.getDouble(_columnIndexOfPriceChangePercentage30d)
          }
          val _tmpSparklineData: List<Double>?
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfSparklineData)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfSparklineData)
          }
          _tmpSparklineData = __converters.toStringList(_tmp)
          val _tmpHigh24h: Double?
          if (_stmt.isNull(_columnIndexOfHigh24h)) {
            _tmpHigh24h = null
          } else {
            _tmpHigh24h = _stmt.getDouble(_columnIndexOfHigh24h)
          }
          val _tmpLow24h: Double?
          if (_stmt.isNull(_columnIndexOfLow24h)) {
            _tmpLow24h = null
          } else {
            _tmpLow24h = _stmt.getDouble(_columnIndexOfLow24h)
          }
          val _tmpTotalVolume: Double?
          if (_stmt.isNull(_columnIndexOfTotalVolume)) {
            _tmpTotalVolume = null
          } else {
            _tmpTotalVolume = _stmt.getDouble(_columnIndexOfTotalVolume)
          }
          val _tmpCirculatingSupply: Double?
          if (_stmt.isNull(_columnIndexOfCirculatingSupply)) {
            _tmpCirculatingSupply = null
          } else {
            _tmpCirculatingSupply = _stmt.getDouble(_columnIndexOfCirculatingSupply)
          }
          val _tmpTotalSupply: Double?
          if (_stmt.isNull(_columnIndexOfTotalSupply)) {
            _tmpTotalSupply = null
          } else {
            _tmpTotalSupply = _stmt.getDouble(_columnIndexOfTotalSupply)
          }
          val _tmpMaxSupply: Double?
          if (_stmt.isNull(_columnIndexOfMaxSupply)) {
            _tmpMaxSupply = null
          } else {
            _tmpMaxSupply = _stmt.getDouble(_columnIndexOfMaxSupply)
          }
          val _tmpAth: Double?
          if (_stmt.isNull(_columnIndexOfAth)) {
            _tmpAth = null
          } else {
            _tmpAth = _stmt.getDouble(_columnIndexOfAth)
          }
          val _tmpAthDate: String?
          if (_stmt.isNull(_columnIndexOfAthDate)) {
            _tmpAthDate = null
          } else {
            _tmpAthDate = _stmt.getText(_columnIndexOfAthDate)
          }
          val _tmpAtl: Double?
          if (_stmt.isNull(_columnIndexOfAtl)) {
            _tmpAtl = null
          } else {
            _tmpAtl = _stmt.getDouble(_columnIndexOfAtl)
          }
          val _tmpAtlDate: String?
          if (_stmt.isNull(_columnIndexOfAtlDate)) {
            _tmpAtlDate = null
          } else {
            _tmpAtlDate = _stmt.getText(_columnIndexOfAtlDate)
          }
          val _tmpLastUpdated: Long
          _tmpLastUpdated = _stmt.getLong(_columnIndexOfLastUpdated)
          _result =
              CoinEntity(_tmpId,_tmpName,_tmpSymbol,_tmpImageUrl,_tmpCurrentPrice,_tmpMarketCap,_tmpMarketCapRank,_tmpPriceChange24h,_tmpPriceChangePercentage24h,_tmpPriceChangePercentage1h,_tmpPriceChangePercentage7d,_tmpPriceChangePercentage30d,_tmpSparklineData,_tmpHigh24h,_tmpLow24h,_tmpTotalVolume,_tmpCirculatingSupply,_tmpTotalSupply,_tmpMaxSupply,_tmpAth,_tmpAthDate,_tmpAtl,_tmpAtlDate,_tmpLastUpdated)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCoinCount(): Int {
    val _sql: String = "SELECT COUNT(*) FROM coins"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCoinChart(coinId: String, timeRange: String): CoinChartEntity? {
    val _sql: String =
        "SELECT * FROM coin_chart WHERE coinId = ? AND timeRange = ? ORDER BY timestamp DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, coinId)
        _argIndex = 2
        _stmt.bindText(_argIndex, timeRange)
        val _columnIndexOfCoinId: Int = getColumnIndexOrThrow(_stmt, "coinId")
        val _columnIndexOfTimeRange: Int = getColumnIndexOrThrow(_stmt, "timeRange")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfPriceDataJson: Int = getColumnIndexOrThrow(_stmt, "priceDataJson")
        val _result: CoinChartEntity?
        if (_stmt.step()) {
          val _tmpCoinId: String
          _tmpCoinId = _stmt.getText(_columnIndexOfCoinId)
          val _tmpTimeRange: String
          _tmpTimeRange = _stmt.getText(_columnIndexOfTimeRange)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpPriceDataJson: String
          _tmpPriceDataJson = _stmt.getText(_columnIndexOfPriceDataJson)
          _result = CoinChartEntity(_tmpCoinId,_tmpTimeRange,_tmpTimestamp,_tmpPriceDataJson)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllCoins() {
    val _sql: String = "DELETE FROM coins"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteCoinChart(coinId: String, timeRange: String) {
    val _sql: String = "DELETE FROM coin_chart WHERE coinId = ? AND timeRange = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, coinId)
        _argIndex = 2
        _stmt.bindText(_argIndex, timeRange)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun pruneOldCharts(cutoff: Long) {
    val _sql: String = "DELETE FROM coin_chart WHERE timestamp < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cutoff)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
