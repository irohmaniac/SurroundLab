package de.surroundlab.app.domain.model

import kotlinx.serialization.Serializable

/**
 * Discovered TV host advertised via NSD (_surroundtest._tcp.).
 */
@Serializable
data class DiscoveredHost(
    val serviceName: String,
    val hostIp: String,
    val port: Int,
    val token: String
)

/**
 * Flat JSON WebSocket envelope shared between TV host, Android mobile controller,
 * and the embedded HTML5/JS browser fallback.
 */
@Serializable
data class WsMessage(
    val type: String,
    val state: SurroundAppState? = null,
    val audioLibrary: List<CustomAudioTrack>? = null,
    val setup: String? = null,
    val mode: String? = null,
    val masterGainDb: Float? = null,
    val lfeLowPassHz: Int? = null,
    val swap71SurroundBack: Boolean? = null,
    val speakerLayoutPreset: String? = null,
    val appLanguage: String? = null,
    // Single speaker actions
    val channelId: String? = null,
    val playing: Boolean? = null,
    val sourceType: String? = null,
    val frequencyHz: Int? = null,
    val audioId: String? = null,
    val applyToAll: Boolean? = null,
    // Generator actions
    val preset: String? = null,
    val customChannels: List<String>? = null,
    val signalType: String? = null,
    // Puck actions
    val x: Float? = null,
    val y: Float? = null,
    val panningMode: String? = null,
    val subwooferEnabled: Boolean? = null,
    val autoOrbit: Boolean? = null,
    val orbitSpeed: String? = null,
    // Error feedback
    val errorMessage: String? = null
)

object WsMessageTypes {
    const val STATE_SYNC = "STATE_SYNC"
    const val LIBRARY_UPDATED = "LIBRARY_UPDATED"
    const val SET_SETUP = "SET_SETUP"
    const val SET_MODE = "SET_MODE"
    const val SET_MASTER_GAIN = "SET_MASTER_GAIN"
    const val SET_LFE_LOW_PASS = "SET_LFE_LOW_PASS"
    const val SET_SWAP_71_SURROUND_BACK = "SET_SWAP_71_SURROUND_BACK"
    const val SET_SPEAKER_LAYOUT_PRESET = "SET_SPEAKER_LAYOUT_PRESET"
    const val SET_LANGUAGE = "SET_LANGUAGE"
    const val PLAY_SINGLE_SPEAKER = "PLAY_SINGLE_SPEAKER"
    const val UPDATE_CHANNEL_CONFIG = "UPDATE_CHANNEL_CONFIG"
    const val UPDATE_GENERATOR = "UPDATE_GENERATOR"
    const val UPDATE_PUCK = "UPDATE_PUCK"
    const val UPDATE_MEASUREMENT_TONE = "UPDATE_MEASUREMENT_TONE"
    const val STOP_ALL = "STOP_ALL"
    const val ERROR = "ERROR"
}
