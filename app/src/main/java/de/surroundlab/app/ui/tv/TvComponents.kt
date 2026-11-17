package de.surroundlab.app.ui.tv

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvPillSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = SurroundColors.SurfaceContainer,
    focusedContainerColor: Color = SurroundColors.SurfaceContainerHighest,
    contentColor: Color = SurroundColors.OnSurface,
    focusedContentColor: Color = SurroundColors.Primary,
    borderColor: Color = SurroundColors.Primary,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(999.dp)),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = containerColor,
            focusedContainerColor = focusedContainerColor,
            contentColor = contentColor,
            focusedContentColor = focusedContentColor
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(3.dp, borderColor),
                shape = RoundedCornerShape(999.dp)
            )
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.04f)
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            content = content
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvCardSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    containerColor: Color = SurroundColors.SurfaceContainer,
    focusedContainerColor: Color = SurroundColors.SurfaceContainerHigh,
    contentColor: Color = SurroundColors.OnSurface,
    focusedContentColor: Color = SurroundColors.OnSurface,
    borderColor: Color = SurroundColors.Primary,
    selected: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val activeBorder = if (selected) {
        Border(
            border = BorderStroke(2.dp, borderColor.copy(alpha = 0.7f)),
            shape = RoundedCornerShape(cornerRadius)
        )
    } else Border.None

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(cornerRadius)),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = containerColor,
            focusedContainerColor = focusedContainerColor,
            contentColor = contentColor,
            focusedContentColor = focusedContentColor
        ),
        border = ClickableSurfaceDefaults.border(
            border = activeBorder,
            focusedBorder = Border(
                border = BorderStroke(3.5.dp, borderColor),
                shape = RoundedCornerShape(cornerRadius)
            )
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.02f),
        content = content
    )
}

@Composable
fun TvKeyLegendPill(keyLabel: String, actionLabel: String) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(SurroundColors.SurfaceContainer)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = keyLabel,
            fontFamily = MonoFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = SurroundColors.Primary,
            maxLines = 1,
            softWrap = false
        )
        Text(
            text = actionLabel,
            fontFamily = MonoFontFamily,
            fontSize = 12.sp,
            color = SurroundColors.OnSurfaceVariant,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun TvIconBadge(
    icon: ImageVector,
    containerColor: Color,
    iconTint: Color,
    size: Dp = 56.dp,
    cornerRadius: Dp = 18.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

/**
 * Isolated native Dialog host for TV overlays so D-Pad focus stays strictly trapped
 * inside the modal while preserving the app's custom DPI scale (LocalDensity).
 */
@Composable
fun TvModalHost(
    onDismiss: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val parentDensity = LocalDensity.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        CompositionLocalProvider(LocalDensity provides parentDensity) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurroundColors.Background.copy(alpha = 0.90f))
                    .padding(36.dp),
                contentAlignment = Alignment.Center,
                content = content
            )
        }
    }
}
