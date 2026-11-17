package de.surroundlab.app.data

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import de.surroundlab.app.audio.AudioDeviceInspector
import de.surroundlab.app.audio.DecodedAudioCache
import de.surroundlab.app.audio.SpatialPanner
import de.surroundlab.app.audio.SurroundAudioEngine
import de.surroundlab.app.domain.model.AppLanguage
import de.surroundlab.app.domain.model.AudioSyncStatus
import de.surroundlab.app.domain.model.ConnectionStatus
import de.surroundlab.app.domain.model.CustomAudioTrack
import de.surroundlab.app.domain.model.DiscoveredHost
import de.surroundlab.app.domain.model.GeneratorPreset
import de.surroundlab.app.domain.model.OrbitSpeed
import de.surroundlab.app.domain.model.PanningMode
import de.surroundlab.app.domain.model.SignalSourceType
import de.surroundlab.app.domain.model.SingleSpeakerChannelConfig
import de.surroundlab.app.domain.model.SpeakerChannel
import de.surroundlab.app.domain.model.SpeakerLayoutPreset
import de.surroundlab.app.domain.model.SpeakerSetup
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.domain.model.TestMode
import de.surroundlab.app.domain.model.WsMessage
import de.surroundlab.app.domain.model.WsMessageTypes
import de.surroundlab.app.network.NetworkUtils
import de.surroundlab.app.network.NsdDiscoveryManager
import de.surroundlab.app.network.SurroundKtorClient
import de.surroundlab.app.network.SurroundKtorServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.cos
import kotlin.math.sin

/**
 * Central Single Source of Truth repository coordinating:
 * - Local DataStore settings & UUID audio library
 * - Real-time DSP audio engine (TV multichannel OR Mobile 2.0 stereo standalone)
 * - Embedded Ktor server & NSD host registration (TV)
 * - Ktor WebSocket client & NSD discovery (Mobile)
 */
class SurroundRepository(context: Context) {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val stateMutex = Mutex()

    val isTvDevice: Boolean = run {
        val umm = appContext.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        umm.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
    }

    private val prefsRepo = PreferencesRepository(appContext)
    val audioLibrary = AudioLibraryRepository(appContext)
    private val decodedCache = DecodedAudioCache()
    private val audioEngine = SurroundAudioEngine(
        context = appContext,
        forceStereoDownmix = !isTvDevice
    )
    private val nsdManager = NsdDiscoveryManager(appContext)
    private val ktorClient = SurroundKtorClient(audioLibrary)

    private val ktorServer = SurroundKtorServer(
        context = appContext,
        audioLibrary = audioLibrary,
        stateProvider = { _localState.value },
        onCommandReceived = { msg -> applyCommand(msg) },
        onLibraryChanged = {
            updateLocalState { state ->
                val tracks = audioLibrary.tracks.value
                val validIds = tracks.map { it.id }.toSet()
                val cleanedConfigs = state.singleSpeaker.channelConfigs.mapValues { (_, cfg) ->
                    if (cfg.audioId != null && cfg.audioId !in validIds) {
                        cfg.copy(audioId = tracks.firstOrNull()?.id)
                    } else cfg
                }
                val activeSingle = state.singleSpeaker.activeChannel
                val stopSingle = activeSingle != null &&
                    state.singleSpeaker.configFor(activeSingle).let { cfg ->
                        cfg.sourceType == SignalSourceType.CUSTOM_AUDIO &&
                            cfg.audioId != null && cfg.audioId !in validIds
                    }
                val puckDeleted = state.puck.audioId != null && state.puck.audioId !in validIds
                state.copy(
                    audioLibrary = tracks,
                    singleSpeaker = state.singleSpeaker.copy(
                        playing = if (stopSingle) false else state.singleSpeaker.playing,
                        channelConfigs = cleanedConfigs
                    ),
                    puck = state.puck.copy(
                        audioId = if (puckDeleted) tracks.firstOrNull()?.id else state.puck.audioId,
                        playing = if (puckDeleted && state.puck.sourceType == SignalSourceType.CUSTOM_AUDIO) {
                            false
                        } else state.puck.playing
                    )
                )
            }
        }
    )

    private val _localState = MutableStateFlow(SurroundAppState())
    private val _uiState = MutableStateFlow(SurroundAppState())
    val uiState: StateFlow<SurroundAppState> = _uiState.asStateFlow()

    private val _connectionStatus = MutableStateFlow(
        if (isTvDevice) ConnectionStatus.TV_HOST_ACTIVE else ConnectionStatus.LOCAL_STANDALONE
    )
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    val discoveredHosts: StateFlow<List<DiscoveredHost>> = nsdManager.discoveredHosts
    val networkError: StateFlow<String?> = ktorClient.lastError
    val localAudioLibrary: StateFlow<List<CustomAudioTrack>> = audioLibrary.tracks
    val audioSyncStatus: StateFlow<AudioSyncStatus> = ktorClient.syncStatus

