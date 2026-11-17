package de.surroundlab.app.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.darkColorScheme as tvDarkColorScheme
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.Shapes as TvShapes
import androidx.tv.material3.Typography as TvTypography

/**
 * Color tokens from DESIGN.md ("SurroundLab — Material You Expressive").
 */
object SurroundColors {
    val Primary = Color(0xFF4DDAD5)
    val OnPrimary = Color(0xFF003737)
    val PrimaryContainer = Color(0xFF004F4F)
    val OnPrimaryContainer = Color(0xFF6FF7F2)

    val Secondary = Color(0xFFFFB77B)
    val OnSecondary = Color(0xFF4C2700)
    val SecondaryContainer = Color(0xFF6B3B00)
    val OnSecondaryContainer = Color(0xFFFFDCC1)

    val Tertiary = Color(0xFFADC6FF)
    val OnTertiary = Color(0xFF102F60)
    val TertiaryContainer = Color(0xFF2B4678)
    val OnTertiaryContainer = Color(0xFFD8E2FF)

    val Background = Color(0xFF0E1414)
    val OnBackground = Color(0xFFDEE4E3)
    val Surface = Color(0xFF111717)
    val OnSurface = Color(0xFFDEE4E3)
    val SurfaceVariant = Color(0xFF1E2625)
    val OnSurfaceVariant = Color(0xFFBEC8C7)

    val SurfaceContainerLowest = Color(0xFF0B1010)
    val SurfaceContainerLow = Color(0xFF151B1B)
    val SurfaceContainer = Color(0xFF192020)
    val SurfaceContainerHigh = Color(0xFF232B2B)
    val SurfaceContainerHighest = Color(0xFF2E3635)

    val Outline = Color(0xFF3E4948)
    val OutlineVariant = Color(0xFF4A5554)

    val Error = Color(0xFFFFB4AB)
    val OnError = Color(0xFF690005)
    val Success = Color(0xFF7ADB94)
    val OnSuccess = Color(0xFF003919)
}

private val SurroundDarkColorScheme = darkColorScheme(
    primary = SurroundColors.Primary,
    onPrimary = SurroundColors.OnPrimary,
    primaryContainer = SurroundColors.PrimaryContainer,
    onPrimaryContainer = SurroundColors.OnPrimaryContainer,
    secondary = SurroundColors.Secondary,
    onSecondary = SurroundColors.OnSecondary,
    secondaryContainer = SurroundColors.SecondaryContainer,
    onSecondaryContainer = SurroundColors.OnSecondaryContainer,
    tertiary = SurroundColors.Tertiary,
    onTertiary = SurroundColors.OnTertiary,
    tertiaryContainer = SurroundColors.TertiaryContainer,
    onTertiaryContainer = SurroundColors.OnTertiaryContainer,
    background = SurroundColors.Background,
    onBackground = SurroundColors.OnBackground,
    surface = SurroundColors.Surface,
    onSurface = SurroundColors.OnSurface,
    surfaceVariant = SurroundColors.SurfaceVariant,
    onSurfaceVariant = SurroundColors.OnSurfaceVariant,
    surfaceContainerLowest = SurroundColors.SurfaceContainerLowest,
    surfaceContainerLow = SurroundColors.SurfaceContainerLow,
    surfaceContainer = SurroundColors.SurfaceContainer,
    surfaceContainerHigh = SurroundColors.SurfaceContainerHigh,
    surfaceContainerHighest = SurroundColors.SurfaceContainerHighest,
    outline = SurroundColors.Outline,
    outlineVariant = SurroundColors.OutlineVariant,
    error = SurroundColors.Error,
    onError = SurroundColors.OnError
)

@OptIn(ExperimentalTvMaterial3Api::class)
private val SurroundTvDarkColorScheme = tvDarkColorScheme(
    primary = SurroundColors.Primary,
    onPrimary = SurroundColors.OnPrimary,
    primaryContainer = SurroundColors.PrimaryContainer,
    onPrimaryContainer = SurroundColors.OnPrimaryContainer,
    secondary = SurroundColors.Secondary,
    onSecondary = SurroundColors.OnSecondary,
    secondaryContainer = SurroundColors.SecondaryContainer,
    onSecondaryContainer = SurroundColors.OnSecondaryContainer,
    tertiary = SurroundColors.Tertiary,
    onTertiary = SurroundColors.OnTertiary,
    tertiaryContainer = SurroundColors.TertiaryContainer,
    onTertiaryContainer = SurroundColors.OnTertiaryContainer,
    background = SurroundColors.Background,
    onBackground = SurroundColors.OnBackground,
    surface = SurroundColors.Surface,
    onSurface = SurroundColors.OnSurface,
    surfaceVariant = SurroundColors.SurfaceVariant,
    onSurfaceVariant = SurroundColors.OnSurfaceVariant,
    border = SurroundColors.Outline,
    borderVariant = SurroundColors.OutlineVariant,
    error = SurroundColors.Error,
    onError = SurroundColors.OnError
)

val MonoFontFamily = FontFamily.Monospace

private val MobileTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.3).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
    )
)

@OptIn(ExperimentalTvMaterial3Api::class)
private val TvTypographyScale = TvTypography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)

private val ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@OptIn(ExperimentalTvMaterial3Api::class)
private val TvExpressiveShapes = TvShapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/**
 * Expressive motion tokens from DESIGN.md.
 */
object SurroundMotion {
    val StateChangeEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val FocusRingEasing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

    fun <T> stateChangeSpec() = tween<T>(durationMillis = 150, easing = StateChangeEasing)
    fun <T> focusRingSpec() = tween<T>(durationMillis = 200, easing = FocusRingEasing)
}

@Composable
fun SurroundLabMobileTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SurroundDarkColorScheme,
        typography = MobileTypography,
        shapes = ExpressiveShapes,
        content = content
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SurroundLabTvTheme(content: @Composable () -> Unit) {
    TvMaterialTheme(
        colorScheme = SurroundTvDarkColorScheme,
        typography = TvTypographyScale,
        shapes = TvExpressiveShapes,
        content = content
    )
}
