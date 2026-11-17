package de.surroundlab.app.ui.mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SpeakerGroup
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.surroundlab.app.domain.model.ConnectionStatus
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.domain.model.TestMode
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors
import kotlin.math.roundToInt

@Composable
fun MobileHomeHubScreen(
    state: SurroundAppState,
    connectionStatus: ConnectionStatus,
    onSelectMode: (TestMode) -> Unit,
    onOpenSetup: () -> Unit,
    onOpenAudioLibrary: () -> Unit,
    onOpenConnectionSettings: () -> Unit
) {
    val s = LocalAppStrings.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurroundColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Sticky Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Connection Status Pill (Clickable to open TV pairing / connection sheet)
                val (dotColor, statusText) = when (connectionStatus) {
                    ConnectionStatus.CONNECTED_TO_TV -> SurroundColors.Success to s.mobileConnConnected(state.hostName)
                    ConnectionStatus.CONNECTING -> SurroundColors.Secondary to s.mobileConnConnecting
                    ConnectionStatus.LOCAL_STANDALONE -> SurroundColors.Primary to s.mobileConnLocal
                    ConnectionStatus.TV_HOST_ACTIVE -> SurroundColors.Primary to s.tvHostActiveLabel(state.hostIp ?: "LAN")
                }
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SurroundColors.SurfaceContainerHigh)
                        .clickable { onOpenConnectionSettings() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelLarge,
                        color = SurroundColors.OnSurface
                    )
                }

                // Tonal Icon Buttons: Audio Library + Settings
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onOpenAudioLibrary,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = SurroundColors.SurfaceContainerHigh,
                            contentColor = SurroundColors.OnSurface
                        )
                    ) {
                        Icon(Icons.Rounded.LibraryMusic, contentDescription = s.mobileLibTitle)
                    }
                    IconButton(
                        onClick = onOpenConnectionSettings,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = SurroundColors.SurfaceContainerHigh,
                            contentColor = SurroundColors.OnSurface
                        )
                    ) {
                        Icon(Icons.Rounded.Settings, contentDescription = s.settingsLabel)
                    }
                }
            }

            // Title & Setup Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SurroundLab",
                        style = MaterialTheme.typography.headlineLarge,
                        color = SurroundColors.OnBackground
                    )
                    Text(
                        text = if (connectionStatus == ConnectionStatus.CONNECTED_TO_TV) {
                            s.mobileRoleRemote
                        } else {
                            s.mobileRoleLocal
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SurroundColors.PrimaryContainer)
                        .clickable { onOpenSetup() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SpeakerGroup,
                        contentDescription = null,
                        tint = SurroundColors.OnPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${state.setup.code} AUDIO",
                        fontFamily = MonoFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.OnPrimaryContainer
                    )
                }
            }
        }

        // 3 Large M3 Expressive Mode Cards
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MobileModeCard(
                badge = "01 TEST",
                title = s.mode1Title,
                description = s.mode1MobileDesc(state.setup.channels.joinToString(", ") { it.shortLabel }),
                footerLeft = s.mobileCard1FooterLeft(state.setup.channels.size),
                footerRight = s.ready,
                icon = Icons.AutoMirrored.Rounded.VolumeUp,
                iconBg = SurroundColors.PrimaryContainer,
                iconFg = SurroundColors.OnPrimaryContainer,
                accent = SurroundColors.Primary,
                onClick = { onSelectMode(TestMode.SINGLE_SPEAKER) }
            )

            MobileModeCard(
                badge = "02 SINE",
                title = s.mode2Title,
                description = s.mode2MobileDesc,
                footerLeft = s.mobileCard2FooterLeft,
                footerRight = "20 Hz – 20 kHz",
                icon = Icons.Rounded.Tune,
                iconBg = SurroundColors.SecondaryContainer,
                iconFg = SurroundColors.OnSecondaryContainer,
                accent = SurroundColors.Secondary,
                onClick = { onSelectMode(TestMode.GENERATOR) }
            )

            MobileModeCard(
                badge = "03 SPATIAL",
                title = s.mode3Title,
                description = s.mode3MobileDesc,
                footerLeft = s.mobileCard3FooterLeft,
                footerRight = s.mobileCard3FooterRight,
                icon = Icons.Rounded.Radar,
                iconBg = SurroundColors.TertiaryContainer,
                iconFg = SurroundColors.OnTertiaryContainer,
                accent = SurroundColors.Tertiary,
                onClick = { onSelectMode(TestMode.PUCK_2D) }
            )

            Spacer(Modifier.height(8.dp))
        }

        // Bottom Telemetry Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurroundColors.SurfaceContainer.copy(alpha = 0.7f))
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (connectionStatus == ConnectionStatus.CONNECTED_TO_TV) {
                    s.mobileFooterTv(state.hostIp ?: "Wi-Fi")
                } else {
                    s.mobileFooterLocal
                },
                fontFamily = MonoFontFamily,
                fontSize = 12.sp,
                color = SurroundColors.OnSurface
            )
            Text(
                text = s.mobileFooterHeadroom(state.masterGainDb.roundToInt()),
                fontFamily = MonoFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SurroundColors.Primary
            )
        }
    }
}

@Composable
private fun MobileModeCard(
    badge: String,
    title: String,
    description: String,
    footerLeft: String,
    footerRight: String,
    icon: ImageVector,
    iconBg: Color,
    iconFg: Color,
    accent: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(SurroundColors.SurfaceContainer)
            .clickable { onClick() }
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconFg, modifier = Modifier.size(30.dp))
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurroundColors.SurfaceContainerHighest)
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = badge,
                    fontFamily = MonoFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = SurroundColors.OnSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = accent
            )
        }

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = SurroundColors.OnSurfaceVariant
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SurroundColors.Outline.copy(alpha = 0.35f))
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = footerLeft,
                style = MaterialTheme.typography.bodyMedium,
                color = SurroundColors.OnSurfaceVariant
            )
            Text(
                text = footerRight,
                fontFamily = MonoFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = accent
            )
        }
    }
}
