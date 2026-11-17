package de.surroundlab.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Chair
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.surroundlab.app.domain.model.SpeakerChannel
import de.surroundlab.app.domain.model.SpeakerLayoutPreset
import de.surroundlab.app.domain.model.SpeakerSetup
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors
import kotlin.math.roundToInt

/**
 * Shared interactive / visual 2D Surround Room Stage used by both TV and Mobile UIs
 * in Mode 1 (Single Speaker Check) and Mode 3 (2D Spatial Sound Puck).
 */
@Composable
fun RoomStageView(
    setup: SpeakerSetup,
    modifier: Modifier = Modifier,
    layoutPreset: SpeakerLayoutPreset = SpeakerLayoutPreset.STANDARD,
    activeSpeaker: SpeakerChannel? = null,
    highlightedSpeaker: SpeakerChannel? = null,
    isPlaying: Boolean = false,
    showPuck: Boolean = false,
    puckX: Float = 0f,
    puckY: Float = 0f,
    channelGains: Map<SpeakerChannel, Float> = emptyMap(),
    subwooferEnabled: Boolean = true,
    onSpeakerClick: ((SpeakerChannel) -> Unit)? = null,
    onPuckDrag: ((Float, Float) -> Unit)? = null
) {
    val s = LocalAppStrings.current
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(SurroundColors.SurfaceContainerHigh)
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        val padPx = with(density) { 42.dp.toPx() }
        val stageHalfW = ((widthPx - padPx * 2f) / 2f).coerceAtLeast(1f)
        val stageHalfH = ((heightPx - padPx * 2f) / 2f).coerceAtLeast(1f)
        val centerX = widthPx / 2f
        val centerY = heightPx / 2f

        val stageInteractionModifier = if (showPuck && onPuckDrag != null) {
            Modifier
                .fillMaxSize()
                .pointerInput(widthPx, heightPx) {
                    detectTapGestures { offset ->
                        val nx = ((offset.x - centerX) / stageHalfW).coerceIn(-1f, 1f)
                        val ny = ((centerY - offset.y) / stageHalfH).coerceIn(-1f, 1f)
                        onPuckDrag(nx, ny)
                    }
                }
                .pointerInput(widthPx, heightPx) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val nx = ((change.position.x - centerX) / stageHalfW).coerceIn(-1f, 1f)
                        val ny = ((centerY - change.position.y) / stageHalfH).coerceIn(-1f, 1f)
                        onPuckDrag(nx, ny)
                    }
                }
        } else {
            Modifier.fillMaxSize()
        }

        Box(modifier = stageInteractionModifier) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val minDim = size.minDimension
                // Concentric acoustic distance rings
                val radii = listOf(minDim * 0.42f, minDim * 0.29f, minDim * 0.16f)
                radii.forEachIndexed { idx, r ->
                    drawCircle(
                        color = if (idx == 0) SurroundColors.Outline.copy(alpha = 0.45f)
                        else SurroundColors.Primary.copy(alpha = 0.18f + idx * 0.06f),
                        radius = r,
                        style = Stroke(width = 2f)
                    )
                }
                // Crosshair axes
                drawLine(
                    color = SurroundColors.Outline.copy(alpha = 0.25f),
                    start = Offset(center.x, 24f),
                    end = Offset(center.x, size.height - 24f),
                    strokeWidth = 1.5f
                )
                drawLine(
                    color = SurroundColors.Outline.copy(alpha = 0.25f),
                    start = Offset(24f, center.y),
                    end = Offset(size.width - 24f, center.y),
                    strokeWidth = 1.5f
                )

                // Active pulse ring around playing single speaker or puck
                if (isPlaying) {
                    if (showPuck) {
                        val px = centerX + puckX * stageHalfW
                        val py = centerY - puckY * stageHalfH
                        drawCircle(
                            color = SurroundColors.Primary.copy(alpha = pulseAlpha),
                            radius = 28.dp.toPx() * pulseScale,
                            center = Offset(px, py)
                        )
                    } else if (activeSpeaker != null) {
                        val sx = centerX + activeSpeaker.stageX(layoutPreset) * stageHalfW
                        val sy = centerY - activeSpeaker.stageY(layoutPreset) * stageHalfH
                        val col = if (activeSpeaker.isLfe) SurroundColors.Secondary else SurroundColors.Primary
                        drawCircle(
                            color = col.copy(alpha = pulseAlpha),
                            radius = 24.dp.toPx() * pulseScale,
                            center = Offset(sx, sy)
                        )
                    }
                }
            }

            // TV Display Front Indicator
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .clip(CircleShape)
                    .background(SurroundColors.PrimaryContainer)
                    .padding(horizontal = 14.dp, vertical = 3.dp)
            ) {
                Text(
                    text = s.roomTvFront,
                    fontFamily = MonoFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.OnPrimaryContainer
                )
            }

            // Sweet Spot Listener Icon
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(SurroundColors.SurfaceContainerHighest),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Chair,
                    contentDescription = s.roomSweetSpotDesc,
                    tint = SurroundColors.Primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Speaker Nodes
            setup.channels.forEach { ch ->
                val sx = centerX + ch.stageX(layoutPreset) * stageHalfW
                val sy = centerY - ch.stageY(layoutPreset) * stageHalfH
                val isActive = (activeSpeaker == ch && isPlaying)
                val isFocused = highlightedSpeaker == ch
                val gain = channelGains[ch] ?: 0f
                val showGainPct = showPuck && (!ch.isLfe || subwooferEnabled)

                val bgColor: Color
                val fgColor: Color
                when {
                    isActive -> {
                        bgColor = if (ch.isLfe) SurroundColors.Secondary else SurroundColors.Primary
                        fgColor = if (ch.isLfe) SurroundColors.OnSecondary else SurroundColors.OnPrimary
                    }
                    isFocused -> {
                        bgColor = SurroundColors.PrimaryContainer
                        fgColor = SurroundColors.OnPrimaryContainer
                    }
                    ch.isLfe -> {
                        bgColor = if (subwooferEnabled) SurroundColors.SecondaryContainer else SurroundColors.SurfaceContainer
                        fgColor = if (subwooferEnabled) SurroundColors.Secondary else SurroundColors.OnSurfaceVariant
                    }
                    showPuck && gain > 0.35f -> {
                        bgColor = SurroundColors.PrimaryContainer
                        fgColor = SurroundColors.Primary
                    }
                    else -> {
                        bgColor = SurroundColors.SurfaceContainerHighest
                        fgColor = SurroundColors.OnSurface
                    }
                }

                val nodeWidthPx = with(density) { (if (showGainPct) 58.dp else 44.dp).toPx() }
                val nodeHeightPx = with(density) { 28.dp.toPx() }

                val label = if (showGainPct) {
                    val pct = (gain * 100f).roundToInt().coerceIn(0, 100)
                    "${ch.shortLabel} $pct%"
                } else {
                    ch.shortLabel
                }

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = (sx - nodeWidthPx / 2f).roundToInt(),
                                y = (sy - nodeHeightPx / 2f).roundToInt()
                            )
                        }
                        .size(
                            width = if (showGainPct) 58.dp else 44.dp,
                            height = 28.dp
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .then(
                            if (onSpeakerClick != null) {
                                Modifier.pointerInput(ch) {
                                    detectTapGestures { onSpeakerClick(ch) }
                                }
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontFamily = MonoFontFamily,
                        fontSize = if (showGainPct) 10.sp else 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = fgColor
                    )
                }
            }

            // Floating 2D Sound Puck
            if (showPuck) {
                val px = centerX + puckX * stageHalfW
                val py = centerY - puckY * stageHalfH
                val puckSizePx = with(density) { 52.dp.toPx() }

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = (px - puckSizePx / 2f).roundToInt(),
                                y = (py - puckSizePx / 2f).roundToInt()
                            )
                        }
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SurroundColors.PrimaryContainer)
                        .padding(5.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) SurroundColors.Primary else SurroundColors.SurfaceContainerHighest),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = s.roomSoundPuckDesc,
                        tint = if (isPlaying) SurroundColors.OnPrimary else SurroundColors.Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
