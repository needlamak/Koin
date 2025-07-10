# True SSoT Crypto App Architecture (MVVM Clean Architecture)

A crypto application using **True Single Source of Truth (SSoT)** with MVVM Clean Architecture, Jetpack Compose, comprehensive data caching, a new **Portfolio Management feature**, and optimal performance.

## Core SSoT Principles

- **Repository as the Only Source of Truth**: All data flows through the repository's `StateFlow`.
- **Single Comprehensive API Call**: One network request serves both list and detail screens.
- **Immediate Data Availability**: No loading states on the detail screen.
- **Reactive UI**: Automatic updates across all screens.
- **Offline-First**: Room provides instant cached data.
- **Integrated Portfolio**: Seamlessly manage purchased assets, with all portfolio data integrated into the SSoT.

---

## I. Project Setup & Core Dependencies

### 1. Project Initialization

- Create a new Android Studio project (Kotlin first).
- Configure `build.gradle` (project and app level) for Kotlin, Compose, and necessary plugins.

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
    // ... other fields from API
)
```

#### Local Data Models (Room Entities)

**Coin Entity:**
```kotlin
@Entity(tableName = "coins")
data class CoinEntity(
    @PrimaryKey val id: String,
    val name: String,
    // ... other coin data fields
    val lastUpdated: Long = System.currentTimeMillis()
)
```

**Portfolio Entities:**
```kotlin
@Entity(tableName = "portfolio_holdings")
data class PortfolioHoldingEntity(
    @PrimaryKey val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinImageUrl: String,
    val quantity: Double,
    val averagePurchasePrice: Double,
    val totalTransactionFees: Double,
    val lastUpdated: Long
)

@Entity(tableName = "portfolio_transactions")
data class PortfolioTransactionEntity(
    @PrimaryKey val id: String,
    val coinId: String,
    val type: String, // "BUY" or "SELL"
    val quantity: Double,
    val pricePerCoin: Double,
    val transactionFee: Double,
    val timestamp: Long
)

@Entity(tableName = "portfolio_balance")
data class PortfolioBalanceEntity(
    @PrimaryKey val id: Int = 1,
    val balance: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)
```

### 3. Local Database (Room)

#### DAO Interfaces

**Coin DAO:**
```kotlin
@Dao
interface CoinDao {
    @Query("SELECT * FROM coins ORDER BY marketCapRank ASC")
    fun getAllCoins(): Flow<List<CoinEntity>>
    
    // ... other queries
}
```

**Portfolio DAO:**
```kotlin
@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio_holdings")
    fun getHoldings(): Flow<List<PortfolioHoldingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolding(holding: PortfolioHoldingEntity)

    // ... other queries for transactions and balance
}
```

#### Room Database Class

```kotlin
@Database(
    entities = [
        CoinEntity::class, 
        CoinChartEntity::class,
        PortfolioHoldingEntity::class,
        PortfolioTransactionEntity::class,
        PortfolioBalanceEntity::class
    ],
    version = 4, // Version updated for portfolio
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CoinDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao
    abstract fun portfolioDao(): PortfolioDao
    
    // ... migrations
}
```

### 4. Repository Implementations (The True SSoT)

#### Coin Repository

Manages fetching and caching of coin market data.
```kotlin
@Singleton
class CoinRepositoryImpl @Inject constructor(...) : CoinRepository {
    // ... implementation using StateFlow as SSoT
}
```

#### Portfolio Repository

Manages all portfolio-related data operations, including buying, selling, and retrieving holdings.
```kotlin
@Singleton
class PortfolioRepositoryImpl @Inject constructor(
    private val portfolioDao: PortfolioDao
) : PortfolioRepository {
    override fun getHoldings(): Flow<List<PortfolioHolding>> { ... }
    override suspend fun buyCoin(coin: Coin, amount: Double, price: Double) { ... }
    // ... other portfolio methods
}
```

### 5. Data Mappers

Mappers to convert between DTOs, Domain models, and Entities for both coins and portfolio items.

---

## III. Domain Layer (Business Logic)

### 1. Domain Models

**Coin Model:**
A single rich model with computed properties for UI display.
```kotlin
data class Coin(
    val id: String,
    val name: String,
    // ... all coin details
) {
    // Computed properties for UI
    val isPositive24h: Boolean get() = priceChangePercentage24h > 0
    val formattedPrice: String get() = //...
}
```

**Portfolio Models:**
```kotlin
data class PortfolioHolding(
    val coinId: String,
    val quantity: Double,
    // ... other fields
)

