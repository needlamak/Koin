# True SSoT Crypto App Architecture (MVVM Clean Architecture)

A crypto application using **True Single Source of Truth (SSoT)** with MVVM Clean Architecture, Jetpack Compose, comprehensive data caching, and optimal performance.

## Core SSoT Principles
- **Repository as the Only Source of Truth**: All data flows through repository's StateFlow
- **Single Comprehensive API Call**: One network request serves both list and detail screens
- **Immediate Data Availability**: No loading states on detail screen
- **Reactive UI**: Automatic updates across all screens
- **Offline-First**: Room provides instant cached data

---

## I. Project Setup & Core Dependencies

### 1. Project Initialization
- Create a new Android Studio project (Kotlin first)
- Configure build.gradle (project and app level) for Kotlin, Compose, and necessary plugins

### 2. Core Android Jetpack & Kotlin Dependencies
```kotlin
// Compose BOM for consistent versions
implementation platform('androidx.compose:compose-bom:2024.02.00')
implementation 'androidx.compose.ui:ui'
implementation 'androidx.compose.ui:ui-tooling-preview'
implementation 'androidx.compose.material3:material3'
implementation 'androidx.compose.ui:ui-tooling'

// Navigation
implementation 'androidx.navigation:navigation-compose:2.7.6'

// Lifecycle & ViewModel
implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
```

### 3. Networking Dependencies
```kotlin
// Retrofit
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// OkHttp
implementation 'com.squareup.okhttp3:okhttp:4.12.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
```

### 4. Local Persistence Dependencies
```kotlin
// Room Database
implementation 'androidx.room:room-runtime:2.6.1'
implementation 'androidx.room:room-ktx:2.6.1'
kapt 'androidx.room:room-compiler:2.6.1'
```

### 5. Dependency Injection Dependencies
```kotlin
// Dagger Hilt
implementation 'com.google.dagger:hilt-android:2.48.1'
kapt 'com.google.dagger:hilt-android-compiler:2.48.1'
implementation 'androidx.hilt:hilt-navigation-compose:1.1.0'
```

---

## II. Data Layer (True SSoT Hub)

### 1. API Service (Single Comprehensive Call)
```kotlin
interface CoinGeckoApiService {
    @GET("coins/markets")
    suspend fun getCoinsWithFullDetails(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = true,
        @Query("price_change_percentage") priceChange: String = "1h,24h,7d,30d"
    ): List<CoinDetailDto>
}
```

### 2. Data Models

#### Remote Data Model (API Response)
```kotlin
@Serializable
data class CoinDetailDto(
    val id: String,
    val name: String,
    val symbol: String,
    val image: String,
    @SerializedName("current_price") val currentPrice: Double,
    @SerializedName("market_cap") val marketCap: Long,
    @SerializedName("market_cap_rank") val marketCapRank: Int,
    @SerializedName("price_change_24h") val priceChange24h: Double,
    @SerializedName("price_change_percentage_24h") val priceChangePercentage24h: Double,
    @SerializedName("price_change_percentage_1h_in_currency") val priceChangePercentage1h: Double?,
    @SerializedName("price_change_percentage_7d_in_currency") val priceChangePercentage7d: Double?,
    @SerializedName("price_change_percentage_30d_in_currency") val priceChangePercentage30d: Double?,
    @SerializedName("sparkline_in_7d") val sparklineIn7d: SparklineDto?,
    @SerializedName("high_24h") val high24h: Double?,
    @SerializedName("low_24h") val low24h: Double?,
    @SerializedName("total_volume") val totalVolume: Long?,
    @SerializedName("circulating_supply") val circulatingSupply: Double?,
    @SerializedName("total_supply") val totalSupply: Double?,
    @SerializedName("max_supply") val maxSupply: Double?,
    val ath: Double?,
    @SerializedName("ath_date") val athDate: String?,
    val atl: Double?,
    @SerializedName("atl_date") val atlDate: String?
)

@Serializable
data class SparklineDto(
    val price: List<Double>
)
```

