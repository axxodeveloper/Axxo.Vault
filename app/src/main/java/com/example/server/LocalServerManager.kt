package com.example.server

import android.content.Context
import android.net.Uri
import com.example.data.models.SharedFile
import com.example.data.models.TransferHistory
import com.example.data.repository.VaultRepository
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.streamProvider
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.header
import io.ktor.server.response.respondOutputStream
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.net.NetworkInterface
import java.util.Collections

class LocalServerManager(
    private val context: Context,
    private val repository: VaultRepository
) {
    private var serverEngine: NettyApplicationEngine? = null
    private var wakeLock: android.os.PowerManager.WakeLock? = null
    private var wifiLock: android.net.wifi.WifiManager.WifiLock? = null

    private val _serverState = MutableStateFlow<ServerStatus>(ServerStatus.Stopped)
    val serverState: StateFlow<ServerStatus> = _serverState

    sealed class ServerStatus {
        object Stopped : ServerStatus()
        data class Running(val ipAddress: String, val port: Int) : ServerStatus()
        data class Error(val message: String) : ServerStatus()
    }

    fun startServer(port: Int = 8080) {
        if (_serverState.value is ServerStatus.Running) return

        val ip = getLocalIpAddress()
        if (ip == "127.0.0.1") {
            _serverState.value = ServerStatus.Error("Please connect to a local Wi-Fi network first.")
            return
        }

        // Acquire WakeLocks
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        wakeLock = powerManager.newWakeLock(android.os.PowerManager.PARTIAL_WAKE_LOCK, "AxxoVault::ServerWakeLock")
        wakeLock?.acquire()

        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
        wifiLock = wifiManager.createWifiLock(android.net.wifi.WifiManager.WIFI_MODE_FULL_HIGH_PERF, "AxxoVault::ServerWifiLock")
        wifiLock?.acquire()

        // Start Foreground Service
        val serviceIntent = android.content.Intent(context, VaultServerService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                _serverState.value = ServerStatus.Running(ip, port)
                serverEngine = embeddedServer(Netty, port = port, host = "0.0.0.0") {
                    configureServerRoutes()
                }
                serverEngine?.start(wait = true)
            } catch (e: Exception) {
                e.printStackTrace()
                if (wakeLock?.isHeld == true) wakeLock?.release()
                if (wifiLock?.isHeld == true) wifiLock?.release()
                context.stopService(android.content.Intent(context, VaultServerService::class.java))
                _serverState.value = ServerStatus.Error(e.localizedMessage ?: "Failed to start local vault node.")
            }
        }
    }

    fun stopServer() {
        if (wakeLock?.isHeld == true) wakeLock?.release()
        if (wifiLock?.isHeld == true) wifiLock?.release()
        
        context.stopService(android.content.Intent(context, VaultServerService::class.java))
        
        if (serverEngine != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    serverEngine?.stop(500, 1000)
                    serverEngine = null
                    _serverState.value = ServerStatus.Stopped
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun Application.configureServerRoutes() {
        val appFilesDir = this@LocalServerManager.context.filesDir
        val appContentResolver = this@LocalServerManager.context.contentResolver

        intercept(ApplicationCallPipeline.Call) {
            val clientIp = call.request.local.remoteHost
            if (clientIp != "127.0.0.1" && clientIp != "localhost" && clientIp != "0:0:0:0:0:0:0:1") {
                trackClient(clientIp)
            }
        }

        routing {
            // Index Portal
            get("/") {
                val ip = getLocalIpAddress()
                val html = PortalHtmlProvider.getPortalHtml(ip, 8080)
                call.respondText(html, ContentType.Text.Html)
            }

            // List shared files in raw JSON to allow peer browser dynamic AJAX loading
            get("/api/files") {
                try {
                    val files = repository.getSharedFilesList()
                    val json = StringBuilder("[")
                    files.forEachIndexed { index, file ->
                        json.append("""{"id":${file.id},"name":"${file.name.replace("\"", "\\\"")}","size":${file.size},"mimeType":"${file.mimeType}"}""")
                        if (index < files.size - 1) json.append(",")
                    }
                    json.append("]")
                    call.respondText(json.toString(), ContentType.Application.Json)
                } catch (e: Exception) {
                    call.respondText("[]", ContentType.Application.Json, HttpStatusCode.InternalServerError)
                }
            }

            // Handle direct downstream file downloads
            get("/download/{id}") {
                val idParam = call.parameters["id"]?.toIntOrNull()
                if (idParam == null) {
                    call.respondText("Bad File ID Parameter", ContentType.Text.Plain, HttpStatusCode.BadRequest)
                    return@get
                }

                val fileRecord = repository.getFileById(idParam)
                if (fileRecord == null) {
                    call.respondText("File not found in Room Registry", ContentType.Text.Plain, HttpStatusCode.NotFound)
                    return@get
                }

                val file = java.io.File(fileRecord.path)
                if (!file.exists()) {
                    call.respondText("File physical cache is missing", ContentType.Text.Plain, HttpStatusCode.NotFound)
                    return@get
                }

                val peerIp = call.request.local.remoteHost
                val transferId = repository.insertTransfer(
                    TransferHistory(
                        fileName = fileRecord.name,
                        direction = "DOWNLOAD",
                        peerIp = peerIp,
                        size = fileRecord.size,
                        bytesTransferred = 0,
                        status = "ACTIVE"
                    )
                ).toInt()

                try {
                    call.response.header(HttpHeaders.ContentDisposition, "attachment; filename=\"${fileRecord.name}\"")
                    call.response.header(HttpHeaders.ContentType, fileRecord.mimeType)

                    call.respondOutputStream(
                        contentType = ContentType.parse(fileRecord.mimeType),
                        status = HttpStatusCode.OK
                    ) {
                        val buffer = ByteArray(128 * 1024) // 128KB high-throughput buffer
                        var bytesRead: Int
                        var totalBytesSent = 0L
                        var lastUpdateTime = 0L
                        file.inputStream().use { stream ->
                            while (stream.read(buffer).also { bytesRead = it } != -1) {
                                write(buffer, 0, bytesRead)
                                flush()
                                totalBytesSent += bytesRead
                                
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastUpdateTime >= 250) { // Throttle database progress writes to max 4 times a sec
                                    repository.updateProgress(transferId, totalBytesSent, "ACTIVE")
                                    lastUpdateTime = currentTime
                                }
                            }
                        }
                        repository.updateProgress(transferId, fileRecord.size, "COMPLETED")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    repository.updateProgress(transferId, 0, "FAILED")
                }
            }

            // Stream multi-part form peer file uploads
            post("/upload") {
                val peerIp = call.request.local.remoteHost
                var fileName = "peer_upload_${System.currentTimeMillis()}"
                var fileSize = 0L
                var transferId = 0
                var targetFile: java.io.File? = null

                try {
                    val multipart = call.receiveMultipart()
                    var part = multipart.readPart()
                    while (part != null) {
                        if (part is io.ktor.http.content.PartData.FileItem) {
                            fileName = part.originalFileName ?: "uploaded_content_${System.currentTimeMillis()}"
                            val ext = java.io.File(fileName).extension
                            val baseName = java.io.File(fileName).nameWithoutExtension

                            val parentDir = java.io.File(appFilesDir, "shared_vault")
                            if (!parentDir.exists()) parentDir.mkdirs()

                            // Avoid naming duplicates in folder
                            var testFile = java.io.File(parentDir, fileName)
                            var counter = 1
                            while (testFile.exists()) {
                                testFile = java.io.File(parentDir, "$baseName-$counter.$ext")
                                counter++
                            }
                            targetFile = testFile

                            transferId = repository.insertTransfer(
                                TransferHistory(
                                    fileName = targetFile.name,
                                    direction = "UPLOAD",
                                    peerIp = peerIp,
                                    size = 0,
                                    bytesTransferred = 0,
                                    status = "ACTIVE"
                                )
                            ).toInt()

                            val outputStream = targetFile.outputStream()
                            outputStream.use { out ->
                                part.streamProvider().use { input ->
                                    val buffer = ByteArray(128 * 1024) // 128KB buffer
                                    var bytesRead: Int
                                    var lastUpdateTime = 0L
                                    while (input.read(buffer).also { bytesRead = it } != -1) {
                                        out.write(buffer, 0, bytesRead)
                                        fileSize += bytesRead
                                        
                                        val currentTime = System.currentTimeMillis()
                                        if (currentTime - lastUpdateTime >= 250) { // Throttle updates to at most 4 times per second
                                            repository.updateProgress(transferId, fileSize, "ACTIVE")
                                            lastUpdateTime = currentTime
                                        }
                                    }
                                }
                            }

                            // Register the newly received file so it is visible in the vault
                            val mimeType = appContentResolver.getType(Uri.fromFile(targetFile)) ?: "application/octet-stream"
                            val fileId = repository.insertSharedFile(
                                SharedFile(
                                    name = targetFile.name,
                                    path = targetFile.absolutePath,
                                    size = fileSize,
                                    mimeType = mimeType
                                )
                            )

                            // Mark successfully completed
                            repository.updateTransfer(
                                TransferHistory(
                                    id = transferId,
                                    fileName = targetFile.name,
                                    direction = "UPLOAD",
                                    peerIp = peerIp,
                                    size = fileSize,
                                    bytesTransferred = fileSize,
                                    status = "COMPLETED"
                                )
                            )
                        }
                        part.dispose()
                        part = multipart.readPart()
                    }
                    call.respondText("Transmitted to vault successfully", ContentType.Text.Plain, HttpStatusCode.OK)
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (transferId != 0) {
                        repository.updateTransfer(
                            TransferHistory(
                                id = transferId,
                                fileName = targetFile?.name ?: fileName,
                                direction = "UPLOAD",
                                peerIp = peerIp,
                                size = fileSize,
                                bytesTransferred = fileSize,
                                status = "FAILED"
                              )
                          )
                    }
                    call.respondText("System fail: ${e.localizedMessage}", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
                }
            }
        }
    }

    data class ConnectedDevice(
        val ip: String,
        val mac: String
    )

    companion object {
        private val activeClients = java.util.concurrent.ConcurrentHashMap<String, Long>()

        fun trackClient(ip: String) {
            activeClients[ip] = System.currentTimeMillis()
        }

        fun getConnectedHotspotClients(): List<ConnectedDevice> {
            val clients = mutableListOf<ConnectedDevice>()
            try {
                // Try reading ARP table directly
                val br = java.io.BufferedReader(java.io.FileReader("/proc/net/arp"))
                var line: String?
                // Skip header
                br.readLine()
                while (br.readLine().also { line = it } != null) {
                    val splitted = line!!.trim().split(" +".toRegex()).toTypedArray()
                    if (splitted.size >= 4) {
                        val ip = splitted[0]
                        val mac = splitted[3]
                        if (mac != "00:00:00:00:00:00" && ip.matches(Regex("\\d{1,3}(\\.\\d{1,3}){3}"))) {
                            clients.add(ConnectedDevice(ip, mac))
                        }
                    }
                }
                br.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (clients.isEmpty()) {
                // Fallback to "ip neigh" command if /proc/net/arp is inaccessible
                try {
                    val process = Runtime.getRuntime().exec("ip neigh show")
                    process.inputStream.bufferedReader().useLines { lines ->
                        lines.forEach { line ->
                            val parts = line.split(" ")
                            if (parts.size >= 5 && parts.contains("lladdr")) {
                                val ip = parts[0]
                                val macIndex = parts.indexOf("lladdr") + 1
                                if (macIndex < parts.size && ip.matches(Regex("\\d{1,3}(\\.\\d{1,3}){3}"))) {
                                    val mac = parts[macIndex]
                                    if (!clients.any { it.ip == ip }) {
                                        clients.add(ConnectedDevice(ip, mac))
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val now = System.currentTimeMillis()
            activeClients.forEach { (ip, lastSeen) ->
                if (now - lastSeen < 60000) { // Active within last 60 seconds
                    if (!clients.any { it.ip == ip }) {
                        clients.add(ConnectedDevice(ip, "Unknown (Web Client)"))
                    }
                }
            }

            return clients
        }

        fun getLocalIpAddress(): String {
            try {
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    val name = intf.name.lowercase()
                    if (name.contains("wlan") || name.contains("swlan") || name.contains("ap") || name.contains("eth") || name.contains("rndis")) {
                        val addrs = Collections.list(intf.inetAddresses)
                        for (addr in addrs) {
                            if (!addr.isLoopbackAddress) {
                                val sAddr = addr.hostAddress
                                val isIPv4 = sAddr?.indexOf(':') ?: -1 < 0
                                if (isIPv4 && sAddr != null) {
                                    return sAddr
                                }
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return "127.0.0.1"
        }
    }
}
