package de.surroundlab.app.domain.model

import kotlinx.serialization.Serializable
import kotlin.math.cos
import kotlin.math.sin

/**
 * Room speaker angle presets matching typical living room / home theater geometries.
 */
@Serializable
enum class SpeakerLayoutPreset(
    val shortLabel: String,
    val label: String,
    val angleSummary: String,
    val detailDescription: String
) {
    COMPACT(
        shortLabel = "Eng (±22°)",
        label = "Eng (Wohnzimmer)",
        angleSummary = "Front ±22° • Surround ±100° • Back ±140°",
        detailDescription = "Für schmale TV-Möbel oder einen kurzen Sitzabstand, bei dem die Frontlautsprecher nah am Fernseher stehen."
    ),
    STANDARD(
        shortLabel = "Standard (±30°)",
        label = "Standard (Kino-Norm)",
        angleSummary = "Front ±30° • Surround ±110° • Back ±150°",
        detailDescription = "Klassisches gleichschenkliges Stereo-/Surround-Dreieck nach Dolby- & ITU-Empfehlung."
    ),
    WIDE(
        shortLabel = "Breit (±40°)",
        label = "Breit (Heimkino)",
        angleSummary = "Front ±40° • Surround ±120° • Back ±155°",
        detailDescription = "Für weit außen aufgestellte Front-Boxen oder eine breite Sitzgruppe mit viel Abstand nach links/rechts."
    )
}

/**
 * Physical surround speaker channels supported by SurroundLab.
 * Coordinates use normalized stage space: x in [-1, +1] (L..R), y in [-1, +1] (Back..Front),
 * derived directly from each channel's azimuth so UI placement and DSP panning align 1:1.
 */
@Serializable
enum class SpeakerChannel(
    val shortLabel: String,
    val displayName: String,
    val azimuthDeg: Float,
    val stageX: Float,
    val stageY: Float,
    val isLfe: Boolean = false
) {
    FL("FL", "Front Left", -30f, -0.43f, 0.745f),
    C("C", "Center", 0f, 0.0f, 0.86f),
    FR("FR", "Front Right", 30f, 0.43f, 0.745f),
    SL("SL", "Surround Left", -110f, -0.808f, -0.294f),
    SR("SR", "Surround Right", 110f, 0.808f, -0.294f),
    SBL("SBL", "Surround Back Left", -150f, -0.43f, -0.745f),
    SBR("SBR", "Surround Back Right", 150f, 0.43f, -0.745f),
    LFE("SUB", "Subwoofer (LFE)", 0f, -0.30f, 0.46f, isLfe = true);

    fun azimuthDeg(preset: SpeakerLayoutPreset): Float = when (preset) {
        SpeakerLayoutPreset.COMPACT -> when (this) {
            FL -> -22f
            C -> 0f
            FR -> 22f
            SL -> -100f
            SR -> 100f
            SBL -> -140f
            SBR -> 140f
            LFE -> 0f
        }
        SpeakerLayoutPreset.STANDARD -> azimuthDeg
        SpeakerLayoutPreset.WIDE -> when (this) {
            FL -> -40f
            C -> 0f
            FR -> 40f
            SL -> -120f
            SR -> 120f
            SBL -> -155f
            SBR -> 155f
            LFE -> 0f
        }
    }

    fun stageX(preset: SpeakerLayoutPreset): Float =
        if (isLfe) stageX else (0.86f * sin(Math.toRadians(azimuthDeg(preset).toDouble()))).toFloat()

    fun stageY(preset: SpeakerLayoutPreset): Float =
        if (isLfe) stageY else (0.86f * cos(Math.toRadians(azimuthDeg(preset).toDouble()))).toFloat()

    /**
     * Channel index inside Android/WAVE 6-channel 5.1 PCM stream:
     * 0: FL, 1: FR, 2: FC, 3: LFE, 4: BL/SL, 5: BR/SR.
     */
    fun indexIn5Point1(): Int = when (this) {
        FL -> 0
        FR -> 1
        C -> 2
        LFE -> 3
        SL -> 4
        SR -> 5
        SBL, SBR -> -1
    }

    /**
     * Channel index inside Android/WAVE 8-channel 7.1 PCM stream:
     * 0: FL, 1: FR, 2: FC, 3: LFE, 4: BL/SBL, 5: BR/SBR, 6: SL, 7: SR.
     */
    fun indexIn7Point1(): Int = when (this) {
        FL -> 0
        FR -> 1
        C -> 2
        LFE -> 3
        SBL -> 4
        SBR -> 5
        SL -> 6
        SR -> 7
    }
}

/**
 * Speaker topologies supported by SurroundLab (2.0 through 7.1).
 */
@Serializable
enum class SpeakerSetup(
    val code: String,
    val displayName: String,
    val subtitle: String,
    val channels: List<SpeakerChannel>,
    val streamChannelCount: Int
) {
    S2_0(
        code = "2.0",
        displayName = "2.0 Stereo",
        subtitle = "Front Links & Rechts",
        channels = listOf(SpeakerChannel.FL, SpeakerChannel.FR),
        streamChannelCount = 2
    ),
    S2_1(
        code = "2.1",
        displayName = "2.1 Audio",
        subtitle = "Stereo + Subwoofer",
        channels = listOf(SpeakerChannel.FL, SpeakerChannel.FR, SpeakerChannel.LFE),
        streamChannelCount = 6
    ),
    S3_1(
        code = "3.1",
        displayName = "3.1 Bühne",
        subtitle = "Front + Center + Sub",
        channels = listOf(SpeakerChannel.FL, SpeakerChannel.C, SpeakerChannel.FR, SpeakerChannel.LFE),
        streamChannelCount = 6
    ),
    S5_0(
        code = "5.0",
        displayName = "5.0 Surround",
        subtitle = "5 Satellitenkanäle",
        channels = listOf(
            SpeakerChannel.FL,
            SpeakerChannel.C,
            SpeakerChannel.FR,
            SpeakerChannel.SL,
            SpeakerChannel.SR
        ),
        streamChannelCount = 6
    ),
    S5_1(
        code = "5.1",
        displayName = "5.1 Cinema",
        subtitle = "6 Diskrete Kanäle",
        channels = listOf(
            SpeakerChannel.FL,
            SpeakerChannel.C,
            SpeakerChannel.FR,
            SpeakerChannel.LFE,
            SpeakerChannel.SL,
            SpeakerChannel.SR
        ),
        streamChannelCount = 6
    ),
    S7_1(
        code = "7.1",
        displayName = "7.1 Extended",
        subtitle = "8 Diskrete Kanäle",
        channels = listOf(
            SpeakerChannel.FL,
            SpeakerChannel.C,
            SpeakerChannel.FR,
            SpeakerChannel.LFE,
            SpeakerChannel.SL,
            SpeakerChannel.SR,
            SpeakerChannel.SBL,
            SpeakerChannel.SBR
        ),
        streamChannelCount = 8
    );

    val hasSubwoofer: Boolean
        get() = channels.any { it.isLfe }

    val satelliteChannels: List<SpeakerChannel>
        get() = channels.filterNot { it.isLfe }

    companion object {
        fun fromCode(code: String): SpeakerSetup =
            entries.firstOrNull { it.code == code || it.name == code } ?: S5_1
    }
}
