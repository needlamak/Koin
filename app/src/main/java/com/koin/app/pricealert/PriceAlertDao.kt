package com.koin.app.pricealert

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.koin.data.pricealert.PriceAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceAlertDao {
    @Query("SELECT * FROM price_alerts WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveAlerts(): Flow<List<PriceAlertEntity>>
    
    @Query("SELECT * FROM price_alerts ORDER BY createdAt DESC")
    fun getAllAlerts(): Flow<List<PriceAlertEntity>>
    
    @Query("SELECT * FROM price_alerts WHERE coinId = :coinId AND isActive = 1")
    fun getActiveAlertsForCoin(coinId: String): Flow<List<PriceAlertEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PriceAlertEntity)
    
    @Update
    suspend fun updateAlert(alert: PriceAlertEntity)
    
    @Delete
    suspend fun deleteAlert(alert: PriceAlertEntity)
    
    @Query("DELETE FROM price_alerts WHERE id = :alertId")
    suspend fun deleteAlertById(alertId: String)
    
    @Query("UPDATE price_alerts SET isTriggered = 1, triggeredAt = :triggeredAt WHERE id = :alertId")
    suspend fun markAlertAsTriggered(alertId: String, triggeredAt: Long)
    
    @Query("UPDATE price_alerts SET isActive = 0 WHERE id = :alertId")
    suspend fun deactivateAlert(alertId: String)
}