package com.koin.domain.coin

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshCoinsUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    suspend operator fun invoke() = repository.refreshCoins()
} 