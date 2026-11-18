package de.surroundlab.app.ui.tv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors

/**
 * 10-foot Android TV info screen for Room Mode Measurement.
 * Explains that automated measurement requires the Android smartphone app as the room microphone,
 * and shows live playback status when a connected phone triggers a sweep.
 */
@Composable
fun TvRoomMeasurementScreen(
    state: SurroundAppState,
    onOpenQrDialog: () -> Unit,
    onBack: () -> Unit
) {
    val s = LocalAppStrings.current
    val r = s.room
    val backFocus = remember { FocusRequester() }

    BackHandler { onBack() }

    LaunchedEffect(Unit) {
        backFocus.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurroundColors.Background)
            .padding(horizontal = 48.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TvPillSurface(
                onClick = onBack,
                modifier = Modifier.focusRequester(backFocus)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = s.back)
                Text(s.homeHubBtn, fontWeight = FontWeight.Bold)
            }

            TvPillSurface(
                onClick = onOpenQrDialog,
                containerColor = SurroundColors.PrimaryContainer,
                contentColor = SurroundColors.OnPrimaryContainer
            ) {
                Icon(Icons.Rounded.QrCode2, contentDescription = null, tint = SurroundColors.Primary)
                Text(r.tvShowQrBtn, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(SurroundColors.SurfaceContainer)
                .padding(34.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TvIconBadge(
                    icon = Icons.Rounded.Mic,
                    containerColor = SurroundColors.PrimaryContainer,
                    iconTint = SurroundColors.OnPrimaryContainer,
                    size = 64.dp,
                    cornerRadius = 20.dp
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurroundColors.SurfaceContainerHighest)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = r.tvInfoBadge,
                            fontFamily = MonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.Primary
                        )
                    }
                    Text(
                        text = r.tvInfoTitle,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.OnSurface
                    )
                }
            }

            Text(
                text = r.tvInfoBody,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = SurroundColors.OnSurfaceVariant
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurroundColors.SurfaceContainerHigh)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                r.tvInfoSteps.forEach { step ->
                    Text(
                        text = step,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = SurroundColors.OnSurface
                    )
                }
            }

            if (state.measurementTone.playing) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurroundColors.PrimaryContainer)
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GraphicEq,
                        contentDescription = null,
                        tint = SurroundColors.Primary,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = r.tvActiveSweepStatus(state.measurementTone.frequencyHz),
                        fontFamily = MonoFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.OnPrimaryContainer
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = s.tvHostActiveLabel(state.hostIp ?: s.wlanCheckPrompt),
                fontFamily = MonoFontFamily,
                fontSize = 13.sp,
                color = SurroundColors.OnSurfaceVariant
            )
            Text(
                text = "Setup ${state.setup.code} • Token ${state.sessionToken}",
                fontFamily = MonoFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SurroundColors.Primary
            )
        }
    }
}
