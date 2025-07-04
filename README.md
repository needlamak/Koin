# Koin
A real-time crypto application with caching data

Koin - Cryptocurrency Tracker App
Overview
Koin is a modern Android application built with Kotlin that allows users to track cryptocurrency prices, view detailed information about different coins, and monitor market trends. The app follows clean architecture principles and uses the latest Android development tools and libraries.

Technical Stack
Language: Kotlin
Architecture: MVVM (Model-View-ViewModel)
UI: Jetpack Compose
Dependency Injection: Hilt
Networking: Retrofit
Local Storage: Room Database
Asynchronous Programming: Kotlin Coroutines & Flow
Image Loading: Coil
Navigation: Compose Navigation
Project Structure
app/
├── src/
│   ├── main/
│   │   ├── java/com/koin/
│   │   │   ├── data/
│   │   │   │   ├── coin/        # Data layer (DTOs, DAOs, Repositories)
│   │   │   │   └── network/     # API services and data sources
│   │   │   ├── di/              # Dependency injection modules
│   │   │   ├── domain/          # Business logic and use cases
│   │   │   ├── ui/              # Presentation layer
│   │   │   │   ├── coindetail/  # Coin detail screen
│   │   │   │   └── coinlist/    # Coin list screen
│   │   │   └── util/            # Utility classes and extensions
Key Features
Coin List
View top cryptocurrencies by market cap
Search functionality
Pull-to-refresh
Price change indicators
Coin Detail
Detailed coin information
Price charts
Market statistics
Historical price data
Implementation Details
Data Layer
API Integration: Uses CoinGecko API for cryptocurrency data
Caching: Implements offline-first approach with Room database
Data Mapping: Converts between DTOs, entities, and domain models
UI Layer
Composable Functions: Uses Jetpack Compose for declarative UI
State Management: Uses ViewModel with StateFlow for UI state
Theming: Implements Material 3 theming
Issues Fixed & Lessons Learned
1. JSON Parsing Error
Issue: NumberFormatException when parsing total_volume from API

Root Cause: API returned floating-point numbers but model expected Long
Fix: Changed totalVolume type from Long? to Double? in all model layers
Lesson: Always verify API response formats and use appropriate data types
2. Compose UI Crash
Issue: ClassCastException in 
CoinDetailScreen

Root Cause: Incorrect alignment casting (Alignment.Center to Alignment.Horizontal)
Fix: Used Alignment.CenterHorizontally instead of type casting
Lesson: Use the correct alignment constants provided by Compose
3. Data Type Consistency
Issue: Mismatch between domain and data models

Root Cause: Inconsistent types across layers
Fix: Ensured all model layers (DTO, Entity, Domain) use matching types
Lesson: Maintain type consistency across all layers of the application
Best Practices Applied
Code Organization
Clear separation of concerns with clean architecture
Package by feature
Consistent naming conventions
Error Handling
Proper error states in UI
Network error handling
Empty state handling
Performance
Paging for large datasets
Image loading with Coil
Efficient state management
Future Improvements
Add user portfolio tracking
Implement price alerts
Add more detailed charts
Support for multiple fiat currencies
Dark/light theme support
Development Notes
Common Pitfalls to Avoid
Type Safety: Always use the correct data types that match the API responses
Threading: Be mindful of thread context when working with coroutines
State Management: Properly handle loading, success, and error states
API Rate Limiting: Implement proper handling for API rate limits
Error Handling: Provide meaningful error messages to users
Testing Strategy
Unit tests for use cases and ViewModels
UI tests for critical user flows
Repository tests for data layer
Network tests for API integration
Conclusion
Koin demonstrates modern Android development practices with a clean architecture approach. The app is built to be maintainable, scalable, and provides a smooth user experience for tracking cryptocurrency prices and market data.