#### Local Data Model (Room Entity)
```kotlin
@Entity(tableName = "coins")
data class CoinEntity(
    @PrimaryKey val id: String,
    val name: String,
    val symbol: String,
    val imageUrl: String,
    val currentPrice: Double,
    val marketCap: Long,
    val marketCapRank: Int,
    val priceChange24h: Double,
    val priceChangePercentage24h: Double,
    val priceChangePercentage1h: Double?,
    val priceChangePercentage7d: Double?,
    val priceChangePercentage30d: Double?,
    val sparklineData: String?, // JSON string of List<Double>
    val high24h: Double?,
    val low24h: Double?,
    val totalVolume: Long?,
    val circulatingSupply: Double?,
    val totalSupply: Double?,
    val maxSupply: Double?,
    val ath: Double?,
    val athDate: String?,
    val atl: Double?,
    val atlDate: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)
```

### 3. Local Database (Room)

#### DAO Interface
```kotlin
@Dao
interface CoinDao {
    @Query("SELECT * FROM coins ORDER BY marketCapRank ASC")
    fun getAllCoins(): Flow<List<CoinEntity>>
    
    @Query("SELECT * FROM coins WHERE id = :coinId")
    fun getCoinById(coinId: String): Flow<CoinEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coins: List<CoinEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoin(coin: CoinEntity)
    
    @Query("DELETE FROM coins")
    suspend fun deleteAllCoins()
    
    @Query("SELECT COUNT(*) FROM coins")
    suspend fun getCoinCount(): Int
}
```

#### Room Database Class
```kotlin
@Database(
    entities = [CoinEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CoinDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao
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
}
```

### 4. Repository Implementation (The True SSoT)

#### Repository Interface
```kotlin
interface CoinRepository {
    fun getAllCoins(): Flow<Result<List<Coin>>>
    fun getCoinById(id: String): Flow<Result<Coin?>>
    suspend fun refreshCoins()
}
```

#### Repository Implementation
```kotlin
@Singleton
class CoinRepositoryImpl @Inject constructor(
    private val apiService: CoinGeckoApiService,
    private val coinDao: CoinDao,
    private val networkUtil: NetworkUtil
) : CoinRepository {
    
    // This is the TRUE Single Source of Truth
    private val _coins = MutableStateFlow<Map<String, Coin>>(emptyMap())
    val coins = _coins.asStateFlow()
    
    override fun getAllCoins(): Flow<Result<List<Coin>>> = 
        coins.map { Result.success(it.values.toList()) }
    
    override fun getCoinById(id: String): Flow<Result<Coin?>> = 
        coins.map { Result.success(it[id]) }
    
    init {
        // Initialize SSoT with cached data first, then refresh
        CoroutineScope(Dispatchers.IO).launch {
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
            // Handle cache loading error
        }
    }
    
    private suspend fun refreshFromNetwork() {
        try {
            val freshData = apiService.getCoinsWithFullDetails()
            val domainCoins = freshData.map { it.toDomain() }
            
            // Update the Single Source of Truth
            _coins.value = domainCoins.associateBy { it.id }
            
            // Cache for offline access
            coinDao.deleteAllCoins()
            coinDao.insertAll(domainCoins.map { it.toEntity() })
            
        } catch (e: Exception) {
            // Network error - cached data remains available in _coins
            // Could emit error state if no cached data exists
            if (_coins.value.isEmpty()) {
                // Handle the case where there's no cached data
            }
        }
    }
    
    override suspend fun refreshCoins() {
        if (networkUtil.isNetworkAvailable()) {
            refreshFromNetwork()
        }
    }
}
```

