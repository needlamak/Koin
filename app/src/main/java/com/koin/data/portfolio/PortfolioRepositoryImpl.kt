package com.koin.data.portfolio

import android.util.Log
import com.koin.domain.coin.CoinRepository
import com.koin.domain.model.Coin
import com.koin.domain.portfolio.Portfolio
import com.koin.domain.portfolio.PortfolioRepository
import com.koin.domain.portfolio.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioRepositoryImpl @Inject constructor(
    private val portfolioDao: PortfolioDao,
    private val coinRepository: CoinRepository
) : PortfolioRepository {

    // This is the TRUE Single Source of Truth for Portfolio
    private val _portfolio = MutableStateFlow(Portfolio.empty())
    val portfolio = _portfolio.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            loadFromCache()
            // Portfolio data is local, no network refresh needed
            // But we need to update with current coin prices
            refreshPortfolioWithCurrentPrices()
        }
    }

    override fun getPortfolio(): Flow<Result<Portfolio>> =
        portfolio.map { Result.success(it) }

    override suspend fun buyCoin(
        coinId: String,
        quantity: Double,
        pricePerCoin: Double,
        transactionFee: Double
    ): Result<Unit> {
        return try {
            // Get coin info from existing repository
            val coinResult = coinRepository.getCoinById(coinId).first()
            val coin = coinResult.getOrNull()
                ?: return Result.failure(Exception("Coin not found"))

            // Check if user has enough balance
            val currentBalance = getCurrentBalance()
            val totalCost = (quantity * pricePerCoin) + transactionFee
            
            if (currentBalance < totalCost) {
                return Result.failure(Exception("Insufficient balance"))
            }

            // Create transaction
            val transaction = createBuyTransaction(coinId, quantity, pricePerCoin, transactionFee)
            
            // Update or create holding
            val existingHolding = portfolioDao.getHoldingByCoinId(coinId)
            if (existingHolding != null) {
                // Calculate new average price
                val totalQuantity = existingHolding.quantity + quantity
                val totalCost = (existingHolding.quantity * existingHolding.averagePurchasePrice) + 
                               (quantity * pricePerCoin)
                val newAveragePrice = totalCost / totalQuantity
                val newTotalFees = existingHolding.totalTransactionFees + transactionFee
                
                val updatedHolding = existingHolding.copy(
                    quantity = totalQuantity,
                    averagePurchasePrice = newAveragePrice,
                    totalTransactionFees = newTotalFees
                )
                portfolioDao.updateHolding(updatedHolding)
            } else {
                // Create new holding
                val newHolding = PortfolioHoldingEntity(
                    coinId = coinId,
                    coinName = coin.name,
                    coinSymbol = coin.symbol,
                    coinImageUrl = coin.imageUrl,
                    quantity = quantity,
                    averagePurchasePrice = pricePerCoin,
                    totalTransactionFees = transactionFee
                )
                portfolioDao.insertHolding(newHolding)
            }

            // Insert transaction
            portfolioDao.insertTransaction(transaction.toEntity())

            // Update balance
            portfolioDao.updateBalance(currentBalance - totalCost)

            // Refresh portfolio state
            refreshPortfolioWithCurrentPrices()

            Result.success(Unit)
        } catch (e: Exception) {
            val errorMsg = "Failed to buy coin: ${e.localizedMessage}"
            Log.e("PortfolioRepository", errorMsg, e)
            _lastError.value = errorMsg
            Result.failure(e)
        }
    }

    override suspend fun sellCoin(
        coinId: String,
        quantity: Double,
        pricePerCoin: Double,
        transactionFee: Double
    ): Result<Unit> {
        return try {
            // Check if user has enough of the coin to sell
            val existingHolding = portfolioDao.getHoldingByCoinId(coinId)
                ?: return Result.failure(Exception("No holdings found for this coin"))

            if (existingHolding.quantity < quantity) {
                return Result.failure(Exception("Insufficient coin quantity"))
            }

            // Create transaction
            val transaction = createSellTransaction(coinId, quantity, pricePerCoin, transactionFee)

            // Update holding
            val newQuantity = existingHolding.quantity - quantity
            if (newQuantity > 0) {
                val updatedHolding = existingHolding.copy(quantity = newQuantity)
                portfolioDao.updateHolding(updatedHolding)
            } else {
                // Remove holding if quantity is 0
                portfolioDao.deleteHolding(existingHolding)
            }

            // Insert transaction
            portfolioDao.insertTransaction(transaction.toEntity())

            // Update balance (add proceeds minus fees)
            val currentBalance = getCurrentBalance()
            val proceeds = (quantity * pricePerCoin) - transactionFee
            portfolioDao.updateBalance(currentBalance + proceeds)

            // Refresh portfolio state
            refreshPortfolioWithCurrentPrices()

            Result.success(Unit)
        } catch (e: Exception) {
            val errorMsg = "Failed to sell coin: ${e.localizedMessage}"
            Log.e("PortfolioRepository", errorMsg, e)
            _lastError.value = errorMsg
            Result.failure(e)
        }
    }

    override suspend fun refreshPortfolio() {
        refreshPortfolioWithCurrentPrices()
    }

    override suspend fun getTransactionHistory() = 
        portfolioDao.getAllTransactions().first().map { it.toDomain() }

    override suspend fun getBalance(): Double = getCurrentBalance()

    override suspend fun resetPortfolio() {
        try {
            portfolioDao.resetPortfolio()
            // Reset balance to initial amount
            portfolioDao.insertBalance(
                PortfolioBalanceEntity(balance = Portfolio.INITIAL_BALANCE)
            )
            _portfolio.value = Portfolio.empty()
        } catch (e: Exception) {
            Log.e("PortfolioRepository", "Failed to reset portfolio", e)
        }
    }

    private suspend fun loadFromCache() {
        try {
            refreshPortfolioWithCurrentPrices()
        } catch (e: Exception) {
            Log.e("PortfolioRepository", "Failed to load portfolio from cache", e)
            _lastError.value = "Failed to load portfolio: ${e.localizedMessage}"
        }
    }

    private suspend fun refreshPortfolioWithCurrentPrices() {
        try {
            val holdings = portfolioDao.getAllHoldings().first()
            val transactions = portfolioDao.getAllTransactions().first()
            val balance = getCurrentBalance()

            // Get current prices for all held coins
            val coinPrices = mutableMapOf<String, Coin>()
            val coinsResult = coinRepository.getAllCoins().first()
            coinsResult.getOrNull()?.forEach { coin ->
                coinPrices[coin.id] = coin
            }

            // Convert holdings to domain with current prices
            val portfolioHoldings = holdings.mapNotNull { holdingEntity ->
                val coin = coinPrices[holdingEntity.coinId]
                coin?.let { holdingEntity.toDomain(it.currentPrice) }
            }

            // Convert transactions to domain
            val portfolioTransactions = transactions.map { it.toDomain() }

            // Update portfolio state
            _portfolio.value = Portfolio(
                balance = balance,
                holdings = portfolioHoldings,
                transactions = portfolioTransactions
            )

        } catch (e: Exception) {
            Log.e("PortfolioRepository", "Failed to refresh portfolio with current prices", e)
            _lastError.value = "Failed to refresh portfolio: ${e.localizedMessage}"
        }
    }

    private suspend fun getCurrentBalance(): Double {
        return try {
            portfolioDao.getBalanceValue() ?: run {
                // Initialize balance if it doesn't exist
                portfolioDao.insertBalance(
                    PortfolioBalanceEntity(balance = Portfolio.INITIAL_BALANCE)
                )
                Portfolio.INITIAL_BALANCE
            }
        } catch (e: Exception) {
            Log.e("PortfolioRepository", "Failed to get balance", e)
            Portfolio.INITIAL_BALANCE
        }
    }
}
