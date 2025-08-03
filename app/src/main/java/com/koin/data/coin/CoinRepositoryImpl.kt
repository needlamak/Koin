package com.koin.data.coin

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.collection.LruCache
import com.koin.app.pricealert.PriceAlertDao
import com.koin.data.coin.dto.PriceDataPoint
import com.koin.data.coin.dto.toPriceDataPoints
import com.koin.data.pricealert.PriceAlertEntity
import com.koin.di.ApplicationScope
import com.koin.domain.coin.CoinRepository
import com.koin.domain.model.Coin
import com.koin.domain.pricealert.PriceAlert
import com.koin.domain.pricealert.PriceAlertType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinRepositoryImpl @Inject constructor(
    private val apiService: CoinGeckoApiService,
    private val coinDao: CoinDao,
    private val networkUtil: NetworkUtil,
    private val priceAlertDao: PriceAlertDao,
    @ApplicationScope private val appScope: CoroutineScope
) : CoinRepository {

    private val _coins = MutableStateFlow<Map<String, Coin>>(emptyMap())
    val coins = _coins.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)
    override val lastError = _lastError.asStateFlow()

    // LRU Cache with max 100 entries to prevent memory leaks
    private val chartCache = LruCache<Pair<String, TimeRange>, List<PriceDataPoint>>(100)

    init {
        appScope.launch {
            loadFromCache()
            if (networkUtil.isNetworkAvailable()) {
                refreshFromNetwork()
            }
        }
    }

    override fun getAllCoins(): Flow<Result<List<Coin>>> = flow {
        try {
            coins.collect { coinsMap ->
                emit(Result.success(coinsMap.values.toList()))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getCoinById(id: String?): Flow<Result<Coin?>> = flow {
        try {
            coins.collect { coinsMap ->
                emit(Result.success(coinsMap[id]))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private suspend fun loadFromCache() {
        try {
            val cachedCoins = coinDao.getAllCoins().take(1).first()
            if (cachedCoins.isNotEmpty()) {
                _coins.value = cachedCoins.map { it.toDomain() }.associateBy { it.id }
                _lastError.value = null
            }
        } catch (e: Exception) {
            val errorMsg = "Failed to load coins from cache: ${e.localizedMessage}"
            Timber.tag("CoinRepositoryImpl").e(e, errorMsg)
            _lastError.value = errorMsg
        }
    }

    suspend fun refreshFromNetwork() {
        try {
            val freshData = apiService.getCoinsWithFullDetails()
            val domainCoins = freshData.map { it.toDomain() }

            // Update in-memory state
            _coins.value = domainCoins.associateBy { it.id }
            _lastError.value = null

            // Update database in transaction
            coinDao.refreshCoins(domainCoins.map { it.toEntity() })

            // Prefetch chart data asynchronously without blocking
            prefetchChartData(domainCoins)

        } catch (e: Exception) {
            val errorMsg = "Failed to refresh coins from network: ${e.localizedMessage}"
            Timber.tag("CoinRepositoryImpl").e(e, errorMsg)
            _lastError.value = errorMsg

            if (_coins.value.isEmpty()) {
                val noCacheMsg = "No cached data available. Please check your internet connection."
                Timber.tag("CoinRepositoryImpl").e(noCacheMsg)
                _lastError.value = noCacheMsg
            }
        }
    }

    private fun prefetchChartData(coins: List<Coin>) {
        val selectedTimeRanges = listOf(
            TimeRange.ONE_DAY,
            TimeRange.ONE_WEEK,
            TimeRange.ONE_YEAR,
            TimeRange.ALL
        )

        appScope.launch {
            val prefetchJobs = coins.take(10).flatMap { coin -> // Limit to top 10 coins
                selectedTimeRanges.map { range ->
                    async {
                        try {
                            getCoinMarketChart(coin.id, range, "usd")
                        } catch (e: Exception) {
                            Timber.tag("CoinRepositoryImpl")
                                .d("Prefetch failed for ${coin.id} $range: ${e.message}")
                        }
                    }
                }
            }
            // Don't await - fire and forget for background prefetch
        }
    }

    override suspend fun refreshCoins(): Result<Unit> = try {
        if (networkUtil.isNetworkAvailable()) {
            refreshFromNetwork()
            Result.success(Unit)
        } else {
            Result.failure(Exception("No network connection available"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getCoinMarketChart(
        coinId: String,
        timeRange: TimeRange,
        vsCurrency: String
    ): List<PriceDataPoint> {
        val cacheKey = coinId to timeRange

        // Check in-memory cache first
        chartCache.get(cacheKey)?.let { return it }

        // Check persistent cache
        val cacheDurationMillis = 60 * 60 * 1000L // 1 hour
        val now = System.currentTimeMillis()

        try {
            val chartEntity = coinDao.getCoinChart(coinId, timeRange.name)
            if (chartEntity != null && (now - chartEntity.timestamp) < cacheDurationMillis) {
                val chartList = Converters().toChartList(chartEntity.priceDataJson) ?: emptyList()
                val priceData = chartList.map { PriceDataPoint(it.first, it.second) }
                chartCache.put(cacheKey, priceData)
                return priceData
            }
        } catch (e: Exception) {
            Timber.tag("CoinRepositoryImpl").w(e, "Failed to load chart from cache")
        }

        // Fetch from network
        return try {
            if (!networkUtil.isNetworkAvailable()) {
                return emptyList()
            }

            val calendar = Calendar.getInstance()
            val endTime = calendar.timeInMillis / 1000
            timeRange.days?.let { calendar.add(Calendar.DAY_OF_YEAR, -it) }
            val startTime = calendar.timeInMillis / 1000

            val response = apiService.getCoinMarketChartRange(
                id = coinId,
                vsCurrency = vsCurrency,
                from = startTime,
                to = endTime
            )

            val data = response.toPriceDataPoints()

            // Cache in memory
            chartCache.put(cacheKey, data)

            // Save to database
            appScope.launch {
                try {
                    val chartList = data.map { it.timestamp to it.price }
                    val entity = CoinChartEntity(
                        coinId = coinId,
                        timeRange = timeRange.name,
                        timestamp = now,
                        priceDataJson = Converters().fromChartList(chartList) ?: "[]"
                    )
                    coinDao.insertCoinChart(entity)
                } catch (e: Exception) {
                    Timber.tag("CoinRepositoryImpl").w(e, "Failed to cache chart data")
                }
            }

            data
        } catch (e: Exception) {
            Timber.tag("CoinRepositoryImpl")
                .e(e, "Error fetching market chart: ${e.localizedMessage}")
            emptyList()
        }
    }

    override suspend fun createPriceAlert(alert: PriceAlert): Result<Unit> {
        return try {
            val entity = PriceAlertEntity(
                id = alert.id,
                coinId = alert.coinId,
                coinName = alert.coinName,
                coinSymbol = alert.coinSymbol,
                coinImageUrl = alert.coinImageUrl,
                targetPrice = alert.targetPrice,
                alertType = alert.alertType.name,
                isActive = alert.isActive,
                isTriggered = alert.isTriggered,
                createdAt = alert.createdAt,
                triggeredAt = alert.triggeredAt
            )
            priceAlertDao.insertAlert(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePriceAlert(alertId: PriceAlertEntity): Result<Unit> {
        return try {
            priceAlertDao.deleteAlert(alertId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override fun getAllPriceAlerts(): Flow<Result<List<PriceAlert>>> {
        return priceAlertDao.getAllAlerts()
            .map { entities ->
                try {
                    val alerts = entities.map { entity ->
                        PriceAlert(
                            id = entity.id,
                            coinId = entity.coinId,
                            coinName = entity.coinName,
                            coinSymbol = entity.coinSymbol,
                            coinImageUrl = entity.coinImageUrl,
                            targetPrice = entity.targetPrice,
                            alertType = PriceAlertType.valueOf(entity.alertType),
                            isActive = entity.isActive,
                            isTriggered = entity.isTriggered,
                            createdAt = entity.createdAt,
                            triggeredAt = entity.triggeredAt
                        )
                    }
                    Result.success(alerts)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }

    override fun getActiveAlertsForCoin(coinId: String): Flow<Result<List<PriceAlert>>> {
        return priceAlertDao.getActiveAlertsForCoin(coinId)
            .map { entities ->
                try {
                    val alerts = entities.map { entity ->
                        PriceAlert(
                            id = entity.id,
                            coinId = entity.coinId,
                            coinName = entity.coinName,
                            coinSymbol = entity.coinSymbol,
                            coinImageUrl = entity.coinImageUrl,
                            targetPrice = entity.targetPrice,
                            alertType = PriceAlertType.valueOf(entity.alertType),
                            isActive = entity.isActive,
                            isTriggered = entity.isTriggered,
                            createdAt = entity.createdAt,
                            triggeredAt = entity.triggeredAt
                        )
                    }
                    Result.success(alerts)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }

    override suspend fun markAlertAsTriggered(alertId: String, triggeredAt: Long): Result<Unit> {
        return try {
            priceAlertDao.markAlertAsTriggered(alertId, triggeredAt)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun clearChartCache() {
        chartCache.evictAll()
    }


}

//@Singleton
//class CoinRepositoryImpl @Inject constructor(
//    private val apiService: CoinGeckoApiService,
//    private val coinDao: CoinDao,
//    private val networkUtil: NetworkUtil,
//    private val priceAlertDao: PriceAlertDao
//) : CoinRepository {
//
//    private val _coins = MutableStateFlow<Map<String, Coin>>(emptyMap())
//    val coins = _coins.asStateFlow()
//
//    private val _lastError = MutableStateFlow<String?>(null)
//
//    override fun getAllCoins(): Flow<Result<List<Coin>>> =
//        coins.map { Result.success(it.values.toList()) }
//
//    override fun getCoinById(id: String?): Flow<Result<Coin?>> =
//        coins.map { Result.success(it[id]) }
//
//    init {
//        CoroutineScope(Dispatchers.IO).launch @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE) {
//            loadFromCache()
//            if (networkUtil.isNetworkAvailable()) {
//                refreshFromNetwork()
//            }
//        }
//    }
//
//    private suspend fun loadFromCache() {
//        try {
//            val cachedCoins = coinDao.getAllCoins().first()
//            if (cachedCoins.isNotEmpty()) {
//                _coins.value = cachedCoins.map { it.toDomain() }.associateBy { it.id }
//            }
//        } catch (e: Exception) {
//            val errorMsg = "Failed to load coins from cache: ${e.localizedMessage}"
//            Timber.tag("CoinRepositoryImpl").e(e, errorMsg)
//            _lastError.value = errorMsg
//        }
//    }
//
//    internal suspend fun refreshFromNetwork() {
//        try {
//            val freshData = apiService.getCoinsWithFullDetails()
//            val domainCoins = freshData.map { it.toDomain() }
//            _coins.value = domainCoins.associateBy { it.id }
//            coinDao.deleteAllCoins()
//            coinDao.insertAll(domainCoins.map { it.toEntity() })
//
//            // Prefetch chart data for all coins and selected time ranges
//            val selectedTimeRanges =
//                listOf(TimeRange.ONE_DAY, TimeRange.ONE_WEEK, TimeRange.ONE_YEAR, TimeRange.ALL)
//            domainCoins.forEach { coin ->
//                selectedTimeRanges.forEach { range ->
//                    CoroutineScope(Dispatchers.IO).launch {
//                        try {
//                            getCoinMarketChart(coin.id, range, "usd")
//                        } catch (_: Exception) { /* Ignore errors for prefetch */
//                        }
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            val errorMsg = "Failed to refresh coins from network: ${e.localizedMessage}"
//            Timber.tag("CoinRepositoryImpl").e(e, errorMsg)
//            _lastError.value = errorMsg
//            if (_coins.value.isEmpty()) {
//                // Handle the case where there's no cached data
//                val noCacheMsg = "No cached data available. Please check your internet connection."
//                Timber.tag("CoinRepositoryImpl").e(noCacheMsg)
//                _lastError.value = noCacheMsg
//            }
//        }
//    }
//
//    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
//    override suspend fun refreshCoins() {
//        if (networkUtil.isNetworkAvailable()) {
//            refreshFromNetwork()
//        }
//    }
//
//    // In-memory cache for chart data: (coinId, timeRange) -> List<PriceDataPoint>
//    private val chartCache = mutableMapOf<Pair<String, TimeRange>, List<PriceDataPoint>>()
//
//    override suspend fun getCoinMarketChart(
//        coinId: String,
//        timeRange: TimeRange,
//        vsCurrency: String
//    ): List<PriceDataPoint> {
//        val cacheKey = coinId to timeRange
//        chartCache[cacheKey]?.let { cached ->
//            return cached
//        }
//
//        // Persistent cache: check DB first
//        val cacheDurationMillis =
//            60 * 60 * 1000L // 1 hour (adjust to match your other coin data cache duration)
//        val now = System.currentTimeMillis()
//        val chartEntity = coinDao.getCoinChart(coinId, timeRange.name)
//        if (chartEntity != null && (now - chartEntity.timestamp) < cacheDurationMillis) {
//            // Parse JSON to List<Pair<Long, Double>>
//            val chartList = Converters().toChartList(chartEntity.priceDataJson) ?: emptyList()
//            val priceData = chartList.map { PriceDataPoint(it.first, it.second) }
//            chartCache[cacheKey] = priceData
//            return priceData
//        }
//
//        // Not cached or expired: fetch from network
//        return try {
//            val calendar = Calendar.getInstance()
//            val endTime = calendar.timeInMillis / 1000 // Current time in seconds
//            timeRange.days?.let { calendar.add(Calendar.DAY_OF_YEAR, -it) }
//            val startTime = calendar.timeInMillis / 1000 // Start time in seconds
//
//            val response = apiService.getCoinMarketChartRange(
//                id = coinId,
//                vsCurrency = vsCurrency,
//                from = startTime,
//                to = endTime
//            )
//            val data = response.toPriceDataPoints()
//            chartCache[cacheKey] = data
//            // Save to DB
//            val chartList = data.map { it.timestamp to it.price }
//            val entity = CoinChartEntity(
//                coinId = coinId,
//                timeRange = timeRange.name,
//                timestamp = now,
//                priceDataJson = Converters().fromChartList(chartList) ?: "[]"
//            )
//            coinDao.insertCoinChart(entity)
//            data
//        } catch (e: Exception) {
//            Timber.tag("CoinRepositoryImpl")
//                .e(e, "Error fetching market chart: ${e.localizedMessage}")
//            emptyList()
//        }
//    }
//
//    override suspend fun createPriceAlert(alert: PriceAlert): Result<Unit> {
//        return try {
//            val entity = PriceAlertEntity(
//                id = alert.id,
//                coinId = alert.coinId,
//                coinName = alert.coinName,
//                coinSymbol = alert.coinSymbol,
//                coinImageUrl = alert.coinImageUrl,
//                targetPrice = alert.targetPrice,
//                alertType = alert.alertType.name,
//                isActive = alert.isActive,
//                isTriggered = alert.isTriggered,
//                createdAt = alert.createdAt,
//                triggeredAt = alert.triggeredAt
//            )
//            priceAlertDao.insertAlert(entity)
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    override suspend fun deletePriceAlert(alertId: PriceAlertEntity): Result<Unit> {
//        return try {
//            priceAlertDao.deleteAlert(alertId)
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//
//    override fun getAllPriceAlerts(): Flow<Result<List<PriceAlert>>> {
//        return priceAlertDao.getAllAlerts()
//            .map { entities ->
//                try {
//                    val alerts = entities.map { entity ->
//                        PriceAlert(
//                            id = entity.id,
//                            coinId = entity.coinId,
//                            coinName = entity.coinName,
//                            coinSymbol = entity.coinSymbol,
//                            coinImageUrl = entity.coinImageUrl,
//                            targetPrice = entity.targetPrice,
//                            alertType = PriceAlertType.valueOf(entity.alertType),
//                            isActive = entity.isActive,
//                            isTriggered = entity.isTriggered,
//                            createdAt = entity.createdAt,
//                            triggeredAt = entity.triggeredAt
//                        )
//                    }
//                    Result.success(alerts)
//                } catch (e: Exception) {
//                    Result.failure(e)
//                }
//            }
//    }
//
//    override fun getActiveAlertsForCoin(coinId: String): Flow<Result<List<PriceAlert>>> {
//        return priceAlertDao.getActiveAlertsForCoin(coinId)
//            .map { entities ->
//                try {
//                    val alerts = entities.map { entity ->
//                        PriceAlert(
//                            id = entity.id,
//                            coinId = entity.coinId,
//                            coinName = entity.coinName,
//                            coinSymbol = entity.coinSymbol,
//                            coinImageUrl = entity.coinImageUrl,
//                            targetPrice = entity.targetPrice,
//                            alertType = PriceAlertType.valueOf(entity.alertType),
//                            isActive = entity.isActive,
//                            isTriggered = entity.isTriggered,
//                            createdAt = entity.createdAt,
//                            triggeredAt = entity.triggeredAt
//                        )
//                    }
//                    Result.success(alerts)
//                } catch (e: Exception) {
//                    Result.failure(e)
//                }
//            }
//    }
//
//    override suspend fun markAlertAsTriggered(alertId: String, triggeredAt: Long): Result<Unit> {
//        return try {
//            priceAlertDao.markAlertAsTriggered(alertId, triggeredAt)
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//
//}
