package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.models.SharedFile
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {
    @Query("SELECT * FROM shared_files ORDER BY timestamp DESC")
    fun getAllSharedFiles(): Flow<List<SharedFile>>

    @Query("SELECT * FROM shared_files ORDER BY timestamp DESC")
    suspend fun getSharedFilesList(): List<SharedFile>

    @Query("SELECT * FROM shared_files WHERE id = :id")
    suspend fun getFileById(id: Int): SharedFile?

    @Query("SELECT * FROM shared_files WHERE name = :name LIMIT 1")
    suspend fun getFileByName(name: String): SharedFile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSharedFile(file: SharedFile): Long

    @Delete
    suspend fun deleteSharedFile(file: SharedFile)

    @Query("DELETE FROM shared_files WHERE id = :id")
    suspend fun deleteFileById(id: Int)
}
