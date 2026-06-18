package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transfer_history")
data class TransferHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val direction: String, // "UPLOAD" or "DOWNLOAD"
    val peerIp: String,
    val size: Long,
    val bytesTransferred: Long = 0,
    val status: String, // "ACTIVE", "COMPLETED", "FAILED"
    val timestamp: Long = System.currentTimeMillis()
)