    private val _statusToast = MutableStateFlow<String?>(null)
    val statusToast: StateFlow<String?> = _statusToast.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow<Boolean?>(null)
    val onboardingCompleted: StateFlow<Boolean?> = _onboardingCompleted.asStateFlow()

    private var hostStarted = false
    private var orbitJob: Job? = null
    private var orbitAngle = 0f
    private var decodeJob: Job? = null
    private var activeDecodeTrackId: String? = null

    init {
        audioEngine.start()
        scope.launch {
            audioLibrary.initialize()
            val settings = prefsRepo.getSnapshot()
            _onboardingCompleted.value = settings.onboardingCompleted
            val hdmi = AudioDeviceInspector.inspectOutputs(appContext)
            val defaultSwap71 = settings.swap71SurroundBack ?: AudioDeviceInspector.isAmlogicPlatform()
            val initialGains = computePuckGainsMap(
                setup = settings.setup,
                x = 0.0f,
                y = 0.55f,
                mode = PanningMode.SWEET_SPOT_VBAP,
                subwoofer = true,
                layoutPreset = settings.speakerLayoutPreset
            )
            updateLocalState { cur ->
                cur.copy(
                    setup = settings.setup,
                    setupCompleted = settings.setupCompleted,
                    masterGainDb = settings.masterGainDb,
                    swap71SurroundBack = defaultSwap71,
                    speakerLayoutPreset = settings.speakerLayoutPreset,
                    tvUiScale = settings.tvUiScale,
                    appLanguage = settings.appLanguage,
                    audioLibrary = audioLibrary.tracks.value,
                    hostName = Build.MODEL.ifBlank { "SurroundLab TV" },
                    hdmiAudioInfo = hdmi,
                    puck = cur.puck.copy(channelGains = initialGains)
                )
            }
            if (isTvDevice) {
                ensureHostStarted()
            } else {
                nsdManager.startDiscovery()
            }
        }

        // Observe connection + remote state to keep UI StateFlow synchronized
        scope.launch {
            combine(
                _localState,
                ktorClient.connectionStatus,
                ktorClient.remoteState,
                ktorServer.clientStats
            ) { local, clientConn, remote, serverStats ->
                if (isTvDevice) {
                    _connectionStatus.value = ConnectionStatus.TV_HOST_ACTIVE
                    local.copy(
                        connectedClients = serverStats.total,
                        connectedAndroidClients = serverStats.android,
                        connectedBrowserClients = serverStats.browser
                    )
                } else {
                    _connectionStatus.value = clientConn
                    if (clientConn == ConnectionStatus.CONNECTED_TO_TV && remote != null) {
                        // Remote TV is the Single Source of Truth -> silence local smartphone audio
                        audioEngine.applyState(remote.copy(activeMode = TestMode.HUB), null)
                        remote
                    } else {
                        local
                    }
                }
            }.collect { resolved ->
                _uiState.value = resolved
            }
        }

        // Auto-reconnect on mobile if a matching TV host appears via mDNS & matches last host
        scope.launch {
            if (!isTvDevice) {
                nsdManager.discoveredHosts.collect { hosts ->
                    if (_connectionStatus.value == ConnectionStatus.LOCAL_STANDALONE && hosts.size == 1) {
                        val host = hosts.first()
                        val last = prefsRepo.getSnapshot()
                        if (last.lastHostIp == host.hostIp || last.lastHostIp.isBlank()) {
                            if (host.token.isNotBlank()) {
                                connectToHost(host.hostIp, host.port, host.token)
                            }
                        }
                    }
                }
            }
        }
    }

    fun ensureHostStarted() {
        if (!isTvDevice || hostStarted) return
        hostStarted = true
        val token = NetworkUtils.generateSessionToken()
        val ip = NetworkUtils.getLocalIpv4Address(appContext)
        val port = 8080
        ktorServer.start(port = port, token = token)
        nsdManager.registerHostService(port = port, token = token)
        scope.launch {
            updateLocalState { cur ->
                cur.copy(
                    hostIp = ip,
                    hostPort = port,
                    sessionToken = token
                )
            }
        }
    }

    fun refreshNetworkAddress() {
        if (!isTvDevice) return
        val ip = NetworkUtils.getLocalIpv4Address(appContext)
        val hdmi = AudioDeviceInspector.inspectOutputs(appContext)
        scope.launch {
            updateLocalState { cur ->
                cur.copy(hostIp = ip, hdmiAudioInfo = hdmi)
            }
        }
    }

