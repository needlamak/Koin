package com.koin.data.coin

import android.Manifest
import androidx.annotation.RequiresPermission
import com.koin.domain.coin.Coin
import com.koin.domain.coin.CoinRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

@Singleton
class CoinRepositoryImpl @Inject constructor(
    private val apiService: CoinGeckoApiService,
    private val coinDao: CoinDao,
    private val networkUtil: NetworkUtil
) : CoinRepository {

    private val _coins = MutableStateFlow<Map<String, Coin>>(emptyMap())
    val coins = _coins.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError = _lastError.asStateFlow()

    override fun getAllCoins(): Flow<Result<List<Coin>>> =
        coins.map { Result.success(it.values.toList()) }

    override fun getCoinById(id: String): Flow<Result<Coin?>> =
        coins.map { Result.success(it[id]) }

    init {
        CoroutineScope(Dispatchers.IO).launch @androidx.annotation.RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE) {
            loadFromCache()
            if (networkUtil.isNetworkAvailable()) {
                refreshFromNetwork()
            }
        }
    }

    private suspend fun loadFromCache() {
        try {
            val cachedCoins = coinDao.getAllCoins().first()
            if (cachedCoins.isNotEmpty()) {
                _coins.value = cachedCoins.map { it.toDomain() }.associateBy { it.id }
            }
        } catch (e: Exception) {
            val errorMsg = "Failed to load coins from cache: ${e.localizedMessage}"
            Log.e("CoinRepositoryImpl", errorMsg, e)
            _lastError.value = errorMsg
        }
    }

    internal suspend fun refreshFromNetwork() {
        try {
            val freshData = apiService.getCoinsWithFullDetails()
            val domainCoins = freshData.map { it.toDomain() }
            _coins.value = domainCoins.associateBy { it.id }
            coinDao.deleteAllCoins()
            coinDao.insertAll(domainCoins.map { it.toEntity() })
        } catch (e: Exception) {
            val errorMsg = "Failed to refresh coins from network: ${e.localizedMessage}"
            Log.e("CoinRepositoryImpl", errorMsg, e)
            _lastError.value = errorMsg
            if (_coins.value.isEmpty()) {
                // Handle the case where there's no cached data
                val noCacheMsg = "No cached data available. Please check your internet connection."
                Log.e("CoinRepositoryImpl", noCacheMsg)
                _lastError.value = noCacheMsg
            }
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override suspend fun refreshCoins() {
        if (networkUtil.isNetworkAvailable()) {
            refreshFromNetwork()
        }
    }
} 