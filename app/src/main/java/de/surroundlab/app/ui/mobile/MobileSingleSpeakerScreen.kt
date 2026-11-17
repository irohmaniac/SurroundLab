package de.surroundlab.app.ui.mobile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.surroundlab.app.domain.model.CustomAudioTrack
import de.surroundlab.app.domain.model.SignalSourceType
import de.surroundlab.app.domain.model.SpeakerChannel
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.ui.components.RoomStageView
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors
import kotlin.math.roundToInt

@Composable
fun MobileSingleSpeakerScreen(
    state: SurroundAppState,
    onBack: () -> Unit,
    onToggleSpeaker: (SpeakerChannel) -> Unit,
    onUpdateChannelConfig: (SpeakerChannel, SignalSourceType, Int, String?, Boolean) -> Unit
) {
    val s = LocalAppStrings.current
    var sheetChannel by remember { mutableStateOf<SpeakerChannel?>(null) }

    BackHandler {
        if (sheetChannel != null) {
            sheetChannel = null
        } else {
            onBack()
        }
    }

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
                        text = s.singleTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = SurroundColors.OnBackground
                    )
                    Text(
                        text = s.singleMobileSubtitle(state.setup.code),
                        fontFamily = MonoFontFamily,
                        fontSize = 11.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }
            }

            val activeCh = state.singleSpeaker.activeChannel
            if (state.singleSpeaker.playing && activeCh != null) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SurroundColors.PrimaryContainer)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = s.singleActiveBadge(activeCh.shortLabel),
                        fontFamily = MonoFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.OnPrimaryContainer
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                RoomStageView(
                    setup = state.setup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.18f)
                        .heightIn(max = 280.dp)
                        .padding(bottom = 6.dp),
                    layoutPreset = state.speakerLayoutPreset,
                    activeSpeaker = state.singleSpeaker.activeChannel,
                    isPlaying = state.singleSpeaker.playing,
                    onSpeakerClick = onToggleSpeaker
                )
            }

            items(state.setup.channels, key = { it.name }) { ch ->
                val isPlayingThis = state.singleSpeaker.playing && state.singleSpeaker.activeChannel == ch
                val cfg = state.singleSpeaker.configFor(ch)
                val accent = if (ch.isLfe) SurroundColors.Secondary else SurroundColors.Primary
                val activeBg = if (ch.isLfe) SurroundColors.SecondaryContainer else SurroundColors.PrimaryContainer
                val activeFg = if (ch.isLfe) SurroundColors.OnSecondaryContainer else SurroundColors.OnPrimaryContainer

                val sourceDesc = when (cfg.sourceType) {
                    SignalSourceType.PINK_NOISE -> "${s.sourceLabel(SignalSourceType.PINK_NOISE)} • ${state.masterGainDb.roundToInt()} dBFS"
                    SignalSourceType.SINE -> s.sineToneLabel(cfg.frequencyHz)
                    SignalSourceType.CUSTOM_AUDIO -> {
                        val tr = state.audioLibrary.firstOrNull { it.id == cfg.audioId }
                            ?: state.audioLibrary.firstOrNull()
                        tr?.name ?: s.noAudioShort
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isPlayingThis) activeBg else SurroundColors.SurfaceContainer)
                        .clickable { onToggleSpeaker(ch) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isPlayingThis) accent else SurroundColors.SurfaceContainerHighest),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ch.shortLabel,
                                fontFamily = MonoFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPlayingThis) {
                                    if (ch.isLfe) SurroundColors.OnSecondary else SurroundColors.OnPrimary
                                } else SurroundColors.OnSurface
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = s.channelName(ch),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isPlayingThis) activeFg else SurroundColors.OnSurface
                            )
                            Text(
                                text = sourceDesc,
                                fontFamily = MonoFontFamily,
                                fontSize = 11.sp,
                                color = if (isPlayingThis) activeFg.copy(alpha = 0.85f) else SurroundColors.OnSurfaceVariant
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isPlayingThis) accent else SurroundColors.SurfaceContainerHigh)
                                .clickable { onToggleSpeaker(ch) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlayingThis) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                tint = if (isPlayingThis) {
                                    if (ch.isLfe) SurroundColors.OnSecondary else SurroundColors.OnPrimary
                                } else accent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isPlayingThis) s.stopBtn else s.testBtn,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPlayingThis) {
                                    if (ch.isLfe) SurroundColors.OnSecondary else SurroundColors.OnPrimary
                                } else SurroundColors.OnSurface
                            )
                        }

                        IconButton(
                            onClick = { sheetChannel = ch },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = SurroundColors.SurfaceContainerHighest,
                                contentColor = if (ch.isLfe) SurroundColors.Secondary else SurroundColors.OnSurfaceVariant
                            ),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = s.configureSignalDesc(s.channelName(ch)),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }

    sheetChannel?.let { targetCh ->
        val cfg = state.singleSpeaker.configFor(targetCh)
        ChannelSourceBottomSheet(
            channel = targetCh,
            initialSource = cfg.sourceType,
            initialFreq = cfg.frequencyHz,
            initialAudioId = cfg.audioId,
            tracks = state.audioLibrary,
            onDismiss = { sheetChannel = null },
            onApply = { src, freq, audioId, applyAll ->
                onUpdateChannelConfig(targetCh, src, freq, audioId, applyAll)
                sheetChannel = null
            }
        )
    }
}

