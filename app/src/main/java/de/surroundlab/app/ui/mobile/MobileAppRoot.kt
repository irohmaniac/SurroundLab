package de.surroundlab.app.ui.mobile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.surroundlab.app.domain.model.ConnectionStatus
import de.surroundlab.app.domain.model.DiscoveredHost
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.domain.model.TestMode
import de.surroundlab.app.ui.SurroundViewModel
import de.surroundlab.app.ui.theme.SurroundColors
import kotlinx.coroutines.delay

private enum class MobileSubOverlay {
    NONE,
    SETUP,
    AUDIO_LIBRARY,
    CONNECTION_SETTINGS
}

@Composable
fun MobileAppRoot(
    vm: SurroundViewModel,
    state: SurroundAppState,
    connectionStatus: ConnectionStatus,
    discoveredHosts: List<DiscoveredHost>,
    networkError: String?
) {
    var overlay by remember(state.setupCompleted) {
        mutableStateOf(if (!state.setupCompleted) MobileSubOverlay.SETUP else MobileSubOverlay.NONE)
    }
    val statusToast by vm.statusToast.collectAsStateWithLifecycle()
    val localAudioLibrary by vm.localAudioLibrary.collectAsStateWithLifecycle()
    val audioSyncStatus by vm.audioSyncStatus.collectAsStateWithLifecycle()
    val onboardingCompleted by vm.onboardingCompleted.collectAsStateWithLifecycle()

    LaunchedEffect(statusToast) {
        if (statusToast != null) {
            delay(3200L)
            vm.clearStatusToast()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            overlay == MobileSubOverlay.SETUP -> {
                MobileSetupScreen(
                    state = state,
                    canNavigateBack = state.setupCompleted,
                    onBack = { overlay = MobileSubOverlay.NONE },
                    onSelectSetup = { setup -> vm.selectSetup(setup, markCompleted = true) },
                    onConfirm = {
                        vm.selectSetup(state.setup, markCompleted = true)
                        overlay = MobileSubOverlay.NONE
                        vm.setMode(TestMode.HUB)
                    }
                )
            }
            overlay == MobileSubOverlay.AUDIO_LIBRARY -> {
                MobileAudioLibraryScreen(
                    state = state,
                    localLibrary = localAudioLibrary,
                    syncStatus = audioSyncStatus,
                    connectionStatus = connectionStatus,
                    onBack = { overlay = MobileSubOverlay.NONE },
                    onImportUri = { uri -> vm.importAudioFromUri(uri) },
                    onRetrySyncTrack = { track -> vm.syncAudioTrackToHost(track) },
                    onSyncAllPending = { vm.syncAllPendingTracks() },
                    onDeleteTrack = { track -> vm.deleteAudioTrack(track) }
                )
            }
            overlay == MobileSubOverlay.CONNECTION_SETTINGS -> {
                MobileConnectionSettingsScreen(
                    state = state,
                    connectionStatus = connectionStatus,
                    discoveredHosts = discoveredHosts,
                    networkError = networkError,
                    onBack = { overlay = MobileSubOverlay.NONE },
                    onRefreshDiscovery = { vm.startDiscovery() },
                    onConnectHost = { ip, port, token -> vm.connectToHost(ip, port, token) },
                    onDisconnectHost = { vm.disconnectFromHost() },
                    onMasterGainChange = { db -> vm.setMasterGainDb(db) },
                    onSwap71Change = { enabled -> vm.setSwap71SurroundBack(enabled) },
                    onSpeakerLayoutPresetChange = { preset -> vm.setSpeakerLayoutPreset(preset) },
                    onLanguageChange = { lang -> vm.setAppLanguage(lang) },
                    onOpenSpeakerSetup = { overlay = MobileSubOverlay.SETUP },
                    onOpenGuide = { vm.reopenOnboarding() }
                )
            }
            state.activeMode == TestMode.HUB -> {
                MobileHomeHubScreen(
                    state = state,
                    connectionStatus = connectionStatus,
                    onSelectMode = { mode -> vm.setMode(mode) },
                    onOpenSetup = { overlay = MobileSubOverlay.SETUP },
                    onOpenAudioLibrary = { overlay = MobileSubOverlay.AUDIO_LIBRARY },
                    onOpenConnectionSettings = { overlay = MobileSubOverlay.CONNECTION_SETTINGS }
                )
            }
            state.activeMode == TestMode.SINGLE_SPEAKER -> {
                MobileSingleSpeakerScreen(
                    state = state,
                    onBack = { vm.setMode(TestMode.HUB) },
                    onToggleSpeaker = { ch -> vm.toggleSingleSpeaker(ch) },
                    onUpdateChannelConfig = { ch, src, freq, audioId, applyAll ->
                        vm.updateSingleSpeakerConfig(ch, src, freq, audioId, applyAll)
                    }
                )
            }
            state.activeMode == TestMode.GENERATOR -> {
                MobileGeneratorScreen(
                    state = state,
                    onBack = { vm.setMode(TestMode.HUB) },
                    onUpdateGenerator = { preset, custom, sig, hz, play ->
                        vm.updateGenerator(preset, custom, sig, hz, play)
                    }
                )
            }
            state.activeMode == TestMode.PUCK_2D -> {
                MobilePuckScreen(
                    state = state,
                    onBack = { vm.setMode(TestMode.HUB) },
                    onUpdatePuck = { x, y, pan, sub, orbit, speed, src, hz, audioId, play ->
                        vm.updatePuck(x, y, pan, sub, orbit, speed, src, hz, audioId, play)
                    }
                )
            }
        }

        statusToast?.let { msg ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                containerColor = SurroundColors.SurfaceContainerHighest,
                contentColor = SurroundColors.OnSurface
            ) {
                Text(msg)
            }
        }

        if (onboardingCompleted == false) {
            MobileOnboardingDialog(
                onFinish = { vm.completeOnboarding() }
            )
        }
    }
}
