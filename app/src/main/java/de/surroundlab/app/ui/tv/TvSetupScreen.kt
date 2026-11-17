package de.surroundlab.app.ui.tv

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material.icons.rounded.SurroundSound
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import de.surroundlab.app.domain.model.SpeakerChannel
import de.surroundlab.app.domain.model.SpeakerSetup
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors

@Composable
fun TvSetupScreen(
    state: SurroundAppState,
    onSelectSetup: (SpeakerSetup) -> Unit,
    onContinueToHub: () -> Unit,
    onOpenQrDialog: () -> Unit
) {
    val s = LocalAppStrings.current
    var previewSetup by remember(state.setup) { mutableStateOf(state.setup) }
    val activeFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        activeFocus.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurroundColors.Background)
            .padding(horizontal = 48.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
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
                TvIconBadge(
                    icon = Icons.Rounded.SurroundSound,
                    containerColor = SurroundColors.PrimaryContainer,
                    iconTint = SurroundColors.OnPrimaryContainer,
                    size = 60.dp,
                    cornerRadius = 22.dp
                )
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "SurroundLab",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.OnBackground
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SurroundColors.SurfaceContainerHigh)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = s.tvTopologyBadge,
                                fontFamily = MonoFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurroundColors.Primary
                            )
                        }
                    }
                    Text(
                        text = s.setupTvSubtitle,
                        fontSize = 16.sp,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                TvPillSurface(onClick = onOpenQrDialog) {
                    Icon(
                        imageVector = Icons.Rounded.QrCode2,
                        contentDescription = null,
                        tint = SurroundColors.Primary
                    )
                    Text(s.connectBrowserIosBtn, fontWeight = FontWeight.Bold)
                }

                TvPillSurface(
                    onClick = onContinueToHub,
                    containerColor = SurroundColors.Primary,
                    focusedContainerColor = SurroundColors.Primary,
                    contentColor = SurroundColors.OnPrimary,
                    focusedContentColor = SurroundColors.OnPrimary,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(s.goToHomeHubBtn, fontWeight = FontWeight.Bold)
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
                }
            }
        }

        // Split Body: 8 cols Setup Grid + 4 cols Spec Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = s.activeSpeakerConfigHeading,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.OnSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TvKeyLegendPill("D-Pad", s.legendNavigate)
                        TvKeyLegendPill("OK", s.legendActivate)
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(SpeakerSetup.entries) { setup ->
                        val isSelected = setup == state.setup
                        TvCardSurface(
                            onClick = {
                                onSelectSetup(setup)
                                previewSetup = setup
                            },
                            modifier = Modifier
                                .height(190.dp)
                                .then(if (isSelected) Modifier.focusRequester(activeFocus) else Modifier)
                                .onFocusChanged { if (it.isFocused) previewSetup = setup },
                            selected = isSelected,
                            containerColor = if (isSelected) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainer,
                            focusedContainerColor = if (isSelected) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainerHigh
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                if (isSelected) {
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .clip(CircleShape)
                                            .background(SurroundColors.Primary)
                                            .padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = null,
                                            tint = SurroundColors.OnPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = s.activeBadgeUpper,
                                            fontFamily = MonoFontFamily,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SurroundColors.OnPrimary
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    MiniTopologyGraphic(
                                        setup = setup,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(92.dp)
                                    )

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = setup.displayName,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) SurroundColors.Primary else SurroundColors.OnSurface
                                        )
                                        Text(
                                            text = s.setupSubtitle(setup),
                                            fontSize = 13.sp,
                                            color = if (isSelected) SurroundColors.OnPrimaryContainer else SurroundColors.OnSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Right Column: Profile Spec Card
            Column(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(28.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = s.profileSpecUpper,
                            fontFamily = MonoFontFamily,
                            fontSize = 12.sp,
                            color = SurroundColors.OnSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SurroundColors.PrimaryContainer)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${s.setupPrefix} ${previewSetup.code}",
                                fontFamily = MonoFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurroundColors.OnPrimaryContainer
                            )
                        }
                    }

                    Text(
                        text = previewSetup.displayName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.OnSurface
                    )

                    Text(
                        text = s.setupStreamDesc(previewSetup.streamChannelCount),
                        fontSize = 14.sp,
                        color = SurroundColors.OnSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SpecRow(s.specChannelsActive, previewSetup.channels.joinToString(" · ") { it.shortLabel })
                        SpecRow(s.specPcmStream, s.specPcmValue(previewSetup.streamChannelCount))
                        if (previewSetup.hasSubwoofer) {
                            SpecRow(s.specSubwooferLfe, s.specSubwooferLfeValue, isSub = true)
                        }
                    }
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
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    if (state.hdmiAudioInfo.multichannelSupported) SurroundColors.Success
                                    else SurroundColors.Secondary
                                )
                        )
                        Text(
                            text = if (state.hdmiAudioInfo.multichannelSupported) {
                                s.hdmiMultichannel(state.hdmiAudioInfo.maxChannels)
                            } else {
                                s.outputStereo(state.hdmiAudioInfo.maxChannels)
                            },
                            fontSize = 13.sp,
                            color = SurroundColors.OnSurfaceVariant
                        )
                    }
                    Text(
                        text = s.headroomProtectionTag,
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

@Composable
private fun SpecRow(label: String, value: String, isSub: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSub) SurroundColors.SecondaryContainer else SurroundColors.SurfaceContainerHigh)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (isSub) SurroundColors.OnSecondaryContainer else SurroundColors.OnSurface
        )
        Text(
            text = value,
            fontFamily = MonoFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSub) SurroundColors.Secondary else SurroundColors.Primary
        )
    }
}

