package de.surroundlab.app.ui.tv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import de.surroundlab.app.domain.model.GeneratorPreset
import de.surroundlab.app.domain.model.SignalSourceType
import de.surroundlab.app.domain.model.SpeakerChannel
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TvGeneratorScreen(
    state: SurroundAppState,
    onBack: () -> Unit,
    onUpdateGenerator: (
        preset: GeneratorPreset?,
        customChannels: Set<SpeakerChannel>?,
        signalType: SignalSourceType?,
        frequencyHz: Int?,
        playing: Boolean?
    ) -> Unit
) {
    BackHandler { onBack() }

    val s = LocalAppStrings.current
    val stepperFocus = remember { FocusRequester() }
    val playFocus = remember { FocusRequester() }
    val gen = state.generator
    val isSine = gen.signalType == SignalSourceType.SINE
    val activeChannels = gen.preset.resolveChannels(state.setup, gen.customChannels)

    LaunchedEffect(Unit) {
        if (isSine) stepperFocus.requestFocus() else playFocus.requestFocus()
    }

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
                        text = s.generatorTitle,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.OnBackground
                    )
                    Text(
                        text = s.activeChannelsPrefix(activeChannels.joinToString(" · ") { it.shortLabel }.ifEmpty { s.noneText }),
                        fontSize = 15.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (isSine) {
                    TvKeyLegendPill("▲/▼", s.legendFreq)
                }
                TvKeyLegendPill("OK", "${s.startAction} / ${s.pauseAction}")
            }
        }

        // Main Split Content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Left Column (8 cols)
            Column(
                modifier = Modifier
                    .weight(0.66f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Preset Chips Row
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GeneratorPreset.entries
                        .filter { it.isAvailableFor(state.setup) }
                        .forEach { preset ->
                            val selected = gen.preset == preset
                            val isSubPreset = preset == GeneratorPreset.P0_1_SUB
                            TvPillSurface(
                                onClick = { onUpdateGenerator(preset, null, null, null, null) },
                                containerColor = when {
                                    selected && isSubPreset -> SurroundColors.Secondary
                                    selected -> SurroundColors.Primary
                                    isSubPreset -> SurroundColors.SecondaryContainer
                                    else -> SurroundColors.SurfaceContainer
                                },
                                focusedContainerColor = when {
                                    selected && isSubPreset -> SurroundColors.Secondary
                                    selected -> SurroundColors.Primary
                                    else -> SurroundColors.SurfaceContainerHigh
                                },
                                contentColor = when {
                                    selected && isSubPreset -> SurroundColors.OnSecondary
                                    selected -> SurroundColors.OnPrimary
                                    isSubPreset -> SurroundColors.OnSecondaryContainer
                                    else -> SurroundColors.OnSurface
                                },
                                focusedContentColor = when {
                                    selected && isSubPreset -> SurroundColors.OnSecondary
                                    selected -> SurroundColors.OnPrimary
                                    else -> SurroundColors.Primary
                                },
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
                            ) {
                                Text(s.generatorPresetLabel(preset), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                }

                // Custom Channel Checkboxes when CUSTOM preset active
                if (gen.preset == GeneratorPreset.CUSTOM) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.setup.channels.forEach { ch ->
                            val enabled = ch in gen.customChannels
                            TvPillSurface(
                                onClick = {
                                    val next = if (enabled) gen.customChannels - ch else gen.customChannels + ch
                                    onUpdateGenerator(null, next, null, null, null)
                                },
                                containerColor = if (enabled) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainer,
                                contentColor = if (enabled) SurroundColors.Primary else SurroundColors.OnSurfaceVariant,
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(ch.shortLabel, fontFamily = MonoFontFamily, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Segmented Signal Switcher (Sine vs Pink Noise)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    val isSine = gen.signalType == SignalSourceType.SINE
                    TvPillSurface(
                        onClick = { onUpdateGenerator(null, null, SignalSourceType.SINE, null, null) },
                        containerColor = if (isSine) SurroundColors.Primary else SurroundColors.SurfaceContainer,
                        focusedContainerColor = if (isSine) SurroundColors.Primary else SurroundColors.SurfaceContainerHigh,
                        contentColor = if (isSine) SurroundColors.OnPrimary else SurroundColors.OnSurface,
                        focusedContentColor = if (isSine) SurroundColors.OnPrimary else SurroundColors.Primary
                    ) {
                        Icon(Icons.Rounded.Waves, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(s.sourceShortLabel(SignalSourceType.SINE), fontWeight = FontWeight.Bold)
                    }
                    TvPillSurface(
                        onClick = { onUpdateGenerator(null, null, SignalSourceType.PINK_NOISE, null, null) },
                        containerColor = if (!isSine) SurroundColors.Primary else SurroundColors.SurfaceContainer,
                        focusedContainerColor = if (!isSine) SurroundColors.Primary else SurroundColors.SurfaceContainerHigh,
                        contentColor = if (!isSine) SurroundColors.OnPrimary else SurroundColors.OnSurface,
                        focusedContentColor = if (!isSine) SurroundColors.OnPrimary else SurroundColors.Primary
                    ) {
                        Icon(Icons.Rounded.GraphicEq, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(s.sourceLabel(SignalSourceType.PINK_NOISE), fontWeight = FontWeight.Bold)
                    }
                }

                if (isSine) {
                    // Central D-Pad Frequency Stepper Card (Initial Focus!)
                    TvCardSurface(
                        onClick = { onUpdateGenerator(null, null, null, null, !gen.playing) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .focusRequester(stepperFocus)
                            .onPreviewKeyEvent { evt ->
                                if (evt.type == KeyEventType.KeyDown) {
                                    val repeat = evt.nativeKeyEvent.repeatCount
                                    val step = when {
                                        repeat > 25 -> 100
                                        repeat > 12 -> 25
                                        repeat > 4 -> 5
                                        else -> 1
                                    }
                                    when (evt.key) {
                                        Key.DirectionUp -> {
                                            onUpdateGenerator(
                                                null, null, null,
                                                (gen.frequencyHz + step).coerceIn(20, 20000),
                                                null
                                            )
                                            true
                                        }
                                        Key.DirectionDown -> {
                                            onUpdateGenerator(
                                                null, null, null,
                                                (gen.frequencyHz - step).coerceIn(20, 20000),
                                                null
                                            )
                                            true
                                        }
                                        else -> false
                                    }
                                } else false
                            },
                        containerColor = SurroundColors.SurfaceContainerHigh,
                        focusedContainerColor = SurroundColors.SurfaceContainerHigh,
                        selected = gen.playing
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.KeyboardArrowUp,
                                    contentDescription = null,
                                    tint = SurroundColors.Primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = s.upStepHint,
                                    fontFamily = MonoFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurroundColors.Primary
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "${gen.frequencyHz}",
                                    fontFamily = MonoFontFamily,
                                    fontSize = 72.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurroundColors.Primary
                                )
                                Text(
                                    text = "Hz",
                                    fontFamily = MonoFontFamily,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurroundColors.OnSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = SurroundColors.Primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = s.downStepHint(gen.playing),
                                    fontFamily = MonoFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurroundColors.Primary
                                )
                            }
                        }
                    }
                } else {
                    Spacer(Modifier.weight(1f))
                }

                // Play/Pause Pill CTA
                TvPillSurface(
                    onClick = { onUpdateGenerator(null, null, null, null, !gen.playing) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(playFocus),
                    containerColor = if (gen.playing) SurroundColors.Secondary else SurroundColors.Primary,
                    focusedContainerColor = if (gen.playing) SurroundColors.Secondary else SurroundColors.Primary,
                    contentColor = if (gen.playing) SurroundColors.OnSecondary else SurroundColors.OnPrimary,
                    focusedContentColor = if (gen.playing) SurroundColors.OnSecondary else SurroundColors.OnPrimary,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (gen.playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = if (gen.playing) {
                                if (gen.signalType == SignalSourceType.PINK_NOISE) s.pinkNoisePauseTv
                                else s.sinePauseTv(gen.frequencyHz)
                            } else {
                                if (gen.signalType == SignalSourceType.PINK_NOISE) s.pinkNoiseStartTv
                                else s.sineStartTv(gen.frequencyHz)
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Right Column (4 cols): Quick Frequency Presets (Sine only)
            if (isSine) {
                Column(
                    modifier = Modifier
                        .weight(0.34f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(28.dp))
                        .background(SurroundColors.SurfaceContainer)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = s.quickSelectUpper,
                            fontFamily = MonoFontFamily,
                            fontSize = 12.sp,
                            color = SurroundColors.OnSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SurroundColors.PrimaryContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = s.referenceUpper,
                                fontFamily = MonoFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurroundColors.OnPrimaryContainer
                            )
                        }
                    }

                    s.quickPresets.forEach { preset ->
                        val isSel = gen.frequencyHz == preset.hz
                        TvCardSurface(
                            onClick = { onUpdateGenerator(null, null, SignalSourceType.SINE, preset.hz, null) },
                            cornerRadius = 16.dp,
                            containerColor = if (isSel) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainerHigh,
                            focusedContainerColor = if (isSel) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainerHighest,
                            selected = isSel
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = preset.title,
                                    fontFamily = MonoFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (preset.hz <= 80) SurroundColors.Secondary else SurroundColors.OnSurface
                                )
                                Text(
                                    text = if (isSel) s.activeBadgeUpper else preset.desc,
                                    fontFamily = MonoFontFamily,
                                    fontSize = 12.sp,
                                    color = if (isSel) SurroundColors.Primary else SurroundColors.OnSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = s.digitalLevelLabel,
                        fontFamily = MonoFontFamily,
                        fontSize = 12.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                    Text(
                        text = "${state.masterGainDb.roundToInt()}.0 dBFS",
                        fontFamily = MonoFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.Primary
                    )
                }
            }
            }
        }
    }
}
