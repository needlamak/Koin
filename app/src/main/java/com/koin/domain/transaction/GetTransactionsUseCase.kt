package com.koin.domain.transaction

import com.koin.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Transaction>> {
        return repository.getAllTransactions()
    }
}