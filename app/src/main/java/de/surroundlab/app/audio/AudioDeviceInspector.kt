package de.surroundlab.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.AudioMixerAttributes
import android.os.Build
import android.util.Log
import de.surroundlab.app.domain.model.HdmiAudioInfo

/**
 * Inspects Android output audio devices for HDMI / eARC multichannel support.
 */
object AudioDeviceInspector {

    private const val TAG = "SurroundLabAudio"

    /**
     * Detects Amlogic-based TV hardware (e.g., Chromecast with Google TV / Sabrina),
     * whose 8-channel HDMI LPCM HAL commonly expects slots 4/5 (Side) before 6/7 (Back).
     */
    fun isAmlogicPlatform(): Boolean {
        val hw = Build.HARDWARE.lowercase()
        val board = Build.BOARD.lowercase()
        val soc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Build.SOC_MODEL.lowercase()
        } else ""
        return hw.contains("amlogic") || board.contains("sabrina") || soc.startsWith("aml")
    }

    fun inspectOutputs(context: Context): HdmiAudioInfo {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)

        var bestDevice: AudioDeviceInfo? = null
        var bestMaxChannels = 2

        for (device in outputs) {
            val type = device.type
            val isExternalDigital = type == AudioDeviceInfo.TYPE_HDMI ||
                type == AudioDeviceInfo.TYPE_HDMI_ARC ||
                type == AudioDeviceInfo.TYPE_HDMI_EARC ||
                type == AudioDeviceInfo.TYPE_USB_DEVICE
            val maxCh = device.channelCounts.maxOrNull() ?: 2
            Log.i(
                TAG,
                "Output device id=${device.id} type=$type name=${device.productName} " +
                    "channels=${device.channelCounts.joinToString()} " +
                    "masks=${device.channelMasks.joinToString { "0x" + it.toString(16) }} " +
                    "encodings=${device.encodings.joinToString()}"
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && isExternalDigital) {
                val mixerAttrs = audioManager.getSupportedMixerAttributes(device)
                Log.i(TAG, "SupportedMixerAttributes(${device.id}): ${mixerAttrs.size} -> $mixerAttrs")
            }
            if (isExternalDigital && maxCh >= bestMaxChannels) {
                bestMaxChannels = maxCh
                bestDevice = device
            } else if (bestDevice == null && maxCh > bestMaxChannels) {
                bestMaxChannels = maxCh
                bestDevice = device
            }
        }

        val fallbackDevice = bestDevice ?: outputs.firstOrNull()
        val name = fallbackDevice?.productName?.toString()?.takeIf { it.isNotBlank() }
            ?: when (fallbackDevice?.type) {
                AudioDeviceInfo.TYPE_HDMI,
                AudioDeviceInfo.TYPE_HDMI_ARC,
                AudioDeviceInfo.TYPE_HDMI_EARC -> "HDMI / AVR Ausgang"
                AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> "Gerätelautsprecher"
                else -> "Standardausgang"
            }

        return HdmiAudioInfo(
            multichannelSupported = bestMaxChannels >= 6,
            maxChannels = bestMaxChannels,
            deviceName = name
        )
    }

    /**
     * On API 34+ (Android 14+, e.g. Chromecast with Google TV), checks if BIT_PERFECT mixer
     * attributes are supported for the HDMI device and applies them if available.
     */
    fun tryEnableDirectMultichannelMixer(
        context: Context,
        channelMask: Int,
        sampleRate: Int,
        encoding: Int
    ): AudioDeviceInfo? {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val hdmiDevice = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            .firstOrNull {
                it.type == AudioDeviceInfo.TYPE_HDMI ||
                    it.type == AudioDeviceInfo.TYPE_HDMI_ARC ||
                    it.type == AudioDeviceInfo.TYPE_HDMI_EARC
            } ?: return null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val mediaAttrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            val directProfiles = audioManager.getDirectProfilesForAttributes(mediaAttrs)
            Log.i(TAG, "DirectProfilesForAttributes(MUSIC): ${directProfiles.joinToString()}")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val mediaAttrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            val supported = audioManager.getSupportedMixerAttributes(hdmiDevice)
            val match = supported.firstOrNull { attr ->
                attr.mixerBehavior == AudioMixerAttributes.MIXER_BEHAVIOR_BIT_PERFECT
            }
            if (match != null) {
                val ok = audioManager.setPreferredMixerAttributes(mediaAttrs, hdmiDevice, match)
                val readBack = audioManager.getPreferredMixerAttributes(mediaAttrs, hdmiDevice)
                Log.i(TAG, "setPreferredMixerAttributes($match) -> $ok, readBack=$readBack")
            } else {
                Log.i(TAG, "No BIT_PERFECT mixer in supported=$supported")
            }
        }
        return hdmiDevice
    }
}
