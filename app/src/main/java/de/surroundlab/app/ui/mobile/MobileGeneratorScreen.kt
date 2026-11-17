package de.surroundlab.app.ui.mobile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.surroundlab.app.domain.model.GeneratorPreset
import de.surroundlab.app.domain.model.SignalSourceType
import de.surroundlab.app.domain.model.SpeakerChannel
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MobileGeneratorScreen(
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
    val s = LocalAppStrings.current
    BackHandler { onBack() }
    val gen = state.generator
    var showHzInputDialog by remember { mutableStateOf(false) }

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
                        text = s.generatorTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = SurroundColors.OnBackground
                    )
                    Text(
                        text = s.generatorMobileSubtitle,
                        fontFamily = MonoFontFamily,
                        fontSize = 11.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurroundColors.SurfaceContainerHighest)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${state.masterGainDb.roundToInt()} dBFS",
                    fontFamily = MonoFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.Primary
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Preset Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GeneratorPreset.entries
                    .filter { it.isAvailableFor(state.setup) }
                    .forEach { preset ->
                        val selected = gen.preset == preset
                        val isSub = preset == GeneratorPreset.P0_1_SUB
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    when {
                                        selected && isSub -> SurroundColors.Secondary
                                        selected -> SurroundColors.Primary
                                        isSub -> SurroundColors.SecondaryContainer
                                        else -> SurroundColors.SurfaceContainer
                                    }
                                )
                                .clickable { onUpdateGenerator(preset, null, null, null, null) }
                                .padding(horizontal = 16.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = s.generatorPresetLabel(preset),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    selected && isSub -> SurroundColors.OnSecondary
                                    selected -> SurroundColors.OnPrimary
                                    isSub -> SurroundColors.OnSecondaryContainer
                                    else -> SurroundColors.OnSurface
                                }
                            )
                        }
                    }
            }

            if (gen.preset == GeneratorPreset.CUSTOM) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.setup.channels.forEach { ch ->
                        val enabled = ch in gen.customChannels
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (enabled) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainer
                                )
                                .clickable {
                                    val next = if (enabled) gen.customChannels - ch else gen.customChannels + ch
                                    onUpdateGenerator(null, next, null, null, null)
                                }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = ch.shortLabel,
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (enabled) SurroundColors.Primary else SurroundColors.OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Segmented Signal Button (Sine vs Pink Noise)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(SurroundColors.SurfaceContainer)
                    .padding(4.dp)
            ) {
                val isSine = gen.signalType == SignalSourceType.SINE
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(if (isSine) SurroundColors.Primary else SurroundColors.SurfaceContainer)
                        .clickable { onUpdateGenerator(null, null, SignalSourceType.SINE, null, null) }
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Waves,
                        contentDescription = null,
                        tint = if (isSine) SurroundColors.OnPrimary else SurroundColors.OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = s.sourceShortLabel(SignalSourceType.SINE),
                        fontWeight = FontWeight.Bold,
                        color = if (isSine) SurroundColors.OnPrimary else SurroundColors.OnSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(if (!isSine) SurroundColors.Primary else SurroundColors.SurfaceContainer)
                        .clickable { onUpdateGenerator(null, null, SignalSourceType.PINK_NOISE, null, null) }
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.GraphicEq,
                        contentDescription = null,
                        tint = if (!isSine) SurroundColors.OnPrimary else SurroundColors.OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = s.sourceLabel(SignalSourceType.PINK_NOISE),
                        fontWeight = FontWeight.Bold,
                        color = if (!isSine) SurroundColors.OnPrimary else SurroundColors.OnSurfaceVariant
                    )
                }
            }

            // Tactile Rotary Jog-Wheel Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FrequencyRotaryJogWheel(
                    frequencyHz = gen.frequencyHz,
                    isPinkNoise = gen.signalType == SignalSourceType.PINK_NOISE,
                    onFrequencyChange = { newHz ->
                        onUpdateGenerator(null, null, SignalSourceType.SINE, newHz, null)
                    },
                    onCenterClick = { showHzInputDialog = true }
                )

                // Fine Tuning Stepper Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(-10, -1, 1, 10).forEach { delta ->
                        val label = if (delta > 0) "+$delta" else "$delta"
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SurroundColors.SurfaceContainerHighest)
                                .clickable {
                                    onUpdateGenerator(
                                        null, null, SignalSourceType.SINE,
                                        (gen.frequencyHz + delta).coerceIn(20, 20000),
                                        null
                                    )
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = if (delta == -10 || delta == 10) "$label Hz" else label,
                                fontFamily = MonoFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurroundColors.OnSurface
                            )
                        }
                    }
                }
            }

            // Quick Frequency Presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(80 to "80 Hz", 400 to "400 Hz", 1000 to "1 kHz", 10000 to "10 kHz").forEach { (hz, label) ->
                    val isSel = gen.frequencyHz == hz
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSel) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainer)
                            .clickable { onUpdateGenerator(null, null, SignalSourceType.SINE, hz, null) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontFamily = MonoFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isSel -> SurroundColors.OnPrimaryContainer
                                hz <= 80 -> SurroundColors.Secondary
                                else -> SurroundColors.OnSurface
                            }
                        )
                    }
                }
            }
        }

        // Bottom Play/Pause CTA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurroundColors.SurfaceContainer.copy(alpha = 0.85f))
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Button(
                onClick = { onUpdateGenerator(null, null, null, null, !gen.playing) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (gen.playing) SurroundColors.Secondary else SurroundColors.Primary,
                    contentColor = if (gen.playing) SurroundColors.OnSecondary else SurroundColors.OnPrimary
                )
            ) {
                Icon(
                    imageVector = if (gen.playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (gen.playing) {
                        if (gen.signalType == SignalSourceType.PINK_NOISE) s.pinkNoisePauseMobile
                        else s.sinePauseMobile(gen.frequencyHz)
                    } else {
                        if (gen.signalType == SignalSourceType.PINK_NOISE) s.pinkNoiseStartMobile
                        else s.sineStartMobile(gen.frequencyHz)
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }

    if (showHzInputDialog) {
        FrequencyInputDialog(
            initialHz = gen.frequencyHz,
            onDismiss = { showHzInputDialog = false },
            onConfirm = { newHz ->
                onUpdateGenerator(null, null, SignalSourceType.SINE, newHz, null)
                showHzInputDialog = false
            }
        )
    }
}

@Composable
private fun FrequencyRotaryJogWheel(
    frequencyHz: Int,
    isPinkNoise: Boolean,
    onFrequencyChange: (Int) -> Unit,
    onCenterClick: () -> Unit
) {
    val s = LocalAppStrings.current
    val haptic = LocalHapticFeedback.current
    var latestHz by remember(frequencyHz) { mutableIntStateOf(frequencyHz) }

    // Logarithmic normalized progress [0..1] for 20 Hz .. 20000 Hz
    val logMin = ln(20.0)
    val logMax = ln(20000.0)
    val normProgress = ((ln(frequencyHz.coerceIn(20, 20000).toDouble()) - logMin) / (logMax - logMin))
        .toFloat()
        .coerceIn(0f, 1f)

    Box(
        modifier = Modifier.size(256.dp),
        contentAlignment = Alignment.Center
    ) {
        var lastAngleDeg by remember { mutableStateOf<Double?>(null) }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val cx = size.width / 2f
                            val cy = size.height / 2f
                            lastAngleDeg = atan2((offset.y - cy).toDouble(), (offset.x - cx).toDouble()) * 180.0 / PI
                        },
                        onDragEnd = { lastAngleDeg = null },
                        onDragCancel = { lastAngleDeg = null },
                        onDrag = { change, _ ->
                            change.consume()
                            val cx = size.width / 2f
                            val cy = size.height / 2f
                            val angleDeg = atan2(
                                (change.position.y - cy).toDouble(),
                                (change.position.x - cx).toDouble()
                            ) * 180.0 / PI
                            val prev = lastAngleDeg
                            if (prev != null) {
                                var delta = angleDeg - prev
                                if (delta > 180.0) delta -= 360.0
                                if (delta < -180.0) delta += 360.0

                                // Logarithmic jog sensitivity: full 360° turn scales smoothly across band
                                val currentLog = ln(latestHz.coerceIn(20, 20000).toDouble())
                                val nextLog = (currentLog + (delta / 360.0) * 2.2).coerceIn(logMin, logMax)
                                var candidate = kotlin.math.exp(nextLog).roundToInt().coerceIn(20, 20000)
                                // Ensure even subtle slow turns nudge by at least ±1 Hz
                                if (candidate == latestHz && kotlin.math.abs(delta) > 2.0) {
                                    candidate = (latestHz + if (delta > 0) 1 else -1).coerceIn(20, 20000)
                                }
                                if (candidate != latestHz) {
                                    latestHz = candidate
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onFrequencyChange(candidate)
                                }
                            }
                            lastAngleDeg = angleDeg
                        }
                    )
                }
        ) {
            val radius = size.minDimension * 0.42f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Outer tick marks
            val tickCount = 48
            for (i in 0 until tickCount) {
                val a = (-225.0 + (i.toDouble() / (tickCount - 1)) * 270.0) * PI / 180.0
                val inner = radius + 6.dp.toPx()
                val outer = radius + if (i % 4 == 0) 14.dp.toPx() else 10.dp.toPx()
                val tickFilled = i.toFloat() / (tickCount - 1) <= normProgress
                drawLine(
                    color = if (tickFilled) SurroundColors.Primary else SurroundColors.Outline,
                    start = Offset(
                        center.x + (inner * cos(a)).toFloat(),
                        center.y + (inner * sin(a)).toFloat()
                    ),
                    end = Offset(
                        center.x + (outer * cos(a)).toFloat(),
                        center.y + (outer * sin(a)).toFloat()
                    ),
                    strokeWidth = if (i % 4 == 0) 3f else 2f,
                    cap = StrokeCap.Round
                )
            }

            // Track background arc
            drawArc(
                color = SurroundColors.SurfaceContainerHighest,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )

            // Active tonal progress arc
            drawArc(
                color = SurroundColors.Primary,
                startAngle = 135f,
                sweepAngle = 270f * normProgress,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )

            // Thumb dot on arc
            val thumbAngle = (135.0 + 270.0 * normProgress) * PI / 180.0
            drawCircle(
                color = SurroundColors.OnPrimaryContainer,
                radius = 7.dp.toPx(),
                center = Offset(
                    center.x + (radius * cos(thumbAngle)).toFloat(),
                    center.y + (radius * sin(thumbAngle)).toFloat()
                )
            )
        }

        // Center clickable Hz readout
        Column(
            modifier = Modifier
                .size(164.dp)
                .clip(CircleShape)
                .background(SurroundColors.SurfaceContainerHighest)
                .clickable { onCenterClick() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isPinkNoise) s.signalUpper else s.freqUpper,
                fontFamily = MonoFontFamily,
                fontSize = 11.sp,
                color = SurroundColors.OnSurfaceVariant
            )
            Text(
                text = if (isPinkNoise) "PINK" else "$frequencyHz Hz",
                fontFamily = MonoFontFamily,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = SurroundColors.Primary
            )
            Text(
                text = s.tapToEnter,
                fontFamily = MonoFontFamily,
                fontSize = 10.sp,
                color = SurroundColors.OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun FrequencyInputDialog(
    initialHz: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    val s = LocalAppStrings.current
    var text by remember { mutableStateOf(initialHz.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurroundColors.SurfaceContainerHigh,
        title = { Text(s.freqDialogTitle, color = SurroundColors.OnSurface) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { input -> text = input.filter { it.isDigit() }.take(5) },
                label = { Text(s.freqDialogRangeLabel) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = (text.toIntOrNull() ?: initialHz).coerceIn(20, 20000)
                    onConfirm(parsed)
                }
            ) {
                Text(s.apply)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(s.cancel) }
        }
    )
}
