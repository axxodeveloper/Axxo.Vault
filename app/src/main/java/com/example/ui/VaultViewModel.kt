package com.example.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.models.SharedFile
import com.example.data.models.TransferHistory
import com.example.data.repository.VaultRepository
import com.example.server.LocalServerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class VaultViewModel(
    private val context: Context,
    private val repository: VaultRepository
) : ViewModel() {
    private val serverManager = LocalServerManager(context.applicationContext, repository)

    val serverState: StateFlow<LocalServerManager.ServerStatus> = serverManager.serverState
    
    val sharedFiles: StateFlow<List<SharedFile>> = repository.allSharedFiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transferHistory: StateFlow<List<TransferHistory>> = repository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeTransfers: StateFlow<List<TransferHistory>> = repository.activeTransfers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _copyingState = MutableStateFlow<String?>(null)
    val copyingState: StateFlow<String?> = _copyingState

    fun toggleServer() {
        viewModelScope.launch {
            if (serverState.value is LocalServerManager.ServerStatus.Running) {
                serverManager.stopServer()
            } else {
                serverManager.startServer(port = 8080)
            }
        }
    }

    fun shareFileFromUri(uri: Uri) {
        viewModelScope.launch {
            _copyingState.value = "Encrypting and indexing file..."
            val success = withContext(Dispatchers.IO) {
                try {
                    var name = "deposit_file_${System.currentTimeMillis()}"
                    var size = 0L

                    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (nameIndex != -1 && cursor.moveToFirst()) {
                            name = cursor.getString(nameIndex) ?: name
                        }
                        if (sizeIndex != -1) {
                            size = cursor.getLong(sizeIndex)
                        }
                    }

                    val parentDir = File(context.filesDir, "shared_vault")
                    if (!parentDir.exists()) parentDir.mkdirs()

                    val ext = File(name).extension
                    val baseName = File(name).nameWithoutExtension
                    var targetFile = File(parentDir, name)
                    var counter = 1
                    while (targetFile.exists()) {
                        targetFile = File(parentDir, "$baseName-$counter.$ext")
                        counter++
                    }

                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(targetFile).use { output ->
                            val buffer = ByteArray(64 * 1024)
                            var bytesRead: Int
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                            }
                        }
                    }

                    val actualSize = if (size > 0) size else targetFile.length()
                    val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

                    repository.insertSharedFile(
                        SharedFile(
                            name = targetFile.name,
                            path = targetFile.absolutePath,
                            size = actualSize,
                            mimeType = mimeType
                        )
                    )
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
            _copyingState.value = if (success) null else "Import failed. Please copy a valid document."
        }
    }

    fun removeSharedFile(file: SharedFile) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val f = File(file.path)
                if (f.exists()) {
                    f.delete()
                }
                repository.deleteSharedFile(file)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveFileToDownloads(file: SharedFile) {
        viewModelScope.launch {
            _copyingState.value = "Saving ${file.name} to Downloads..."
            val success = withContext(Dispatchers.IO) {
                try {
                    val srcFile = File(file.path)
                    if (!srcFile.exists()) return@withContext false

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        val resolver = context.contentResolver
                        val contentValues = android.content.ContentValues().apply {
                            put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, file.name)
                            put(android.provider.MediaStore.MediaColumns.MIME_TYPE, file.mimeType)
                            put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
                        }
                        val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                        if (uri != null) {
                            resolver.openOutputStream(uri)?.use { output ->
                                srcFile.inputStream().use { input ->
                                    input.copyTo(output)
                                }
                            }
                            true
                        } else {
                            false
                        }
                    } else {
                        val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                        val destFile = File(downloadsDir, file.name)
                        srcFile.inputStream().use { input ->
                            destFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
            _copyingState.value = if (success) "Saved ${file.name} to Downloads!" else "Failed to save file."
        }
    }

    fun clearImportStatus() {
        _copyingState.value = null
    }

    fun clearTransferHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        serverManager.stopServer()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val context: Context,
        private val repository: VaultRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VaultViewModel::class.java)) {
                return VaultViewModel(context, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
