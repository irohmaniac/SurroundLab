package de.surroundlab.app.ui.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SurroundSound
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.domain.model.TestMode
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors
import kotlin.math.roundToInt

@Composable
fun TvHomeHubScreen(
    state: SurroundAppState,
    onSelectMode: (TestMode) -> Unit,
    onOpenSetup: () -> Unit,
    onOpenQrDialog: () -> Unit,
    onOpenAudioLibrary: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val s = LocalAppStrings.current
    val firstModeFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        firstModeFocus.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurroundColors.Background)
            .padding(horizontal = 48.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 10-Foot Global Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                TvIconBadge(
                    icon = Icons.Rounded.SurroundSound,
                    containerColor = SurroundColors.PrimaryContainer,
                    iconTint = SurroundColors.OnPrimaryContainer,
                    size = 60.dp,
                    cornerRadius = 22.dp
                )
                Text(
                    text = "SurroundLab",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.OnBackground
                )
            }

            // Right Header Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TvPillSurface(onClick = onOpenSetup) {
                    Text(s.hubSetupChipLabel, color = SurroundColors.OnSurfaceVariant, fontSize = 14.sp)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurroundColors.PrimaryContainer)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = state.setup.code,
                            fontFamily = MonoFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.OnPrimaryContainer
                        )
                    }
                }

                TvPillSurface(onClick = onOpenAudioLibrary) {
                    Icon(Icons.Rounded.LibraryMusic, contentDescription = null, tint = SurroundColors.Tertiary)
                    Text(s.hubAudioBtn(state.audioLibrary.size), fontWeight = FontWeight.SemiBold)
                }

                TvPillSurface(
                    onClick = onOpenQrDialog,
                    containerColor = SurroundColors.PrimaryContainer,
                    contentColor = SurroundColors.OnPrimaryContainer
                ) {
                    Icon(Icons.Rounded.QrCode2, contentDescription = null, tint = SurroundColors.Primary)
                    Text(s.hubBrowserIosBtn, fontWeight = FontWeight.Bold)
                }

                TvPillSurface(onClick = onOpenSettings) {
                    Icon(Icons.Rounded.Settings, contentDescription = s.settingsLabel)
                }
            }
        }

        // Main Section Header + 3 Large Mode Cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = s.tvHubSectionTitle,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.OnSurface
                    )
                    Text(
                        text = s.tvHubSectionSubtitle,
                        fontSize = 15.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurroundColors.SurfaceContainer)
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "48 kHz Float-PCM",
                            fontFamily = MonoFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.Primary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurroundColors.SurfaceContainer)
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = s.tvHubChannelsBadge(state.setup.channels.size, state.setup.code),
                            fontFamily = MonoFontFamily,
                            fontSize = 12.sp,
                            color = SurroundColors.OnSurfaceVariant
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                TvModeCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .focusRequester(firstModeFocus),
                    badgeText = s.mode1Badge,
                    title = s.mode1Title,
                    description = s.mode1TvDesc(state.setup.channels.joinToString(", ") { it.shortLabel }),
                    footerIcon = Icons.Rounded.PlayCircle,
                    footerText = s.mode1Footer,
                    icon = Icons.AutoMirrored.Rounded.VolumeUp,
                    iconContainer = SurroundColors.PrimaryContainer,
                    iconTint = SurroundColors.OnPrimaryContainer,
                    accentColor = SurroundColors.Primary,
                    onClick = { onSelectMode(TestMode.SINGLE_SPEAKER) }
                )

                TvModeCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    badgeText = s.mode2Badge,
                    title = s.mode2Title,
                    description = s.mode2TvDesc,
                    footerIcon = Icons.Rounded.GraphicEq,
                    footerText = s.mode2Footer,
                    icon = Icons.Rounded.Tune,
                    iconContainer = SurroundColors.SecondaryContainer,
                    iconTint = SurroundColors.OnSecondaryContainer,
                    accentColor = SurroundColors.Secondary,
                    onClick = { onSelectMode(TestMode.GENERATOR) }
                )

                TvModeCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    badgeText = s.mode3Badge,
                    title = s.mode3Title,
                    description = s.mode3TvDesc,
                    footerIcon = Icons.Rounded.TrackChanges,
                    footerText = s.mode3Footer,
                    icon = Icons.Rounded.Radar,
                    iconContainer = SurroundColors.TertiaryContainer,
                    iconTint = SurroundColors.OnTertiaryContainer,
                    accentColor = SurroundColors.Tertiary,
                    onClick = { onSelectMode(TestMode.PUCK_2D) }
                )

                TvModeCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    badgeText = s.mode4Badge,
                    title = s.mode4Title,
                    description = s.mode4TvDesc,
                    footerIcon = Icons.Rounded.Mic,
                    footerText = s.mode4Footer,
                    icon = Icons.Rounded.GraphicEq,
                    iconContainer = SurroundColors.PrimaryContainer,
                    iconTint = SurroundColors.OnPrimaryContainer,
                    accentColor = SurroundColors.Primary,
                    onClick = { onSelectMode(TestMode.ROOM_MEASUREMENT) }
                )
            }
        }

        // Bottom Telemetry Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurroundColors.SurfaceContainer.copy(alpha = 0.7f))
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(SurroundColors.Primary)
                    )
                    Text(
                        text = s.tvHostActiveLabel(state.hostIp ?: s.wlanCheckPrompt),
                        fontFamily = MonoFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = SurroundColors.OnSurface
                    )
                }
                Text("•", color = SurroundColors.Outline)
                Text(
                    text = "Token: ${state.sessionToken}",
                    fontFamily = MonoFontFamily,
                    fontSize = 13.sp,
                    color = SurroundColors.Primary,
                    fontWeight = FontWeight.Bold
                )
                Text("•", color = SurroundColors.Outline)
                val hasAndroid = state.connectedAndroidClients > 0
                val hasBrowser = state.connectedBrowserClients > 0
                val remoteConnected = hasAndroid || hasBrowser
                val remoteText = when {
                    hasAndroid && hasBrowser -> s.remoteAndroidAndBrowser
                    hasAndroid -> s.remoteAndroidOnly(state.connectedAndroidClients)
                    hasBrowser -> s.remoteBrowserOnly(state.connectedBrowserClients)
                    else -> s.remoteNone
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (remoteConnected) SurroundColors.Success else SurroundColors.Outline)
                    )
                    Text(
                        text = remoteText,
                        fontFamily = MonoFontFamily,
                        fontSize = 13.sp,
                        fontWeight = if (remoteConnected) FontWeight.Bold else FontWeight.Normal,
                        color = if (remoteConnected) SurroundColors.Success else SurroundColors.OnSurfaceVariant
                    )
                }
            }

            Text(
                text = s.headroomStatusBar(state.masterGainDb.roundToInt()),
                fontFamily = MonoFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SurroundColors.Primary
            )
        }
    }
}

@Composable
private fun TvModeCard(
    modifier: Modifier = Modifier,
    badgeText: String,
    title: String,
    description: String,
    footerIcon: ImageVector,
    footerText: String,
    icon: ImageVector,
    iconContainer: Color,
    iconTint: Color,
    accentColor: Color,
    onClick: () -> Unit
) {
    TvCardSurface(
        onClick = onClick,
        modifier = modifier,
        borderColor = accentColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    TvIconBadge(
                        icon = icon,
                        containerColor = iconContainer,
                        iconTint = iconTint,
                        size = 56.dp,
                        cornerRadius = 18.dp
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurroundColors.SurfaceContainerHighest)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontFamily = MonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = title,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.OnSurface
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = SurroundColors.OnSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(footerIcon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                    Text(
                        text = footerText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = accentColor
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = accentColor
                )
            }
        }
    }
}