data class PortfolioTransaction(
    val id: String,
    val type: String, // "BUY" or "SELL"
    // ... other fields
)

data class PortfolioBalance(
    val balance: Double,
    val lastUpdated: Long
)

data class Portfolio(
    val holdings: List<PortfolioHolding>,
    val balance: PortfolioBalance,
    // ... computed properties for total value, PnL, etc.
)
```

### 2. Use Cases

Use cases encapsulate specific business logic, interacting with repositories.
- `GetCoinsUseCase`
- `GetCoinByIdUseCase`
- `RefreshCoinsUseCase`
- **`GetPortfolioUseCase`**: Retrieves the entire user portfolio.
- **`BuyCoinUseCase`**: Handles the logic for purchasing a coin.
- **`SellCoinUseCase`**: Handles the logic for selling a holding.
- **`ResetPortfolioUseCase`**: Resets the portfolio to its initial state.

---

## IV. Presentation Layer (Jetpack Compose UI)

### 1. UI State Models

```kotlin
data class CoinListUiState(...)
data class PortfolioUiState(...)
```

### 2. ViewModels

**`CoinListViewModel`:**
Manages the state for the coin list, now includes logic to initiate a purchase.
```kotlin
@HiltViewModel
class CoinListViewModel @Inject constructor(
    private val repository: CoinRepository,
    private val portfolioRepository: PortfolioRepository,
    // ... other dependencies
) : ViewModel() {
    // ...
    fun handleEvent(event: CoinListUiEvent) {
        when (event) {
            is CoinListUiEvent.BuyCoin -> buyCoin(event.coin, event.amount)
            // ... other events
        }
    }
    
    private fun buyCoin(coin: Coin, amount: Double) {
        viewModelScope.launch {
            portfolioRepository.buyCoin(coin, amount, coin.currentPrice)
        }
    }
}
```

**`PortfolioViewModel`:**
Manages the state for the portfolio screen, handling events for buying, selling, and refreshing portfolio data.
```kotlin
@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val getPortfolioUseCase: GetPortfolioUseCase,
    private val buyCoinUseCase: BuyCoinUseCase,
    // ... other use cases
) : ViewModel() {
    // ... state management using combine and StateFlow
}
```

### 3. Composable Screens

**`CoinListScreen`:**
Displays the list of coins. Now features a swipe-to-buy action on each `CoinItem`, which opens a bottom sheet to confirm the purchase.
```kotlin
@Composable
fun CoinListScreen(...) {
    // ...
    items(items = state.filteredCoins, key = { it.id }) { coin ->
        CoinItem(
            coin = coin, 
            onClick = { onCoinClick(coin.id) },
            onToggleWatchlist = { ... },
            onBuyClick = {
                // Triggers the buy bottom sheet
            }
        )
    }
    // ...
}
```

**`PortfolioScreen`:**
A dedicated screen to display the user's portfolio, including current holdings, overall balance, performance charts, and transaction history.

---

## V. Dependency Injection (Dagger Hilt)

### Hilt Modules

**`DatabaseModule`:**
Provides instances of the `CoinDatabase`, `CoinDao`, and the new `PortfolioDao`.
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    // ...
    @Provides
    fun providePortfolioDao(database: CoinDatabase): PortfolioDao {
        return database.portfolioDao()
    }
}
```

**`RepositoryModule`:**
Binds repository interfaces to their implementations.
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCoinRepository(impl: CoinRepositoryImpl): CoinRepository

    @Binds
    @Singleton
    abstract fun bindPortfolioRepository(impl: PortfolioRepositoryImpl): PortfolioRepository
    
    // ... other bindings
}
```

---

## VI. Key Benefits of This True SSoT Architecture

### ✅ Performance, Reactivity, and Offline-First
The core benefits of SSoT remain, ensuring a fast, consistent, and reliable user experience.

### ✅ Integrated Portfolio Management
- **Seamless Buying**: Users can buy coins directly from the coin list.
- **Centralized Logic**: All portfolio calculations and data management are handled within the domain and data layers, keeping the UI clean.
- **Persistent State**: Portfolio data is stored locally, making it available offline and persistent across app sessions.

This architecture provides a robust and scalable foundation for a feature-rich crypto application, maintaining a true Single Source of Truth for all application data.
