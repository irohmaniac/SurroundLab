package de.surroundlab.app.ui.mobile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.surroundlab.app.domain.model.SpeakerSetup
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors
import de.surroundlab.app.ui.tv.MiniTopologyGraphic

@Composable
fun MobileSetupScreen(
    state: SurroundAppState,
    canNavigateBack: Boolean,
    onBack: () -> Unit,
    onSelectSetup: (SpeakerSetup) -> Unit,
    onConfirm: () -> Unit
) {
    val s = LocalAppStrings.current
    if (canNavigateBack) {
        BackHandler { onBack() }
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
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (canNavigateBack) {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = SurroundColors.SurfaceContainerHigh,
                            contentColor = SurroundColors.OnSurface
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = s.back)
                    }
                }
                Column {
                    Text(
                        text = s.mobileSetupTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = SurroundColors.OnBackground
                    )
                    Text(
                        text = s.mobileSetupSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SurroundColors.OnSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurroundColors.PrimaryContainer)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = s.setupActiveTag(state.setup.code),
                    fontFamily = MonoFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.OnPrimaryContainer
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Assist Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurroundColors.PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = null,
                        tint = SurroundColors.OnPrimaryContainer
                    )
                }
                Text(
                    text = s.mobileSetupSelectPrompt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SurroundColors.OnSurfaceVariant
                )
            }

            // 2x3 Grid of Speaker Setups
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(SpeakerSetup.entries) { setup ->
                    val isSelected = setup == state.setup
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainer
                            )
                            .then(
                                if (isSelected) {
                                    Modifier.border(
                                        BorderStroke(2.dp, SurroundColors.Primary),
                                        RoundedCornerShape(20.dp)
                                    )
                                } else Modifier
                            )
                            .clickable { onSelectSetup(setup) }
                            .padding(14.dp)
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(SurroundColors.Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = s.activeBadgeUpper,
                                    tint = SurroundColors.OnPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MiniTopologyGraphic(
                                setup = setup,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(92.dp)
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = setup.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) SurroundColors.Primary else SurroundColors.OnSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = s.setupSubtitle(setup),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) SurroundColors.OnPrimaryContainer else SurroundColors.OnSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Bottom CTA Pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurroundColors.SurfaceContainer.copy(alpha = 0.85f))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurroundColors.Primary,
                    contentColor = SurroundColors.OnPrimary
                )
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = s.mobileSetupConfirmBtn(state.setup.code),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
