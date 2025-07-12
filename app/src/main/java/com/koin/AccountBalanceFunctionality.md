
✦ Here's the documentation for the changes made to make the account balance functional:

Account Balance Functionality

This set of changes ensures that the user's account balance is properly initialized and accurately updated following buy and sell transactions.

New Files Created


* app/src/main/java/com/koin/data/portfolio/PortfolioInitializer.kt:
    * A new class responsible for initializing the user's portfolio balance when the application starts.
    * It checks if a balance already exists in the database; if not, it inserts a default initial balance (e.g., 10,000.0).
    * Uses runBlocking to perform this check and insertion synchronously during app startup.

Existing Files Modified


* app/src/main/java/com/koin/data/portfolio/PortfolioDao.kt:
    * Added `insertBalance(balance: PortfolioBalanceEntity)`: A new Room DAO method with OnConflictStrategy.REPLACE. This method allows for both inserting a new balance record and updating an existing one, simplifying balance
      management.
* app/src/main/java/com/koin/domain/portfolio/PortfolioRepository.kt:
    * Added `suspend fun insertInitialBalance(amount: Double)`: A new abstract method to the PortfolioRepository interface, defining the contract for initializing the balance.
* app/src/main/java/com/koin/data/portfolio/PortfolioRepositoryImpl.kt:
    * Implemented `insertInitialBalance(amount: Double)`: Provides the concrete implementation for the new interface method, calling portfolioDao.insertBalance().
    * Modified `getBalance()`: Removed the logic that attempted to insert an initial balance directly within the getBalance Flow. The initial balance is now handled by PortfolioInitializer.
    * Updated balance update calls: Changed calls to portfolioDao.updateBalance() within buyCoin() and sellCoin() to portfolioDao.insertBalance(PortfolioBalanceEntity(balance = newBalance)). This leverages the
      OnConflictStrategy.REPLACE in insertBalance to ensure the balance record is always created if it doesn't exist, and updated if it does.
* app/src/main/java/com/koin/CryptoApp.kt:
    * Injected `PortfolioInitializer`: The PortfolioInitializer is now injected into the CryptoApp class using Hilt.
    * Called `portfolioInitializer.initialize()`: The initialize() method of PortfolioInitializer is called within the onCreate() lifecycle method of the application. This ensures the balance is set up as soon as the app starts.

Overall Flow


1. App Startup: When the CryptoApp starts, portfolioInitializer.initialize() is called.
2. Initial Balance Check: PortfolioInitializer checks the database via PortfolioRepository to see if a PortfolioBalanceEntity already exists.
3. Initialization (if needed): If no balance is found, PortfolioInitializer calls portfolioRepository.insertInitialBalance() to set a default starting balance (e.g., 10,000.0).
4. Transaction Updates: During buyCoin() and sellCoin() operations in PortfolioRepositoryImpl, the updated balance is saved using portfolioDao.insertBalance(). Due to OnConflictStrategy.REPLACE, this method seamlessly handles
   both the initial creation of the balance record (if it somehow wasn't created during initialization) and subsequent updates.
