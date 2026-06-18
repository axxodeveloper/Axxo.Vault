package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shared_files")
data class SharedFile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val path: String, // Internal cache/files storage path
    val size: Long,
    val mimeType: String,
    val timestamp: Long = System.currentTimeMillis()
)
