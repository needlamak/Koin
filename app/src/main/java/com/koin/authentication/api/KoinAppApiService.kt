package com.koin.authentication.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface KoinAppApiService {
    
    @POST("koinapp-transfer-processor")
    suspend fun transferFunds(
        @Body request: TransferRequest
    ): Response<TransferResponse>
    
    @GET("transactions/{walletAddress}")
    suspend fun getTransactions(
        @Path("walletAddress") walletAddress: String
    ): Response<TransactionsResponse>
}

data class TransferRequest(
    val from_wallet_address: String,
    val to_wallet_address: String,
    val amount: Double
)

data class TransferResponse(
    val message: String,
    val transaction_id: String
)

data class TransactionsResponse(
    val transactions: List<Transaction>
)

data class Transaction(
    val transaction_id: String,
    val from_wallet_address: String,
    val to_wallet_address: String,
    val amount: Double,
    val currency: String,
    val status: String,
    val timestamp: Long
)