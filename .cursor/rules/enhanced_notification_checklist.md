# Complete Notification System Implementation Checklist

## Overview
Adding price alerts and portfolio notifications to existing crypto coin application with clean architecture (Data → Domain → Repository → Presentation layers).

## 1. Data Layer Components

### New Room Entities
- [ ] **UserEntity** - Local user profile (extensible for future authentication)
  - Local primary key ID
  - Basic notification settings
  - Default threshold values
  - Future fields: remoteUserId, lastSyncTime
  - **⚠️ AVOID**: Using auto-generated IDs that might conflict with remote sync
  - **✅ CONSIDER**: UUID for future-proof remote synchronization

- [ ] **NotificationPreferenceEntity** - User alert settings
  - User ID reference
  - Notification type (price alert, portfolio)
  - Threshold values (percentage/dollar amounts)
  - Enabled/disabled status
  - **⚠️ PITFALL**: Not validating threshold ranges (negative values, extreme percentages)
  - **✅ MODERN**: Add validation annotations and constraints

- [ ] **UserWatchlistEntity** - Coins user wants to monitor
  - User ID reference
  - Coin ID reference
  - Alert thresholds for specific coins
  - Created/modified timestamps
  - **⚠️ AVOID**: Storing duplicate coin data; use foreign keys properly
  - **✅ CONSIDER**: Soft delete flags for user experience

### New DAOs
- [ ] **UserDao** - User profile operations
  - **✅ MODERN**: Use suspend functions for all operations
  - **✅ CONSIDER**: Add upsert operations for conflict resolution
- [ ] **NotificationPreferenceDao** - Alert settings management
  - **⚠️ PITFALL**: Not handling batch operations efficiently
  - **✅ MODERN**: Use @Transaction for atomic operations
- [ ] **UserWatchlistDao** - Watchlist operations
  - **✅ CONSIDER**: Add Flow-based observers for real-time updates

### Database Migration
- [ ] **Update database version** from 1 to 2
- [ ] **Create migration logic** to handle existing installations
- [ ] **Update CoinDatabase class** to include new entities
- [ ] **Add new TypeConverters** if needed for complex data types
- [ ] **⚠️ CRITICAL**: Test migration with existing data thoroughly
- [ ] **✅ MODERN**: Use Room's migration testing tools
- [ ] **✅ CONSIDER**: Implement fallback destructive migration with data backup

## 2. Domain Layer Components

### Use Cases
- [ ] **ManageUserPreferencesUseCase** - Handle user settings
  - **✅ MODERN**: Use sealed classes for Result states
  - **⚠️ AVOID**: Throwing exceptions for business logic failures
- [ ] **CreatePriceAlertUseCase** - Set up price alerts
  - **✅ CONSIDER**: Input validation and sanitization
  - **⚠️ PITFALL**: Not checking for duplicate alerts
- [ ] **MonitorCoinPricesUseCase** - Background price monitoring logic
  - **✅ MODERN**: Use Kotlin coroutines with proper cancellation
  - **⚠️ AVOID**: Blocking operations in background workers
- [ ] **CheckPortfolioChangesUseCase** - Portfolio notification logic
  - **✅ CONSIDER**: Debouncing rapid price changes
  - **⚠️ PITFALL**: Not handling edge cases (zero portfolio value)
- [ ] **SendNotificationUseCase** - Trigger notification delivery
  - **✅ MODERN**: Use dependency injection for NotificationManager
  - **⚠️ AVOID**: Hardcoded notification IDs (use hash of coin ID)

### Business Logic
- [ ] **Price threshold comparison logic**
  - **✅ MODERN**: Use BigDecimal for precise calculations
  - **⚠️ PITFALL**: Floating-point precision errors with Double
- [ ] **Portfolio value calculation**
  - **✅ CONSIDER**: Handle multiple currencies and conversion rates
  - **⚠️ AVOID**: Assumptions about single currency
- [ ] **Alert frequency management**
  - **✅ MODERN**: Implement exponential backoff for repeated alerts
  - **⚠️ PITFALL**: Spamming users with too frequent notifications

## 3. Repository Layer Components

### New Repositories
- [ ] **UserRepository** - User profile management
  - **✅ MODERN**: Implement offline-first with single source of truth
  - **⚠️ AVOID**: Direct database calls from ViewModels
