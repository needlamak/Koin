package com.koin.data.portfolio

import com.koin.domain.portfolio.PortfolioHolding
import com.koin.domain.portfolio.Transaction
import com.koin.domain.portfolio.TransactionType
import java.util.UUID

// Entity to Domain
fun PortfolioHoldingEntity.toDomain(currentPrice: Double): PortfolioHolding {
    return PortfolioHolding(
        coinId = coinId,
        coinName = coinName,
        coinSymbol = coinSymbol,
        coinImageUrl = coinImageUrl,
        quantity = quantity,
        averagePurchasePrice = averagePurchasePrice,
        currentPrice = currentPrice,
        totalTransactionFees = totalTransactionFees
    )
}

fun PortfolioTransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        coinId = coinId,
        type = TransactionType.valueOf(type),
        quantity = quantity,
        pricePerCoin = pricePerCoin,
        transactionFee = transactionFee,
        timestamp = timestamp
    )
}

// Domain to Entity
fun PortfolioHolding.toEntity(): PortfolioHoldingEntity {
    return PortfolioHoldingEntity(
        coinId = coinId,
        coinName = coinName,
        coinSymbol = coinSymbol,
        coinImageUrl = coinImageUrl,
        quantity = quantity,
        averagePurchasePrice = averagePurchasePrice,
        totalTransactionFees = totalTransactionFees
    )
}

fun Transaction.toEntity(): PortfolioTransactionEntity {
    return PortfolioTransactionEntity(
        id = id,
        coinId = coinId,
        type = type.name,
        quantity = quantity,
        pricePerCoin = pricePerCoin,
        transactionFee = transactionFee,
        timestamp = timestamp
    )
}

// Helper functions for creating new transactions
fun createBuyTransaction(
    coinId: String,
    quantity: Double,
    pricePerCoin: Double,
    transactionFee: Double = 0.0
): Transaction {
    return Transaction(
        id = UUID.randomUUID().toString(),
        coinId = coinId,
        type = TransactionType.BUY,
        quantity = quantity,
        pricePerCoin = pricePerCoin,
        transactionFee = transactionFee,
        timestamp = System.currentTimeMillis()
    )
}

fun createSellTransaction(
    coinId: String,
    quantity: Double,
    pricePerCoin: Double,
    transactionFee: Double = 0.0
): Transaction {
    return Transaction(
        id = UUID.randomUUID().toString(),
        coinId = coinId,
        type = TransactionType.SELL,
        quantity = quantity,
        pricePerCoin = pricePerCoin,
        transactionFee = transactionFee,
        timestamp = System.currentTimeMillis()
    )
}