@Composable
fun MiniTopologyGraphic(setup: SpeakerSetup, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurroundColors.SurfaceContainerHighest.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        // Sweet spot dot
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(SurroundColors.SurfaceContainer),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(SurroundColors.Primary)
            )
        }

        val chSet = setup.channels.toSet()
        if (SpeakerChannel.FL in chSet) {
            SpeakerMiniBox(Modifier.align(Alignment.TopStart).padding(10.dp), false)
        }
        if (SpeakerChannel.C in chSet) {
            SpeakerMiniBox(Modifier.align(Alignment.TopCenter).padding(top = 10.dp), false)
        }
        if (SpeakerChannel.FR in chSet) {
            SpeakerMiniBox(Modifier.align(Alignment.TopEnd).padding(10.dp), false)
        }
        if (SpeakerChannel.SL in chSet) {
            val align = if (SpeakerChannel.SBL in chSet) Alignment.CenterStart else Alignment.BottomStart
            SpeakerMiniBox(Modifier.align(align).padding(10.dp), false)
        }
        if (SpeakerChannel.SR in chSet) {
            val align = if (SpeakerChannel.SBR in chSet) Alignment.CenterEnd else Alignment.BottomEnd
            SpeakerMiniBox(Modifier.align(align).padding(10.dp), false)
        }
        if (SpeakerChannel.SBL in chSet) {
            SpeakerMiniBox(Modifier.align(Alignment.BottomStart).padding(horizontal = 24.dp, vertical = 10.dp), false)
        }
        if (SpeakerChannel.SBR in chSet) {
            SpeakerMiniBox(Modifier.align(Alignment.BottomEnd).padding(horizontal = 24.dp, vertical = 10.dp), false)
        }
        if (SpeakerChannel.LFE in chSet) {
            SpeakerMiniBox(
                Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 46.dp, y = (-10).dp),
                true
            )
        }
    }
}

@Composable
private fun SpeakerMiniBox(modifier: Modifier, isSub: Boolean) {
    Box(
        modifier = modifier
            .size(width = if (isSub) 16.dp else 18.dp, height = if (isSub) 16.dp else 11.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSub) SurroundColors.Secondary else SurroundColors.Primary),
        contentAlignment = Alignment.Center
    ) {
        if (isSub) {
            Text(
                text = ".1",
                fontFamily = MonoFontFamily,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = SurroundColors.OnSecondary
            )
        }
    }
}