- [ ] **NotificationRepository** - Alert preferences
  - **✅ CONSIDER**: Cache frequently accessed preferences
  - **⚠️ PITFALL**: Not invalidating cache on preference changes
- [ ] **WatchlistRepository** - Tracked coins management
  - **✅ MODERN**: Use Flow for reactive data streams
  - **⚠️ AVOID**: Polling database for changes
- [ ] **Extend existing CoinRepository** - Add background monitoring capabilities
  - **✅ CONSIDER**: Separate interfaces for UI and background operations
  - **⚠️ PITFALL**: Mixing UI and background data flows

### Robustness Enhancements
- [ ] **Network interceptors** for API rate limiting and error handling
  - **✅ MODERN**: Implement exponential backoff with jitter
  - **⚠️ AVOID**: Aggressive retry policies that waste battery
- [ ] **Offline-first strategy** with Room caching
  - **✅ CONSIDER**: Timestamp-based cache invalidation
  - **⚠️ PITFALL**: Serving stale data without user awareness
- [ ] **Retry mechanisms** for failed API calls
  - **✅ MODERN**: Use WorkManager's built-in retry with backoff
  - **⚠️ AVOID**: Infinite retry loops
- [ ] **Data synchronization logic** for consistency
  - **✅ CONSIDER**: Conflict resolution strategies
  - **⚠️ PITFALL**: Data corruption during sync failures
- [ ] **Use existing CoinGeckoApiService** - No separate API implementation
  - **✅ MODERN**: Add rate limiting annotations
  - **⚠️ AVOID**: Bypassing existing error handling

## 4. Background Services Layer

### WorkManager Components
- [ ] **PriceMonitoringWorker** - Periodically calls existing CoinGeckoApiService
  - **✅ MODERN**: Use CoroutineWorker for suspend functions
  - **⚠️ AVOID**: Blocking operations in doWork()
  - **✅ CONSIDER**: Batch API calls to reduce network overhead
- [ ] **NotificationTriggerWorker** - Compares prices with thresholds and sends alerts
  - **✅ MODERN**: Use dependency injection for testability
  - **⚠️ PITFALL**: Not handling notification permission denial
- [ ] **WorkManager constraints** - Network requirements and battery optimization
  - **✅ CONSIDER**: Different constraints for different work types
  - **⚠️ AVOID**: Overly restrictive constraints that prevent execution
- [ ] **Periodic work requests** - Appropriate intervals (minimum 15 minutes)
  - **✅ MODERN**: Use ExistingPeriodicWorkPolicy.UPDATE for dynamic intervals
  - **⚠️ PITFALL**: Ignoring battery optimization settings

### Work Configuration
- [ ] **Work chaining** - Price fetch then notification check
  - **✅ MODERN**: Use WorkContinuation for complex workflows
  - **⚠️ AVOID**: Overly complex work chains that are hard to debug
- [ ] **Error handling and retry policies**
  - **✅ CONSIDER**: Different retry policies for different error types
  - **⚠️ PITFALL**: Not distinguishing between recoverable and non-recoverable errors
- [ ] **Work observers** for monitoring status
  - **✅ MODERN**: Use LiveData observers with proper lifecycle handling
  - **⚠️ AVOID**: Memory leaks from unregistered observers
- [ ] **Unique work names** to avoid duplicates
  - **✅ CONSIDER**: Include user ID in work names for multi-user apps
  - **⚠️ PITFALL**: Work name collisions in multi-user scenarios
- [ ] **ExistingPeriodicWorkPolicy.KEEP** to avoid unnecessary restarts
  - **✅ MODERN**: Use UPDATE policy when configuration changes
  - **⚠️ AVOID**: Stale work configurations

## 5. Notification System Components

### Notification Channels
- [ ] **Price Alert Channel** - HIGH importance for urgent alerts
  - **✅ MODERN**: Respect user's notification preferences
  - **⚠️ PITFALL**: Overriding user's channel settings
- [ ] **Portfolio Channel** - DEFAULT importance for regular updates
  - **✅ CONSIDER**: Separate channels for different alert types
  - **⚠️ AVOID**: Using single channel for all notifications
- [ ] **Channel descriptions** for user understanding
  - **✅ MODERN**: Use localized strings for descriptions
  - **⚠️ PITFALL**: Technical jargon in user-facing descriptions
- [ ] **Sound, vibration, and LED settings**
  - **✅ CONSIDER**: Custom sounds for different alert types
  - **⚠️ AVOID**: Annoying or overly loud notification sounds
