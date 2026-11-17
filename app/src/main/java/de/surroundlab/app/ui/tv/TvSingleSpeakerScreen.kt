package de.surroundlab.app.ui.tv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
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
import de.surroundlab.app.domain.model.CustomAudioTrack
import de.surroundlab.app.domain.model.SignalSourceType
import de.surroundlab.app.domain.model.SpeakerChannel
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.ui.components.RoomStageView
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors

@Composable
fun TvSingleSpeakerScreen(
    state: SurroundAppState,
    onBack: () -> Unit,
    onToggleSpeaker: (SpeakerChannel) -> Unit,
    onUpdateChannelConfig: (SpeakerChannel, SignalSourceType, Int, String?, Boolean) -> Unit
) {
    val s = LocalAppStrings.current
    val channels = state.setup.channels
    var focusedChannel by remember(channels) {
        mutableStateOf(state.singleSpeaker.activeChannel ?: channels.firstOrNull())
    }
    var configTargetChannel by remember { mutableStateOf<SpeakerChannel?>(null) }

    val rowFocusRequesters = remember(channels) { channels.map { FocusRequester() } }
    val gearFocusRequesters = remember(channels) { channels.map { FocusRequester() } }

    LaunchedEffect(channels) {
        rowFocusRequesters.firstOrNull()?.requestFocus()
    }

    BackHandler {
        if (configTargetChannel != null) {
            configTargetChannel = null
        } else {
            onBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurroundColors.Background)
                .padding(horizontal = 48.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    TvPillSurface(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = s.back)
                        Text(s.homeHubBtn, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(
                            text = s.singleTitle,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.OnBackground
                        )
                        Text(
                            text = s.singleTvSubtitle(state.setup.displayName),
                            fontSize = 15.sp,
                            color = SurroundColors.OnSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TvKeyLegendPill("▲/▼", s.legendChannel)
                    TvKeyLegendPill("OK", s.legendStartStop)
                    TvKeyLegendPill("►", s.legendSignalChoice)
                }
            }

            // Split View: Left (~55%) Speaker List + Right (~45%) Room Diagram
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(0.55f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    itemsIndexed(channels, key = { _, ch -> ch.name }) { idx, ch ->
                        val isPlayingThis = state.singleSpeaker.playing && state.singleSpeaker.activeChannel == ch
                        val cfg = state.singleSpeaker.configFor(ch)
                        val sourceSummary = when (cfg.sourceType) {
                            SignalSourceType.PINK_NOISE -> s.pinkNoiseFreqRange
                            SignalSourceType.SINE -> s.sineToneLabel(cfg.frequencyHz)
                            SignalSourceType.CUSTOM_AUDIO -> {
                                val track = state.audioLibrary.firstOrNull { it.id == cfg.audioId }
                                    ?: state.audioLibrary.firstOrNull()
                                track?.name ?: s.noAudioSelected
                            }
                        }

                        val accent = if (ch.isLfe) SurroundColors.Secondary else SurroundColors.Primary
                        val activeBg = if (ch.isLfe) SurroundColors.SecondaryContainer else SurroundColors.PrimaryContainer
                        val activeFg = if (ch.isLfe) SurroundColors.OnSecondaryContainer else SurroundColors.OnPrimaryContainer

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TvCardSurface(
                                onClick = { onToggleSpeaker(ch) },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(rowFocusRequesters[idx])
                                    .onFocusChanged { if (it.isFocused) focusedChannel = ch }
                                    .onPreviewKeyEvent { evt ->
                                        if (evt.type == KeyEventType.KeyDown && evt.key == Key.DirectionRight) {
                                            gearFocusRequesters[idx].requestFocus()
                                            true
                                        } else false
                                    },
                                cornerRadius = 22.dp,
                                containerColor = if (isPlayingThis) activeBg else SurroundColors.SurfaceContainer,
                                focusedContainerColor = if (isPlayingThis) activeBg else SurroundColors.SurfaceContainerHigh,
                                borderColor = accent,
                                selected = isPlayingThis
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 18.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(
                                                    if (isPlayingThis) accent else SurroundColors.SurfaceContainerHighest
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = ch.shortLabel,
                                                fontFamily = MonoFontFamily,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isPlayingThis) {
                                                    if (ch.isLfe) SurroundColors.OnSecondary else SurroundColors.OnPrimary
                                                } else SurroundColors.OnSurface
                                            )
                                        }
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = s.channelName(ch),
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isPlayingThis) activeFg else SurroundColors.OnSurface
                                                )
                                                if (isPlayingThis) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(CircleShape)
                                                            .background(accent)
                                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = s.activeBadgeUpper,
                                                            fontFamily = MonoFontFamily,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (ch.isLfe) SurroundColors.OnSecondary else SurroundColors.OnPrimary
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = sourceSummary,
                                                fontFamily = MonoFontFamily,
                                                fontSize = 12.sp,
                                                color = if (isPlayingThis) activeFg.copy(alpha = 0.85f) else SurroundColors.OnSurfaceVariant
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(if (isPlayingThis) accent else SurroundColors.SurfaceContainerHighest)
                                            .padding(horizontal = 14.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingThis) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                                            contentDescription = null,
                                            tint = if (isPlayingThis) {
                                                if (ch.isLfe) SurroundColors.OnSecondary else SurroundColors.OnPrimary
                                            } else accent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = if (isPlayingThis) s.stopBtn else s.testBtn,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPlayingThis) {
                                                if (ch.isLfe) SurroundColors.OnSecondary else SurroundColors.OnPrimary
                                            } else SurroundColors.OnSurface
                                        )
                                    }
                                }
                            }

                            // Gear Button (reachable via Right arrow from row)
                            TvCardSurface(
                                onClick = { configTargetChannel = ch },
                                modifier = Modifier
                                    .size(58.dp)
                                    .focusRequester(gearFocusRequesters[idx])
                                    .onFocusChanged { if (it.isFocused) focusedChannel = ch }
                                    .onPreviewKeyEvent { evt ->
                                        if (evt.type == KeyEventType.KeyDown && evt.key == Key.DirectionLeft) {
                                            rowFocusRequesters[idx].requestFocus()
                                            true
                                        } else false
                                    },
                                cornerRadius = 29.dp,
                                containerColor = SurroundColors.SurfaceContainer,
                                focusedContainerColor = SurroundColors.PrimaryContainer,
                                borderColor = accent
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = s.configureSignalDesc(s.channelName(ch)),
                                        tint = accent
                                    )
                                }
                            }
                        }
                    }
                }

                // Right Column: Synchronized Room Diagram
                Column(
                    modifier = Modifier
                        .weight(0.45f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(28.dp))
                        .background(SurroundColors.SurfaceContainer)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    val activeCh = state.singleSpeaker.activeChannel
                    val displayCh = activeCh ?: focusedChannel
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = s.syncRoomPlanUpper,
                                fontFamily = MonoFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurroundColors.Primary
                            )
                            Text(
                                text = displayCh?.let { s.channelName(it) } ?: s.ready,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurroundColors.OnSurface
                            )
                        }
                        if (state.singleSpeaker.playing && activeCh != null) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SurroundColors.PrimaryContainer)
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = s.signalActiveUpper,
                                    fontFamily = MonoFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurroundColors.Primary
                                )
                            }
                        }
                    }

                    RoomStageView(
                        setup = state.setup,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 14.dp),
                        layoutPreset = state.speakerLayoutPreset,
                        activeSpeaker = activeCh,
                        highlightedSpeaker = focusedChannel,
                        isPlaying = state.singleSpeaker.playing
                    )

                    val cfg = displayCh?.let { state.singleSpeaker.configFor(it) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = s.sourcePrefix(s.sourceLabel(cfg?.sourceType ?: SignalSourceType.PINK_NOISE)),
                            fontFamily = MonoFontFamily,
                            fontSize = 12.sp,
                            color = SurroundColors.OnSurfaceVariant
                        )
                        Text(
                            text = if (cfg?.sourceType == SignalSourceType.SINE) "${cfg.frequencyHz} Hz" else "48 kHz PCM",
                            fontFamily = MonoFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.Primary
                        )
                    }
                }
            }
        }

        // Channel Signal Configuration Dialog
        configTargetChannel?.let { targetCh ->
            val currentCfg = state.singleSpeaker.configFor(targetCh)
            TvChannelConfigDialog(
                channel = targetCh,
                initialSource = currentCfg.sourceType,
                initialFrequency = currentCfg.frequencyHz,
                initialAudioId = currentCfg.audioId,
                tracks = state.audioLibrary,
                onApply = { src, freq, audioId, applyAll ->
                    onUpdateChannelConfig(targetCh, src, freq, audioId, applyAll)
                    configTargetChannel = null
                },
                onDismiss = { configTargetChannel = null }
            )
        }
    }
}