    fun startDiscovery() {
        if (!isTvDevice) {
            nsdManager.startDiscovery()
        }
    }

    fun connectToHost(hostIp: String, port: Int, token: String) {
        if (isTvDevice) return
        scope.launch {
            // Stop any local playback when handing control over to TV host
            stopAll()
            prefsRepo.saveLastConnection(hostIp.trim(), port, token.trim().uppercase())
            ktorClient.connect(hostIp, port, token)
        }
    }

    fun disconnectFromHost() {
        if (isTvDevice) return
        ktorClient.disconnect()
    }

    fun clearStatusToast() {
        _statusToast.value = null
    }

    fun completeOnboarding() {
        _onboardingCompleted.value = true
        scope.launch {
            prefsRepo.saveOnboardingCompleted(true)
        }
    }

    fun reopenOnboarding() {
        _onboardingCompleted.value = false
    }

    // ---- User Actions (routed either to TV host via WS or applied locally) ----

    fun selectSetup(setup: SpeakerSetup, markCompleted: Boolean = true) {
        scope.launch {
            if (!isTvDevice && isConnectedToRemote()) {
                ktorClient.sendCommand(WsMessage(type = WsMessageTypes.SET_SETUP, setup = setup.code))
                // Also mark setup completed locally on phone
                prefsRepo.saveSetup(setup, markCompleted)
            } else {
                prefsRepo.saveSetup(setup, markCompleted)
                applyCommand(WsMessage(type = WsMessageTypes.SET_SETUP, setup = setup.code))
            }
        }
    }

    fun setMode(mode: TestMode) {
        dispatchCommand(WsMessage(type = WsMessageTypes.SET_MODE, mode = mode.name))
    }

    fun setMasterGainDb(gainDb: Float) {
        val clamped = gainDb.coerceIn(-30f, 0f)
        scope.launch {
            if (isTvDevice || !isConnectedToRemote()) {
                prefsRepo.saveMasterGainDb(clamped)
            }
        }
        dispatchCommand(WsMessage(type = WsMessageTypes.SET_MASTER_GAIN, masterGainDb = clamped))
    }

    fun setSwap71SurroundBack(enabled: Boolean) {
        scope.launch {
            if (isTvDevice || !isConnectedToRemote()) {
                prefsRepo.saveSwap71SurroundBack(enabled)
            }
        }
        dispatchCommand(
            WsMessage(
                type = WsMessageTypes.SET_SWAP_71_SURROUND_BACK,
                swap71SurroundBack = enabled
            )
        )
    }

    fun setSpeakerLayoutPreset(preset: SpeakerLayoutPreset) {
        scope.launch {
            if (isTvDevice || !isConnectedToRemote()) {
                prefsRepo.saveSpeakerLayoutPreset(preset)
            }
        }
        dispatchCommand(
            WsMessage(
                type = WsMessageTypes.SET_SPEAKER_LAYOUT_PRESET,
                speakerLayoutPreset = preset.name
            )
        )
    }

    fun setTvUiScale(scale: Float) {
        if (!isTvDevice) return
        val clamped = scale.coerceIn(0.60f, 1.15f)
        scope.launch {
            prefsRepo.saveTvUiScale(clamped)
            updateLocalState { it.copy(tvUiScale = clamped) }
        }
    }

    fun setAppLanguage(language: AppLanguage) {
        scope.launch {
            if (isTvDevice || !isConnectedToRemote()) {
                prefsRepo.saveAppLanguage(language)
            }
        }
        dispatchCommand(
            WsMessage(
                type = WsMessageTypes.SET_LANGUAGE,
                appLanguage = language.code
            )
        )
    }

    fun toggleSingleSpeaker(channel: SpeakerChannel) {
        val cur = _uiState.value.singleSpeaker
        val willPlay = !(cur.playing && cur.activeChannel == channel)
        val cfg = cur.configFor(channel)
        dispatchCommand(
            WsMessage(
                type = WsMessageTypes.PLAY_SINGLE_SPEAKER,
                channelId = channel.name,
                playing = willPlay,
                sourceType = cfg.sourceType.name,
                frequencyHz = cfg.frequencyHz,
                audioId = cfg.audioId
            )
        )
    }

    fun updateSingleSpeakerConfig(
        channel: SpeakerChannel,
        sourceType: SignalSourceType,
        frequencyHz: Int,
        audioId: String?,
        applyToAll: Boolean = false
    ) {
        dispatchCommand(
            WsMessage(
                type = WsMessageTypes.UPDATE_CHANNEL_CONFIG,
                channelId = channel.name,
                sourceType = sourceType.name,
                frequencyHz = frequencyHz.coerceIn(20, 20000),
                audioId = audioId,
                applyToAll = applyToAll
            )
        )
    }

