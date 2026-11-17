package de.surroundlab.app.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import de.surroundlab.app.SurroundLabApp
import de.surroundlab.app.domain.model.AppLanguage
import de.surroundlab.app.domain.model.AudioSyncStatus
import de.surroundlab.app.domain.model.ConnectionStatus
import de.surroundlab.app.domain.model.CustomAudioTrack
import de.surroundlab.app.domain.model.DiscoveredHost
import de.surroundlab.app.domain.model.GeneratorPreset
import de.surroundlab.app.domain.model.OrbitSpeed
import de.surroundlab.app.domain.model.PanningMode
import de.surroundlab.app.domain.model.SignalSourceType
import de.surroundlab.app.domain.model.SpeakerChannel
import de.surroundlab.app.domain.model.SpeakerLayoutPreset
import de.surroundlab.app.domain.model.SpeakerSetup
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.domain.model.TestMode
import kotlinx.coroutines.flow.StateFlow

/**
 * MVVM ViewModel exposing reactive StateFlows and unidirectional user actions
 * to both Android TV and Android Mobile Compose UIs.
 */
class SurroundViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as SurroundLabApp).repository

    val isTvDevice: Boolean = repo.isTvDevice
    val uiState: StateFlow<SurroundAppState> = repo.uiState
    val connectionStatus: StateFlow<ConnectionStatus> = repo.connectionStatus
    val discoveredHosts: StateFlow<List<DiscoveredHost>> = repo.discoveredHosts
    val networkError: StateFlow<String?> = repo.networkError
    val statusToast: StateFlow<String?> = repo.statusToast
    val localAudioLibrary: StateFlow<List<CustomAudioTrack>> = repo.localAudioLibrary
    val audioSyncStatus: StateFlow<AudioSyncStatus> = repo.audioSyncStatus
    val onboardingCompleted: StateFlow<Boolean?> = repo.onboardingCompleted

    fun selectSetup(setup: SpeakerSetup, markCompleted: Boolean = true) =
        repo.selectSetup(setup, markCompleted)

    fun setMode(mode: TestMode) = repo.setMode(mode)

    fun setMasterGainDb(gainDb: Float) = repo.setMasterGainDb(gainDb)

    fun setSwap71SurroundBack(enabled: Boolean) = repo.setSwap71SurroundBack(enabled)

    fun setSpeakerLayoutPreset(preset: SpeakerLayoutPreset) = repo.setSpeakerLayoutPreset(preset)

    fun setTvUiScale(scale: Float) = repo.setTvUiScale(scale)

    fun setAppLanguage(language: AppLanguage) = repo.setAppLanguage(language)

    fun toggleSingleSpeaker(channel: SpeakerChannel) = repo.toggleSingleSpeaker(channel)

    fun updateSingleSpeakerConfig(
        channel: SpeakerChannel,
        sourceType: SignalSourceType,
        frequencyHz: Int,
        audioId: String?,
        applyToAll: Boolean = false
    ) = repo.updateSingleSpeakerConfig(channel, sourceType, frequencyHz, audioId, applyToAll)

    fun updateGenerator(
        preset: GeneratorPreset? = null,
        customChannels: Set<SpeakerChannel>? = null,
        signalType: SignalSourceType? = null,
        frequencyHz: Int? = null,
        playing: Boolean? = null
    ) = repo.updateGenerator(preset, customChannels, signalType, frequencyHz, playing)

    fun updatePuck(
        x: Float? = null,
        y: Float? = null,
        panningMode: PanningMode? = null,
        subwooferEnabled: Boolean? = null,
        autoOrbit: Boolean? = null,
        orbitSpeed: OrbitSpeed? = null,
        sourceType: SignalSourceType? = null,
        frequencyHz: Int? = null,
        audioId: String? = null,
        playing: Boolean? = null
    ) = repo.updatePuck(
        x, y, panningMode, subwooferEnabled, autoOrbit, orbitSpeed,
        sourceType, frequencyHz, audioId, playing
    )

    fun stopAll() = repo.stopAll()

    fun importAudioFromUri(uri: Uri) = repo.importAudioFromUri(uri)

    fun syncAudioTrackToHost(track: CustomAudioTrack) = repo.syncAudioTrackToHost(track)

    fun syncAllPendingTracks() = repo.syncAllPendingTracks()

    fun deleteAudioTrack(track: CustomAudioTrack) = repo.deleteAudioTrack(track)

    fun startDiscovery() = repo.startDiscovery()

    fun connectToHost(hostIp: String, port: Int, token: String) =
        repo.connectToHost(hostIp, port, token)

    fun disconnectFromHost() = repo.disconnectFromHost()

    fun refreshNetworkAddress() = repo.refreshNetworkAddress()

    fun clearStatusToast() = repo.clearStatusToast()

    fun completeOnboarding() = repo.completeOnboarding()

    fun reopenOnboarding() = repo.reopenOnboarding()
}
