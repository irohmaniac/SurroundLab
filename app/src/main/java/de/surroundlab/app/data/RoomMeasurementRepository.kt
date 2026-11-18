package de.surroundlab.app.data

import android.content.Context
import de.surroundlab.app.domain.model.ListeningPositionTrace
import de.surroundlab.app.domain.model.RoomMeasurementSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Persists saved room mode / frequency response measurement sessions locally as JSON.
 */
class RoomMeasurementRepository(context: Context) {

    private val file = File(context.applicationContext.filesDir, "room_measurements.json")
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
    }

    private val _sessions = MutableStateFlow<List<RoomMeasurementSession>>(emptyList())
    val sessions: StateFlow<List<RoomMeasurementSession>> = _sessions.asStateFlow()

    fun initialize() {
        scope.launch {
            mutex.withLock {
                if (!file.exists()) {
                    _sessions.value = emptyList()
                    return@withLock
                }
                val loaded = runCatching {
                    json.decodeFromString<List<RoomMeasurementSession>>(file.readText())
                }.getOrDefault(emptyList())
                _sessions.value = loaded.sortedByDescending { it.createdAtMs }
            }
        }
    }

    suspend fun saveSession(session: RoomMeasurementSession) {
        mutex.withLock {
            val updated = (_sessions.value.filterNot { it.id == session.id } + session)
                .sortedByDescending { it.createdAtMs }
            persistLocked(updated)
        }
    }

    suspend fun addPositionToSession(sessionId: String, position: ListeningPositionTrace) {
        mutex.withLock {
            val updated = _sessions.value.map { session ->
                if (session.id == sessionId) {
                    session.copy(positions = session.positions + position)
                } else session
            }
            persistLocked(updated)
        }
    }

    suspend fun updateSessionMeta(sessionId: String, name: String, note: String) {
        mutex.withLock {
            val cleanName = name.trim().ifBlank { return@withLock }
            val updated = _sessions.value.map { session ->
                if (session.id == sessionId) {
                    session.copy(name = cleanName, note = note.trim())
                } else session
            }
            persistLocked(updated)
        }
    }

    suspend fun renamePosition(sessionId: String, positionId: String, newName: String) {
        mutex.withLock {
            val clean = newName.trim().ifBlank { return@withLock }
            val updated = _sessions.value.map { session ->
                if (session.id == sessionId) {
                    session.copy(
                        positions = session.positions.map { pos ->
                            if (pos.id == positionId) pos.copy(name = clean) else pos
                        }
                    )
                } else session
            }
            persistLocked(updated)
        }
    }

    suspend fun deletePosition(sessionId: String, positionId: String) {
        mutex.withLock {
            val updated = _sessions.value.mapNotNull { session ->
                if (session.id == sessionId) {
                    val remaining = session.positions.filterNot { it.id == positionId }
                    if (remaining.isEmpty()) null else session.copy(positions = remaining)
                } else session
            }
            persistLocked(updated)
        }
    }

    suspend fun deleteSession(sessionId: String) {
        mutex.withLock {
            val updated = _sessions.value.filterNot { it.id == sessionId }
            persistLocked(updated)
        }
    }

    private fun persistLocked(list: List<RoomMeasurementSession>) {
        _sessions.value = list
        runCatching {
            file.writeText(json.encodeToString(list))
        }
    }
}