    fun updateGenerator(
        preset: GeneratorPreset? = null,
        customChannels: Set<SpeakerChannel>? = null,
        signalType: SignalSourceType? = null,
        frequencyHz: Int? = null,
        playing: Boolean? = null
    ) {
        dispatchCommand(
            WsMessage(
                type = WsMessageTypes.UPDATE_GENERATOR,
                preset = preset?.name,
                customChannels = customChannels?.map { it.name },
                signalType = signalType?.name,
                frequencyHz = frequencyHz?.coerceIn(20, 20000),
                playing = playing
            )
        )
    }

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
    ) {
        dispatchCommand(
            WsMessage(
                type = WsMessageTypes.UPDATE_PUCK,
                x = x?.coerceIn(-1f, 1f),
                y = y?.coerceIn(-1f, 1f),
                panningMode = panningMode?.name,
                subwooferEnabled = subwooferEnabled,
                autoOrbit = autoOrbit,
                orbitSpeed = orbitSpeed?.name,
                sourceType = sourceType?.name,
                frequencyHz = frequencyHz?.coerceIn(20, 20000),
                audioId = audioId,
                playing = playing
            )
        )
    }

    fun stopAll() {
        dispatchCommand(WsMessage(type = WsMessageTypes.STOP_ALL))
    }

    fun importAudioFromUri(uri: Uri) {
        scope.launch {
            val result = audioLibrary.importFromUri(uri, pendingSync = !isTvDevice)
            result.onSuccess { track ->
                if (!isTvDevice && isConnectedToRemote()) {
                    val file = audioLibrary.getTrackFile(track.id)
                    if (file != null) {
                        val remoteRes = ktorClient.uploadAudioTrack(track, file)
                        remoteRes.onSuccess {
                            _statusToast.value = "Auf TV synchronisiert: ${track.name}"
                        }.onFailure { err ->
                            _statusToast.value = "Lokal gespeichert, TV-Upload fehlgeschlagen: ${err.message}"
                        }
                    } else {
                        _statusToast.value = "Lokale Audiodatei nicht gefunden."
                    }
                } else {
                    updateLocalState { it.copy(audioLibrary = audioLibrary.tracks.value) }
                    _statusToast.value = if (isTvDevice) {
                        "Hinzugefügt: ${track.name}"
                    } else {
                        "Lokal gespeichert (sync beim TV-Connect): ${track.name}"
                    }
                }
            }.onFailure { err ->
                _statusToast.value = err.message ?: "Audiodatei konnte nicht importiert werden."
            }
        }
    }

    fun syncAudioTrackToHost(track: CustomAudioTrack) {
        if (isTvDevice || !isConnectedToRemote()) return
        scope.launch {
            val file = audioLibrary.getTrackFile(track.id)
            if (file == null) {
                _statusToast.value = "Lokale Datei fehlt für: ${track.name}"
                return@launch
            }
            val res = ktorClient.uploadAudioTrack(track, file)
            res.onSuccess {
                _statusToast.value = "Auf TV synchronisiert: ${track.name}"
            }.onFailure { err ->
                _statusToast.value = "TV-Upload fehlgeschlagen: ${err.message}"
            }
        }
    }

    fun syncAllPendingTracks() {
        if (isTvDevice || !isConnectedToRemote()) return
        scope.launch {
            ktorClient.syncPendingToHost(forceAllUnsynced = true)
        }
    }

    fun deleteAudioTrack(track: CustomAudioTrack) {
        scope.launch {
            if (!isTvDevice && isConnectedToRemote()) {
                val remoteHashes = _uiState.value.audioLibrary.map { it.sha256 }.toSet()
                if (track.sha256 in remoteHashes) {
                    val remoteDel = ktorClient.deleteAudioOnHost(track.id)
                    remoteDel.onSuccess {
                        // Also purge local Smartphone copy by sha256 (Phone UUID != TV UUID) so it never re-syncs
                        audioLibrary.deleteTrackBySha256(track.sha256)
                        audioLibrary.deleteTrack(track.id)
                        _statusToast.value = "Auf TV & Handy gelöscht: ${track.name}"
                    }.onFailure { err ->
                        _statusToast.value = "Löschen auf TV fehlgeschlagen: ${err.message}"
                    }
                } else {
                    // Track existed only locally on Smartphone
                    audioLibrary.deleteTrackBySha256(track.sha256)
                    audioLibrary.deleteTrack(track.id)
                    _statusToast.value = "Lokal gelöscht: ${track.name}"
                }
            } else {
                audioLibrary.deleteTrack(track.id)
                audioLibrary.deleteTrackBySha256(track.sha256)
                decodedCache.evict(track.id)
                updateLocalState { state ->
                    val newSingle = state.singleSpeaker.copy(
                        playing = if (state.singleSpeaker.activeChannel != null &&
                            state.singleSpeaker.configFor(state.singleSpeaker.activeChannel).audioId == track.id
                        ) false else state.singleSpeaker.playing
                    )
                    val newPuck = if (state.puck.audioId == track.id) {
                        state.puck.copy(audioId = null, playing = false)
                    } else state.puck
                    state.copy(
                        audioLibrary = audioLibrary.tracks.value,
                        singleSpeaker = newSingle,
                        puck = newPuck
                    )
                }
                _statusToast.value = "Gelöscht: ${track.name}"
            }
        }
    }

    private fun isConnectedToRemote(): Boolean =
        _connectionStatus.value == ConnectionStatus.CONNECTED_TO_TV

    private fun dispatchCommand(msg: WsMessage) {
        scope.launch {
            if (!isTvDevice && isConnectedToRemote()) {
                ktorClient.sendCommand(msg)
            } else {
                applyCommand(msg)
            }
        }
    }

    private suspend fun applyCommand(msg: WsMessage) {
        var shouldOrbit = false
        updateLocalState { cur ->
            val next = when (msg.type) {
                WsMessageTypes.SET_SETUP -> {
                    val newSetup = msg.setup?.let { SpeakerSetup.fromCode(it) } ?: cur.setup
                    if (isTvDevice) {
                        scope.launch { prefsRepo.saveSetup(newSetup, completed = true) }
                    }
                    val updatedGains = computePuckGainsMap(
                        setup = newSetup,
                        x = cur.puck.x,
                        y = cur.puck.y,
                        mode = cur.puck.panningMode,
                        subwoofer = cur.puck.subwooferEnabled && newSetup.hasSubwoofer,
                        layoutPreset = cur.speakerLayoutPreset
                    )
                    val newPreset = if (cur.generator.preset.isAvailableFor(newSetup)) {
                        cur.generator.preset
                    } else GeneratorPreset.ALL_ACTIVE
                    cur.copy(
                        setup = newSetup,
                        setupCompleted = true,
                        singleSpeaker = cur.singleSpeaker.copy(playing = false, activeChannel = null),
                        generator = cur.generator.copy(preset = newPreset, playing = false),
                        puck = cur.puck.copy(
                            subwooferEnabled = newSetup.hasSubwoofer && cur.puck.subwooferEnabled,
                            playing = false,
                            autoOrbit = false,
                            channelGains = updatedGains
                        )
                    )
                }

                WsMessageTypes.SET_MODE -> {
                    val mode = msg.mode?.let { runCatching { TestMode.valueOf(it) }.getOrNull() } ?: cur.activeMode
                    val leavingMode = mode != cur.activeMode
                    if (leavingMode) {
                        cur.copy(
                            activeMode = mode,
                            singleSpeaker = cur.singleSpeaker.copy(playing = false),
                            generator = cur.generator.copy(playing = false),
                            puck = cur.puck.copy(playing = false, autoOrbit = false)
                        )
                    } else {
                        cur
                    }
                }

                WsMessageTypes.SET_MASTER_GAIN -> {
                    val gain = (msg.masterGainDb ?: cur.masterGainDb).coerceIn(-30f, 0f)
                    if (isTvDevice) {
                        scope.launch { prefsRepo.saveMasterGainDb(gain) }
                    }
                    cur.copy(masterGainDb = gain)
                }

                WsMessageTypes.SET_SWAP_71_SURROUND_BACK -> {
                    val swap = msg.swap71SurroundBack ?: cur.swap71SurroundBack
                    if (isTvDevice) {
                        scope.launch { prefsRepo.saveSwap71SurroundBack(swap) }
                    }
                    cur.copy(swap71SurroundBack = swap)
                }

                WsMessageTypes.SET_SPEAKER_LAYOUT_PRESET -> {
                    val nextPreset = msg.speakerLayoutPreset?.let { raw ->
                        runCatching { SpeakerLayoutPreset.valueOf(raw) }.getOrNull()
                    } ?: cur.speakerLayoutPreset
                    if (isTvDevice) {
                        scope.launch { prefsRepo.saveSpeakerLayoutPreset(nextPreset) }
                    }
                    val updatedGains = computePuckGainsMap(
                        setup = cur.setup,
                        x = cur.puck.x,
                        y = cur.puck.y,
                        mode = cur.puck.panningMode,
                        subwoofer = cur.puck.subwooferEnabled,
                        layoutPreset = nextPreset
                    )
                    cur.copy(
                        speakerLayoutPreset = nextPreset,
                        puck = cur.puck.copy(channelGains = updatedGains)
                    )
                }

                WsMessageTypes.SET_LANGUAGE -> {
                    val lang = AppLanguage.fromCode(msg.appLanguage)
                    if (isTvDevice) {
                        scope.launch { prefsRepo.saveAppLanguage(lang) }
                    }
                    cur.copy(appLanguage = lang)
                }

                WsMessageTypes.PLAY_SINGLE_SPEAKER -> {
                    val ch = msg.channelId?.let { runCatching { SpeakerChannel.valueOf(it) }.getOrNull() }
                    if (ch == null || ch !in cur.setup.channels) {
                        cur
                    } else {
                        val prevCfg = cur.singleSpeaker.configFor(ch)
                        val newSource = msg.sourceType?.let { runCatching { SignalSourceType.valueOf(it) }.getOrNull() }
                            ?: prevCfg.sourceType
                        val newFreq = (msg.frequencyHz ?: prevCfg.frequencyHz).coerceIn(20, 20000)
                        val validIds = cur.audioLibrary.map { it.id }.toSet()
                        val candidateId = msg.audioId ?: prevCfg.audioId
                        val newAudioId = if (candidateId != null && candidateId in validIds) {
                            candidateId
                        } else cur.audioLibrary.firstOrNull()?.id
                        val updatedMap = cur.singleSpeaker.channelConfigs + (
                            ch to SingleSpeakerChannelConfig(
                                sourceType = newSource,
                                frequencyHz = newFreq,
                                audioId = newAudioId
                            )
                            )
                        cur.copy(
                            activeMode = TestMode.SINGLE_SPEAKER,
                            singleSpeaker = cur.singleSpeaker.copy(
                                activeChannel = ch,
                                playing = msg.playing ?: true,
                                channelConfigs = updatedMap
                            )
                        )
                    }
                }

                WsMessageTypes.UPDATE_CHANNEL_CONFIG -> {
                    val ch = msg.channelId?.let { runCatching { SpeakerChannel.valueOf(it) }.getOrNull() }
                    if (ch == null) {
                        cur
                    } else {
                        val prevCfg = cur.singleSpeaker.configFor(ch)
                        val newSource = msg.sourceType?.let { runCatching { SignalSourceType.valueOf(it) }.getOrNull() }
                            ?: prevCfg.sourceType
                        val newFreq = (msg.frequencyHz ?: prevCfg.frequencyHz).coerceIn(20, 20000)
                        val validIds = cur.audioLibrary.map { it.id }.toSet()
                        val candidateId = msg.audioId ?: prevCfg.audioId
                        val newAudioId = if (candidateId != null && candidateId in validIds) {
                            candidateId
                        } else cur.audioLibrary.firstOrNull()?.id
                        val newCfg = SingleSpeakerChannelConfig(newSource, newFreq, newAudioId)
                        val updatedMap = if (msg.applyToAll == true) {
                            cur.setup.channels.associateWith { newCfg }
                        } else {
                            cur.singleSpeaker.channelConfigs + (ch to newCfg)
                        }
                        cur.copy(
                            singleSpeaker = cur.singleSpeaker.copy(channelConfigs = updatedMap)
                        )
                    }
                }

                WsMessageTypes.UPDATE_GENERATOR -> {
                    val oldGen = cur.generator
                    val newPreset = msg.preset?.let { runCatching { GeneratorPreset.valueOf(it) }.getOrNull() }
                        ?: oldGen.preset
                    val newCustom = msg.customChannels?.mapNotNull {
                        runCatching { SpeakerChannel.valueOf(it) }.getOrNull()
                    }?.toSet() ?: oldGen.customChannels
                    val newSig = msg.signalType?.let { runCatching { SignalSourceType.valueOf(it) }.getOrNull() }
                        ?: oldGen.signalType

                    // SPEC.md 5.2 Smart start frequency: jump to 60 Hz when switching to 0.1 Sub (>150 Hz),
                    // jump back to 400 Hz when returning to satellite preset.
                    var nextFreq = (msg.frequencyHz ?: oldGen.frequencyHz).coerceIn(20, 20000)
                    if (msg.frequencyHz == null && msg.preset != null && newPreset != oldGen.preset) {
                        if (newPreset == GeneratorPreset.P0_1_SUB && oldGen.frequencyHz > 150) {
                            nextFreq = 60
                        } else if (oldGen.preset == GeneratorPreset.P0_1_SUB && newPreset != GeneratorPreset.P0_1_SUB) {
                            nextFreq = 400
                        }
                    }

                    cur.copy(
                        activeMode = TestMode.GENERATOR,
                        generator = oldGen.copy(
                            preset = newPreset,
                            customChannels = newCustom,
                            signalType = newSig,
                            frequencyHz = nextFreq,
                            playing = msg.playing ?: oldGen.playing
                        )
                    )
                }

                WsMessageTypes.UPDATE_PUCK -> {
                    val oldPuck = cur.puck
                    val nx = (msg.x ?: oldPuck.x).coerceIn(-1f, 1f)
                    val ny = (msg.y ?: oldPuck.y).coerceIn(-1f, 1f)
                    val nMode = msg.panningMode?.let { runCatching { PanningMode.valueOf(it) }.getOrNull() }
                        ?: oldPuck.panningMode
                    val nSub = msg.subwooferEnabled ?: oldPuck.subwooferEnabled
                    val nOrbit = msg.autoOrbit ?: oldPuck.autoOrbit
                    val nOrbitSpeed = msg.orbitSpeed?.let { runCatching { OrbitSpeed.valueOf(it) }.getOrNull() }
                        ?: oldPuck.orbitSpeed
                    val nSource = msg.sourceType?.let { runCatching { SignalSourceType.valueOf(it) }.getOrNull() }
                        ?: oldPuck.sourceType
                    val nFreq = (msg.frequencyHz ?: oldPuck.frequencyHz).coerceIn(20, 20000)
                    val validIds = cur.audioLibrary.map { it.id }.toSet()
                    val candidateAudioId = msg.audioId ?: oldPuck.audioId
                    val nAudioId = if (candidateAudioId != null && candidateAudioId in validIds) {
                        candidateAudioId
                    } else cur.audioLibrary.firstOrNull()?.id
                    val nPlaying = msg.playing ?: oldPuck.playing
                    val gains = computePuckGainsMap(
                        cur.setup,
                        nx,
                        ny,
                        nMode,
                        nSub,
                        cur.speakerLayoutPreset
                    )

                    cur.copy(
                        activeMode = TestMode.PUCK_2D,
                        puck = oldPuck.copy(
                            x = nx,
                            y = ny,
                            panningMode = nMode,
                            subwooferEnabled = nSub,
                            autoOrbit = nOrbit,
                            orbitSpeed = nOrbitSpeed,
                            sourceType = nSource,
                            frequencyHz = nFreq,
                            audioId = nAudioId,
                            playing = nPlaying,
                            channelGains = gains
                        )
                    )
                }

                WsMessageTypes.STOP_ALL -> {
                    cur.copy(
                        singleSpeaker = cur.singleSpeaker.copy(playing = false),
                        generator = cur.generator.copy(playing = false),
                        puck = cur.puck.copy(playing = false, autoOrbit = false)
                    )
                }

                else -> cur
            }
            shouldOrbit = next.activeMode == TestMode.PUCK_2D && next.puck.autoOrbit && next.puck.playing
            next
        }

        if (shouldOrbit) {
            startAutoOrbitLoop()
        } else {
            stopAutoOrbitLoop()
        }
    }

    private fun startAutoOrbitLoop() {
        if (orbitJob?.isActive == true) return
        orbitJob = scope.launch {
            var lastTimeNs = System.nanoTime()
            while (isActive) {
                delay(33L) // ~30 Hz smooth orbit
                val now = System.nanoTime()
                val dt = ((now - lastTimeNs) / 1_000_000_000f).coerceIn(0.001f, 0.1f)
                lastTimeNs = now

                val state = _localState.value
                if (state.activeMode != TestMode.PUCK_2D || !state.puck.autoOrbit || !state.puck.playing) {
                    break
                }
                orbitAngle = (orbitAngle + state.puck.orbitSpeed.radiansPerSecond * dt) % (2f * Math.PI.toFloat())
                val nx = 0.78f * sin(orbitAngle)
                val ny = 0.78f * cos(orbitAngle)
                updateLocalState { cur ->
                    val gains = computePuckGainsMap(
                        setup = cur.setup,
                        x = nx,
                        y = ny,
                        mode = cur.puck.panningMode,
                        subwoofer = cur.puck.subwooferEnabled,
                        layoutPreset = cur.speakerLayoutPreset
                    )
                    cur.copy(
                        puck = cur.puck.copy(
                            x = nx,
                            y = ny,
                            channelGains = gains
                        )
                    )
                }
            }
        }
    }

    private fun stopAutoOrbitLoop() {
        orbitJob?.cancel()
        orbitJob = null
    }

    private suspend fun updateLocalState(transform: (SurroundAppState) -> SurroundAppState) {
        val updated = stateMutex.withLock {
            val next = transform(_localState.value)
            val playingTrackId = resolveActivePlayingTrackId(next)
            val needsDecode = playingTrackId != null && decodedCache.getIfCached(playingTrackId) == null
            val stamped = next.copy(
                loadingAudioTrackId = if (needsDecode) playingTrackId else null,
                stateRevision = next.stateRevision + 1L
            )
            _localState.value = stamped
            stamped
        }

        val playingTrackId = resolveActivePlayingTrackId(updated)
        val cachedSamples = playingTrackId?.let { decodedCache.getIfCached(it) }
        if (isTvDevice || !isConnectedToRemote()) {
            audioEngine.applyState(updated, cachedSamples)
        }
        if (isTvDevice) {
            ktorServer.broadcastState(updated)
        }

        val targetToDecode = playingTrackId ?: resolvePrewarmTrackId(updated)
        if ((isTvDevice || !isConnectedToRemote()) &&
            targetToDecode != null &&
            decodedCache.getIfCached(targetToDecode) == null
        ) {
            scheduleBackgroundDecode(targetToDecode)
        }
    }

    private fun resolveActivePlayingTrackId(state: SurroundAppState): String? = when (state.activeMode) {
        TestMode.SINGLE_SPEAKER -> {
            val ch = state.singleSpeaker.activeChannel
            val cfg = ch?.let { state.singleSpeaker.configFor(it) }
            if (state.singleSpeaker.playing && cfg?.sourceType == SignalSourceType.CUSTOM_AUDIO) {
                cfg.audioId ?: state.audioLibrary.firstOrNull()?.id
            } else null
        }
        TestMode.PUCK_2D -> {
            if (state.puck.playing && state.puck.sourceType == SignalSourceType.CUSTOM_AUDIO) {
                state.puck.audioId ?: state.audioLibrary.firstOrNull()?.id
            } else null
        }
        else -> null
    }

    private fun resolvePrewarmTrackId(state: SurroundAppState): String? = when (state.activeMode) {
        TestMode.PUCK_2D -> {
            if (state.puck.sourceType == SignalSourceType.CUSTOM_AUDIO) {
                state.puck.audioId ?: state.audioLibrary.firstOrNull()?.id
            } else null
        }
        TestMode.SINGLE_SPEAKER -> {
            val ch = state.singleSpeaker.activeChannel
            val cfg = ch?.let { state.singleSpeaker.configFor(it) }
            if (cfg?.sourceType == SignalSourceType.CUSTOM_AUDIO) {
                cfg.audioId ?: state.audioLibrary.firstOrNull()?.id
            } else null
        }
        else -> null
    }

    private fun scheduleBackgroundDecode(trackId: String) {
        if (activeDecodeTrackId == trackId && decodeJob?.isActive == true) return
        decodeJob?.cancel()
        activeDecodeTrackId = trackId
        decodeJob = scope.launch(Dispatchers.Default) {
            val file = audioLibrary.getTrackFile(trackId)
            val result = if (file != null) decodedCache.decodeAndCache(trackId, file) else null
            val decodedSamples = result?.getOrNull()
            if (!isActive) return@launch
            val current = stateMutex.withLock {
                val cur = _localState.value
                if (cur.loadingAudioTrackId == trackId) {
                    val cleared = cur.copy(
                        loadingAudioTrackId = null,
                        stateRevision = cur.stateRevision + 1L
                    )
                    _localState.value = cleared
                    cleared
                } else cur
            }
            val activePlayId = resolveActivePlayingTrackId(current)
            if (activePlayId == trackId && decodedSamples != null && (isTvDevice || !isConnectedToRemote())) {
                audioEngine.applyState(current, decodedSamples)
            }
            if (isTvDevice) {
                ktorServer.broadcastState(current)
            }
            if (activeDecodeTrackId == trackId) {
                activeDecodeTrackId = null
            }
        }
    }

    private fun computePuckGainsMap(
        setup: SpeakerSetup,
        x: Float,
        y: Float,
        mode: PanningMode,
        subwoofer: Boolean,
        layoutPreset: SpeakerLayoutPreset
    ): Map<SpeakerChannel, Float> {
        val gains = FloatArray(8)
        SpatialPanner.computeGains(setup, x, y, mode, subwoofer, gains, layoutPreset)
        return setup.channels.associateWith { ch -> gains[ch.ordinal] }
    }

    fun shutdown() {
        stopAutoOrbitLoop()
        decodeJob?.cancel()
        audioEngine.stop()
        nsdManager.unregisterHostService()
        nsdManager.stopDiscovery()
        ktorServer.stop()
        ktorClient.close()
    }
}
