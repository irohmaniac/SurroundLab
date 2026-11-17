package de.surroundlab.app.audio

import de.surroundlab.app.domain.model.PanningMode
import de.surroundlab.app.domain.model.SpeakerChannel
import de.surroundlab.app.domain.model.SpeakerLayoutPreset
import de.surroundlab.app.domain.model.SpeakerSetup
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Pure 2D spatial panning calculator for:
 * - Mode A (SWEET_SPOT_VBAP / "Virtuelles Objekt"): Sharp Constant-Power VBAP azimuth direction
 *   paired with psychoacoustic listener-distance attenuation (louder/enveloping near the head,
 *   softer as the virtual object moves away toward the walls).
 * - Mode B (ROOM_DISTANCE / "Lautsprecher-Fokus"): Direct floorplan speaker proximity focus.
 *
 * Writes directly into a caller-supplied FloatArray(8) indexed by SpeakerChannel.ordinal
 * to guarantee zero allocations in real-time loops.
 */
object SpatialPanner {

    private val satelliteRingOrder = listOf(
        SpeakerChannel.C,
        SpeakerChannel.FR,
        SpeakerChannel.SR,
        SpeakerChannel.SBR,
        SpeakerChannel.SBL,
        SpeakerChannel.SL,
        SpeakerChannel.FL
    )

    fun computeGains(
        setup: SpeakerSetup,
        x: Float,
        y: Float,
        panningMode: PanningMode,
        subwooferEnabled: Boolean,
        outGains: FloatArray,
        layoutPreset: SpeakerLayoutPreset = SpeakerLayoutPreset.STANDARD
    ) {
        for (i in outGains.indices) {
            outGains[i] = 0f
        }

        val cx = x.coerceIn(-1f, 1f)
        val cy = y.coerceIn(-1f, 1f)
        val activeSet = setup.channels.toSet()

        if (setup.hasSubwoofer && subwooferEnabled) {
            outGains[SpeakerChannel.LFE.ordinal] = 1.0f
        }

        when (panningMode) {
            PanningMode.SWEET_SPOT_VBAP ->
                computeVirtualObjectGains(activeSet, cx, cy, layoutPreset, outGains)
            PanningMode.ROOM_DISTANCE ->
                computeDistanceGains(activeSet, cx, cy, layoutPreset, outGains)
        }
    }

