package de.surroundlab.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class TestMode {
    HUB,
    SINGLE_SPEAKER,
    GENERATOR,
    PUCK_2D
}

@Serializable
enum class SignalSourceType(val label: String) {
    PINK_NOISE("Pink Noise"),
    SINE("Sinuston"),
    CUSTOM_AUDIO("Eigene Audiodatei")
}

@Serializable
enum class GeneratorPreset(val label: String) {
    ALL_ACTIVE("Alle aktiven Kanäle"),
    P2_1("2.1 Stereo + Sub"),
    P2_0("2.0 Front"),
    P0_1_SUB("0.1 Nur Subwoofer"),
    CUSTOM("Benutzerdefiniert");

    fun resolveChannels(setup: SpeakerSetup, customChannels: Set<SpeakerChannel>): Set<SpeakerChannel> {
        val active = setup.channels.toSet()
        return when (this) {
            ALL_ACTIVE -> active
            P2_1 -> setOf(SpeakerChannel.FL, SpeakerChannel.FR, SpeakerChannel.LFE).intersect(active)
            P2_0 -> setOf(SpeakerChannel.FL, SpeakerChannel.FR).intersect(active)
            P0_1_SUB -> if (setup.hasSubwoofer) setOf(SpeakerChannel.LFE) else emptySet()
            CUSTOM -> customChannels.intersect(active)
        }
    }

    fun isAvailableFor(setup: SpeakerSetup): Boolean = when (this) {
        P2_1 -> setup.hasSubwoofer
        P0_1_SUB -> setup.hasSubwoofer
        else -> true
    }
}

@Serializable
enum class PanningMode(val label: String) {
    SWEET_SPOT_VBAP("Virtuelles Objekt"),
    ROOM_DISTANCE("Lautsprecher-Fokus")
}

@Serializable
enum class OrbitSpeed(val label: String, val radiansPerSecond: Float) {
    SLOW("Langsam", 0.55f),
    MEDIUM("Mittel", 1.15f),
    FAST("Schnell", 2.20f)
}

@Serializable
data class SingleSpeakerChannelConfig(
    val sourceType: SignalSourceType = SignalSourceType.PINK_NOISE,
    val frequencyHz: Int = 400,
    val audioId: String? = null
)

@Serializable
data class SingleSpeakerState(
    val activeChannel: SpeakerChannel? = null,
    val playing: Boolean = false,
    val channelConfigs: Map<SpeakerChannel, SingleSpeakerChannelConfig> = emptyMap()
) {
    fun configFor(channel: SpeakerChannel): SingleSpeakerChannelConfig =
        channelConfigs[channel] ?: SingleSpeakerChannelConfig(
            sourceType = SignalSourceType.PINK_NOISE,
            frequencyHz = if (channel.isLfe) 60 else 400
        )
}

@Serializable
data class GeneratorState(
    val preset: GeneratorPreset = GeneratorPreset.ALL_ACTIVE,
    val customChannels: Set<SpeakerChannel> = setOf(SpeakerChannel.FL, SpeakerChannel.FR),
    val signalType: SignalSourceType = SignalSourceType.SINE,
    val frequencyHz: Int = 400,
    val playing: Boolean = false
)

@Serializable
data class PuckState(
    val x: Float = 0.0f,
    val y: Float = 0.55f,
    val panningMode: PanningMode = PanningMode.SWEET_SPOT_VBAP,
    val subwooferEnabled: Boolean = true,
    val autoOrbit: Boolean = false,
    val orbitSpeed: OrbitSpeed = OrbitSpeed.MEDIUM,
    val sourceType: SignalSourceType = SignalSourceType.PINK_NOISE,
    val frequencyHz: Int = 400,
    val audioId: String? = null,
    val playing: Boolean = false,
    val channelGains: Map<SpeakerChannel, Float> = emptyMap()
)

@Serializable
data class CustomAudioTrack(
    val id: String,
    val name: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val sha256: String,
    val sampleRate: Int,
    val channels: Int,
    val createdAtMs: Long,
    val pendingSync: Boolean = false
)

enum class TrackSyncStatus {
    SYNCED_ON_TV,
    UPLOADING,
    PENDING_LOCAL,
    FAILED,
    DELETING
}

data class AudioSyncStatus(
    val uploadingSha256s: Set<String> = emptySet(),
    val failedSha256Errors: Map<String, String> = emptyMap(),
    val deletingTrackIds: Set<String> = emptySet()
)

@Serializable
enum class ConnectionStatus {
    TV_HOST_ACTIVE,
    CONNECTED_TO_TV,
    CONNECTING,
    LOCAL_STANDALONE
}

@Serializable
data class HdmiAudioInfo(
    val multichannelSupported: Boolean = true,
    val maxChannels: Int = 2,
    val deviceName: String = "Standardausgang"
)

@Serializable
data class SurroundAppState(
    val setup: SpeakerSetup = SpeakerSetup.S5_1,
    val setupCompleted: Boolean = false,
    val activeMode: TestMode = TestMode.HUB,
    val masterGainDb: Float = -12.0f,
    val swap71SurroundBack: Boolean = false,
    val speakerLayoutPreset: SpeakerLayoutPreset = SpeakerLayoutPreset.STANDARD,
    val tvUiScale: Float = 0.75f,
    val appLanguage: AppLanguage = AppLanguage.AUTO,
    val singleSpeaker: SingleSpeakerState = SingleSpeakerState(),
    val generator: GeneratorState = GeneratorState(),
    val puck: PuckState = PuckState(),
    val audioLibrary: List<CustomAudioTrack> = emptyList(),
    val hostName: String = "SurroundLab TV",
    val hostIp: String? = null,
    val hostPort: Int = 8080,
    val sessionToken: String = "",
    val connectedClients: Int = 0,
    val connectedAndroidClients: Int = 0,
    val connectedBrowserClients: Int = 0,
    val hdmiAudioInfo: HdmiAudioInfo = HdmiAudioInfo(),
    val loadingAudioTrackId: String? = null,
    val stateRevision: Long = 0L
)
