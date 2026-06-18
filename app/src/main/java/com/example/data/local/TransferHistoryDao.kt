package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.TransferHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferHistoryDao {
    @Query("SELECT * FROM transfer_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<TransferHistory>>

    @Query("SELECT * FROM transfer_history WHERE status = 'ACTIVE' ORDER BY timestamp DESC")
    fun getActiveTransfers(): Flow<List<TransferHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: TransferHistory): Long

    @Update
    suspend fun updateTransfer(transfer: TransferHistory)

    @Query("UPDATE transfer_history SET bytesTransferred = :bytes, status = :status WHERE id = :id")
    suspend fun updateProgress(id: Int, bytes: Long, status: String)

    @Query("DELETE FROM transfer_history")
    suspend fun clearAllHistory()
}
