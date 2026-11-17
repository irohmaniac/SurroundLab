package de.surroundlab.app.ui.mobile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SquareFoot
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.surroundlab.app.domain.model.OrbitSpeed
import de.surroundlab.app.domain.model.PanningMode
import de.surroundlab.app.domain.model.SignalSourceType
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.ui.components.RoomStageView
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors
import kotlin.math.log10
import kotlin.math.roundToInt

@Composable
fun MobilePuckScreen(
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
    BackHandler { onBack() }
    val puck = state.puck
    var lastDragSendMs by remember { mutableLongStateOf(0L) }
    var sourceMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurroundColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = SurroundColors.SurfaceContainerHigh,
                        contentColor = SurroundColors.OnSurface
                    )
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = s.back)
                }
                Column {
                    Text(
                        text = s.puckTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = SurroundColors.OnBackground
                    )
                    Text(
                        text = s.puckMobileSubtitle,
                        fontFamily = MonoFontFamily,
                        fontSize = 11.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurroundColors.TertiaryContainer)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "X:${(puck.x * 100).roundToInt()} Y:${(puck.y * 100).roundToInt()}",
                    fontFamily = MonoFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.OnTertiaryContainer
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Segmented Panning Mode Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(SurroundColors.SurfaceContainer)
                    .padding(4.dp)
            ) {
                PanningMode.entries.forEach { mode ->
                    val isSel = puck.panningMode == mode
                    val icon = if (mode == PanningMode.SWEET_SPOT_VBAP) {
                        Icons.Rounded.CenterFocusStrong
                    } else {
                        Icons.Rounded.SquareFoot
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .background(if (isSel) SurroundColors.Primary else SurroundColors.SurfaceContainer)
                            .clickable { onUpdatePuck(null, null, mode, null, null, null, null, null, null, null) }
                            .padding(vertical = 9.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = if (isSel) SurroundColors.OnPrimary else SurroundColors.OnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = s.panningModeLabel(mode),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) SurroundColors.OnPrimary else SurroundColors.OnSurfaceVariant
                        )
                    }
                }
            }

            // Control Pills Bar: Sub toggle, Auto-Orbit, Source Dropdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.setup.hasSubwoofer) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Switch(
                            checked = puck.subwooferEnabled,
                            onCheckedChange = { enabled ->
                                onUpdatePuck(null, null, null, enabled, null, null, null, null, null, null)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SurroundColors.OnSecondary,
                                checkedTrackColor = SurroundColors.Secondary
                            )
                        )
                        Text(
                            text = ".1 SUB",
                            fontFamily = MonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.Secondary
                        )
                    }
                }

                // Auto-Orbit Pill
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (puck.autoOrbit) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainerHighest
                        )
                        .clickable {
                            val nextOrbit = !puck.autoOrbit
                            onUpdatePuck(null, null, null, null, nextOrbit, null, null, null, null, if (nextOrbit) true else null)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        Icons.Rounded.Sync,
                        contentDescription = null,
                        tint = SurroundColors.Primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (puck.autoOrbit) s.orbitWithSpeed(s.orbitSpeedLabel(puck.orbitSpeed)) else s.autoOrbitBtn,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.Primary
                    )
                }

                // Source Select Pill
                Box {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurroundColors.SurfaceContainerHighest)
                            .clickable { sourceMenuExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = s.sourceLabel(puck.sourceType),
                            fontFamily = MonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.Primary
                        )
                    }
                    DropdownMenu(
                        expanded = sourceMenuExpanded,
                        onDismissRequest = { sourceMenuExpanded = false }
                    ) {
                        SignalSourceType.entries.forEach { src ->
                            DropdownMenuItem(
                                text = { Text(s.sourceLabel(src)) },
                                onClick = {
                                    sourceMenuExpanded = false
                                    val audioId = if (src == SignalSourceType.CUSTOM_AUDIO) {
                                        puck.audioId ?: state.audioLibrary.firstOrNull()?.id
                                    } else puck.audioId
                                    onUpdatePuck(null, null, null, null, null, null, src, null, audioId, null)
                                }
                            )
                        }
                    }
                }
            }

            if (puck.sourceType == SignalSourceType.CUSTOM_AUDIO) {
                if (state.audioLibrary.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurroundColors.SurfaceContainer)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LibraryMusic,
                            contentDescription = null,
                            tint = SurroundColors.Secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = s.noAudioOnTvHint,
                            fontSize = 12.sp,
                            color = SurroundColors.Secondary
                        )
                    }
                } else {
                    val effectiveAudioId = puck.audioId ?: state.audioLibrary.firstOrNull()?.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        state.audioLibrary.forEach { track ->
                            val isSel = effectiveAudioId == track.id
                            Row(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if (isSel) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainer
                                    )
                                    .clickable {
                                        onUpdatePuck(
                                            null, null, null, null, null, null,
                                            SignalSourceType.CUSTOM_AUDIO, null, track.id, null
                                        )
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSel) Icons.Rounded.Check else Icons.Rounded.LibraryMusic,
                                    contentDescription = null,
                                    tint = if (isSel) SurroundColors.Primary else SurroundColors.OnSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = track.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) SurroundColors.OnPrimaryContainer else SurroundColors.OnSurface
                                )
                            }
                        }
                    }
                }
            }

            if (puck.autoOrbit) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OrbitSpeed.entries.forEach { spd ->
                        val isSel = puck.orbitSpeed == spd
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(CircleShape)
                                .background(if (isSel) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainer)
                                .clickable { onUpdatePuck(null, null, null, null, true, spd, null, null, null, null) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = s.speedPrefix(s.orbitSpeedLabel(spd)),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) SurroundColors.Primary else SurroundColors.OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 2D Touch Radar Stage (throttled ~40 Hz for smooth LAN updates)
            RoomStageView(
                setup = state.setup,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.02f)
                    .heightIn(max = 340.dp),
                layoutPreset = state.speakerLayoutPreset,
                isPlaying = puck.playing,
                showPuck = true,
                puckX = puck.x,
                puckY = puck.y,
                channelGains = puck.channelGains,
                subwooferEnabled = puck.subwooferEnabled,
                onPuckDrag = { nx, ny ->
                    val now = System.currentTimeMillis()
                    if (now - lastDragSendMs >= 25L) {
                        lastDragSendMs = now
                        onUpdatePuck(nx, ny, null, null, false, null, null, null, null, null)
                    }
                }
            )

            // Live Gain Matrix Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                state.setup.channels.take(6).forEach { ch ->
                    val gain = if (ch.isLfe && !puck.subwooferEnabled) 0f else (puck.channelGains[ch] ?: 0f)
                    val dbText = if (gain <= 0.001f) "-∞" else "${"%.1f".format(20.0 * log10(gain.toDouble()))}"
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = ch.shortLabel,
                            fontFamily = MonoFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ch.isLfe) SurroundColors.Secondary else SurroundColors.OnSurfaceVariant
                        )
                        Text(
                            text = "${dbText}dB",
                            fontFamily = MonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ch.isLfe) SurroundColors.Secondary else SurroundColors.Primary
                        )
                    }
                }
            }
        }

        // Bottom Play/Pause Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurroundColors.SurfaceContainer.copy(alpha = 0.85f))
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Button(
                onClick = { onUpdatePuck(null, null, null, null, null, null, null, null, null, !puck.playing) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (puck.playing) SurroundColors.Secondary else SurroundColors.Primary,
                    contentColor = if (puck.playing) SurroundColors.OnSecondary else SurroundColors.OnPrimary
                )
            ) {
                Icon(
                    imageVector = if (puck.playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = when {
                        puck.playing && state.loadingAudioTrackId != null -> s.puckDecodingMobile
                        puck.playing -> s.puckPauseMobile
                        else -> s.puckStartMobile
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