    private fun computeVirtualObjectGains(
        activeSet: Set<SpeakerChannel>,
        x: Float,
        y: Float,
        layoutPreset: SpeakerLayoutPreset,
        outGains: FloatArray
    ) {
        val activeSatellites = satelliteRingOrder
            .filter { it in activeSet }
            .sortedBy { it.azimuthDeg(layoutPreset) }

        if (activeSatellites.isEmpty()) return

        val radius = hypot(x, y)
        val uniformGain = 1.0f / sqrt(activeSatellites.size.toFloat())

        if (activeSatellites.size == 1) {
            outGains[activeSatellites[0].ordinal] = 1.0f
            return
        }

        // Azimuth in degrees: 0 = front (+Y), +90 = right (+X), -90 = left (-X), +-180 = rear (-Y)
        val thetaDeg = (atan2(x.toDouble(), y.toDouble()) * (180.0 / PI)).toFloat()

        val flAz = SpeakerChannel.FL.azimuthDeg(layoutPreset)
        val frAz = SpeakerChannel.FR.azimuthDeg(layoutPreset)
        val hasFrontTrio = SpeakerChannel.FL in activeSet &&
            SpeakerChannel.C in activeSet &&
            SpeakerChannel.FR in activeSet

        if (hasFrontTrio && thetaDeg in flAz..frAz && frAz > 0.001f) {
            // Smooth front-stage center spread (constant-power blend across FL / C / FR)
            // eliminates the abrupt center timbre/width collapse at 0 deg while keeping 100% FL/FR at edges.
            val u = (thetaDeg / frAz).coerceIn(-1f, 1f)
            val vFl: Float
            val vC: Float
            val vFr: Float
            if (u <= 0f) {
                val ang = (u + 1f) * (PI.toFloat() * 0.5f)
                vFl = cos(ang)
                vC = sin(ang)
                vFr = 0f
            } else {
                val ang = u * (PI.toFloat() * 0.5f)
                vFl = 0f
                vC = cos(ang)
                vFr = sin(ang)
            }
            val phantomAng = ((u + 1f) * 0.5f) * (PI.toFloat() * 0.5f)
            val pFl = cos(phantomAng)
            val pFr = sin(phantomAng)
            val divergence = 0.24f
            val rawFl = (1f - divergence) * vFl + divergence * pFl
            val rawC = (1f - divergence) * vC
            val rawFr = (1f - divergence) * vFr + divergence * pFr
            val frontNorm = sqrt(rawFl * rawFl + rawC * rawC + rawFr * rawFr)
            val invFrontNorm = if (frontNorm > 1e-6f) 1.0f / frontNorm else 1.0f
            outGains[SpeakerChannel.FL.ordinal] = rawFl * invFrontNorm
            outGains[SpeakerChannel.C.ordinal] = rawC * invFrontNorm
            outGains[SpeakerChannel.FR.ordinal] = rawFr * invFrontNorm
        } else {
            // Find enclosing speaker pair on circular azimuth ring
            val count = activeSatellites.size
            val lastAz = activeSatellites[count - 1].azimuthDeg(layoutPreset)
            val firstAz = activeSatellites[0].azimuthDeg(layoutPreset)
            var idx1 = count - 1
            var idx2 = 0
            var spanDeg = (firstAz + 360f) - lastAz
            var deltaDeg = if (thetaDeg >= lastAz) {
                thetaDeg - lastAz
            } else {
                thetaDeg + 360f - lastAz
            }

            for (i in 0 until count - 1) {
                val a1 = activeSatellites[i].azimuthDeg(layoutPreset)
                val a2 = activeSatellites[i + 1].azimuthDeg(layoutPreset)
                if (thetaDeg in a1..a2) {
                    idx1 = i
                    idx2 = i + 1
                    spanDeg = a2 - a1
                    deltaDeg = thetaDeg - a1
                    break
                }
            }

            val fraction = if (spanDeg > 0.001f) (deltaDeg / spanDeg).coerceIn(0f, 1f) else 0f
            val panAngle = fraction * (PI.toFloat() * 0.5f)
            // Wide speaker spans (e.g., 80 deg between FL<->SL or FR<->SR) suffer an acoustic
            // off-axis loudness dip with pure 0.707 constant-power summing.
            val wideSpanFactor = ((spanDeg - 40f) / 60f).coerceIn(0f, 1f)
            val midpointShape = sin(PI.toFloat() * fraction)
            val spanCompensation = 1.0f + 0.24f * wideSpanFactor * midpointShape
            val g1 = cos(panAngle) * spanCompensation
            val g2 = sin(panAngle) * spanCompensation

            outGains[activeSatellites[idx1].ordinal] = g1
            outGains[activeSatellites[idx2].ordinal] = g2
        }

        // Smooth blend from center near-head envelopment (r < 0.25) into sharp directional panning,
        // paired with a balanced listener-distance curve (0 dB near head -> ~-3.8 dB at room edge).
        val edgeBlend = (radius / 0.25f).coerceIn(0f, 1f)
        if (edgeBlend < 1f) {
            var energy = 0f
            for (ch in activeSatellites) {
                val blended = uniformGain * (1f - edgeBlend) + outGains[ch.ordinal] * edgeBlend
                outGains[ch.ordinal] = blended
                energy += blended * blended
            }
            if (energy > 1e-6f) {
                val invNorm = 1.0f / sqrt(energy)
                for (ch in activeSatellites) {
                    outGains[ch.ordinal] *= invNorm
                }
            }
        }

        val normR = radius.coerceIn(0f, 1.0f)
        val distanceAtten = 1.0f / (1.0f + 0.55f * normR)
        for (ch in activeSatellites) {
            outGains[ch.ordinal] *= distanceAtten
        }
    }

    private fun computeDistanceGains(
        activeSet: Set<SpeakerChannel>,
        x: Float,
        y: Float,
        layoutPreset: SpeakerLayoutPreset,
        outGains: FloatArray
    ) {
        val activeSatellites = activeSet.filterNot { it.isLfe }
        if (activeSatellites.isEmpty()) return

        var energySum = 0f
        for (ch in activeSatellites) {
            val dist = hypot(x - ch.stageX(layoutPreset), y - ch.stageY(layoutPreset))
            val weight = 1.0f / (dist.pow(2.5f) + 0.02f)
            outGains[ch.ordinal] = weight
            energySum += weight * weight
        }

        if (energySum > 1e-6f) {
            val invNorm = 1.0f / sqrt(energySum)
            for (ch in activeSatellites) {
                outGains[ch.ordinal] = (outGains[ch.ordinal] * invNorm).coerceIn(0f, 1f)
            }
        }
    }
}
