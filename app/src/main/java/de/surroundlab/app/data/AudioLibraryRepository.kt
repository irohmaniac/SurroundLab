package de.surroundlab.app.data

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.provider.OpenableColumns
import de.surroundlab.app.domain.model.CustomAudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest
import java.util.UUID

/**
 * Manages uploaded / imported custom audio files under internal app storage:
 * `context.filesDir/custom_audio/<uuid>.audio` with a JSON metadata index.
 *
 * Security guarantees (AGENTS.md 4.1):
 * - Never uses client-provided filenames for filesystem paths (strict UUID naming).
 * - Enforces a strict 30 MB upload ceiling before persisting.
 * - Validates via MediaExtractor that at least one valid audio track exists.
 */
class AudioLibraryRepository(private val context: Context) {

    companion object {
        const val MAX_FILE_SIZE_BYTES = 30L * 1024L * 1024L
        private const val AUDIO_DIR_NAME = "custom_audio"
        private const val INDEX_FILE_NAME = "index.json"
        private val UUID_REGEX = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    }

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    private val mutex = Mutex()
    private val audioDir: File = File(context.filesDir, AUDIO_DIR_NAME).apply { mkdirs() }
    private val indexFile: File = File(audioDir, INDEX_FILE_NAME)

    private val _tracks = MutableStateFlow<List<CustomAudioTrack>>(emptyList())
    val tracks: StateFlow<List<CustomAudioTrack>> = _tracks.asStateFlow()

    suspend fun initialize() = withContext(Dispatchers.IO) {
        mutex.withLock {
            loadIndexLocked()
        }
    }

    suspend fun importFromUri(uri: Uri, pendingSync: Boolean = false): Result<CustomAudioTrack> = withContext(Dispatchers.IO) {
        runCatching {
            val rawName = resolveDisplayNameFromUri(uri) ?: "ReferenzAudio.audio"
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Audiodatei konnte nicht geöffnet werden.")
            inputStream.use { stream ->
                importFromStreamInternal(stream, rawName, pendingSync)
            }
        }
    }

    suspend fun importFromStream(inputStream: InputStream, rawFilename: String?): Result<CustomAudioTrack> =
        withContext(Dispatchers.IO) {
            runCatching {
                importFromStreamInternal(inputStream, rawFilename ?: "Upload.audio", pendingSync = false)
            }
        }

