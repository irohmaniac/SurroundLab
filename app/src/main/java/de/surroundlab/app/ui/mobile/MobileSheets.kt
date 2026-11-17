package de.surroundlab.app.ui.mobile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CastConnected
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SpeakerGroup
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.surroundlab.app.domain.model.AppLanguage
import de.surroundlab.app.domain.model.AudioSyncStatus
import de.surroundlab.app.domain.model.ConnectionStatus
import de.surroundlab.app.domain.model.CustomAudioTrack
import de.surroundlab.app.domain.model.DiscoveredHost
import de.surroundlab.app.domain.model.SpeakerLayoutPreset
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.domain.model.TrackSyncStatus
import de.surroundlab.app.network.UpdateState
import de.surroundlab.app.ui.components.OnboardingSlide
import de.surroundlab.app.ui.i18n.AFTVNEWS_DOWNLOADER_CODE
import de.surroundlab.app.ui.i18n.GITHUB_LATEST_APK_URL
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.i18n.PAYPAL_DONATE_URL
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors
import kotlin.math.roundToInt

private data class LibraryRowItem(
    val track: CustomAudioTrack,
    val status: TrackSyncStatus,
    val errorDetail: String? = null
)

/**
 * Custom Audio Library screen using Zero-Permission Storage Access Framework (`OpenDocument`).
 */
