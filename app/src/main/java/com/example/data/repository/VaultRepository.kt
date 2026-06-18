package com.example.data.repository

import com.example.data.local.FileDao
import com.example.data.local.TransferHistoryDao
import com.example.data.models.SharedFile
import com.example.data.models.TransferHistory
import kotlinx.coroutines.flow.Flow

class VaultRepository(
    private val fileDao: FileDao,
    private val transferHistoryDao: TransferHistoryDao
) {
    val allSharedFiles: Flow<List<SharedFile>> = fileDao.getAllSharedFiles()
    val allHistory: Flow<List<TransferHistory>> = transferHistoryDao.getAllHistory()
    val activeTransfers: Flow<List<TransferHistory>> = transferHistoryDao.getActiveTransfers()

    suspend fun getSharedFilesList(): List<SharedFile> = fileDao.getSharedFilesList()
    suspend fun getFileById(id: Int): SharedFile? = fileDao.getFileById(id)
    suspend fun getFileByName(name: String): SharedFile? = fileDao.getFileByName(name)

    suspend fun insertSharedFile(file: SharedFile): Long = fileDao.insertSharedFile(file)
    suspend fun deleteSharedFile(file: SharedFile) = fileDao.deleteSharedFile(file)
    suspend fun deleteFileById(id: Int) = fileDao.deleteFileById(id)

    suspend fun insertTransfer(transfer: TransferHistory): Long = transferHistoryDao.insertTransfer(transfer)
    suspend fun updateTransfer(transfer: TransferHistory) = transferHistoryDao.updateTransfer(transfer)
    suspend fun updateProgress(id: Int, bytes: Long, status: String) = transferHistoryDao.updateProgress(id, bytes, status)
    suspend fun clearHistory() = transferHistoryDao.clearAllHistory()
}