- [ ] **Android version compatibility**
  - **✅ MODERN**: Use NotificationChannelCompat for backwards compatibility
  - **⚠️ PITFALL**: Not handling pre-O devices properly

### Notification Building
- [ ] **NotificationManager** system service
  - **✅ MODERN**: Use NotificationManagerCompat for compatibility
  - **⚠️ AVOID**: Assuming notification permissions are granted
- [ ] **NotificationCompat.Builder** for content creation
  - **✅ CONSIDER**: Rich notification content with expandable layouts
  - **⚠️ PITFALL**: Not testing on different screen sizes
- [ ] **PendingIntent** for handling notification taps
  - **✅ MODERN**: Use FLAG_IMMUTABLE for Android 12+
  - **⚠️ CRITICAL**: Security vulnerabilities with mutable PendingIntents
- [ ] **Custom notification layouts** for different alert types
  - **✅ CONSIDER**: Notification actions (snooze, dismiss, etc.)
  - **⚠️ AVOID**: Overly complex layouts that don't render properly

## 6. Application Class Setup

### Initialization Requirements
- [ ] **WorkManager configuration** in onCreate
  - **✅ MODERN**: Use Configuration.Builder with custom threading
  - **⚠️ AVOID**: Blocking operations in Application.onCreate()
- [ ] **Notification channels creation** on app start
  - **✅ CONSIDER**: Lazy initialization to improve startup time
  - **⚠️ PITFALL**: Not handling channel creation failures
- [ ] **Dependency injection setup** (if using Hilt/Koin)
  - **✅ MODERN**: Use @HiltAndroidApp for Hilt integration
  - **⚠️ AVOID**: Manual dependency wiring in Application class
- [ ] **App lifecycle handling** for background work
  - **✅ CONSIDER**: ProcessLifecycleOwner for app state awareness
  - **⚠️ PITFALL**: Not handling app kills and restarts properly

## 7. Android Manifest Updates

### Permissions
- [ ] **POST_NOTIFICATIONS** - Android 13+ notification permission
  - **✅ MODERN**: Handle permission request flow gracefully
  - **⚠️ CRITICAL**: App won't show notifications without this permission
- [ ] **RECEIVE_BOOT_COMPLETED** - Restart background work after device reboot
  - **✅ CONSIDER**: Check if actually needed (WorkManager handles this)
  - **⚠️ AVOID**: Unnecessary permissions that concern users
- [ ] **WAKE_LOCK** - WorkManager to wake device when needed
  - **✅ MODERN**: WorkManager handles this automatically
  - **⚠️ PITFALL**: Explicit wake locks can drain battery
- [ ] **FOREGROUND_SERVICE** - If using foreground services
  - **✅ CONSIDER**: Use foreground service only for critical operations
  - **⚠️ AVOID**: Unnecessary foreground services (battery drain)
- [ ] **INTERNET** - Already exists, confirm presence
  - **✅ VERIFY**: Required for API calls in background workers

### Service Declarations
- [ ] **WorkManager service entries**
  - **✅ MODERN**: Let WorkManager handle service declarations
  - **⚠️ AVOID**: Manual service declarations for WorkManager
- [ ] **Notification channel service** if using custom implementation
  - **✅ CONSIDER**: Use system services instead of custom ones
  - **⚠️ PITFALL**: Custom services can be killed by system
- [ ] **BroadcastReceiver** for boot restart functionality
  - **✅ MODERN**: Use WorkManager's built-in boot restart
  - **⚠️ AVOID**: Custom broadcast receivers for WorkManager tasks

## 8. Gradle Dependencies

### Required Dependencies
- [ ] **WorkManager libraries**
  ```kotlin
  implementation "androidx.work:work-runtime-ktx:2.9.0"
  implementation "androidx.work:work-testing:2.9.0" // For testing
  ```
- [ ] **Notification compat libraries**
  ```kotlin
  implementation "androidx.core:core-ktx:1.12.0"
  implementation "androidx.core:core-splashscreen:1.0.1" // Modern approach
  ```
- [ ] **Room database extensions** (if additional Room features needed)
  ```kotlin
  implementation "androidx.room:room-paging:2.6.1" // For pagination
  ```
- [ ] **Dependency injection** (Hilt/Koin if not already included)
  ```kotlin
  implementation "com.google.dagger:hilt-android:2.48"
  implementation "androidx.hilt:hilt-work:1.1.0" // For WorkManager
  ```