@Composable
private fun TvChannelConfigDialog(
    channel: SpeakerChannel,
    initialSource: SignalSourceType,
    initialFrequency: Int,
    initialAudioId: String?,
    tracks: List<CustomAudioTrack>,
    onApply: (SignalSourceType, Int, String?, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    var selectedSource by remember(channel) { mutableStateOf(initialSource) }
    var freq by remember(channel) { mutableIntStateOf(initialFrequency) }
    var selectedAudioId by remember(channel, initialAudioId, tracks) {
        mutableStateOf(tracks.firstOrNull { it.id == initialAudioId }?.id ?: tracks.firstOrNull()?.id)
    }
    var applyToAll by remember { mutableStateOf(false) }

    val firstFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { firstFocus.requestFocus() }

    TvModalHost(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .width(620.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(SurroundColors.SurfaceContainerHigh)
                .padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = s.channelDialogTitle(s.channelName(channel), channel.shortLabel),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.OnSurface
                )
                TvPillSurface(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = s.close)
                }
            }

            // Source Type Options
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SignalSourceType.entries.forEachIndexed { idx, src ->
                    val isSel = selectedSource == src
                    TvPillSurface(
                        onClick = { selectedSource = src },
                        modifier = if (idx == 0) Modifier.focusRequester(firstFocus) else Modifier,
                        containerColor = if (isSel) SurroundColors.Primary else SurroundColors.SurfaceContainer,
                        focusedContainerColor = if (isSel) SurroundColors.Primary else SurroundColors.SurfaceContainerHighest,
                        contentColor = if (isSel) SurroundColors.OnPrimary else SurroundColors.OnSurface,
                        focusedContentColor = if (isSel) SurroundColors.OnPrimary else SurroundColors.Primary
                    ) {
                        Text(s.sourceLabel(src), fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (selectedSource == SignalSourceType.SINE) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurroundColors.SurfaceContainer)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = s.sineFreqLabel(freq),
                        fontFamily = MonoFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.Primary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(-100, -10, 10, 100).forEach { delta ->
                            TvPillSurface(
                                onClick = { freq = (freq + delta).coerceIn(20, 20000) },
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (delta > 0) "+$delta Hz" else "$delta Hz",
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(60, 80, 400, 1000, 5000).forEach { presetHz ->
                            TvPillSurface(
                                onClick = { freq = presetHz },
                                containerColor = if (freq == presetHz) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainerHighest,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text("${presetHz}Hz", fontFamily = MonoFontFamily, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            if (selectedSource == SignalSourceType.CUSTOM_AUDIO) {
                if (tracks.isEmpty()) {
                    Text(
                        text = s.noCustomAudioTvHint,
                        color = SurroundColors.Secondary,
                        fontSize = 14.sp
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(tracks, key = { _, t -> t.id }) { _, track ->
                            val isSel = selectedAudioId == track.id
                            val sec = (track.durationMs / 1000L).coerceAtLeast(0L)
                            TvCardSurface(
                                onClick = { selectedAudioId = track.id },
                                cornerRadius = 14.dp,
                                containerColor = if (isSel) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainer,
                                selected = isSel
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = track.name,
                                            color = SurroundColors.OnSurface,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${sec}s • ${track.sampleRate} Hz • ${track.channels}ch",
                                            fontFamily = MonoFontFamily,
                                            fontSize = 11.sp,
                                            color = SurroundColors.OnSurfaceVariant
                                        )
                                    }
                                    if (isSel) {
                                        Icon(Icons.Rounded.Check, contentDescription = null, tint = SurroundColors.Primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Apply to all channels toggle + Confirm button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TvPillSurface(
                    onClick = { applyToAll = !applyToAll },
                    containerColor = if (applyToAll) SurroundColors.SecondaryContainer else SurroundColors.SurfaceContainer,
                    contentColor = if (applyToAll) SurroundColors.OnSecondaryContainer else SurroundColors.OnSurface
                ) {
                    if (applyToAll) {
                        Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                    Text(s.applyToAllChannels, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }

                TvPillSurface(
                    onClick = { onApply(selectedSource, freq, selectedAudioId, applyToAll) },
                    containerColor = SurroundColors.Primary,
                    focusedContainerColor = SurroundColors.Primary,
                    contentColor = SurroundColors.OnPrimary,
                    focusedContentColor = SurroundColors.OnPrimary
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null)
                    Text(s.apply, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