### 5. Data Mappers
```kotlin
// DTO to Domain
fun CoinDetailDto.toDomain(): Coin {
    return Coin(
        id = id,
        name = name,
        symbol = symbol,
        imageUrl = image,
        currentPrice = currentPrice,
        marketCap = marketCap,
        marketCapRank = marketCapRank,
        priceChange24h = priceChange24h,
        priceChangePercentage24h = priceChangePercentage24h,
        priceChangePercentage1h = priceChangePercentage1h,
        priceChangePercentage7d = priceChangePercentage7d,
        priceChangePercentage30d = priceChangePercentage30d,
        sparklineData = sparklineIn7d?.price,
        high24h = high24h,
        low24h = low24h,
        totalVolume = totalVolume,
        circulatingSupply = circulatingSupply,
        totalSupply = totalSupply,
        maxSupply = maxSupply,
        ath = ath,
        athDate = athDate,
        atl = atl,
        atlDate = atlDate
    )
}

// Domain to Entity
fun Coin.toEntity(): CoinEntity {
    return CoinEntity(
        id = id,
        name = name,
        symbol = symbol,
        imageUrl = imageUrl,
        currentPrice = currentPrice,
        marketCap = marketCap,
        marketCapRank = marketCapRank,
        priceChange24h = priceChange24h,
        priceChangePercentage24h = priceChangePercentage24h,
        priceChangePercentage1h = priceChangePercentage1h,
        priceChangePercentage7d = priceChangePercentage7d,
        priceChangePercentage30d = priceChangePercentage30d,
        sparklineData = sparklineData?.let { Gson().toJson(it) },
        high24h = high24h,
        low24h = low24h,
        totalVolume = totalVolume,
        circulatingSupply = circulatingSupply,
        totalSupply = totalSupply,
        maxSupply = maxSupply,
        ath = ath,
        athDate = athDate,
        atl = atl,
        atlDate = atlDate
    )
}

// Entity to Domain
fun CoinEntity.toDomain(): Coin {
    return Coin(
        id = id,
        name = name,
        symbol = symbol,
        imageUrl = imageUrl,
        currentPrice = currentPrice,
        marketCap = marketCap,
        marketCapRank = marketCapRank,
        priceChange24h = priceChange24h,
        priceChangePercentage24h = priceChangePercentage24h,
        priceChangePercentage1h = priceChangePercentage1h,
        priceChangePercentage7d = priceChangePercentage7d,
        priceChangePercentage30d = priceChangePercentage30d,
        sparklineData = sparklineData?.let { 
            Gson().fromJson(it, object : TypeToken<List<Double>>() {}.type)
        },
        high24h = high24h,
        low24h = low24h,
        totalVolume = totalVolume,
        circulatingSupply = circulatingSupply,
        totalSupply = totalSupply,
        maxSupply = maxSupply,
        ath = ath,
        athDate = athDate,
        atl = atl,
        atlDate = atlDate
    )
}
```

---

## III. Domain Layer (Business Logic)