@Composable
fun MobileAudioLibraryScreen(
    state: SurroundAppState,
    localLibrary: List<CustomAudioTrack>,
    syncStatus: AudioSyncStatus,
    connectionStatus: ConnectionStatus,
    onBack: () -> Unit,
    onImportUri: (Uri) -> Unit,
    onRetrySyncTrack: (CustomAudioTrack) -> Unit,
    onSyncAllPending: () -> Unit,
    onDeleteTrack: (CustomAudioTrack) -> Unit
) {
    val s = LocalAppStrings.current
    BackHandler { onBack() }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            onImportUri(uri)
        }
    }

    val isConnected = connectionStatus == ConnectionStatus.CONNECTED_TO_TV
    val remoteTracks = state.audioLibrary
    val remoteHashes = remember(remoteTracks) { remoteTracks.map { it.sha256 }.toSet() }
    val unsyncedLocal = remember(localLibrary, remoteHashes, isConnected) {
        if (isConnected) localLibrary.filter { it.sha256 !in remoteHashes } else localLibrary
    }

    val displayItems: List<LibraryRowItem> = remember(
        remoteTracks,
        unsyncedLocal,
        syncStatus,
        isConnected
    ) {
        if (isConnected) {
            val remoteItems = remoteTracks.map { tr ->
                val status = when {
                    tr.id in syncStatus.deletingTrackIds -> TrackSyncStatus.DELETING
                    tr.sha256 in syncStatus.uploadingSha256s -> TrackSyncStatus.UPLOADING
                    else -> TrackSyncStatus.SYNCED_ON_TV
                }
                LibraryRowItem(tr, status)
            }
            val localOnlyItems = unsyncedLocal.map { tr ->
                val err = syncStatus.failedSha256Errors[tr.sha256]
                val status = when {
                    tr.id in syncStatus.deletingTrackIds -> TrackSyncStatus.DELETING
                    tr.sha256 in syncStatus.uploadingSha256s -> TrackSyncStatus.UPLOADING
                    err != null -> TrackSyncStatus.FAILED
                    else -> TrackSyncStatus.PENDING_LOCAL
                }
                LibraryRowItem(tr, status, err)
            }
            localOnlyItems + remoteItems
        } else {
            localLibrary.map { tr ->
                LibraryRowItem(
                    track = tr,
                    status = if (tr.pendingSync) TrackSyncStatus.PENDING_LOCAL else TrackSyncStatus.SYNCED_ON_TV
                )
            }
        }
    }

    val pendingLocalCount = if (isConnected) unsyncedLocal.size else localLibrary.count { it.pendingSync }
    val activeTransfers = syncStatus.uploadingSha256s.size + syncStatus.deletingTrackIds.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurroundColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = SurroundColors.SurfaceContainerHigh,
                        contentColor = SurroundColors.OnSurface
                    )
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = s.back)
                }
                Column {
                    Text(
                        text = s.mobileLibTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = SurroundColors.OnBackground
                    )
                    Text(
                        text = s.mobileLibSubtitle,
                        fontFamily = MonoFontFamily,
                        fontSize = 11.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurroundColors.SurfaceContainerHighest)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                val totalMb = displayItems.sumOf { it.track.sizeBytes } / (1024f * 1024f)
                Text(
                    text = s.libCountBadge(displayItems.size, "%.1f".format(totalMb)),
                    fontFamily = MonoFontFamily,
                    fontSize = 11.sp,
                    color = SurroundColors.OnSurfaceVariant
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Live Sync Status Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isConnected && pendingLocalCount == 0) SurroundColors.PrimaryContainer
                        else SurroundColors.SurfaceContainerHigh
                    )
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isConnected) SurroundColors.Primary else SurroundColors.SurfaceContainerHighest
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (activeTransfers > 0) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp,
                                color = SurroundColors.OnPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.CloudSync,
                                contentDescription = null,
                                tint = if (isConnected) SurroundColors.OnPrimary else SurroundColors.OnSurfaceVariant
                            )
                        }
                    }
                    Column {
                        Text(
                            text = when {
                                activeTransfers > 0 -> s.syncInProgressTitle
                                isConnected && pendingLocalCount == 0 -> s.syncAllDoneTitle
                                isConnected -> s.syncPendingTitle(pendingLocalCount)
                                else -> s.syncLocalTitle
                            },
                            fontWeight = FontWeight.Bold,
                            color = if (isConnected && pendingLocalCount == 0) {
                                SurroundColors.Primary
                            } else SurroundColors.OnSurface
                        )
                        Text(
                            text = when {
                                activeTransfers > 0 -> s.syncActiveSub(activeTransfers)
                                isConnected && pendingLocalCount == 0 -> s.syncAllDoneSub
                                isConnected -> s.syncPendingSub
                                else -> s.syncOfflineSub
                            },
                            fontSize = 12.sp,
                            color = SurroundColors.OnSurfaceVariant
                        )
                    }
                }

                if (isConnected && pendingLocalCount > 0 && activeTransfers == 0) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurroundColors.Primary)
                            .clickable { onSyncAllPending() }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = s.syncNowBtn,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.OnPrimary
                        )
                    }
                }
            }

            if (displayItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurroundColors.SurfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            Icons.Rounded.LibraryMusic,
                            contentDescription = null,
                            tint = SurroundColors.Primary,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = s.libEmptyTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = SurroundColors.OnSurface
                        )
                        Text(
                            text = s.libEmptyBody,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SurroundColors.OnSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayItems, key = { "${it.track.id}_${it.status}" }) { item ->
                        val track = item.track
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(SurroundColors.SurfaceContainer)
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = track.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SurroundColors.OnSurface
                                )
                                val sec = (track.durationMs / 1000L).coerceAtLeast(0L)
                                val sizeMb = track.sizeBytes / (1024f * 1024f)
                                Text(
                                    text = "${sec}s • ${track.sampleRate} Hz • ${track.channels}ch • ${"%.1f".format(sizeMb)} MB",
                                    fontFamily = MonoFontFamily,
                                    fontSize = 11.sp,
                                    color = SurroundColors.OnSurfaceVariant
                                )

                                // Clear Per-Track Sync Badge
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    when (item.status) {
                                        TrackSyncStatus.SYNCED_ON_TV -> {
                                            Icon(
                                                imageVector = Icons.Rounded.CheckCircle,
                                                contentDescription = null,
                                                tint = SurroundColors.Success,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = if (isConnected) s.statusSyncedOnTv else s.statusSavedLocally,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = SurroundColors.Success
                                            )
                                        }
                                        TrackSyncStatus.UPLOADING -> {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(12.dp),
                                                strokeWidth = 2.dp,
                                                color = SurroundColors.Primary
                                            )
                                            Text(
                                                text = s.statusUploading,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = SurroundColors.Primary
                                            )
                                        }
                                        TrackSyncStatus.PENDING_LOCAL -> {
                                            Icon(
                                                imageVector = Icons.Rounded.Schedule,
                                                contentDescription = null,
                                                tint = SurroundColors.Secondary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = if (isConnected) s.statusOnlyOnPhone else s.statusWaitingSync,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = SurroundColors.Secondary
                                            )
                                        }
                                        TrackSyncStatus.FAILED -> {
                                            Icon(
                                                imageVector = Icons.Rounded.ErrorOutline,
                                                contentDescription = null,
                                                tint = SurroundColors.Error,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = item.errorDetail ?: s.statusUploadFailed,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = SurroundColors.Error
                                            )
                                        }
                                        TrackSyncStatus.DELETING -> {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(12.dp),
                                                strokeWidth = 2.dp,
                                                color = SurroundColors.Error
                                            )
                                            Text(
                                                text = s.statusDeletingOnTv,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = SurroundColors.Error
                                            )
                                        }
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isConnected && (item.status == TrackSyncStatus.PENDING_LOCAL || item.status == TrackSyncStatus.FAILED)) {
                                    IconButton(onClick = { onRetrySyncTrack(track) }) {
                                        Icon(
                                            imageVector = Icons.Rounded.UploadFile,
                                            contentDescription = s.sendToTvDesc,
                                            tint = SurroundColors.Primary
                                        )
                                    }
                                }
                                if (item.status != TrackSyncStatus.DELETING && item.status != TrackSyncStatus.UPLOADING) {
                                    IconButton(onClick = { onDeleteTrack(track) }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Delete,
                                            contentDescription = s.deleteBtn,
                                            tint = SurroundColors.Error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Upload Button (SAF OpenDocument)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurroundColors.SurfaceContainer.copy(alpha = 0.85f))
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Button(
                onClick = { openDocumentLauncher.launch(arrayOf("audio/*")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurroundColors.Primary,
                    contentColor = SurroundColors.OnPrimary
                )
            ) {
                Icon(Icons.Rounded.UploadFile, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(s.uploadAudioBtn, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

/**
 * TV Host Discovery (mDNS), Manual IP Pairing, Master Digital Headroom, Language & Setup shortcut.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MobileConnectionSettingsScreen(
    state: SurroundAppState,
    connectionStatus: ConnectionStatus,
    discoveredHosts: List<DiscoveredHost>,
    networkError: String?,
    onBack: () -> Unit,
    onRefreshDiscovery: () -> Unit,
    onConnectHost: (String, Int, String) -> Unit,
    onDisconnectHost: () -> Unit,
    onMasterGainChange: (Float) -> Unit,
    onSwap71Change: (Boolean) -> Unit,
    onSpeakerLayoutPresetChange: (SpeakerLayoutPreset) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onOpenSpeakerSetup: () -> Unit,
    onOpenGuide: () -> Unit,
    updateState: UpdateState,
    currentVersion: String,
    onCheckForUpdates: () -> Unit,
    onDownloadUpdate: () -> Unit,
    onInstallUpdate: () -> Unit
) {
    val s = LocalAppStrings.current
    BackHandler { onBack() }

    var manualIp by remember { mutableStateOf(state.hostIp ?: "192.168.1.") }
    var manualPort by remember { mutableStateOf("8080") }
    var manualToken by remember { mutableStateOf(state.sessionToken) }
    var showTvInstallGuide by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurroundColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = SurroundColors.SurfaceContainerHigh,
                        contentColor = SurroundColors.OnSurface
                    )
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = s.back)
                }
                Column {
                    Text(
                        text = s.mobileConnTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = SurroundColors.OnBackground
                    )
                    Text(
                        text = s.mobileConnSubtitle,
                        fontFamily = MonoFontFamily,
                        fontSize = 11.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onRefreshDiscovery,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = SurroundColors.SurfaceContainerHigh,
                    contentColor = SurroundColors.Primary
                )
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = s.refreshSearchDesc)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active TV Connection Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = s.currentModeTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.OnSurface
                )
                Text(
                    text = when (connectionStatus) {
                        ConnectionStatus.CONNECTED_TO_TV -> s.connConnectedDetail(state.hostIp ?: "", state.hostPort)
                        ConnectionStatus.CONNECTING -> s.connConnectingDetail
                        else -> s.connLocalDetail
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = SurroundColors.OnSurfaceVariant
                )

                if (networkError != null) {
                    Text(
                        text = networkError,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SurroundColors.Error
                    )
                }

                if (connectionStatus == ConnectionStatus.CONNECTED_TO_TV ||
                    connectionStatus == ConnectionStatus.CONNECTING
                ) {
                    OutlinedButton(
                        onClick = onDisconnectHost,
                        modifier = Modifier.fillMaxWidth(),
                        shape = CircleShape
                    ) {
                        Text(s.disconnectTvBtn)
                    }
                }
            }

            // Discovered TV Hosts via mDNS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = s.discoveredTvsTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.OnSurface
                )

                if (discoveredHosts.isEmpty()) {
                    Text(
                        text = s.noTvsFoundText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SurroundColors.OnSurfaceVariant
                    )
                } else {
                    discoveredHosts.forEach { host ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurroundColors.PrimaryContainer)
                                .clickable { onConnectHost(host.hostIp, host.port, host.token) }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Rounded.CastConnected, contentDescription = null, tint = SurroundColors.Primary)
                                Column {
                                    Text(host.serviceName, fontWeight = FontWeight.Bold, color = SurroundColors.OnPrimaryContainer)
                                    Text(
                                        text = "${host.hostIp}:${host.port}",
                                        fontFamily = MonoFontFamily,
                                        fontSize = 12.sp,
                                        color = SurroundColors.Primary
                                    )
                                }
                            }
                            Text(s.connectBtn, fontWeight = FontWeight.Bold, color = SurroundColors.Primary)
                        }
                    }
                }
            }

            // Manual IP & Token Fallback (SPEC.md 1.1)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = s.manualConnTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.OnSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = manualIp,
                        onValueChange = { manualIp = it },
                        label = { Text(s.tvIpLabel) },
                        modifier = Modifier.weight(0.65f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = manualPort,
                        onValueChange = { manualPort = it.filter { c -> c.isDigit() } },
                        label = { Text(s.portLabel) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.35f),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = manualToken,
                    onValueChange = { manualToken = it.uppercase().take(6) },
                    label = { Text(s.sessionTokenLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Button(
                    onClick = {
                        onConnectHost(
                            manualIp,
                            manualPort.toIntOrNull() ?: 8080,
                            manualToken
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurroundColors.Primary,
                        contentColor = SurroundColors.OnPrimary
                    )
                ) {
                    Text(s.manualConnectBtn, fontWeight = FontWeight.Bold)
                }
            }

            // Master Headroom Slider (-30 dB to 0 dB, default -12 dB)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = s.masterLevelTitleMobile,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.OnSurface
                    )
                    Text(
                        text = "${state.masterGainDb.roundToInt()} dBFS",
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.Primary
                    )
                }
                Slider(
                    value = state.masterGainDb,
                    onValueChange = { onMasterGainChange(it) },
                    valueRange = -30f..0f,
                    colors = SliderDefaults.colors(
                        thumbColor = SurroundColors.Primary,
                        activeTrackColor = SurroundColors.Primary
                    )
                )
            }

            // 7.1 Surround / Back HDMI-HAL Compatibility Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = s.swap71Title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.OnSurface
                    )
                    Text(
                        text = s.swap71DescMobile,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }
                Switch(
                    checked = state.swap71SurroundBack,
                    onCheckedChange = { onSwap71Change(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SurroundColors.OnPrimary,
                        checkedTrackColor = SurroundColors.Primary
                    )
                )
            }

            // Speaker Layout Angle Presets (Virtuelles Objekt)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = s.layoutAnglesTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.OnSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SpeakerLayoutPreset.entries.forEach { preset ->
                        val selected = state.speakerLayoutPreset == preset
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (selected) SurroundColors.PrimaryContainer
                                    else SurroundColors.SurfaceContainerHigh
                                )
                                .clickable { onSpeakerLayoutPresetChange(preset) }
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = s.layoutPresetShort(preset),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) SurroundColors.OnPrimaryContainer else SurroundColors.OnSurfaceVariant
                            )
                        }
                    }
                }
                Text(
                    text = s.layoutPresetSummary(state.speakerLayoutPreset),
                    fontFamily = MonoFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SurroundColors.Primary
                )
                Text(
                    text = s.layoutPresetDetail(state.speakerLayoutPreset),
                    style = MaterialTheme.typography.bodySmall,
                    color = SurroundColors.OnSurfaceVariant
                )
            }

            // Language Selector (AUTO + 6 Idiomatic Languages)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = s.languageSectionTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.OnSurface
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        val selected = state.appLanguage == lang
                        val label = if (lang == AppLanguage.AUTO) {
                            s.languageAutoHint(AppLanguage.AUTO.resolve().nativeName)
                        } else {
                            lang.nativeName
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (selected) SurroundColors.PrimaryContainer
                                    else SurroundColors.SurfaceContainerHigh
                                )
                                .clickable { onLanguageChange(lang) }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) SurroundColors.OnPrimaryContainer else SurroundColors.OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            // In-App GitHub Update Checker & Installer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SystemUpdate,
                        contentDescription = null,
                        tint = SurroundColors.Primary
                    )
                    Column {
                        Text(
                            text = s.updateSectionTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.OnSurface
                        )
                        Text(
                            text = s.updateCurrentVersion(currentVersion),
                            fontFamily = MonoFontFamily,
                            fontSize = 12.sp,
                            color = SurroundColors.OnSurfaceVariant
                        )
                    }
                }

                when (updateState) {
                    UpdateState.Idle -> Unit
                    UpdateState.Checking -> {
                        Text(
                            text = s.updateCheckingText,
                            style = MaterialTheme.typography.bodySmall,
                            color = SurroundColors.Primary
                        )
                    }
                    is UpdateState.UpToDate -> {
                        Text(
                            text = s.updateUpToDateText(updateState.currentVersion),
                            style = MaterialTheme.typography.bodySmall,
                            color = SurroundColors.Primary
                        )
                    }
                    is UpdateState.UpdateAvailable -> {
                        Text(
                            text = s.updateAvailableText(updateState.latestVersion),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.Primary
                        )
                    }
                    is UpdateState.Downloading -> {
                        Text(
                            text = s.updateDownloadingText(updateState.progressPercent),
                            fontFamily = MonoFontFamily,
                            fontSize = 12.sp,
                            color = SurroundColors.Primary
                        )
                        LinearProgressIndicator(
                            progress = { updateState.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = SurroundColors.Primary,
                            trackColor = SurroundColors.SurfaceContainerHigh
                        )
                    }
                    is UpdateState.ReadyToInstall -> {
                        Text(
                            text = s.updateAvailableText(updateState.version),
                            style = MaterialTheme.typography.bodySmall,
                            color = SurroundColors.Primary
                        )
                    }
                    is UpdateState.PermissionRequired -> {
                        Text(
                            text = s.updatePermissionHint,
                            style = MaterialTheme.typography.bodySmall,
                            color = SurroundColors.Tertiary
                        )
                    }
                    is UpdateState.Error -> {
                        Text(
                            text = s.updateErrorText(updateState.message),
                            style = MaterialTheme.typography.bodySmall,
                            color = SurroundColors.Error
                        )
                    }
                }

                when (updateState) {
                    is UpdateState.UpdateAvailable -> {
                        Button(
                            onClick = onDownloadUpdate,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurroundColors.Primary,
                                contentColor = SurroundColors.OnPrimary
                            )
                        ) {
                            Text(s.updateDownloadInstallBtn, fontWeight = FontWeight.Bold)
                        }
                    }
                    is UpdateState.ReadyToInstall, is UpdateState.PermissionRequired -> {
                        Button(
                            onClick = onInstallUpdate,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurroundColors.Primary,
                                contentColor = SurroundColors.OnPrimary
                            )
                        ) {
                            Text(s.updateInstallNowBtn, fontWeight = FontWeight.Bold)
                        }
                    }
                    UpdateState.Checking, is UpdateState.Downloading -> Unit
                    else -> {
                        OutlinedButton(
                            onClick = onCheckForUpdates,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = CircleShape
                        ) {
                            Text(s.updateCheckBtn, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Button(
                onClick = onOpenSpeakerSetup,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurroundColors.SurfaceContainerHigh,
                    contentColor = SurroundColors.OnSurface
                )
            ) {
                Icon(Icons.Rounded.SpeakerGroup, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(s.setupChangeFullBtn(state.setup.code), fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { showTvInstallGuide = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurroundColors.PrimaryContainer,
                    contentColor = SurroundColors.OnPrimaryContainer
                )
            ) {
                Icon(Icons.Rounded.Tv, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(s.tvInstallMenuBtn, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    onBack()
                    onOpenGuide()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = CircleShape
            ) {
                Text(s.guideShowBtn, fontWeight = FontWeight.SemiBold)
            }

            val context = LocalContext.current
            OutlinedButton(
                onClick = { openPayPalDonate(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = SurroundColors.Tertiary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(s.donatePaypalBtn, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (showTvInstallGuide) {
        MobileTvInstallGuideDialog(
            onDismiss = { showTvInstallGuide = false }
        )
    }
}

/**
 * Interactive 4-slide mobile onboarding walkthrough explaining overview, TV installation via Downloader,
 * control modes, auto-discovery, QR browser fallback, and 7.1 HDMI channel swap tips.
 */
@Composable
fun MobileOnboardingDialog(
    onFinish: () -> Unit
) {
    val s = LocalAppStrings.current
    var step by remember { mutableIntStateOf(0) }
    val slides = remember(s) {
        val tvInstallSlide = OnboardingSlide(
            badge = s.tvInstallBadge,
            title = s.tvInstallTitle,
            subtitle = s.tvInstallSubtitle,
            icon = Icons.Rounded.Tv,
            bullets = s.tvInstallSteps
        )
        listOf(
            s.onboardingSlides[0],
            tvInstallSlide,
            s.onboardingSlides[1],
            s.onboardingSlides[2]
        )
    }
    val current = slides[step]
    val isLast = step == slides.lastIndex
    val isTvInstallSlide = step == 1

    Dialog(
        onDismissRequest = onFinish,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurroundColors.Background.copy(alpha = 0.92f))
                .padding(horizontal = 20.dp, vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(SurroundColors.SurfaceContainerHigh)
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = current.badge,
                        fontFamily = MonoFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.Primary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        slides.indices.forEach { idx ->
                            Box(
                                modifier = Modifier
                                    .size(width = if (idx == step) 20.dp else 8.dp, height = 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (idx == step) SurroundColors.Primary
                                        else SurroundColors.SurfaceContainerHighest
                                    )
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurroundColors.PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = current.icon,
                            contentDescription = null,
                            tint = SurroundColors.OnPrimaryContainer,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column {
                        Text(
                            text = current.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.OnSurface
                        )
                        Text(
                            text = current.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = SurroundColors.OnSurfaceVariant
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    current.bullets.forEach { (headline, desc) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurroundColors.SurfaceContainer)
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = headline,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SurroundColors.Primary
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = SurroundColors.OnSurfaceVariant
                            )
                        }
                    }

                    if (isTvInstallSlide) {
                        TvInstallDownloadLinkCard()
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (step > 0) {
                        OutlinedButton(
                            onClick = { step-- },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = CircleShape
                        ) {
                            Text(s.onboardingSkipToNext, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Button(
                        onClick = { if (isLast) onFinish() else step++ },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurroundColors.Primary,
                            contentColor = SurroundColors.OnPrimary
                        )
                    ) {
                        Text(if (isLast) s.onboardingFinish else s.onboardingNext, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Reusable card showing the direct GitHub Release APK link for Downloader by AFTVnews
 * with a one-tap copy button.
 */
@Composable
private fun TvInstallDownloadLinkCard() {
    val s = LocalAppStrings.current
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurroundColors.PrimaryContainer.copy(alpha = 0.35f))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AFTVnews Code",
                style = MaterialTheme.typography.labelMedium,
                color = SurroundColors.OnSurfaceVariant
            )
            Text(
                text = AFTVNEWS_DOWNLOADER_CODE,
                fontFamily = MonoFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SurroundColors.Primary
            )
        }
        Text(
            text = GITHUB_LATEST_APK_URL,
            fontFamily = MonoFontFamily,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = SurroundColors.OnSurfaceVariant
        )
        OutlinedButton(
            onClick = {
                clipboard.setText(AnnotatedString(GITHUB_LATEST_APK_URL))
                Toast.makeText(context, s.tvInstallUrlCopiedToast, Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            shape = CircleShape
        ) {
            Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(s.tvInstallCopyUrlBtn, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Standalone TV installation guide dialog reachable from Mobile Settings at any time.
 */
@Composable
fun MobileTvInstallGuideDialog(
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurroundColors.Background.copy(alpha = 0.92f))
                .padding(horizontal = 20.dp, vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(SurroundColors.SurfaceContainerHigh)
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = s.tvInstallBadge,
                    fontFamily = MonoFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.Primary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurroundColors.PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tv,
                            contentDescription = null,
                            tint = SurroundColors.OnPrimaryContainer,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column {
                        Text(
                            text = s.tvInstallTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.OnSurface
                        )
                        Text(
                            text = s.tvInstallSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = SurroundColors.OnSurfaceVariant
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    s.tvInstallSteps.forEach { (headline, desc) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurroundColors.SurfaceContainer)
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = headline,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SurroundColors.Primary
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = SurroundColors.OnSurfaceVariant
                            )
                        }
                    }

                    TvInstallDownloadLinkCard()
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurroundColors.Primary,
                        contentColor = SurroundColors.OnPrimary
                    )
                ) {
                    Text(s.tvInstallCloseBtn, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Opens the developer's PayPal.Me donation page safely in the device browser.
 */
internal fun openPayPalDonate(context: Context) {
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PAYPAL_DONATE_URL)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