    suspend fun importFromFile(tempFile: File, rawFilename: String?): Result<CustomAudioTrack> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (!tempFile.exists() || tempFile.length() == 0L) {
                    throw IllegalArgumentException("Leere Audiodatei.")
                }
                if (tempFile.length() > MAX_FILE_SIZE_BYTES) {
                    throw IllegalArgumentException("Datei überschreitet das Limit von 30 MB.")
                }
                FileInputStream(tempFile).use { stream ->
                    importFromStreamInternal(stream, rawFilename ?: "Upload.audio", pendingSync = false)
                }
            }
        }

    private suspend fun importFromStreamInternal(
        inputStream: InputStream,
        rawFilename: String,
        pendingSync: Boolean
    ): CustomAudioTrack {
        val tempFile = File.createTempFile("sl_import_", ".tmp", context.cacheDir)
        try {
            val digest = MessageDigest.getInstance("SHA-256")
            var totalBytes = 0L
            val buffer = ByteArray(16 * 1024)
            FileOutputStream(tempFile).use { out ->
                while (true) {
                    val read = inputStream.read(buffer)
                    if (read <= 0) break
                    totalBytes += read
                    if (totalBytes > MAX_FILE_SIZE_BYTES) {
                        throw IllegalArgumentException("Datei überschreitet das Limit von 30 MB.")
                    }
                    digest.update(buffer, 0, read)
                    out.write(buffer, 0, read)
                }
            }
            if (totalBytes == 0L) {
                throw IllegalArgumentException("Leere Audiodatei.")
            }

            val sha256 = digest.digest().joinToString("") { "%02x".format(it) }
            val audioMeta = probeAudioMetadata(tempFile)
            val safeName = sanitizeDisplayName(rawFilename)

            return mutex.withLock {
                // Deduplicate by SHA-256 if already present
                val existing = _tracks.value.firstOrNull { it.sha256 == sha256 && getTrackFile(it.id)?.exists() == true }
                if (existing != null) {
                    if (pendingSync && !existing.pendingSync) {
                        val flagged = existing.copy(pendingSync = true)
                        val updated = _tracks.value.map { if (it.id == existing.id) flagged else it }
                        _tracks.value = updated
                        saveIndexLocked(updated)
                        return@withLock flagged
                    }
                    return@withLock existing
                }

                val uuid = UUID.randomUUID().toString()
                val targetFile = File(audioDir, "$uuid.audio")
                tempFile.copyTo(targetFile, overwrite = true)

                val track = CustomAudioTrack(
                    id = uuid,
                    name = safeName,
                    durationMs = audioMeta.durationMs,
                    sizeBytes = totalBytes,
                    sha256 = sha256,
                    sampleRate = audioMeta.sampleRate,
                    channels = audioMeta.channels,
                    createdAtMs = System.currentTimeMillis(),
                    pendingSync = pendingSync
                )
                val updated = listOf(track) + _tracks.value
                _tracks.value = updated
                saveIndexLocked(updated)
                track
            }
        } finally {
            tempFile.delete()
        }
    }

    suspend fun markSynced(id: String) = withContext(Dispatchers.IO) {
        mutex.withLock {
            var changed = false
            val updated = _tracks.value.map { tr ->
                if (tr.id == id && tr.pendingSync) {
                    changed = true
                    tr.copy(pendingSync = false)
                } else tr
            }
            if (changed) {
                _tracks.value = updated
                saveIndexLocked(updated)
            }
        }
    }

    suspend fun deleteTrack(id: String): Boolean = withContext(Dispatchers.IO) {
        if (!UUID_REGEX.matches(id)) return@withContext false
        mutex.withLock {
            val file = File(audioDir, "$id.audio")
            if (file.exists()) {
                file.delete()
            }
            val updated = _tracks.value.filterNot { it.id == id }
            val removed = updated.size != _tracks.value.size
            if (removed) {
                _tracks.value = updated
                saveIndexLocked(updated)
            }
            removed
        }
    }

    suspend fun deleteTrackBySha256(sha256: String): Boolean = withContext(Dispatchers.IO) {
        if (sha256.isBlank()) return@withContext false
        mutex.withLock {
            val matches = _tracks.value.filter { it.sha256 == sha256 }
            if (matches.isEmpty()) return@withContext false
            for (tr in matches) {
                val file = File(audioDir, "${tr.id}.audio")
                if (file.exists()) {
                    file.delete()
                }
            }
            val updated = _tracks.value.filterNot { it.sha256 == sha256 }
            _tracks.value = updated
            saveIndexLocked(updated)
            true
        }
    }

    fun getTrackFile(id: String): File? {
        if (!UUID_REGEX.matches(id)) return null
        val candidate = File(audioDir, "$id.audio")
        return if (candidate.canonicalPath.startsWith(audioDir.canonicalPath) && candidate.exists()) {
            candidate
        } else {
            null
        }
    }

    fun getTrackById(id: String): CustomAudioTrack? = _tracks.value.firstOrNull { it.id == id }

    private fun loadIndexLocked() {
        if (!indexFile.exists()) {
            _tracks.value = emptyList()
            return
        }
        val parsed = runCatching {
            json.decodeFromString<List<CustomAudioTrack>>(indexFile.readText())
        }.getOrElse { emptyList() }
        // Only keep tracks whose UUID file actually exists on disk
        val valid = parsed.filter { UUID_REGEX.matches(it.id) && File(audioDir, "${it.id}.audio").exists() }
        _tracks.value = valid
    }

    private fun saveIndexLocked(list: List<CustomAudioTrack>) {
        indexFile.writeText(json.encodeToString(list))
    }

    private data class ProbedAudioMetadata(
        val durationMs: Long,
        val sampleRate: Int,
        val channels: Int
    )

    private fun probeAudioMetadata(file: File): ProbedAudioMetadata {
        val extractor = MediaExtractor()
        try {
            extractor.setDataSource(file.absolutePath)
            val trackCount = extractor.trackCount
            for (i in 0 until trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME) ?: continue
                if (mime.startsWith("audio/")) {
                    val sampleRate = if (format.containsKey(MediaFormat.KEY_SAMPLE_RATE)) {
                        format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                    } else 48000
                    val channels = if (format.containsKey(MediaFormat.KEY_CHANNEL_COUNT)) {
                        format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                    } else 2
                    val durationUs = if (format.containsKey(MediaFormat.KEY_DURATION)) {
                        format.getLong(MediaFormat.KEY_DURATION)
                    } else 0L
                    return ProbedAudioMetadata(
                        durationMs = (durationUs / 1000L).coerceAtLeast(0L),
                        sampleRate = sampleRate,
                        channels = channels
                    )
                }
            }
            throw IllegalArgumentException("Keine gültige Audiospur in der Datei gefunden.")
        } catch (e: IllegalArgumentException) {
            throw e
        } catch (e: Exception) {
            throw IllegalArgumentException("Nicht unterstütztes oder defektes Audioformat.", e)
        } finally {
            extractor.release()
        }
    }

    private fun resolveDisplayNameFromUri(uri: Uri): String? {
        return runCatching {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (idx >= 0) cursor.getString(idx) else null
                    } else null
                }
        }.getOrNull()
    }

    private fun sanitizeDisplayName(raw: String): String {
        val baseName = raw.substringAfterLast('/').substringAfterLast('\\')
        val cleaned = baseName
            .replace(Regex("[\\r\\n\\t\\u0000]"), "")
            .replace(Regex("[^a-zA-Z0-9._\\- äöüÄÖÜß()]"), "_")
            .trim()
        return cleaned.ifEmpty { "Referenztrack" }.take(80)
    }
}
