package com.koin.domain.portfolio

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPortfolioUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    operator fun invoke(): Flow<List<PortfolioHolding>> = repository.getHoldings()
}

@Singleton
class BuyCoinUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    suspend operator fun invoke(
        coin: com.koin.domain.model.Coin,
        amount: Double
    ): Unit = repository.buyCoin(coin, amount)
}

@Singleton
class SellCoinUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    suspend operator fun invoke(
        coinId: String,
        quantity: Double,
        pricePerCoin: Double
    ): Unit = repository.sellCoin(coinId, quantity, pricePerCoin)
}

@Singleton
class RefreshPortfolioUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    suspend operator fun invoke() = repository.refreshPortfolio()
}

@Singleton
class GetTransactionHistoryUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    suspend operator fun invoke(): List<Transaction> = repository.getTransactionHistory()
}

@Singleton
class GetBalanceUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    operator fun invoke(): Flow<PortfolioBalance?> = repository.getBalance()
}

//@Singleton
//class GetBalanceUseCase @Inject constructor(
//    private val repository: PortfolioRepository
//) {
//    suspend operator fun invoke(): Flow<PortfolioBalance?> = repository.getBalance()
//}

@Singleton
class ResetPortfolioUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    suspend operator fun invoke() = repository.resetPortfolio()
}
