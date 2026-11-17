package de.surroundlab.app.network

import android.content.Context
import de.surroundlab.app.data.AudioLibraryRepository
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.domain.model.WsMessage
import de.surroundlab.app.domain.model.WsMessageTypes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.utils.io.jvm.javaio.toInputStream
import io.ktor.websocket.CloseReason
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

/**
 * Embedded Ktor CIO HTTP REST + WebSocket server running on the TV host (port 8080).
 *
 * Security (AGENTS.md 4.1):
 * - Enforces valid 6-char session token on all /api/ and /ws routes.
 * - Rejects uploads > 30 MB or invalid audio tracks (MediaExtractor verified).
 * - Stores uploaded audio strictly under UUID filenames via AudioLibraryRepository.
 */
class SurroundKtorServer(
    private val context: Context,
    private val audioLibrary: AudioLibraryRepository,
    private val stateProvider: () -> SurroundAppState,
    private val onCommandReceived: suspend (WsMessage) -> Unit,
    private val onLibraryChanged: suspend () -> Unit
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    data class ClientStats(
        val total: Int = 0,
        val android: Int = 0,
        val browser: Int = 0
    )

    private var scope: CoroutineScope? = null
    private var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null
    private val sessions = ConcurrentHashMap<DefaultWebSocketSession, String>()

    private val _clientStats = MutableStateFlow(ClientStats())
    val clientStats: StateFlow<ClientStats> = _clientStats.asStateFlow()

    @Volatile
    var sessionToken: String = ""
        private set

    @Volatile
    var boundPort: Int = 8080
        private set

    fun start(port: Int = 8080, token: String) {
        stop()
        sessionToken = token
        boundPort = port
        val serverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope = serverScope

        val engine = embeddedServer(CIO, port = port, host = "0.0.0.0") {
            install(ContentNegotiation) {
                json(json)
            }
            install(WebSockets) {
                pingPeriod = 15.seconds
                timeout = 30.seconds
                maxFrameSize = 1024L * 1024L
                masking = false
            }

            routing {
                // Embedded Single-Page Web App (Milestone 7)
                get("/") {
                    serveAsset(call, "web/index.html", ContentType.Text.Html)
                }
                get("/web/{assetPath...}") {
                    val relPath = call.parameters.getAll("assetPath")?.joinToString("/") ?: "index.html"
                    if (relPath.contains("..") || relPath.startsWith("/")) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@get
                    }
                    val contentType = when {
                        relPath.endsWith(".html") -> ContentType.Text.Html
                        relPath.endsWith(".css") -> ContentType.Text.CSS
                        relPath.endsWith(".js") -> ContentType.Application.JavaScript
                        relPath.endsWith(".svg") -> ContentType.Image.SVG
                        else -> ContentType.Application.OctetStream
                    }
                    serveAsset(call, "web/$relPath", contentType)
                }

                get("/api/state") {
                    if (!call.hasValidToken()) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Ungültiges Session-Token"))
                        return@get
                    }
                    call.respondText(
                        text = json.encodeToString(stateProvider()),
                        contentType = ContentType.Application.Json
                    )
                }

                get("/api/audio") {
                    if (!call.hasValidToken()) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Ungültiges Session-Token"))
                        return@get
                    }
                    call.respondText(
                        text = json.encodeToString(audioLibrary.tracks.value),
                        contentType = ContentType.Application.Json
                    )
                }

                post("/api/audio/upload") {
                    if (!call.hasValidToken()) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Ungültiges Session-Token"))
                        return@post
                    }

                    val contentLength = call.request.headers[HttpHeaders.ContentLength]?.toLongOrNull()
                    if (contentLength != null && contentLength > AudioLibraryRepository.MAX_FILE_SIZE_BYTES + 2 * 1024 * 1024L) {
                        call.respond(HttpStatusCode.PayloadTooLarge, mapOf("error" to "Maximale Dateigröße 30 MB überschritten."))
                        return@post
                    }

                    var uploadedTrackJson: String? = null
                    var errorReason: String? = null

                    try {
                        val multipart = call.receiveMultipart(
                            formFieldLimit = AudioLibraryRepository.MAX_FILE_SIZE_BYTES + 1024 * 1024L
                        )
                        multipart.forEachPart { part ->
                            try {
                                if (part is PartData.FileItem && uploadedTrackJson == null && errorReason == null) {
                                    val stream = part.provider().toInputStream()
                                    val result = stream.use { input ->
                                        audioLibrary.importFromStream(input, part.originalFileName)
                                    }
                                    result.onSuccess { track ->
                                        uploadedTrackJson = json.encodeToString(track)
                                    }.onFailure { err ->
                                        errorReason = err.message ?: "Ungültige Audiodatei"
                                    }
                                }
                            } finally {
                                part.dispose()
                            }
                        }
                    } catch (e: Exception) {
                        errorReason = e.message ?: "Upload fehlgeschlagen"
                    }

                    when {
                        uploadedTrackJson != null -> {
                            onLibraryChanged()
                            broadcastLibraryUpdated()
                            call.respondText(
                                text = uploadedTrackJson!!,
                                contentType = ContentType.Application.Json,
                                status = HttpStatusCode.Created
                            )
                        }
                        errorReason?.contains("30 MB") == true -> {
                            call.respond(HttpStatusCode.PayloadTooLarge, mapOf("error" to errorReason))
                        }
                        else -> {
                            call.respond(
                                HttpStatusCode.BadRequest,
                                mapOf("error" to (errorReason ?: "Keine Audiodatei im Request gefunden."))
                            )
                        }
                    }
                }

                delete("/api/audio/{id}") {
                    if (!call.hasValidToken()) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Ungültiges Session-Token"))
                        return@delete
                    }
                    val id = call.parameters["id"]
                    if (id.isNullOrBlank()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Fehlende Track-ID"))
                        return@delete
                    }
                    val deleted = audioLibrary.deleteTrack(id)
                    if (deleted) {
                        onLibraryChanged()
                        broadcastLibraryUpdated()
                        call.respond(HttpStatusCode.OK, mapOf("deleted" to id))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Track nicht gefunden"))
                    }
                }

                webSocket("/ws") {
                    val tokenParam = call.request.queryParameters["token"]
                        ?: call.request.headers["X-Session-Token"]
                    if (tokenParam != sessionToken) {
                        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Ungültiges Token"))
                        return@webSocket
                    }

                    val clientKind = when (call.request.queryParameters["client"]?.lowercase()) {
                        "android" -> "android"
                        else -> "browser"
                    }
                    sessions[this] = clientKind
                    updateClientStats()
                    try {
                        // Send immediate full state sync on connection
                        val syncMsg = WsMessage(
                            type = WsMessageTypes.STATE_SYNC,
                            state = stateProvider()
                        )
                        send(Frame.Text(json.encodeToString(syncMsg)))

                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val raw = frame.readText()
                                val msg = runCatching {
                                    json.decodeFromString<WsMessage>(raw)
                                }.getOrNull()
                                if (msg != null) {
                                    onCommandReceived(msg)
                                } else {
                                    val err = WsMessage(
                                        type = WsMessageTypes.ERROR,
                                        errorMessage = "Ungültiges Nachrichtenformat"
                                    )
                                    send(Frame.Text(json.encodeToString(err)))
                                }
                            }
                        }
                    } finally {
                        sessions.remove(this)
                        updateClientStats()
                    }
                }
            }
        }

        server = engine
        serverScope.launch {
            runCatching { engine.start(wait = false) }
        }
    }

    fun broadcastState(state: SurroundAppState) {
        val currentScope = scope ?: return
        if (sessions.isEmpty()) return
        val payload = json.encodeToString(
            WsMessage(
                type = WsMessageTypes.STATE_SYNC,
                state = state
            )
        )
        currentScope.launch {
            broadcastRaw(payload)
        }
    }

    private suspend fun broadcastLibraryUpdated() {
        val payload = json.encodeToString(
            WsMessage(
                type = WsMessageTypes.LIBRARY_UPDATED,
                audioLibrary = audioLibrary.tracks.value
            )
        )
        broadcastRaw(payload)
    }

    private suspend fun broadcastRaw(payload: String) {
        val dead = ArrayList<DefaultWebSocketSession>()
        for (session in sessions.keys) {
            val ok = runCatching {
                session.send(Frame.Text(payload))
            }.isSuccess
            if (!ok) {
                dead.add(session)
            }
        }
        if (dead.isNotEmpty()) {
            for (s in dead) {
                sessions.remove(s)
            }
            updateClientStats()
        }
    }

    private fun updateClientStats() {
        val values = sessions.values.toList()
        val android = values.count { it == "android" }
        val browser = values.count { it == "browser" }
        _clientStats.value = ClientStats(
            total = values.size,
            android = android,
            browser = browser
        )
    }

    private suspend fun serveAsset(call: ApplicationCall, assetPath: String, contentType: ContentType) {
        try {
            val bytes = context.assets.open(assetPath).use { it.readBytes() }
            call.respondBytes(bytes = bytes, contentType = contentType)
        } catch (_: FileNotFoundException) {
            call.respond(HttpStatusCode.NotFound, "Asset nicht gefunden: $assetPath")
        }
    }

    private fun ApplicationCall.hasValidToken(): Boolean {
        val token = request.queryParameters["token"] ?: request.headers["X-Session-Token"]
        return token == sessionToken && sessionToken.isNotEmpty()
    }

    fun stop() {
        sessions.clear()
        _clientStats.value = ClientStats()
        runCatching { server?.stop(250L, 500L) }
        server = null
        scope?.cancel()
        scope = null
    }
}