### 1. Domain Model (Single Rich Model)
```kotlin
data class Coin(
    val id: String,
    val name: String,
    val symbol: String,
    val imageUrl: String,
    val currentPrice: Double,
    val marketCap: Long,
    val marketCapRank: Int,
    val priceChange24h: Double,
    val priceChangePercentage24h: Double,
    val priceChangePercentage1h: Double?,
    val priceChangePercentage7d: Double?,
    val priceChangePercentage30d: Double?,
    val sparklineData: List<Double>?,
    val high24h: Double?,
    val low24h: Double?,
    val totalVolume: Long?,
    val circulatingSupply: Double?,
    val totalSupply: Double?,
    val maxSupply: Double?,
    val ath: Double?,
    val athDate: String?,
    val atl: Double?,
    val atlDate: String?
) {
    // Computed properties for list display
    val isPositive24h: Boolean get() = priceChangePercentage24h > 0
    val formattedPrice: String get() = NumberFormat.getCurrencyInstance().format(currentPrice)
    val formattedMarketCap: String get() = formatLargeNumber(marketCap)
    val formattedPriceChange: String get() = "${if (isPositive24h) "+" else ""}${String.format("%.2f", priceChangePercentage24h)}%"
    
    // Computed properties for details display
    val hasSparklineData: Boolean get() = !sparklineData.isNullOrEmpty()
    val supplyPercentage: Double? get() = 
        if (totalSupply != null && circulatingSupply != null) 
            (circulatingSupply / totalSupply) * 100 
        else null
    
    val formattedVolume: String get() = totalVolume?.let { formatLargeNumber(it) } ?: "N/A"
    val formattedSupply: String get() = circulatingSupply?.let { formatLargeNumber(it.toLong()) } ?: "N/A"
}

private fun formatLargeNumber(number: Long): String {
    return when {
        number >= 1_000_000_000_000 -> "${String.format("%.1f", number / 1_000_000_000_000.0)}T"
        number >= 1_000_000_000 -> "${String.format("%.1f", number / 1_000_000_000.0)}B"
        number >= 1_000_000 -> "${String.format("%.1f", number / 1_000_000.0)}M"
        number >= 1_000 -> "${String.format("%.1f", number / 1_000.0)}K"
        else -> number.toString()
    }
}
```

### 2. Use Cases (Single Shared Use Case)
```kotlin
@Singleton
class GetCoinsUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    operator fun invoke(): Flow<Result<List<Coin>>> = repository.getAllCoins()
}

@Singleton
class GetCoinByIdUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    operator fun invoke(coinId: String): Flow<Result<Coin?>> = repository.getCoinById(coinId)
}

@Singleton
class RefreshCoinsUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    suspend operator fun invoke() = repository.refreshCoins()
}
```

---

## IV. Presentation Layer (Jetpack Compose UI)

### 1. UI State Models
```kotlin
data class CoinListUiState(
    val isLoading: Boolean = false,
    val coins: List<Coin> = emptyList(),
    val error: String? = null,
    val isRefreshing: Boolean = false
)

data class CoinDetailsUiState(
    val coin: Coin? = null,
    val error: String? = null
)
```

### 2. Shared ViewModel (Navigation Graph Scoped)
```kotlin
@HiltViewModel
class SharedCoinViewModel @Inject constructor(
    private val getCoinsUseCase: GetCoinsUseCase,
    private val refreshCoinsUseCase: RefreshCoinsUseCase
) : ViewModel() {
    
    private val _selectedCoinId = MutableStateFlow<String?>(null)
    val selectedCoinId = _selectedCoinId.asStateFlow()
    
    // Single data source for all screens
    val coins = getCoinsUseCase()
        .catch { emit(Result.failure(it)) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Result.success(emptyList())
        )
    
    // Derived state for selected coin (no separate network call needed!)
    val selectedCoin = combine(selectedCoinId, coins) { id, coinsResult ->
        id?.let { coinId ->
            coinsResult.getOrNull()?.find { it.id == coinId }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()
    
    fun selectCoin(coinId: String) {
        _selectedCoinId.value = coinId
    }
    
    fun refreshCoins() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                refreshCoinsUseCase()
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
```

### 3. Composable Screens

#### Coin List Screen
```kotlin
@Composable
fun CoinListScreen(
    sharedViewModel: SharedCoinViewModel = hiltViewModel(),
    onCoinClick: (String) -> Unit
) {
    val coinsResult by sharedViewModel.coins.collectAsState()
    val isRefreshing by sharedViewModel.isRefreshing.collectAsState()
    
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { sharedViewModel.refreshCoins() }
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        coinsResult.fold(
            onSuccess = { coins ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(coins) { coin ->
                        CoinListItem(
                            coin = coin,
                            onClick = { 
                                sharedViewModel.selectCoin(coin.id)
                                onCoinClick(coin.id)
                            }
                        )
                    }
                }
            },
            onFailure = { error ->
                ErrorScreen(
                    error = error.message ?: "Unknown error",
                    onRetry = { sharedViewModel.refreshCoins() }
                )
            }
        )
        
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
```

