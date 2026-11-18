package de.surroundlab.app.ui.tv

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.surroundlab.app.domain.model.TestMode
import de.surroundlab.app.ui.SurroundViewModel

@Composable
fun TvAppRoot(
    vm: SurroundViewModel,
    state: de.surroundlab.app.domain.model.SurroundAppState
) {
    var showSetupScreen by remember(state.setupCompleted) { mutableStateOf(!state.setupCompleted) }
    var showQrDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showLibraryDialog by remember { mutableStateOf(false) }
    val onboardingCompleted by vm.onboardingCompleted.collectAsStateWithLifecycle()
    val updateState by vm.updateState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            showSetupScreen -> {
                TvSetupScreen(
                    state = state,
                    onSelectSetup = { setup -> vm.selectSetup(setup, markCompleted = true) },
                    onContinueToHub = {
                        vm.selectSetup(state.setup, markCompleted = true)
                        showSetupScreen = false
                        vm.setMode(TestMode.HUB)
                    },
                    onOpenQrDialog = { showQrDialog = true }
                )
            }
            state.activeMode == TestMode.HUB -> {
                TvHomeHubScreen(
                    state = state,
                    onSelectMode = { mode -> vm.setMode(mode) },
                    onOpenSetup = { showSetupScreen = true },
                    onOpenQrDialog = { showQrDialog = true },
                    onOpenAudioLibrary = { showLibraryDialog = true },
                    onOpenSettings = { showSettingsDialog = true }
                )
            }
            state.activeMode == TestMode.SINGLE_SPEAKER -> {
                TvSingleSpeakerScreen(
                    state = state,
                    onBack = { vm.setMode(TestMode.HUB) },
                    onToggleSpeaker = { ch -> vm.toggleSingleSpeaker(ch) },
                    onUpdateChannelConfig = { ch, src, freq, audioId, applyAll ->
                        vm.updateSingleSpeakerConfig(ch, src, freq, audioId, applyAll)
                    }
                )
            }
            state.activeMode == TestMode.GENERATOR -> {
                TvGeneratorScreen(
                    state = state,
                    onBack = { vm.setMode(TestMode.HUB) },
                    onUpdateGenerator = { preset, custom, sig, hz, play ->
                        vm.updateGenerator(preset, custom, sig, hz, play)
                    }
                )
            }
            state.activeMode == TestMode.PUCK_2D -> {
                TvPuckScreen(
                    state = state,
                    onBack = { vm.setMode(TestMode.HUB) },
                    onUpdatePuck = { x, y, pan, sub, orbit, speed, src, hz, audioId, play ->
                        vm.updatePuck(x, y, pan, sub, orbit, speed, src, hz, audioId, play)
                    }
                )
            }
            state.activeMode == TestMode.ROOM_MEASUREMENT -> {
                TvRoomMeasurementScreen(
                    state = state,
                    onOpenQrDialog = { showQrDialog = true },
                    onBack = { vm.setMode(TestMode.HUB) }
                )
            }
        }

        if (showQrDialog) {
            TvQrPairingDialog(
                state = state,
                onDismiss = { showQrDialog = false }
            )
        }

        if (showSettingsDialog) {
            TvSettingsDialog(
                state = state,
                onMasterGainChange = { db -> vm.setMasterGainDb(db) },
                onLfeLowPassChange = { hz -> vm.setLfeLowPassHz(hz) },
                onSwap71Change = { enabled -> vm.setSwap71SurroundBack(enabled) },
                onSpeakerLayoutPresetChange = { preset -> vm.setSpeakerLayoutPreset(preset) },
                onTvUiScaleChange = { scale -> vm.setTvUiScale(scale) },
                onLanguageChange = { lang -> vm.setAppLanguage(lang) },
                onOpenSpeakerSetup = { showSetupScreen = true },
                onRefreshNetwork = { vm.refreshNetworkAddress() },
                onOpenGuide = { vm.reopenOnboarding() },
                updateState = updateState,
                currentVersion = vm.currentAppVersion,
                onCheckForUpdates = { vm.checkForUpdates() },
                onDownloadUpdate = { vm.downloadAndInstallUpdate() },
                onInstallUpdate = { vm.installDownloadedUpdate() },
                onDismiss = { showSettingsDialog = false }
            )
        }

        if (showLibraryDialog) {
            TvAudioLibraryDialog(
                tracks = state.audioLibrary,
                onDeleteTrack = { track -> vm.deleteAudioTrack(track) },
                onDismiss = { showLibraryDialog = false }
            )
        }

        if (onboardingCompleted == false) {
            TvOnboardingDialog(
                onFinish = { vm.completeOnboarding() }
            )
        }
    }
}