@Composable
private fun ChannelSourceBottomSheet(
    channel: SpeakerChannel,
    initialSource: SignalSourceType,
    initialFreq: Int,
    initialAudioId: String?,
    tracks: List<CustomAudioTrack>,
    onDismiss: () -> Unit,
    onApply: (SignalSourceType, Int, String?, Boolean) -> Unit
) {
    val s = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var source by remember(channel) { mutableStateOf(initialSource) }
    var freq by remember(channel) { mutableIntStateOf(initialFreq) }
    var audioId by remember(channel, initialAudioId, tracks) {
        mutableStateOf(tracks.firstOrNull { it.id == initialAudioId }?.id ?: tracks.firstOrNull()?.id)
    }
    var applyAll by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurroundColors.SurfaceContainerHigh
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${s.channelName(channel)} (${channel.shortLabel})",
                style = MaterialTheme.typography.titleLarge,
                color = SurroundColors.OnSurface
            )
            Text(
                text = s.channelSheetSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = SurroundColors.OnSurfaceVariant
            )

            SignalSourceType.entries.forEach { option ->
                val selected = source == option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (selected) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainer
                        )
                        .clickable { source = option }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RadioButton(
                        selected = selected,
                        onClick = { source = option },
                        colors = RadioButtonDefaults.colors(selectedColor = SurroundColors.Primary)
                    )
                    Column {
                        Text(
                            text = s.sourceLabel(option),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (selected) SurroundColors.Primary else SurroundColors.OnSurface
                        )
                        Text(
                            text = when (option) {
                                SignalSourceType.PINK_NOISE -> s.pinkNoiseDesc
                                SignalSourceType.SINE -> s.sineDesc(freq)
                                SignalSourceType.CUSTOM_AUDIO -> s.customAudioDesc
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = SurroundColors.OnSurfaceVariant
                        )
                    }
                }
            }

            if (source == SignalSourceType.SINE) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurroundColors.SurfaceContainer)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(s.frequencyLabel, color = SurroundColors.OnSurfaceVariant)
                        Text(
                            text = "$freq Hz",
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.Primary
                        )
                    }
                    Slider(
                        value = freq.toFloat(),
                        onValueChange = { freq = it.roundToInt().coerceIn(20, 20000) },
                        valueRange = 20f..20000f,
                        colors = SliderDefaults.colors(
                            thumbColor = SurroundColors.Primary,
                            activeTrackColor = SurroundColors.Primary
                        )
                    )
                }
            }

            if (source == SignalSourceType.CUSTOM_AUDIO) {
                if (tracks.isEmpty()) {
                    Text(
                        text = s.noCustomAudioMobileHint,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SurroundColors.Secondary
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 210.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(tracks, key = { it.id }) { track ->
                            val isSel = audioId == track.id
                            val sec = (track.durationMs / 1000L).coerceAtLeast(0L)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSel) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainer)
                                    .clickable { audioId = track.id }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.name,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSel) SurroundColors.Primary else SurroundColors.OnSurface
                                    )
                                    Text(
                                        text = "${sec}s • ${track.sampleRate} Hz • ${track.channels}ch",
                                        fontFamily = MonoFontFamily,
                                        fontSize = 11.sp,
                                        color = SurroundColors.OnSurfaceVariant
                                    )
                                }
                                if (isSel) Icon(Icons.Rounded.Check, contentDescription = null, tint = SurroundColors.Primary)
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { applyAll = !applyAll },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = applyAll, onCheckedChange = { applyAll = it })
                Text(
                    text = s.applyToAllChannels,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SurroundColors.OnSurface
                )
            }

            Button(
                onClick = { onApply(source, freq, audioId, applyAll) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurroundColors.Primary,
                    contentColor = SurroundColors.OnPrimary
                )
            ) {
                Text(s.applySignalBtn, fontWeight = FontWeight.Bold)
            }
        }
    }
}
