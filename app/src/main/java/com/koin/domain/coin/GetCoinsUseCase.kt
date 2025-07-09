package com.koin.domain.coin

import com.koin.domain.model.Coin
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCoinsUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    operator fun invoke(): Flow<Result<List<Coin>>> = repository.getAllCoins()
} 