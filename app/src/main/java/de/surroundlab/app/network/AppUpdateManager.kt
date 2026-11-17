package de.surroundlab.app.network

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import de.surroundlab.app.BuildConfig
import de.surroundlab.app.ui.i18n.GITHUB_LATEST_APK_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

sealed interface UpdateState {
    data object Idle : UpdateState
    data object Checking : UpdateState
    data class UpToDate(val currentVersion: String) : UpdateState
    data class UpdateAvailable(
        val latestVersion: String,
        val downloadUrl: String,
        val releaseNotes: String
    ) : UpdateState

    data class Downloading(val progressPercent: Int) : UpdateState
    data class ReadyToInstall(val apkFile: File, val version: String) : UpdateState
    data class PermissionRequired(val apkFile: File, val version: String) : UpdateState
    data class Error(val message: String) : UpdateState
}

@Serializable
private data class GitHubReleaseDto(
    @SerialName("tag_name") val tagName: String = "",
    val body: String = "",
    val assets: List<GitHubAssetDto> = emptyList()
)

@Serializable
private data class GitHubAssetDto(
    val name: String = "",
    @SerialName("browser_download_url") val browserDownloadUrl: String = ""
)

/**
 * Checks GitHub Releases for newer SurroundLab versions, downloads the APK with progress,
 * and launches the system package installer via FileProvider.
 */
class AppUpdateManager(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }
    private val _state = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val state: StateFlow<UpdateState> = _state.asStateFlow()

    val currentVersionClean: String
        get() = BuildConfig.VERSION_NAME.substringBefore("-")

    suspend fun checkForUpdates() {
        _state.value = UpdateState.Checking
        withContext(Dispatchers.IO) {
            runCatching {
                val conn = (URL(GITHUB_LATEST_API_URL).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 10_000
                    readTimeout = 10_000
                    setRequestProperty("Accept", "application/vnd.github+json")
                    setRequestProperty("User-Agent", "SurroundLab/${BuildConfig.VERSION_NAME}")
                }
                val code = conn.responseCode
                if (code !in 200..299) {
                    conn.disconnect()
                    throw IllegalStateException("HTTP $code")
                }
                val body = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()

                val release = json.decodeFromString<GitHubReleaseDto>(body)
                val latestTag = release.tagName.trim().removePrefix("v").removePrefix("V")
                if (latestTag.isBlank()) {
                    throw IllegalStateException("Missing release tag")
                }

                if (isNewerVersion(latestTag, currentVersionClean)) {
                    val apkAsset = release.assets.firstOrNull {
                        it.name.equals("SurroundLab.apk", ignoreCase = true)
                    } ?: release.assets.firstOrNull {
                        it.name.endsWith(".apk", ignoreCase = true)
                    }
                    val url = apkAsset?.browserDownloadUrl?.takeIf { it.isNotBlank() }
                        ?: GITHUB_LATEST_APK_URL
                    _state.value = UpdateState.UpdateAvailable(
                        latestVersion = latestTag,
                        downloadUrl = url,
                        releaseNotes = release.body.trim()
                    )
                } else {
                    _state.value = UpdateState.UpToDate(currentVersionClean)
                }
            }.onFailure { err ->
                _state.value = UpdateState.Error(err.message ?: "Network error")
            }
        }
    }

    suspend fun downloadAndInstall(downloadUrl: String, version: String) {
        _state.value = UpdateState.Downloading(0)
        withContext(Dispatchers.IO) {
            runCatching {
                val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
                updatesDir.listFiles()?.forEach { it.delete() }
                val targetFile = File(updatesDir, "SurroundLab-$version.apk")

                val conn = followRedirects(downloadUrl)
                val totalBytes = conn.contentLengthLong
                conn.inputStream.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        val buffer = ByteArray(8192)
                        var downloaded = 0L
                        var lastReported = -1
                        while (true) {
                            val read = input.read(buffer)
                            if (read <= 0) break
                            output.write(buffer, 0, read)
                            downloaded += read
                            if (totalBytes > 0L) {
                                val pct = ((downloaded * 100L) / totalBytes).toInt().coerceIn(0, 100)
                                if (pct != lastReported) {
                                    lastReported = pct
                                    _state.value = UpdateState.Downloading(pct)
                                }
                            }
                        }
                    }
                }
                conn.disconnect()
                withContext(Dispatchers.Main) {
                    launchApkInstaller(targetFile, version)
                }
            }.onFailure { err ->
                _state.value = UpdateState.Error(err.message ?: "Download failed")
            }
        }
    }

    fun launchApkInstaller(apkFile: File, version: String) {
        if (!apkFile.exists()) {
            _state.value = UpdateState.Error("APK file missing")
            return
        }
        if (!context.packageManager.canRequestPackageInstalls()) {
            _state.value = UpdateState.PermissionRequired(apkFile, version)
            val permIntent = Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            runCatching { context.startActivity(permIntent) }
            return
        }

        _state.value = UpdateState.ReadyToInstall(apkFile, version)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching {
            context.startActivity(installIntent)
        }.onFailure { err ->
            _state.value = UpdateState.Error(err.message ?: "Installer unavailable")
        }
    }

    private fun followRedirects(initialUrl: String): HttpURLConnection {
        var currentUrl = initialUrl
        repeat(6) {
            val conn = (URL(currentUrl).openConnection() as HttpURLConnection).apply {
                instanceFollowRedirects = false
                connectTimeout = 15_000
                readTimeout = 30_000
                setRequestProperty("User-Agent", "SurroundLab/${BuildConfig.VERSION_NAME}")
            }
            val code = conn.responseCode
            if (code in 300..399) {
                val location = conn.getHeaderField("Location")
                conn.disconnect()
                if (location.isNullOrBlank()) throw IllegalStateException("Empty redirect")
                currentUrl = URL(URL(currentUrl), location).toString()
            } else if (code in 200..299) {
                return conn
            } else {
                conn.disconnect()
                throw IllegalStateException("HTTP $code")
            }
        }
        throw IllegalStateException("Too many redirects")
    }

    private fun isNewerVersion(candidate: String, current: String): Boolean {
        val candParts = candidate.split('.').mapNotNull { it.filter(Char::isDigit).toIntOrNull() }
        val currParts = current.split('.').mapNotNull { it.filter(Char::isDigit).toIntOrNull() }
        val maxLen = maxOf(candParts.size, currParts.size)
        for (i in 0 until maxLen) {
            val c = candParts.getOrElse(i) { 0 }
            val cur = currParts.getOrElse(i) { 0 }
            if (c != cur) return c > cur
        }
        return false
    }

    companion object {
        private const val GITHUB_LATEST_API_URL =
            "https://api.github.com/repos/irohmaniac/SurroundLab/releases/latest"
    }
}
