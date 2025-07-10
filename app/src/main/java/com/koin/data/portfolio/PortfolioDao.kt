package com.koin.data.portfolio

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {
    // Holdings operations
    @Query("SELECT * FROM portfolio_holdings ORDER BY quantity * averagePurchasePrice DESC")
    fun getAllHoldings(): Flow<List<PortfolioHoldingEntity>>
    
    @Query("SELECT * FROM portfolio_holdings WHERE coinId = :coinId")
    suspend fun getHoldingByCoinId(coinId: String): PortfolioHoldingEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolding(holding: PortfolioHoldingEntity)
    
    @Update
    suspend fun updateHolding(holding: PortfolioHoldingEntity)
    
    @Delete
    suspend fun deleteHolding(holding: PortfolioHoldingEntity)
    
    @Query("DELETE FROM portfolio_holdings")
    suspend fun deleteAllHoldings()
    
    // Transaction operations
    @Query("SELECT * FROM portfolio_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<PortfolioTransactionEntity>>
    
    @Query("SELECT * FROM portfolio_transactions WHERE coinId = :coinId ORDER BY timestamp DESC")
    suspend fun getTransactionsByCoinId(coinId: String): List<PortfolioTransactionEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: PortfolioTransactionEntity)
    
    @Query("DELETE FROM portfolio_transactions")
    suspend fun deleteAllTransactions()
    
    // Balance operations
    @Query("SELECT * FROM portfolio_balance WHERE id = 1")
    fun getBalance(): Flow<PortfolioBalanceEntity?>
    
    @Query("SELECT balance FROM portfolio_balance WHERE id = 1")
    suspend fun getBalanceValue(): Double?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalance(balance: PortfolioBalanceEntity)
    
    @Query("UPDATE portfolio_balance SET balance = :balance, lastUpdated = :timestamp WHERE id = 1")
    suspend fun updateBalance(balance: Double, timestamp: Long = System.currentTimeMillis())
    
    @Query("DELETE FROM portfolio_balance")
    suspend fun deleteBalance()
    
    // Bulk operations for reset
    @Transaction
    suspend fun resetPortfolio() {
        deleteAllHoldings()
        deleteAllTransactions()
        deleteBalance()
    }
}