### ProGuard Rules
- [ ] **WorkManager ProGuard rules** in proguard-rules.pro
  ```proguard
  -keep class * extends androidx.work.Worker
  -keep class * extends androidx.work.InputMerger
  ```
- [ ] **Notification classes keep rules**
  ```proguard
  -keep class * extends android.app.Notification$*
  ```
- [ ] **Room entity keep rules** for new entities
  ```proguard
  -keep class * extends androidx.room.RoomDatabase
  -keep @androidx.room.Entity class *
  ```

## 9. Integration Points

### With Existing Architecture
- [ ] **Use existing CoinGeckoApiService** in background workers
  - **✅ MODERN**: Inject service through dependency injection
  - **⚠️ AVOID**: Direct instantiation in workers
- [ ] **Extend existing repositories** for background operations
  - **✅ CONSIDER**: Separate interfaces for UI and background operations
  - **⚠️ PITFALL**: Mixing UI lifecycle with background operations
- [ ] **Maintain single source of truth** for API calls
  - **✅ MODERN**: Use repository pattern consistently
  - **⚠️ AVOID**: Bypassing existing data layer
- [ ] **Leverage existing Room setup** for new entities
  - **✅ CONSIDER**: Database versioning and migration strategy
  - **⚠️ PITFALL**: Breaking existing database schema

### Data Flow
- [ ] **WorkManager → Repositories → DAOs → Room Database**
  - **✅ MODERN**: Use suspend functions throughout the chain
  - **⚠️ AVOID**: Blocking operations that can cause ANRs
- [ ] **WorkManager → Existing API Service → Notification System**
  - **✅ CONSIDER**: Circuit breaker pattern for API failures
  - **⚠️ PITFALL**: Not handling API rate limits properly
- [ ] **Background workers bypass UI layer** (ViewModels have no effect)
  - **✅ MODERN**: Use separate use cases for background operations
  - **⚠️ AVOID**: Coupling background work with UI state
- [ ] **System notification service** handles delivery independently
  - **✅ CONSIDER**: Notification scheduling and batching
  - **⚠️ PITFALL**: Not handling notification permission denial

## 10. Testing Considerations

### Testing Components
- [ ] **Unit tests** for new use cases
  - **✅ MODERN**: Use MockK for Kotlin-friendly mocking
  - **⚠️ AVOID**: Testing implementation details instead of behavior
- [ ] **Repository tests** with mock data
  - **✅ CONSIDER**: In-memory database for faster tests
  - **⚠️ PITFALL**: Not testing error scenarios
- [ ] **WorkManager testing** with TestListenableWorkerBuilder
  - **✅ MODERN**: Use TestCoroutineDispatcher for time control
  - **⚠️ AVOID**: Flaky tests due to timing issues
- [ ] **Notification testing** with NotificationManagerCompat
  - **✅ CONSIDER**: Shadow testing for notification behavior
  - **⚠️ PITFALL**: Not testing on different API levels

### Modern Testing Approaches
- [ ] **Integration tests** with Room database
  - **✅ MODERN**: Use @SmallTest, @MediumTest, @LargeTest annotations
  - **⚠️ AVOID**: Slow tests that block CI/CD pipeline
- [ ] **End-to-end testing** with background work
  - **✅ CONSIDER**: Use TestRule for work scheduling
  - **⚠️ PITFALL**: Tests that depend on real network calls
- [ ] **Performance testing** for battery and memory usage
  - **✅ MODERN**: Use Android Profiler for analysis
  - **⚠️ AVOID**: Ignoring performance impacts of background work

## 11. Implementation Notes

### Key Principles
- [ ] **Extend existing code** rather than creating separate implementations
  - **✅ MODERN**: Use composition over inheritance where appropriate
  - **⚠️ AVOID**: Tight coupling that makes testing difficult
- [ ] **Future-proof design** for authentication integration
  - **✅ CONSIDER**: Abstract user management interfaces
  - **⚠️ PITFALL**: Over-engineering for uncertain requirements
- [ ] **Clean architecture maintained** across all layers
  - **✅ MODERN**: Use dependency inversion principle consistently
  - **⚠️ AVOID**: Circular dependencies between layers
- [ ] **Background operations independent** of UI lifecycle
  - **✅ CONSIDER**: Use Application scope for background operations
  - **⚠️ PITFALL**: Memory leaks from UI references in background
