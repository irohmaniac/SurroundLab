package de.surroundlab.app.ui.tv

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeDown
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import de.surroundlab.app.domain.model.AppLanguage
import de.surroundlab.app.domain.model.CustomAudioTrack
import de.surroundlab.app.domain.model.SpeakerLayoutPreset
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.ui.components.QrCodeGenerator
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors
import kotlin.math.roundToInt

/**
 * Full-screen TV modal showing the ZXing QR code (`http://<TV-IP>:8080/?token=<TOKEN>`)
 * for instant smartphone / iOS / desktop browser pairing.
 */
@Composable
fun TvQrPairingDialog(
    state: SurroundAppState,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    val closeFocus = remember { FocusRequester() }
    val ip = state.hostIp ?: s.wlanNotDetected
    val url = if (state.hostIp != null) {
        "http://${state.hostIp}:${state.hostPort}/?token=${state.sessionToken}"
    } else {
        "http://<TV-IP>:${state.hostPort}/?token=${state.sessionToken}"
    }
    val qrBitmap = remember(url) { QrCodeGenerator.generateQrBitmap(url, 640) }

    LaunchedEffect(Unit) {
        closeFocus.requestFocus()
    }

    TvModalHost(onDismiss = onDismiss) {
        Row(
            modifier = Modifier
                .width(760.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(SurroundColors.SurfaceContainerHigh)
                .padding(32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            if (qrBitmap != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(18.dp)
                ) {
                    Image(
                        bitmap = qrBitmap,
                        contentDescription = "Pairing QR Code",
                        modifier = Modifier.size(300.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TvIconBadge(
                    icon = Icons.Rounded.QrCode2,
                    containerColor = SurroundColors.PrimaryContainer,
                    iconTint = SurroundColors.OnPrimaryContainer,
                    size = 54.dp
                )

                Text(
                    text = s.qrTitle,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.OnSurface
                )

                Text(
                    text = s.qrBody,
                    fontSize = 14.sp,
                    color = SurroundColors.OnSurfaceVariant
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurroundColors.SurfaceContainer)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "http://$ip:${state.hostPort}",
                        fontFamily = MonoFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.Primary
                    )
                    Text(
                        text = "Session-Token: ${state.sessionToken}",
                        fontFamily = MonoFontFamily,
                        fontSize = 13.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }

                TvPillSurface(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(closeFocus),
                    containerColor = SurroundColors.Primary,
                    focusedContainerColor = SurroundColors.Primary,
                    contentColor = SurroundColors.OnPrimary,
                    focusedContentColor = SurroundColors.OnPrimary,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = s.ok, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                }
            }
        }
    }
}

/**
 * TV Settings Modal: Master digital headroom (-30 dB to 0 dB), HDMI multichannel diagnostics,
 * language selection, and shortcut to Speaker Setup.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TvSettingsDialog(
    state: SurroundAppState,
    onMasterGainChange: (Float) -> Unit,
    onSwap71Change: (Boolean) -> Unit,
    onSpeakerLayoutPresetChange: (SpeakerLayoutPreset) -> Unit,
    onTvUiScaleChange: (Float) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onOpenSpeakerSetup: () -> Unit,
    onRefreshNetwork: () -> Unit,
    onOpenGuide: () -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    val firstFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        firstFocus.requestFocus()
    }

    TvModalHost(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .width(700.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(SurroundColors.SurfaceContainerHigh)
                .verticalScroll(rememberScrollState())
                .padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TvIconBadge(
                        icon = Icons.Rounded.Settings,
                        containerColor = SurroundColors.PrimaryContainer,
                        iconTint = SurroundColors.OnPrimaryContainer,
                        size = 48.dp
                    )
                    Column {
                        Text(
                            text = s.tvSettingsTitle,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.OnSurface
                        )
                        Text(
                            text = s.tvSettingsSubtitle,
                            fontSize = 14.sp,
                            color = SurroundColors.OnSurfaceVariant
                        )
                    }
                }

                TvPillSurface(onClick = onDismiss) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = s.close)
                    Text(s.close, fontWeight = FontWeight.Bold)
                }
            }

            // Master Headroom Control
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = s.masterLevelTitleTv(-12),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SurroundColors.OnSurface
                    )
                    Text(
                        text = "${state.masterGainDb.roundToInt()} dBFS",
                        fontFamily = MonoFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.Primary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TvPillSurface(
                        onClick = { onMasterGainChange((state.masterGainDb - 3f).coerceAtLeast(-30f)) },
                        modifier = Modifier.focusRequester(firstFocus)
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.VolumeDown, contentDescription = null)
                        Text("-3 dB", fontFamily = MonoFontFamily, fontWeight = FontWeight.Bold)
                    }
                    TvPillSurface(
                        onClick = { onMasterGainChange(-12f) },
                        containerColor = SurroundColors.PrimaryContainer,
                        contentColor = SurroundColors.OnPrimaryContainer
                    ) {
                        Text("Default (-12 dB)", fontFamily = MonoFontFamily, fontWeight = FontWeight.Bold)
                    }
                    TvPillSurface(
                        onClick = { onMasterGainChange((state.masterGainDb + 3f).coerceAtMost(0f)) }
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null)
                        Text("+3 dB", fontFamily = MonoFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 7.1 Surround / Surround-Back HDMI-HAL Mapping Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 14.dp)) {
                    Text(
                        text = s.swap71Title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SurroundColors.OnSurface
                    )
                    Text(
                        text = s.swap71DescTv,
                        fontSize = 12.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }
                TvPillSurface(
                    onClick = { onSwap71Change(!state.swap71SurroundBack) },
                    containerColor = if (state.swap71SurroundBack) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainerHighest,
                    contentColor = if (state.swap71SurroundBack) SurroundColors.OnPrimaryContainer else SurroundColors.OnSurfaceVariant
                ) {
                    Text(
                        text = if (state.swap71SurroundBack) s.stateOn.uppercase() else s.stateOff.uppercase(),
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Room Speaker Angle Presets (Virtuelles Objekt)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = s.layoutAnglesTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SurroundColors.OnSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SpeakerLayoutPreset.entries.forEach { preset ->
                        val selected = state.speakerLayoutPreset == preset
                        TvPillSurface(
                            onClick = { onSpeakerLayoutPresetChange(preset) },
                            containerColor = if (selected) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainerHighest,
                            contentColor = if (selected) SurroundColors.OnPrimaryContainer else SurroundColors.OnSurfaceVariant
                        ) {
                            Text(
                                text = s.layoutPresetShort(preset),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(
                    text = "${s.layoutPresetSummary(state.speakerLayoutPreset)}\n${s.layoutPresetDetail(state.speakerLayoutPreset)}",
                    fontSize = 12.sp,
                    color = SurroundColors.OnSurfaceVariant
                )
            }

            // TV Display Density / UI Scale (DPI)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val scalePct = (state.tvUiScale * 100f).roundToInt()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = s.tvUiScaleTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SurroundColors.OnSurface
                    )
                    Text(
                        text = "$scalePct %",
                        fontFamily = MonoFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.Primary
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TvPillSurface(
                        onClick = { onTvUiScaleChange((state.tvUiScale - 0.05f).coerceAtLeast(0.60f)) }
                    ) {
                        Text(s.scaleSmallerBtn, fontFamily = MonoFontFamily, fontWeight = FontWeight.Bold)
                    }
                    TvPillSurface(
                        onClick = { onTvUiScaleChange(0.75f) },
                        containerColor = if (scalePct == 75) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainerHighest,
                        contentColor = if (scalePct == 75) SurroundColors.OnPrimaryContainer else SurroundColors.OnSurfaceVariant
                    ) {
                        Text(s.scaleDefaultBtn, fontFamily = MonoFontFamily, fontWeight = FontWeight.Bold)
                    }
                    TvPillSurface(
                        onClick = { onTvUiScaleChange((state.tvUiScale + 0.05f).coerceAtMost(1.15f)) }
                    ) {
                        Text(s.scaleLargerBtn, fontFamily = MonoFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Language Selector (AUTO + 6 Idiomatic Languages)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = s.languageSectionTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SurroundColors.OnSurface
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        val selected = state.appLanguage == lang
                        val label = if (lang == AppLanguage.AUTO) {
                            s.languageAutoHint(AppLanguage.AUTO.resolve().nativeName)
                        } else {
                            lang.nativeName
                        }
                        TvPillSurface(
                            onClick = { onLanguageChange(lang) },
                            containerColor = if (selected) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainerHighest,
                            contentColor = if (selected) SurroundColors.OnPrimaryContainer else SurroundColors.OnSurfaceVariant,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // HDMI Diagnostic Box (SPEC.md 3.2)
            val hdmi = state.hdmiAudioInfo
            val diagBg = if (hdmi.multichannelSupported) {
                SurroundColors.PrimaryContainer.copy(alpha = 0.55f)
            } else {
                SurroundColors.SecondaryContainer.copy(alpha = 0.65f)
            }
            val diagFg = if (hdmi.multichannelSupported) SurroundColors.Primary else SurroundColors.Secondary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(diagBg)
                    .padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = diagFg,
                    modifier = Modifier.size(24.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = s.audioOutputDiag(hdmi.deviceName, hdmi.maxChannels),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.OnSurface
                    )
                    Text(
                        text = if (hdmi.multichannelSupported) s.hdmiDiagOk else s.hdmiDiagStereoWarn,
                        fontSize = 13.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TvPillSurface(
                    onClick = {
                        onDismiss()
                        onOpenSpeakerSetup()
                    },
                    containerColor = SurroundColors.Primary,
                    focusedContainerColor = SurroundColors.Primary,
                    contentColor = SurroundColors.OnPrimary,
                    focusedContentColor = SurroundColors.OnPrimary
                ) {
                    Text(s.setupChangeBtn(state.setup.code), fontWeight = FontWeight.Bold)
                }
                TvPillSurface(onClick = onRefreshNetwork) {
                    Text(s.checkNetworkHdmiBtn)
                }
                TvPillSurface(
                    onClick = {
                        onDismiss()
                        onOpenGuide()
                    }
                ) {
                    Text(s.guideBtnShort)
                }
            }
        }
    }
}

/**
 * TV Audio Library Modal displaying custom uploaded tracks with deletion option.
 */
@Composable
fun TvAudioLibraryDialog(
    tracks: List<CustomAudioTrack>,
    onDeleteTrack: (CustomAudioTrack) -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    val closeFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        closeFocus.requestFocus()
    }

    TvModalHost(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .width(680.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(SurroundColors.SurfaceContainerHigh)
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TvIconBadge(
                        icon = Icons.Rounded.LibraryMusic,
                        containerColor = SurroundColors.TertiaryContainer,
                        iconTint = SurroundColors.OnTertiaryContainer,
                        size = 48.dp
                    )
                    Column {
                        Text(
                            text = s.tvAudioLibTitle(tracks.size),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.OnSurface
                        )
                        Text(
                            text = s.tvAudioLibSubtitle,
                            fontSize = 14.sp,
                            color = SurroundColors.OnSurfaceVariant
                        )
                    }
                }

                TvPillSurface(
                    onClick = onDismiss,
                    modifier = Modifier.focusRequester(closeFocus)
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = null)
                    Text(s.close, fontWeight = FontWeight.Bold)
                }
            }

            if (tracks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurroundColors.SurfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = s.tvAudioLibEmpty,
                        textAlign = TextAlign.Center,
                        color = SurroundColors.OnSurfaceVariant,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tracks, key = { it.id }) { track ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(SurroundColors.SurfaceContainer)
                                .padding(horizontal = 18.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SurroundColors.OnSurface
                                )
                                val sec = (track.durationMs / 1000L).coerceAtLeast(0L)
                                val sizeMb = track.sizeBytes / (1024f * 1024f)
                                Text(
                                    text = "${sec}s • ${track.sampleRate} Hz • ${"%.1f".format(sizeMb)} MB",
                                    fontFamily = MonoFontFamily,
                                    fontSize = 12.sp,
                                    color = SurroundColors.OnSurfaceVariant
                                )
                            }
                            TvPillSurface(
                                onClick = { onDeleteTrack(track) },
                                containerColor = SurroundColors.SurfaceContainerHighest,
                                focusedContainerColor = SurroundColors.Error.copy(alpha = 0.25f),
                                focusedContentColor = SurroundColors.Error,
                                borderColor = SurroundColors.Error,
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Rounded.Delete, contentDescription = s.deleteBtn, modifier = Modifier.size(18.dp))
                                Text(s.deleteBtn, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 10-foot 3-slide welcome walkthrough shown on first TV launch (re-openable from TV Settings),
 * isolated in TvModalHost so D-Pad focus stays trapped inside the walkthrough controls.
 */
@Composable
fun TvOnboardingDialog(
    onFinish: () -> Unit
) {
    val s = LocalAppStrings.current
    var step by remember { mutableIntStateOf(0) }
    val slides = s.onboardingSlides
    val current = slides[step]
    val isLast = step == slides.lastIndex
    val primaryFocus = remember { FocusRequester() }

    LaunchedEffect(step) {
        if (step == 0) {
            kotlinx.coroutines.delay(80L)
            runCatching { primaryFocus.requestFocus() }
        }
    }

    TvModalHost(onDismiss = { if (step > 0) step-- else onFinish() }) {
        Column(
            modifier = Modifier
                .width(760.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(SurroundColors.SurfaceContainerHigh)
                .verticalScroll(rememberScrollState())
                .padding(30.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = current.badge,
                    fontFamily = MonoFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.Primary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    slides.indices.forEach { idx ->
                        Box(
                            modifier = Modifier
                                .size(width = if (idx == step) 26.dp else 10.dp, height = 10.dp)
                                .clip(CircleShape)
                                .background(
                                    if (idx == step) SurroundColors.Primary
                                    else SurroundColors.SurfaceContainerHighest
                                )
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TvIconBadge(
                    icon = current.icon,
                    containerColor = SurroundColors.PrimaryContainer,
                    iconTint = SurroundColors.OnPrimaryContainer,
                    size = 54.dp
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = current.title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.OnSurface
                    )
                    Text(
                        text = current.subtitle,
                        fontSize = 14.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                current.bullets.forEach { (headline, desc) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(SurroundColors.SurfaceContainer)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = headline,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.Primary
                        )
                        Text(
                            text = desc,
                            fontSize = 14.sp,
                            color = SurroundColors.OnSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 0) {
                    TvPillSurface(
                        onClick = { step-- }
                    ) {
                        Text(
                            text = s.onboardingSkipToNext,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    Spacer(Modifier.width(1.dp))
                }

                TvPillSurface(
                    onClick = { if (isLast) onFinish() else step++ },
                    modifier = Modifier.focusRequester(primaryFocus),
                    containerColor = SurroundColors.Primary,
                    focusedContainerColor = SurroundColors.Primary,
                    contentColor = SurroundColors.OnPrimary,
                    focusedContentColor = SurroundColors.OnPrimary,
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (isLast) s.onboardingFinish else s.onboardingNext,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
