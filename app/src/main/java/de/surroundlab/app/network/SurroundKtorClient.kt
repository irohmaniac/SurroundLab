package de.surroundlab.app.network

import de.surroundlab.app.data.AudioLibraryRepository
import de.surroundlab.app.domain.model.AudioSyncStatus
import de.surroundlab.app.domain.model.ConnectionStatus
import de.surroundlab.app.domain.model.CustomAudioTrack
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.domain.model.WsMessage
import de.surroundlab.app.domain.model.WsMessageTypes
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.delete
import io.ktor.client.request.forms.ChannelProvider
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.cio.readChannel
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Ktor CIO WebSocket + HTTP client used by Android smartphones to remote-control a TV host
 * and synchronize custom audio files via SHA-256 comparison.
 */
class SurroundKtorClient(
    private val audioLibrary: AudioLibraryRepository
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingIntervalMillis = 15_000L
        }
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 180_000L
            connectTimeoutMillis = 5_000L
        }
    }

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.LOCAL_STANDALONE)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    private val _remoteState = MutableStateFlow<SurroundAppState?>(null)
    val remoteState: StateFlow<SurroundAppState?> = _remoteState.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    private val _syncStatus = MutableStateFlow(AudioSyncStatus())
    val syncStatus: StateFlow<AudioSyncStatus> = _syncStatus.asStateFlow()

    private val syncMutex = Mutex()
    private var connectionJob: Job? = null
    private var activeSession: DefaultClientWebSocketSession? = null
    private var currentHostIp: String = ""
    private var currentPort: Int = 8080
    private var currentToken: String = ""
    private var syncedHashes = false

    fun connect(hostIp: String, port: Int, token: String) {
        val cleanIp = hostIp.trim()
        if (!NetworkUtils.isPrivateLanHost(cleanIp)) {
            _lastError.value = "Verbindung abgelehnt: Nur private LAN-IPs erlaubt."
            return
        }
        if (token.isBlank()) {
            _lastError.value = "Session-Token fehlt."
            return
        }

        disconnect()
        currentHostIp = cleanIp
        currentPort = port
        currentToken = token.trim().uppercase()
        syncedHashes = false
        _lastError.value = null
        _connectionStatus.value = ConnectionStatus.CONNECTING

        connectionJob = scope.launch {
            var attempts = 0
            while (attempts < 3) {
                attempts++
                try {
                    val wsUrl = "ws://$currentHostIp:$currentPort/ws?token=$currentToken&client=android"
                    client.webSocket(urlString = wsUrl) {
                        activeSession = this
                        syncedHashes = false
                        _connectionStatus.value = ConnectionStatus.CONNECTED_TO_TV
                        _lastError.value = null
                        attempts = 0

                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val msg = runCatching {
                                    json.decodeFromString<WsMessage>(frame.readText())
                                }.getOrNull() ?: continue

                                when (msg.type) {
                                    WsMessageTypes.STATE_SYNC -> {
                                        msg.state?.let { remote ->
                                            _remoteState.value = remote
                                            if (!syncedHashes) {
                                                syncedHashes = true
                                                launch { syncPendingTracks(remote.audioLibrary, forceAllUnsynced = false) }
                                            }
                                        }
                                    }
                                    WsMessageTypes.LIBRARY_UPDATED -> {
                                        val lib = msg.audioLibrary
                                        val cur = _remoteState.value
                                        if (lib != null && cur != null) {
                                            _remoteState.value = cur.copy(audioLibrary = lib)
                                        }
                                    }
                                    WsMessageTypes.ERROR -> {
                                        _lastError.value = msg.errorMessage
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    _lastError.value = "TV-Verbindung getrennt (${e.message ?: "Netzwerkfehler"})"
                } finally {
                    activeSession = null
                }

                if (_connectionStatus.value == ConnectionStatus.LOCAL_STANDALONE) break
                _connectionStatus.value = ConnectionStatus.CONNECTING
                delay(1500L)
            }
            if (_connectionStatus.value != ConnectionStatus.LOCAL_STANDALONE) {
                disconnect()
            }
        }
    }

    fun disconnect() {
        connectionJob?.cancel()
        connectionJob = null
        activeSession = null
        _remoteState.value = null
        _syncStatus.value = AudioSyncStatus()
        _connectionStatus.value = ConnectionStatus.LOCAL_STANDALONE
    }

    suspend fun sendCommand(message: WsMessage) {
        val session = activeSession ?: return
        runCatching {
            session.send(Frame.Text(json.encodeToString(message)))
        }.onFailure { err ->
            _lastError.value = err.message
        }
    }

    suspend fun uploadAudioTrack(track: CustomAudioTrack, file: File): Result<CustomAudioTrack> {
        _syncStatus.update { cur ->
            cur.copy(
                uploadingSha256s = cur.uploadingSha256s + track.sha256,
                failedSha256Errors = cur.failedSha256Errors - track.sha256
            )
        }
        return try {
            val result = uploadAudioToHost(file, track.name)
            result.onSuccess {
                audioLibrary.markSynced(track.id)
                _syncStatus.update { cur ->
                    cur.copy(failedSha256Errors = cur.failedSha256Errors - track.sha256)
                }
            }.onFailure { err ->
                _syncStatus.update { cur ->
                    cur.copy(
                        failedSha256Errors = cur.failedSha256Errors +
                            (track.sha256 to (err.message ?: "Upload fehlgeschlagen"))
                    )
                }
            }
            result
        } finally {
            _syncStatus.update { cur ->
                cur.copy(uploadingSha256s = cur.uploadingSha256s - track.sha256)
            }
        }
    }

    suspend fun uploadAudioToHost(file: File, displayName: String): Result<CustomAudioTrack> {
        if (!NetworkUtils.isPrivateLanHost(currentHostIp)) {
            return Result.failure(IllegalStateException("Keine aktive private LAN-Verbindung"))
        }
        val safeHeaderName = displayName
            .replace("\"", "")
            .replace(";", "_")
            .replace("\r", "")
            .replace("\n", "")
            .ifBlank { "track.audio" }

        return runCatching {
            val url = "http://$currentHostIp:$currentPort/api/audio/upload?token=$currentToken"
            val response = client.submitFormWithBinaryData(
                url = url,
                formData = formData {
                    append(
                        key = "file",
                        value = ChannelProvider(size = file.length()) { file.readChannel() },
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, "application/octet-stream")
                            append(
                                HttpHeaders.ContentDisposition,
                                "form-data; name=\"file\"; filename=\"$safeHeaderName\""
                            )
                        }
                    )
                }
            )
            if (!response.status.isSuccess()) {
                throw IllegalStateException("Upload fehlgeschlagen (${response.status.value}): ${response.bodyAsText()}")
            }
            json.decodeFromString<CustomAudioTrack>(response.bodyAsText())
        }
    }

    suspend fun deleteAudioOnHost(trackId: String): Result<Boolean> {
        if (!NetworkUtils.isPrivateLanHost(currentHostIp)) {
            return Result.failure(IllegalStateException("Keine aktive LAN-Verbindung zum TV"))
        }
        _syncStatus.update { cur -> cur.copy(deletingTrackIds = cur.deletingTrackIds + trackId) }
        return try {
            runCatching {
                val url = "http://$currentHostIp:$currentPort/api/audio/$trackId?token=$currentToken"
                val response = client.delete(url) {
                    header("X-Session-Token", currentToken)
                }
                if (response.status.isSuccess() || response.status == HttpStatusCode.NotFound) {
                    true
                } else {
                    throw IllegalStateException("TV lehnte Löschen ab (${response.status.value})")
                }
            }
        } finally {
            _syncStatus.update { cur -> cur.copy(deletingTrackIds = cur.deletingTrackIds - trackId) }
        }
    }

    suspend fun syncPendingToHost(forceAllUnsynced: Boolean = false) {
        val remoteLib = _remoteState.value?.audioLibrary ?: return
        syncPendingTracks(remoteLib, forceAllUnsynced)
    }

    /**
     * Automatically syncs pending offline smartphone audio tracks to the TV host
     * whenever their SHA-256 hash is not yet present on the TV.
     */
    private suspend fun syncPendingTracks(
        remoteLibrary: List<CustomAudioTrack>,
        forceAllUnsynced: Boolean
    ) {
        syncMutex.withLock {
            val remoteHashes = remoteLibrary.map { it.sha256 }.toSet()
            val localTracks = audioLibrary.tracks.value
            for (local in localTracks) {
                if (local.sha256 in remoteHashes) {
                    if (local.pendingSync) {
                        audioLibrary.markSynced(local.id)
                    }
                    continue
                }
                if (!local.pendingSync && !forceAllUnsynced) continue
                val file = audioLibrary.getTrackFile(local.id) ?: continue
                uploadAudioTrack(local, file)
            }
        }
    }

    fun close() {
        disconnect()
        client.close()
    }
}