#### Coin Details Screen (No Loading State!)
```kotlin
@Composable
fun CoinDetailsScreen(
    coinId: String,
    sharedViewModel: SharedCoinViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val selectedCoin by sharedViewModel.selectedCoin.collectAsState()
    
    // Data is already available from the shared fetch!
    selectedCoin?.let { coin ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            CoinDetailsHeader(
                coin = coin,
                onBackClick = onBackClick
            )
            
            CoinPriceChart(
                coin = coin,
                modifier = Modifier.padding(16.dp)
            )
            
            CoinStatistics(
                coin = coin,
                modifier = Modifier.padding(16.dp)
            )
            
            CoinSupplyInfo(
                coin = coin,
                modifier = Modifier.padding(16.dp)
            )
        }
    } ?: run {
        // This should rarely happen since data is pre-loaded
        LoadingScreen()
    }
}
```

### 4. Navigation Graph
```kotlin
@Composable
fun CoinNavGraph(
    navController: NavHostController,
    sharedViewModel: SharedCoinViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = "coin_list"
    ) {
        composable("coin_list") {
            CoinListScreen(
                sharedViewModel = sharedViewModel,
                onCoinClick = { coinId ->
                    navController.navigate("coin_details/$coinId")
                }
            )
        }
        
        composable(
            "coin_details/{coinId}",
            arguments = listOf(navArgument("coinId") { type = NavType.StringType })
        ) { backStackEntry ->
            val coinId = backStackEntry.arguments?.getString("coinId") ?: return@composable
            CoinDetailsScreen(
                coinId = coinId,
                sharedViewModel = sharedViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
```

---

## V. Dependency Injection (Dagger Hilt)

### 1. Application Class
```kotlin
@HiltAndroidApp
class CoinApplication : Application()
```

### 2. Hilt Modules

#### Network Module
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideCoinGeckoApiService(retrofit: Retrofit): CoinGeckoApiService {
        return retrofit.create(CoinGeckoApiService::class.java)
    }
}
```

#### Database Module
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideCoinDatabase(@ApplicationContext context: Context): CoinDatabase {
        return Room.databaseBuilder(
            context,
            CoinDatabase::class.java,
            "coin_database"
        ).build()
    }
    
    @Provides
    fun provideCoinDao(database: CoinDatabase): CoinDao {
        return database.coinDao()
    }
}
```

#### Repository Module
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindCoinRepository(
        coinRepositoryImpl: CoinRepositoryImpl
    ): CoinRepository
}
```

---

## VI. Key Benefits of This True SSoT Architecture

### ✅ **Performance Optimizations**
- **Single API Call**: Only one network request serves both screens
- **No Duplicate Data**: Data stored once in repository's StateFlow
- **Immediate Details**: No loading state on details screen
- **Efficient Memory Usage**: Shared data across all consumers

### ✅ **Reactive & Consistent**
- **Automatic Updates**: Changes propagate to all observing screens
- **Consistent State**: All screens show the same data
- **Real-time Updates**: StateFlow ensures UI reflects latest data

### ✅ **Offline-First**
- **Instant Cache Loading**: Room provides immediate data on app start
- **Graceful Degradation**: App works without network
- **Smart Caching**: Fresh data replaces cache when available

### ✅ **Rate Limit Friendly**
- **Minimal API Calls**: One comprehensive call instead of multiple
- **Efficient Refresh**: Only refresh when needed
- **Respect API Limits**: Built-in throttling and error handling

### ✅ **Maintainable & Testable**
- **Clear Data Flow**: Repository → UseCase → ViewModel → UI
- **Single Responsibility**: Each layer has clear purpose
- **Easy Testing**: Mock repository for all testing scenarios
- **Type Safety**: Strong typing throughout the architecture

This architecture provides true Single Source of Truth with optimal performance, caching, and user experience!