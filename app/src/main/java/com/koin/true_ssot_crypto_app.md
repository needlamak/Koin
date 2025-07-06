[//]: # (# True SSoT Crypto App Architecture &#40;MVVM Clean Architecture&#41;)

[//]: # ()
[//]: # (A crypto application using **True Single Source of Truth &#40;SSoT&#41;** with MVVM Clean Architecture, Jetpack Compose, comprehensive data caching, and optimal performance.)

[//]: # ()
[//]: # (## Core SSoT Principles)

[//]: # (- **Repository as the Only Source of Truth**: All data flows through repository's StateFlow)

[//]: # (- **Single Comprehensive API Call**: One network request serves both list and detail screens)

[//]: # (- **Immediate Data Availability**: No loading states on detail screen)

[//]: # (- **Reactive UI**: Automatic updates across all screens)

[//]: # (- **Offline-First**: Room provides instant cached data)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (## I. Project Setup & Core Dependencies)

[//]: # ()
[//]: # (### 1. Project Initialization)

[//]: # (- Create a new Android Studio project &#40;Kotlin first&#41;)

[//]: # (- Configure build.gradle &#40;project and app level&#41; for Kotlin, Compose, and necessary plugins)

[//]: # ()
[//]: # (### 2. Core Android Jetpack & Kotlin Dependencies)

[//]: # (```kotlin)

[//]: # (// Compose BOM for consistent versions)

[//]: # (implementation platform&#40;'androidx.compose:compose-bom:2024.02.00'&#41;)

[//]: # (implementation 'androidx.compose.ui:ui')

[//]: # (implementation 'androidx.compose.ui:ui-tooling-preview')

[//]: # (implementation 'androidx.compose.material3:material3')

[//]: # (implementation 'androidx.compose.ui:ui-tooling')

[//]: # ()
[//]: # (// Navigation)

[//]: # (implementation 'androidx.navigation:navigation-compose:2.7.6')

[//]: # ()
[//]: # (// Lifecycle & ViewModel)

[//]: # (implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0')

[//]: # (implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0')

[//]: # ()
[//]: # (// Coroutines)

[//]: # (implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3')

[//]: # (implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3')

[//]: # (```)

[//]: # ()
[//]: # (### 3. Networking Dependencies)

[//]: # (```kotlin)

[//]: # (// Retrofit)

[//]: # (implementation 'com.squareup.retrofit2:retrofit:2.9.0')

[//]: # (implementation 'com.squareup.retrofit2:converter-gson:2.9.0')

[//]: # ()
[//]: # (// OkHttp)

[//]: # (implementation 'com.squareup.okhttp3:okhttp:4.12.0')

[//]: # (implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0')

[//]: # (```)

[//]: # ()
[//]: # (### 4. Local Persistence Dependencies)

[//]: # (```kotlin)

[//]: # (// Room Database)

[//]: # (implementation 'androidx.room:room-runtime:2.6.1')

[//]: # (implementation 'androidx.room:room-ktx:2.6.1')

[//]: # (kapt 'androidx.room:room-compiler:2.6.1')

[//]: # (```)

[//]: # ()
[//]: # (### 5. Dependency Injection Dependencies)

[//]: # (```kotlin)

[//]: # (// Dagger Hilt)

[//]: # (implementation 'com.google.dagger:hilt-android:2.48.1')

[//]: # (kapt 'com.google.dagger:hilt-android-compiler:2.48.1')

[//]: # (implementation 'androidx.hilt:hilt-navigation-compose:1.1.0')

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (## II. Data Layer &#40;True SSoT Hub&#41;)

[//]: # ()
[//]: # (### 1. API Service &#40;Single Comprehensive Call&#41;)

[//]: # (```kotlin)

[//]: # (interface CoinGeckoApiService {)

[//]: # (    @GET&#40;"coins/markets"&#41;)

[//]: # (    suspend fun getCoinsWithFullDetails&#40;)

[//]: # (        @Query&#40;"vs_currency"&#41; currency: String = "usd",)

[//]: # (        @Query&#40;"order"&#41; order: String = "market_cap_desc",)

[//]: # (        @Query&#40;"per_page"&#41; perPage: Int = 100,)

[//]: # (        @Query&#40;"page"&#41; page: Int = 1,)

[//]: # (        @Query&#40;"sparkline"&#41; sparkline: Boolean = true,)

[//]: # (        @Query&#40;"price_change_percentage"&#41; priceChange: String = "1h,24h,7d,30d")

[//]: # (    &#41;: List<CoinDetailDto>)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (### 2. Data Models)

[//]: # ()
[//]: # (#### Remote Data Model &#40;API Response&#41;)

[//]: # (```kotlin)

[//]: # (@Serializable)

[//]: # (data class CoinDetailDto&#40;)

[//]: # (    val id: String,)

[//]: # (    val name: String,)

[//]: # (    val symbol: String,)

[//]: # (    val image: String,)

[//]: # (    @SerializedName&#40;"current_price"&#41; val currentPrice: Double,)

[//]: # (    @SerializedName&#40;"market_cap"&#41; val marketCap: Long,)

[//]: # (    @SerializedName&#40;"market_cap_rank"&#41; val marketCapRank: Int,)

[//]: # (    @SerializedName&#40;"price_change_24h"&#41; val priceChange24h: Double,)

[//]: # (    @SerializedName&#40;"price_change_percentage_24h"&#41; val priceChangePercentage24h: Double,)

[//]: # (    @SerializedName&#40;"price_change_percentage_1h_in_currency"&#41; val priceChangePercentage1h: Double?,)

[//]: # (    @SerializedName&#40;"price_change_percentage_7d_in_currency"&#41; val priceChangePercentage7d: Double?,)

[//]: # (    @SerializedName&#40;"price_change_percentage_30d_in_currency"&#41; val priceChangePercentage30d: Double?,)

[//]: # (    @SerializedName&#40;"sparkline_in_7d"&#41; val sparklineIn7d: SparklineDto?,)

[//]: # (    @SerializedName&#40;"high_24h"&#41; val high24h: Double?,)

[//]: # (    @SerializedName&#40;"low_24h"&#41; val low24h: Double?,)

[//]: # (    @SerializedName&#40;"total_volume"&#41; val totalVolume: Long?,)

[//]: # (    @SerializedName&#40;"circulating_supply"&#41; val circulatingSupply: Double?,)

[//]: # (    @SerializedName&#40;"total_supply"&#41; val totalSupply: Double?,)

[//]: # (    @SerializedName&#40;"max_supply"&#41; val maxSupply: Double?,)

[//]: # (    val ath: Double?,)

[//]: # (    @SerializedName&#40;"ath_date"&#41; val athDate: String?,)

[//]: # (    val atl: Double?,)

[//]: # (    @SerializedName&#40;"atl_date"&#41; val atlDate: String?)

[//]: # (&#41;)

[//]: # ()
[//]: # (@Serializable)

[//]: # (data class SparklineDto&#40;)

[//]: # (    val price: List<Double>)

[//]: # (&#41;)

[//]: # (```)

[//]: # ()
[//]: # (#### Local Data Model &#40;Room Entity&#41;)

[//]: # (```kotlin)

[//]: # (@Entity&#40;tableName = "coins"&#41;)

[//]: # (data class CoinEntity&#40;)

[//]: # (    @PrimaryKey val id: String,)

[//]: # (    val name: String,)

[//]: # (    val symbol: String,)

[//]: # (    val imageUrl: String,)

[//]: # (    val currentPrice: Double,)

[//]: # (    val marketCap: Long,)

[//]: # (    val marketCapRank: Int,)

[//]: # (    val priceChange24h: Double,)

[//]: # (    val priceChangePercentage24h: Double,)

[//]: # (    val priceChangePercentage1h: Double?,)

[//]: # (    val priceChangePercentage7d: Double?,)

[//]: # (    val priceChangePercentage30d: Double?,)

[//]: # (    val sparklineData: String?, // JSON string of List<Double>)

[//]: # (    val high24h: Double?,)

[//]: # (    val low24h: Double?,)

[//]: # (    val totalVolume: Long?,)

[//]: # (    val circulatingSupply: Double?,)

[//]: # (    val totalSupply: Double?,)

[//]: # (    val maxSupply: Double?,)

[//]: # (    val ath: Double?,)

[//]: # (    val athDate: String?,)

[//]: # (    val atl: Double?,)

[//]: # (    val atlDate: String?,)

[//]: # (    val lastUpdated: Long = System.currentTimeMillis&#40;&#41;)

[//]: # (&#41;)

[//]: # (```)

[//]: # ()
[//]: # (### 3. Local Database &#40;Room&#41;)

[//]: # ()
[//]: # (#### DAO Interface)

[//]: # (```kotlin)

[//]: # (@Dao)

[//]: # (interface CoinDao {)

[//]: # (    @Query&#40;"SELECT * FROM coins ORDER BY marketCapRank ASC"&#41;)

[//]: # (    fun getAllCoins&#40;&#41;: Flow<List<CoinEntity>>)

[//]: # (    )
[//]: # (    @Query&#40;"SELECT * FROM coins WHERE id = :coinId"&#41;)

[//]: # (    fun getCoinById&#40;coinId: String&#41;: Flow<CoinEntity?>)

[//]: # (    )
[//]: # (    @Insert&#40;onConflict = OnConflictStrategy.REPLACE&#41;)

[//]: # (    suspend fun insertAll&#40;coins: List<CoinEntity>&#41;)

[//]: # (    )
[//]: # (    @Insert&#40;onConflict = OnConflictStrategy.REPLACE&#41;)

[//]: # (    suspend fun insertCoin&#40;coin: CoinEntity&#41;)

[//]: # (    )
[//]: # (    @Query&#40;"DELETE FROM coins"&#41;)

[//]: # (    suspend fun deleteAllCoins&#40;&#41;)

[//]: # (    )
[//]: # (    @Query&#40;"SELECT COUNT&#40;*&#41; FROM coins"&#41;)

[//]: # (    suspend fun getCoinCount&#40;&#41;: Int)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (#### Room Database Class)

[//]: # (```kotlin)

[//]: # (@Database&#40;)

[//]: # (    entities = [CoinEntity::class],)

[//]: # (    version = 1,)

[//]: # (    exportSchema = false)

[//]: # (&#41;)

[//]: # (@TypeConverters&#40;Converters::class&#41;)

[//]: # (abstract class CoinDatabase : RoomDatabase&#40;&#41; {)

[//]: # (    abstract fun coinDao&#40;&#41;: CoinDao)

[//]: # (})

[//]: # ()
[//]: # (class Converters {)

[//]: # (    @TypeConverter)

[//]: # (    fun fromStringList&#40;value: List<Double>?&#41;: String? {)

[//]: # (        return value?.let { Gson&#40;&#41;.toJson&#40;it&#41; })

[//]: # (    })

[//]: # (    )
[//]: # (    @TypeConverter)

[//]: # (    fun toStringList&#40;value: String?&#41;: List<Double>? {)

[//]: # (        return value?.let { )

[//]: # (            Gson&#40;&#41;.fromJson&#40;it, object : TypeToken<List<Double>>&#40;&#41; {}.type&#41; )

[//]: # (        })

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (### 4. Repository Implementation &#40;The True SSoT&#41;)

[//]: # ()
[//]: # (#### Repository Interface)

[//]: # (```kotlin)

[//]: # (interface CoinRepository {)

[//]: # (    fun getAllCoins&#40;&#41;: Flow<Result<List<Coin>>>)

[//]: # (    fun getCoinById&#40;id: String&#41;: Flow<Result<Coin?>>)

[//]: # (    suspend fun refreshCoins&#40;&#41;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (#### Repository Implementation)

[//]: # (```kotlin)

[//]: # (@Singleton)

[//]: # (class CoinRepositoryImpl @Inject constructor&#40;)

[//]: # (    private val apiService: CoinGeckoApiService,)

[//]: # (    private val coinDao: CoinDao,)

[//]: # (    private val networkUtil: NetworkUtil)

[//]: # (&#41; : CoinRepository {)

[//]: # (    )
[//]: # (    // This is the TRUE Single Source of Truth)

[//]: # (    private val _coins = MutableStateFlow<Map<String, Coin>>&#40;emptyMap&#40;&#41;&#41;)

[//]: # (    val coins = _coins.asStateFlow&#40;&#41;)

[//]: # (    )
[//]: # (    override fun getAllCoins&#40;&#41;: Flow<Result<List<Coin>>> = )

[//]: # (        coins.map { Result.success&#40;it.values.toList&#40;&#41;&#41; })

[//]: # (    )
[//]: # (    override fun getCoinById&#40;id: String&#41;: Flow<Result<Coin?>> = )

[//]: # (        coins.map { Result.success&#40;it[id]&#41; })

[//]: # (    )
[//]: # (    init {)

[//]: # (        // Initialize SSoT with cached data first, then refresh)

[//]: # (        CoroutineScope&#40;Dispatchers.IO&#41;.launch {)

[//]: # (            loadFromCache&#40;&#41;)

[//]: # (            if &#40;networkUtil.isNetworkAvailable&#40;&#41;&#41; {)

[//]: # (                refreshFromNetwork&#40;&#41;)

[//]: # (            })

[//]: # (        })

[//]: # (    })

[//]: # (    )
[//]: # (    private suspend fun loadFromCache&#40;&#41; {)

[//]: # (        try {)

[//]: # (            val cachedCoins = coinDao.getAllCoins&#40;&#41;.first&#40;&#41;)

[//]: # (            if &#40;cachedCoins.isNotEmpty&#40;&#41;&#41; {)

[//]: # (                _coins.value = cachedCoins.map { it.toDomain&#40;&#41; }.associateBy { it.id })

[//]: # (            })

[//]: # (        } catch &#40;e: Exception&#41; {)

[//]: # (            // Handle cache loading error)

[//]: # (        })

[//]: # (    })

[//]: # (    )
[//]: # (    private suspend fun refreshFromNetwork&#40;&#41; {)

[//]: # (        try {)

[//]: # (            val freshData = apiService.getCoinsWithFullDetails&#40;&#41;)

[//]: # (            val domainCoins = freshData.map { it.toDomain&#40;&#41; })

[//]: # (            )
[//]: # (            // Update the Single Source of Truth)

[//]: # (            _coins.value = domainCoins.associateBy { it.id })

[//]: # (            )
[//]: # (            // Cache for offline access)

[//]: # (            coinDao.deleteAllCoins&#40;&#41;)

[//]: # (            coinDao.insertAll&#40;domainCoins.map { it.toEntity&#40;&#41; }&#41;)

[//]: # (            )
[//]: # (        } catch &#40;e: Exception&#41; {)

[//]: # (            // Network error - cached data remains available in _coins)

[//]: # (            // Could emit error state if no cached data exists)

[//]: # (            if &#40;_coins.value.isEmpty&#40;&#41;&#41; {)

[//]: # (                // Handle the case where there's no cached data)

[//]: # (            })

[//]: # (        })

[//]: # (    })

[//]: # (    )
[//]: # (    override suspend fun refreshCoins&#40;&#41; {)

[//]: # (        if &#40;networkUtil.isNetworkAvailable&#40;&#41;&#41; {)

[//]: # (            refreshFromNetwork&#40;&#41;)

[//]: # (        })

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (### 5. Data Mappers)

[//]: # (```kotlin)

[//]: # (// DTO to Domain)

[//]: # (fun CoinDetailDto.toDomain&#40;&#41;: Coin {)

[//]: # (    return Coin&#40;)

[//]: # (        id = id,)

[//]: # (        name = name,)

[//]: # (        symbol = symbol,)

[//]: # (        imageUrl = image,)

[//]: # (        currentPrice = currentPrice,)

[//]: # (        marketCap = marketCap,)

[//]: # (        marketCapRank = marketCapRank,)

[//]: # (        priceChange24h = priceChange24h,)

[//]: # (        priceChangePercentage24h = priceChangePercentage24h,)

[//]: # (        priceChangePercentage1h = priceChangePercentage1h,)

[//]: # (        priceChangePercentage7d = priceChangePercentage7d,)

[//]: # (        priceChangePercentage30d = priceChangePercentage30d,)

[//]: # (        sparklineData = sparklineIn7d?.price,)

[//]: # (        high24h = high24h,)

[//]: # (        low24h = low24h,)

[//]: # (        totalVolume = totalVolume,)

[//]: # (        circulatingSupply = circulatingSupply,)

[//]: # (        totalSupply = totalSupply,)

[//]: # (        maxSupply = maxSupply,)

[//]: # (        ath = ath,)

[//]: # (        athDate = athDate,)

[//]: # (        atl = atl,)

[//]: # (        atlDate = atlDate)

[//]: # (    &#41;)

[//]: # (})

[//]: # ()
[//]: # (// Domain to Entity)

[//]: # (fun Coin.toEntity&#40;&#41;: CoinEntity {)

[//]: # (    return CoinEntity&#40;)

[//]: # (        id = id,)

[//]: # (        name = name,)

[//]: # (        symbol = symbol,)

[//]: # (        imageUrl = imageUrl,)

[//]: # (        currentPrice = currentPrice,)

[//]: # (        marketCap = marketCap,)

[//]: # (        marketCapRank = marketCapRank,)

[//]: # (        priceChange24h = priceChange24h,)

[//]: # (        priceChangePercentage24h = priceChangePercentage24h,)

[//]: # (        priceChangePercentage1h = priceChangePercentage1h,)

[//]: # (        priceChangePercentage7d = priceChangePercentage7d,)

[//]: # (        priceChangePercentage30d = priceChangePercentage30d,)

[//]: # (        sparklineData = sparklineData?.let { Gson&#40;&#41;.toJson&#40;it&#41; },)

[//]: # (        high24h = high24h,)

[//]: # (        low24h = low24h,)

[//]: # (        totalVolume = totalVolume,)

[//]: # (        circulatingSupply = circulatingSupply,)

[//]: # (        totalSupply = totalSupply,)

[//]: # (        maxSupply = maxSupply,)

[//]: # (        ath = ath,)

[//]: # (        athDate = athDate,)

[//]: # (        atl = atl,)

[//]: # (        atlDate = atlDate)

[//]: # (    &#41;)

[//]: # (})

[//]: # ()
[//]: # (// Entity to Domain)

[//]: # (fun CoinEntity.toDomain&#40;&#41;: Coin {)

[//]: # (    return Coin&#40;)

[//]: # (        id = id,)

[//]: # (        name = name,)

[//]: # (        symbol = symbol,)

[//]: # (        imageUrl = imageUrl,)

[//]: # (        currentPrice = currentPrice,)

[//]: # (        marketCap = marketCap,)

[//]: # (        marketCapRank = marketCapRank,)

[//]: # (        priceChange24h = priceChange24h,)

[//]: # (        priceChangePercentage24h = priceChangePercentage24h,)

[//]: # (        priceChangePercentage1h = priceChangePercentage1h,)

[//]: # (        priceChangePercentage7d = priceChangePercentage7d,)

[//]: # (        priceChangePercentage30d = priceChangePercentage30d,)

[//]: # (        sparklineData = sparklineData?.let { )

[//]: # (            Gson&#40;&#41;.fromJson&#40;it, object : TypeToken<List<Double>>&#40;&#41; {}.type&#41;)

[//]: # (        },)

[//]: # (        high24h = high24h,)

[//]: # (        low24h = low24h,)

[//]: # (        totalVolume = totalVolume,)

[//]: # (        circulatingSupply = circulatingSupply,)

[//]: # (        totalSupply = totalSupply,)

[//]: # (        maxSupply = maxSupply,)

[//]: # (        ath = ath,)

[//]: # (        athDate = athDate,)

[//]: # (        atl = atl,)

[//]: # (        atlDate = atlDate)

[//]: # (    &#41;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (## III. Domain Layer &#40;Business Logic&#41;)

[//]: # ()
[//]: # (### 1. Domain Model &#40;Single Rich Model&#41;)

[//]: # (```kotlin)

[//]: # (data class Coin&#40;)

[//]: # (    val id: String,)

[//]: # (    val name: String,)

[//]: # (    val symbol: String,)

[//]: # (    val imageUrl: String,)

[//]: # (    val currentPrice: Double,)

[//]: # (    val marketCap: Long,)

[//]: # (    val marketCapRank: Int,)

[//]: # (    val priceChange24h: Double,)

[//]: # (    val priceChangePercentage24h: Double,)

[//]: # (    val priceChangePercentage1h: Double?,)

[//]: # (    val priceChangePercentage7d: Double?,)

[//]: # (    val priceChangePercentage30d: Double?,)

[//]: # (    val sparklineData: List<Double>?,)

[//]: # (    val high24h: Double?,)

[//]: # (    val low24h: Double?,)

[//]: # (    val totalVolume: Long?,)

[//]: # (    val circulatingSupply: Double?,)

[//]: # (    val totalSupply: Double?,)

[//]: # (    val maxSupply: Double?,)

[//]: # (    val ath: Double?,)

[//]: # (    val athDate: String?,)

[//]: # (    val atl: Double?,)

[//]: # (    val atlDate: String?)

[//]: # (&#41; {)

[//]: # (    // Computed properties for list display)

[//]: # (    val isPositive24h: Boolean get&#40;&#41; = priceChangePercentage24h > 0)

[//]: # (    val formattedPrice: String get&#40;&#41; = NumberFormat.getCurrencyInstance&#40;&#41;.format&#40;currentPrice&#41;)

[//]: # (    val formattedMarketCap: String get&#40;&#41; = formatLargeNumber&#40;marketCap&#41;)

[//]: # (    val formattedPriceChange: String get&#40;&#41; = "${if &#40;isPositive24h&#41; "+" else ""}${String.format&#40;"%.2f", priceChangePercentage24h&#41;}%")

[//]: # (    )
[//]: # (    // Computed properties for details display)

[//]: # (    val hasSparklineData: Boolean get&#40;&#41; = !sparklineData.isNullOrEmpty&#40;&#41;)

[//]: # (    val supplyPercentage: Double? get&#40;&#41; = )

[//]: # (        if &#40;totalSupply != null && circulatingSupply != null&#41; )

[//]: # (            &#40;circulatingSupply / totalSupply&#41; * 100 )

[//]: # (        else null)

[//]: # (    )
[//]: # (    val formattedVolume: String get&#40;&#41; = totalVolume?.let { formatLargeNumber&#40;it&#41; } ?: "N/A")

[//]: # (    val formattedSupply: String get&#40;&#41; = circulatingSupply?.let { formatLargeNumber&#40;it.toLong&#40;&#41;&#41; } ?: "N/A")

[//]: # (})

[//]: # ()
[//]: # (private fun formatLargeNumber&#40;number: Long&#41;: String {)

[//]: # (    return when {)

[//]: # (        number >= 1_000_000_000_000 -> "${String.format&#40;"%.1f", number / 1_000_000_000_000.0&#41;}T")

[//]: # (        number >= 1_000_000_000 -> "${String.format&#40;"%.1f", number / 1_000_000_000.0&#41;}B")

[//]: # (        number >= 1_000_000 -> "${String.format&#40;"%.1f", number / 1_000_000.0&#41;}M")

[//]: # (        number >= 1_000 -> "${String.format&#40;"%.1f", number / 1_000.0&#41;}K")

[//]: # (        else -> number.toString&#40;&#41;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (### 2. Use Cases &#40;Single Shared Use Case&#41;)

[//]: # (```kotlin)

[//]: # (@Singleton)

[//]: # (class GetCoinsUseCase @Inject constructor&#40;)

[//]: # (    private val repository: CoinRepository)

[//]: # (&#41; {)

[//]: # (    operator fun invoke&#40;&#41;: Flow<Result<List<Coin>>> = repository.getAllCoins&#40;&#41;)

[//]: # (})

[//]: # ()
[//]: # (@Singleton)

[//]: # (class GetCoinByIdUseCase @Inject constructor&#40;)

[//]: # (    private val repository: CoinRepository)

[//]: # (&#41; {)

[//]: # (    operator fun invoke&#40;coinId: String&#41;: Flow<Result<Coin?>> = repository.getCoinById&#40;coinId&#41;)

[//]: # (})

[//]: # ()
[//]: # (@Singleton)

[//]: # (class RefreshCoinsUseCase @Inject constructor&#40;)

[//]: # (    private val repository: CoinRepository)

[//]: # (&#41; {)

[//]: # (    suspend operator fun invoke&#40;&#41; = repository.refreshCoins&#40;&#41;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (## IV. Presentation Layer &#40;Jetpack Compose UI&#41;)

[//]: # ()
[//]: # (### 1. UI State Models)

[//]: # (```kotlin)

[//]: # (data class CoinListUiState&#40;)

[//]: # (    val isLoading: Boolean = false,)

[//]: # (    val coins: List<Coin> = emptyList&#40;&#41;,)

[//]: # (    val error: String? = null,)

[//]: # (    val isRefreshing: Boolean = false)

[//]: # (&#41;)

[//]: # ()
[//]: # (data class CoinDetailsUiState&#40;)

[//]: # (    val coin: Coin? = null,)

[//]: # (    val error: String? = null)

[//]: # (&#41;)

[//]: # (```)

[//]: # ()
[//]: # (### 2. Shared ViewModel &#40;Navigation Graph Scoped&#41;)

[//]: # (```kotlin)

[//]: # (@HiltViewModel)

[//]: # (class SharedCoinViewModel @Inject constructor&#40;)

[//]: # (    private val getCoinsUseCase: GetCoinsUseCase,)

[//]: # (    private val refreshCoinsUseCase: RefreshCoinsUseCase)

[//]: # (&#41; : ViewModel&#40;&#41; {)

[//]: # (    )
[//]: # (    private val _selectedCoinId = MutableStateFlow<String?>&#40;null&#41;)

[//]: # (    val selectedCoinId = _selectedCoinId.asStateFlow&#40;&#41;)

[//]: # (    )
[//]: # (    // Single data source for all screens)

[//]: # (    val coins = getCoinsUseCase&#40;&#41;)

[//]: # (        .catch { emit&#40;Result.failure&#40;it&#41;&#41; })

[//]: # (        .stateIn&#40;)

[//]: # (            viewModelScope,)

[//]: # (            SharingStarted.WhileSubscribed&#40;5000&#41;,)

[//]: # (            Result.success&#40;emptyList&#40;&#41;&#41;)

[//]: # (        &#41;)

[//]: # (    )
[//]: # (    // Derived state for selected coin &#40;no separate network call needed!&#41;)

[//]: # (    val selectedCoin = combine&#40;selectedCoinId, coins&#41; { id, coinsResult ->)

[//]: # (        id?.let { coinId ->)

[//]: # (            coinsResult.getOrNull&#40;&#41;?.find { it.id == coinId })

[//]: # (        })

[//]: # (    }.stateIn&#40;)

[//]: # (        viewModelScope,)

[//]: # (        SharingStarted.WhileSubscribed&#40;5000&#41;,)

[//]: # (        null)

[//]: # (    &#41;)

[//]: # (    )
[//]: # (    private val _isRefreshing = MutableStateFlow&#40;false&#41;)

[//]: # (    val isRefreshing = _isRefreshing.asStateFlow&#40;&#41;)

[//]: # (    )
[//]: # (    fun selectCoin&#40;coinId: String&#41; {)

[//]: # (        _selectedCoinId.value = coinId)

[//]: # (    })

[//]: # (    )
[//]: # (    fun refreshCoins&#40;&#41; {)

[//]: # (        viewModelScope.launch {)

[//]: # (            _isRefreshing.value = true)

[//]: # (            try {)

[//]: # (                refreshCoinsUseCase&#40;&#41;)

[//]: # (            } finally {)

[//]: # (                _isRefreshing.value = false)

[//]: # (            })

[//]: # (        })

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (### 3. Composable Screens)

[//]: # ()
[//]: # (#### Coin List Screen)

[//]: # (```kotlin)

[//]: # (@Composable)

[//]: # (fun CoinListScreen&#40;)

[//]: # (    sharedViewModel: SharedCoinViewModel = hiltViewModel&#40;&#41;,)

[//]: # (    onCoinClick: &#40;String&#41; -> Unit)

[//]: # (&#41; {)

[//]: # (    val coinsResult by sharedViewModel.coins.collectAsState&#40;&#41;)

[//]: # (    val isRefreshing by sharedViewModel.isRefreshing.collectAsState&#40;&#41;)

[//]: # (    )
[//]: # (    val pullRefreshState = rememberPullRefreshState&#40;)

[//]: # (        refreshing = isRefreshing,)

[//]: # (        onRefresh = { sharedViewModel.refreshCoins&#40;&#41; })

[//]: # (    &#41;)

[//]: # (    )
[//]: # (    Box&#40;)

[//]: # (        modifier = Modifier)

[//]: # (            .fillMaxSize&#40;&#41;)

[//]: # (            .pullRefresh&#40;pullRefreshState&#41;)

[//]: # (    &#41; {)

[//]: # (        coinsResult.fold&#40;)

[//]: # (            onSuccess = { coins ->)

[//]: # (                LazyColumn&#40;)

[//]: # (                    modifier = Modifier.fillMaxSize&#40;&#41;,)

[//]: # (                    contentPadding = PaddingValues&#40;16.dp&#41;,)

[//]: # (                    verticalArrangement = Arrangement.spacedBy&#40;8.dp&#41;)

[//]: # (                &#41; {)

[//]: # (                    items&#40;coins&#41; { coin ->)

[//]: # (                        CoinListItem&#40;)

[//]: # (                            coin = coin,)

[//]: # (                            onClick = { )

[//]: # (                                sharedViewModel.selectCoin&#40;coin.id&#41;)

[//]: # (                                onCoinClick&#40;coin.id&#41;)

[//]: # (                            })

[//]: # (                        &#41;)

[//]: # (                    })

[//]: # (                })

[//]: # (            },)

[//]: # (            onFailure = { error ->)

[//]: # (                ErrorScreen&#40;)

[//]: # (                    error = error.message ?: "Unknown error",)

[//]: # (                    onRetry = { sharedViewModel.refreshCoins&#40;&#41; })

[//]: # (                &#41;)

[//]: # (            })

[//]: # (        &#41;)

[//]: # (        )
[//]: # (        PullRefreshIndicator&#40;)

[//]: # (            refreshing = isRefreshing,)

[//]: # (            state = pullRefreshState,)

[//]: # (            modifier = Modifier.align&#40;Alignment.TopCenter&#41;)

[//]: # (        &#41;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (#### Coin Details Screen &#40;No Loading State!&#41;)

[//]: # (```kotlin)

[//]: # (@Composable)

[//]: # (fun CoinDetailsScreen&#40;)

[//]: # (    coinId: String,)

[//]: # (    sharedViewModel: SharedCoinViewModel = hiltViewModel&#40;&#41;,)

[//]: # (    onBackClick: &#40;&#41; -> Unit)

[//]: # (&#41; {)

[//]: # (    val selectedCoin by sharedViewModel.selectedCoin.collectAsState&#40;&#41;)

[//]: # (    )
[//]: # (    // Data is already available from the shared fetch!)

[//]: # (    selectedCoin?.let { coin ->)

[//]: # (        Column&#40;)

[//]: # (            modifier = Modifier)

[//]: # (                .fillMaxSize&#40;&#41;)

[//]: # (                .verticalScroll&#40;rememberScrollState&#40;&#41;&#41;)

[//]: # (        &#41; {)

[//]: # (            CoinDetailsHeader&#40;)

[//]: # (                coin = coin,)

[//]: # (                onBackClick = onBackClick)

[//]: # (            &#41;)

[//]: # (            )
[//]: # (            CoinPriceChart&#40;)

[//]: # (                coin = coin,)

[//]: # (                modifier = Modifier.padding&#40;16.dp&#41;)

[//]: # (            &#41;)

[//]: # (            )
[//]: # (            CoinStatistics&#40;)

[//]: # (                coin = coin,)

[//]: # (                modifier = Modifier.padding&#40;16.dp&#41;)

[//]: # (            &#41;)

[//]: # (            )
[//]: # (            CoinSupplyInfo&#40;)

[//]: # (                coin = coin,)

[//]: # (                modifier = Modifier.padding&#40;16.dp&#41;)

[//]: # (            &#41;)

[//]: # (        })

[//]: # (    } ?: run {)

[//]: # (        // This should rarely happen since data is pre-loaded)

[//]: # (        LoadingScreen&#40;&#41;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (### 4. Navigation Graph)

[//]: # (```kotlin)

[//]: # (@Composable)

[//]: # (fun CoinNavGraph&#40;)

[//]: # (    navController: NavHostController,)

[//]: # (    sharedViewModel: SharedCoinViewModel = hiltViewModel&#40;&#41;)

[//]: # (&#41; {)

[//]: # (    NavHost&#40;)

[//]: # (        navController = navController,)

[//]: # (        startDestination = "coin_list")

[//]: # (    &#41; {)

[//]: # (        composable&#40;"coin_list"&#41; {)

[//]: # (            CoinListScreen&#40;)

[//]: # (                sharedViewModel = sharedViewModel,)

[//]: # (                onCoinClick = { coinId ->)

[//]: # (                    navController.navigate&#40;"coin_details/$coinId"&#41;)

[//]: # (                })

[//]: # (            &#41;)

[//]: # (        })

[//]: # (        )
[//]: # (        composable&#40;)

[//]: # (            "coin_details/{coinId}",)

[//]: # (            arguments = listOf&#40;navArgument&#40;"coinId"&#41; { type = NavType.StringType }&#41;)

[//]: # (        &#41; { backStackEntry ->)

[//]: # (            val coinId = backStackEntry.arguments?.getString&#40;"coinId"&#41; ?: return@composable)

[//]: # (            CoinDetailsScreen&#40;)

[//]: # (                coinId = coinId,)

[//]: # (                sharedViewModel = sharedViewModel,)

[//]: # (                onBackClick = { navController.popBackStack&#40;&#41; })

[//]: # (            &#41;)

[//]: # (        })

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (## V. Dependency Injection &#40;Dagger Hilt&#41;)

[//]: # ()
[//]: # (### 1. Application Class)

[//]: # (```kotlin)

[//]: # (@HiltAndroidApp)

[//]: # (class CoinApplication : Application&#40;&#41;)

[//]: # (```)

[//]: # ()
[//]: # (### 2. Hilt Modules)

[//]: # ()
[//]: # (#### Network Module)

[//]: # (```kotlin)

[//]: # (@Module)

[//]: # (@InstallIn&#40;SingletonComponent::class&#41;)

[//]: # (object NetworkModule {)

[//]: # (    )
[//]: # (    @Provides)

[//]: # (    @Singleton)

[//]: # (    fun provideOkHttpClient&#40;&#41;: OkHttpClient {)

[//]: # (        return OkHttpClient.Builder&#40;&#41;)

[//]: # (            .addInterceptor&#40;HttpLoggingInterceptor&#40;&#41;.apply {)

[//]: # (                level = HttpLoggingInterceptor.Level.BODY)

[//]: # (            }&#41;)

[//]: # (            .addInterceptor { chain ->)

[//]: # (                val request = chain.request&#40;&#41;.newBuilder&#40;&#41;)

[//]: # (                    .addHeader&#40;"Accept", "application/json"&#41;)

[//]: # (                    .build&#40;&#41;)

[//]: # (                chain.proceed&#40;request&#41;)

[//]: # (            })

[//]: # (            .build&#40;&#41;)

[//]: # (    })

[//]: # (    )
[//]: # (    @Provides)

[//]: # (    @Singleton)

[//]: # (    fun provideRetrofit&#40;okHttpClient: OkHttpClient&#41;: Retrofit {)

[//]: # (        return Retrofit.Builder&#40;&#41;)

[//]: # (            .baseUrl&#40;"https://api.coingecko.com/api/v3/"&#41;)

[//]: # (            .client&#40;okHttpClient&#41;)

[//]: # (            .addConverterFactory&#40;GsonConverterFactory.create&#40;&#41;&#41;)

[//]: # (            .build&#40;&#41;)

[//]: # (    })

[//]: # (    )
[//]: # (    @Provides)

[//]: # (    @Singleton)

[//]: # (    fun provideCoinGeckoApiService&#40;retrofit: Retrofit&#41;: CoinGeckoApiService {)

[//]: # (        return retrofit.create&#40;CoinGeckoApiService::class.java&#41;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (#### Database Module)

[//]: # (```kotlin)

[//]: # (@Module)

[//]: # (@InstallIn&#40;SingletonComponent::class&#41;)

[//]: # (object DatabaseModule {)

[//]: # (    )
[//]: # (    @Provides)

[//]: # (    @Singleton)

[//]: # (    fun provideCoinDatabase&#40;@ApplicationContext context: Context&#41;: CoinDatabase {)

[//]: # (        return Room.databaseBuilder&#40;)

[//]: # (            context,)

[//]: # (            CoinDatabase::class.java,)

[//]: # (            "coin_database")

[//]: # (        &#41;.build&#40;&#41;)

[//]: # (    })

[//]: # (    )
[//]: # (    @Provides)

[//]: # (    fun provideCoinDao&#40;database: CoinDatabase&#41;: CoinDao {)

[//]: # (        return database.coinDao&#40;&#41;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (#### Repository Module)

[//]: # (```kotlin)

[//]: # (@Module)

[//]: # (@InstallIn&#40;SingletonComponent::class&#41;)

[//]: # (abstract class RepositoryModule {)

[//]: # (    )
[//]: # (    @Binds)

[//]: # (    @Singleton)

[//]: # (    abstract fun bindCoinRepository&#40;)

[//]: # (        coinRepositoryImpl: CoinRepositoryImpl)

[//]: # (    &#41;: CoinRepository)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (## VI. Key Benefits of This True SSoT Architecture)

[//]: # ()
[//]: # (### ✅ **Performance Optimizations**)

[//]: # (- **Single API Call**: Only one network request serves both screens)

[//]: # (- **No Duplicate Data**: Data stored once in repository's StateFlow)

[//]: # (- **Immediate Details**: No loading state on details screen)

[//]: # (- **Efficient Memory Usage**: Shared data across all consumers)

[//]: # ()
[//]: # (### ✅ **Reactive & Consistent**)

[//]: # (- **Automatic Updates**: Changes propagate to all observing screens)

[//]: # (- **Consistent State**: All screens show the same data)

[//]: # (- **Real-time Updates**: StateFlow ensures UI reflects latest data)

[//]: # ()
[//]: # (### ✅ **Offline-First**)

[//]: # (- **Instant Cache Loading**: Room provides immediate data on app start)

[//]: # (- **Graceful Degradation**: App works without network)

[//]: # (- **Smart Caching**: Fresh data replaces cache when available)

[//]: # ()
[//]: # (### ✅ **Rate Limit Friendly**)

[//]: # (- **Minimal API Calls**: One comprehensive call instead of multiple)

[//]: # (- **Efficient Refresh**: Only refresh when needed)

[//]: # (- **Respect API Limits**: Built-in throttling and error handling)

[//]: # ()
[//]: # (### ✅ **Maintainable & Testable**)

[//]: # (- **Clear Data Flow**: Repository → UseCase → ViewModel → UI)

[//]: # (- **Single Responsibility**: Each layer has clear purpose)

[//]: # (- **Easy Testing**: Mock repository for all testing scenarios)

[//]: # (- **Type Safety**: Strong typing throughout the architecture)

[//]: # ()
[//]: # (This architecture provides true Single Source of Truth with optimal performance, caching, and user experience!)