- [ ] **Sensitive price thresholds** (2-5%) for testing purposes
  - **✅ MODERN**: Make thresholds configurable for different environments
  - **⚠️ AVOID**: Hardcoded values that can't be adjusted

### Development Sequence
1. **Data Layer** - Entities, DAOs, migration
   - **✅ MODERN**: Start with tests for data layer
   - **⚠️ AVOID**: Building without proper migration testing
2. **Domain Layer** - Use cases and business logic
   - **✅ CONSIDER**: Input validation and error handling
   - **⚠️ PITFALL**: Business logic leaking into other layers
3. **Repository Layer** - Data coordination
   - **✅ MODERN**: Use Flow for reactive data
   - **⚠️ AVOID**: Blocking operations in repository methods
4. **Background Services** - WorkManager implementation
   - **✅ CONSIDER**: Work constraints and battery optimization
   - **⚠️ PITFALL**: Not handling work cancellation properly
5. **Notification System** - Channels and building
   - **✅ MODERN**: Test on different Android versions
   - **⚠️ AVOID**: Assuming notification permissions are granted
6. **Application Setup** - Initialization and configuration
   - **✅ CONSIDER**: Lazy initialization for better startup time
   - **⚠️ PITFALL**: Blocking operations in Application.onCreate()
7. **Manifest & Gradle** - Permissions and dependencies
   - **✅ MODERN**: Use latest stable versions
   - **⚠️ AVOID**: Adding unnecessary permissions
8. **Testing** - Unit and integration tests
   - **✅ CONSIDER**: Test coverage and quality metrics
   - **⚠️ PITFALL**: Tests that don't provide real confidence

## 12. Future Considerations

### Authentication Integration
- [ ] **RemoteUserId field** in UserEntity
  - **✅ MODERN**: Use UUID for remote user identification
  - **⚠️ AVOID**: Exposing internal user IDs externally
- [ ] **Cloud sync capabilities** in repositories
  - **✅ CONSIDER**: Conflict resolution strategies
  - **⚠️ PITFALL**: Data loss during sync conflicts
- [ ] **User session management** in Application class
  - **✅ MODERN**: Use encrypted shared preferences for tokens
  - **⚠️ AVOID**: Storing sensitive data in plain text
- [ ] **Data synchronization** between local and remote
  - **✅ CONSIDER**: Incremental sync for better performance
  - **⚠️ PITFALL**: Full sync that overwhelms the network

### Performance Optimizations
- [ ] **Battery optimization** settings
  - **✅ MODERN**: Guide users to whitelist app from battery optimization
  - **⚠️ AVOID**: Aggressive background work that drains battery
- [ ] **Network usage monitoring**
  - **✅ CONSIDER**: Respect user's data usage preferences
  - **⚠️ PITFALL**: Excessive network usage on metered connections
- [ ] **Background work frequency** optimization
  - **✅ MODERN**: Use adaptive intervals based on user behavior
  - **⚠️ AVOID**: Fixed intervals that don't match user needs
- [ ] **Notification grouping** and summary
  - **✅ CONSIDER**: Smart grouping based on coin categories
  - **⚠️ PITFALL**: Overwhelming users with too many notifications

### Security Considerations
- [ ] **API key protection** and rotation
  - **✅ MODERN**: Use certificate pinning for API calls
  - **⚠️ CRITICAL**: Never store API keys in source code
- [ ] **Data encryption** at rest and in transit
  - **✅ CONSIDER**: Encrypt sensitive user data in database
  - **⚠️ AVOID**: Storing sensitive data without encryption
- [ ] **Notification content security**
  - **✅ MODERN**: Don't include sensitive data in notification content
  - **⚠️ PITFALL**: Exposing portfolio values in lock screen notifications

### Accessibility and Inclusivity
- [ ] **Notification accessibility** for users with disabilities
  - **✅ MODERN**: Use proper content descriptions
  - **⚠️ AVOID**: Ignoring accessibility guidelines
- [ ] **Internationalization** for global users
  - **✅ CONSIDER**: Support for different number formats and currencies
  - **⚠️ PITFALL**: Hardcoded strings and formats
- [ ] **Dark mode support** for all notification elements
  - **✅ MODERN**: Use system theme-aware colors
  - **⚠️ AVOID**: Hardcoded colors that don't adapt to themes