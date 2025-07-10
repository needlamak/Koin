package com.koin.domain.portfolio

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPortfolioUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    operator fun invoke(): Flow<Result<Portfolio>> = repository.getPortfolio()
}

@Singleton
class BuyCoinUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    suspend operator fun invoke(
        coinId: String,
        quantity: Double,
        pricePerCoin: Double,
        transactionFee: Double = 0.0
    ): Result<Unit> = repository.buyCoin(coinId, quantity, pricePerCoin, transactionFee)
}

@Singleton
class SellCoinUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    suspend operator fun invoke(
        coinId: String,
        quantity: Double,
        pricePerCoin: Double,
        transactionFee: Double = 0.0
    ): Result<Unit> = repository.sellCoin(coinId, quantity, pricePerCoin, transactionFee)
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
    suspend operator fun invoke(): Double = repository.getBalance()
}

@Singleton
class ResetPortfolioUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    suspend operator fun invoke() = repository.resetPortfolio()
}
