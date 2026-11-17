package de.surroundlab.app.ui.tv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import de.surroundlab.app.domain.model.OrbitSpeed
import de.surroundlab.app.domain.model.PanningMode
import de.surroundlab.app.domain.model.SignalSourceType
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.ui.components.RoomStageView
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.roundToInt

@Composable
fun TvPuckScreen(
    state: SurroundAppState,
    onBack: () -> Unit,
    onUpdatePuck: (
        x: Float?,
        y: Float?,
        panningMode: PanningMode?,
        subwooferEnabled: Boolean?,
        autoOrbit: Boolean?,
        orbitSpeed: OrbitSpeed?,
        sourceType: SignalSourceType?,
        frequencyHz: Int?,
        audioId: String?,
        playing: Boolean?
    ) -> Unit
) {
    val s = LocalAppStrings.current
    val puck = state.puck
    var puckSteerActive by remember { mutableStateOf(true) }
    val stageFocus = remember { FocusRequester() }
    val sideMenuFocus = remember { FocusRequester() }

    LaunchedEffect(puckSteerActive) {
        if (puckSteerActive) {
            stageFocus.requestFocus()
        } else {
            sideMenuFocus.requestFocus()
        }
    }

    BackHandler {
        if (puckSteerActive) {
            puckSteerActive = false
        } else {
            onBack()
        }
    }

    val azimuthDeg = ((atan2(puck.x.toDouble(), puck.y.toDouble()) * 180.0 / PI).roundToInt() + 360) % 360
    val distanceM = (hypot(puck.x, puck.y) * 3.2f).coerceIn(0f, 3.5f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurroundColors.Background)
            .padding(horizontal = 48.dp, vertical = 26.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                TvPillSurface(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = s.back)
                    Text(s.homeHubBtn, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(
                        text = s.puckTitle,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.OnBackground
                    )
                    Text(
                        text = s.puckTvSubtitle(
                            s.panningModeLabel(puck.panningMode),
                            azimuthDeg,
                            s.panningModeHint(puck.panningMode)
                        ),
                        fontSize = 15.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (puckSteerActive) {
                    TvKeyLegendPill("▲▼◄►", s.legendMovePuck)
                    TvKeyLegendPill("OK", "${s.startBtn} / ${s.pauseBtn}")
                } else {
                    TvKeyLegendPill(s.legendSideMenuActive, s.legendBackForHub)
                }
            }
        }

        // Split View: Left (~64%) 2D Radar Stage + Right (~36%) Live Meters & Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Left: Interactive D-Pad Stage
            Column(
                modifier = Modifier
                    .weight(0.64f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(28.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .then(
                        if (puckSteerActive) {
                            Modifier.border(
                                BorderStroke(3.dp, SurroundColors.Primary),
                                RoundedCornerShape(28.dp)
                            )
                        } else Modifier
                    )
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (puckSteerActive) SurroundColors.Primary else SurroundColors.PrimaryContainer
                                )
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (puckSteerActive) s.legendMovePuck.uppercase() else s.puckTitle.uppercase(),
                                fontFamily = MonoFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (puckSteerActive) SurroundColors.OnPrimary else SurroundColors.OnPrimaryContainer
                            )
                        }
                        Text(
                            text = "X: ${"%.2f".format(puck.x)}  Y: ${"%.2f".format(puck.y)}",
                            fontFamily = MonoFontFamily,
                            fontSize = 13.sp,
                            color = SurroundColors.Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (state.setup.hasSubwoofer) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if (puck.subwooferEnabled) SurroundColors.SecondaryContainer
                                        else SurroundColors.SurfaceContainerHighest
                                    )
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = ".1 SUB ${if (puck.subwooferEnabled) s.stateOn.uppercase() else s.stateOff.uppercase()}",
                                    fontFamily = MonoFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (puck.subwooferEnabled) SurroundColors.Secondary else SurroundColors.OnSurfaceVariant
                                )
                            }
                        }
                        if (puck.autoOrbit) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SurroundColors.PrimaryContainer)
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = s.orbitWithSpeed(s.orbitSpeedLabel(puck.orbitSpeed)).uppercase(),
                                    fontFamily = MonoFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurroundColors.Primary
                                )
                            }
                        }
                    }
                }

                // Focusable Stage Surface intercepting D-Pad when puckSteerActive == true
                TvCardSurface(
                    onClick = {
                        if (puckSteerActive) {
                            onUpdatePuck(null, null, null, null, null, null, null, null, null, !puck.playing)
                        } else {
                            puckSteerActive = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 12.dp)
                        .focusRequester(stageFocus)
                        .onPreviewKeyEvent { evt ->
                            if (!puckSteerActive || evt.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                            val repeat = evt.nativeKeyEvent.repeatCount
                            val delta = when {
                                repeat > 10 -> 0.12f
                                repeat > 3 -> 0.08f
                                else -> 0.05f
                            }
                            when (evt.key) {
                                Key.DirectionUp -> {
                                    onUpdatePuck(puck.x, (puck.y + delta).coerceIn(-1f, 1f), null, null, false, null, null, null, null, null)
                                    true
                                }
                                Key.DirectionDown -> {
                                    onUpdatePuck(puck.x, (puck.y - delta).coerceIn(-1f, 1f), null, null, false, null, null, null, null, null)
                                    true
                                }
                                Key.DirectionLeft -> {
                                    onUpdatePuck((puck.x - delta).coerceIn(-1f, 1f), puck.y, null, null, false, null, null, null, null, null)
                                    true
                                }
                                Key.DirectionRight -> {
                                    onUpdatePuck((puck.x + delta).coerceIn(-1f, 1f), puck.y, null, null, false, null, null, null, null, null)
                                    true
                                }
                                Key.Menu -> {
                                    puckSteerActive = false
                                    true
                                }
                                else -> false
                            }
                        },
                    cornerRadius = 24.dp,
                    containerColor = SurroundColors.SurfaceContainerHigh,
                    focusedContainerColor = SurroundColors.SurfaceContainerHigh
                ) {
                    RoomStageView(
                        setup = state.setup,
                        modifier = Modifier.fillMaxSize(),
                        layoutPreset = state.speakerLayoutPreset,
                        isPlaying = puck.playing,
                        showPuck = true,
                        puckX = puck.x,
                        puckY = puck.y,
                        channelGains = puck.channelGains,
                        subwooferEnabled = puck.subwooferEnabled
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = s.puckSourcePrefix(s.sourceLabel(puck.sourceType)) +
                            if (puck.sourceType == SignalSourceType.SINE) " (${puck.frequencyHz} Hz)" else "",
                        fontFamily = MonoFontFamily,
                        fontSize = 12.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                    Text(
                        text = s.virtualDistance("%.1f".format(distanceM)),
                        fontFamily = MonoFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.Primary
                    )
                }
            }

            // Right: Control Sidebar (full column without channel meters so all controls remain visible)
            Column(
                modifier = Modifier
                    .weight(0.36f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(28.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = s.roomControlUpper,
                        fontFamily = MonoFontFamily,
                        fontSize = 12.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                    Text(
                        text = "DSP ${state.setup.code}",
                        fontFamily = MonoFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.Primary
                    )
                }

                // Interactive Control Sidebar
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TvPillSurface(
                            onClick = { onUpdatePuck(null, null, null, null, null, null, null, null, null, !puck.playing) },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(sideMenuFocus),
                            containerColor = if (puck.playing) SurroundColors.Secondary else SurroundColors.Primary,
                            focusedContainerColor = if (puck.playing) SurroundColors.Secondary else SurroundColors.Primary,
                            contentColor = if (puck.playing) SurroundColors.OnSecondary else SurroundColors.OnPrimary,
                            focusedContentColor = if (puck.playing) SurroundColors.OnSecondary else SurroundColors.OnPrimary,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                if (puck.playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = when {
                                    puck.playing && state.loadingAudioTrackId != null -> s.puckDecodingTv
                                    puck.playing -> s.pauseBtn
                                    else -> s.startBtn
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        TvPillSurface(
                            onClick = { puckSteerActive = true },
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Rounded.Radar, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text(s.puckBtnLabel, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TvPillSurface(
                            onClick = {
                                val nextAuto = !puck.autoOrbit
                                onUpdatePuck(null, null, null, null, nextAuto, null, null, null, null, if (nextAuto) true else null)
                            },
                            modifier = Modifier.weight(1f),
                            containerColor = if (puck.autoOrbit) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainerHigh,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Rounded.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(s.orbitStateLabel(puck.autoOrbit), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        TvPillSurface(
                            onClick = {
                                val speeds = OrbitSpeed.entries
                                val nextSpeed = speeds[(puck.orbitSpeed.ordinal + 1) % speeds.size]
                                onUpdatePuck(null, null, null, null, null, nextSpeed, null, null, null, null)
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(s.orbitSpeedLabel(puck.orbitSpeed), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TvPillSurface(
                            onClick = {
                                val nextPan = if (puck.panningMode == PanningMode.SWEET_SPOT_VBAP) {
                                    PanningMode.ROOM_DISTANCE
                                } else PanningMode.SWEET_SPOT_VBAP
                                onUpdatePuck(null, null, nextPan, null, null, null, null, null, null, null)
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Rounded.CenterFocusStrong, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(s.panningModeLabel(puck.panningMode), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (state.setup.hasSubwoofer) {
                            TvPillSurface(
                                onClick = {
                                    onUpdatePuck(null, null, null, !puck.subwooferEnabled, null, null, null, null, null, null)
                                },
                                containerColor = if (puck.subwooferEnabled) SurroundColors.SecondaryContainer else SurroundColors.SurfaceContainerHigh,
                                contentColor = if (puck.subwooferEnabled) SurroundColors.Secondary else SurroundColors.OnSurfaceVariant,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(".1 Sub", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    TvPillSurface(
                        onClick = {
                            val sources = SignalSourceType.entries
                            val nextSrc = sources[(puck.sourceType.ordinal + 1) % sources.size]
                            val audioId = if (nextSrc == SignalSourceType.CUSTOM_AUDIO) {
                                puck.audioId ?: state.audioLibrary.firstOrNull()?.id
                            } else puck.audioId
                            onUpdatePuck(null, null, null, null, null, null, nextSrc, null, audioId, null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = s.sourcePrefix(s.sourceLabel(puck.sourceType)),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (puck.sourceType == SignalSourceType.CUSTOM_AUDIO) {
                        val tracks = state.audioLibrary
                        val activeIdx = tracks.indexOfFirst { it.id == puck.audioId }.takeIf { it >= 0 } ?: 0
                        val activeTrack = tracks.getOrNull(activeIdx)
                        TvPillSurface(
                            onClick = {
                                if (tracks.isNotEmpty()) {
                                    val nextTrack = tracks[(activeIdx + 1) % tracks.size]
                                    onUpdatePuck(null, null, null, null, null, null, SignalSourceType.CUSTOM_AUDIO, null, nextTrack.id, null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = SurroundColors.PrimaryContainer,
                            contentColor = SurroundColors.OnPrimaryContainer,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = if (activeTrack != null) {
                                    s.trackCycleLabel(activeIdx + 1, tracks.size, activeTrack.name)
                                } else {
                                    s.noAudioUploaded
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
