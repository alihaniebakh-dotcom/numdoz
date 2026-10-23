package ir.numdoz.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

private val NumDozDarkScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = BgBase,
    primaryContainer = NeonPurple,
    onPrimaryContainer = Color.White,
    secondary = NeonPurple,
    onSecondary = Color.White,
    tertiary = NeonPink,
    onTertiary = BgBase,
    background = BgBase,
    onBackground = TextPrimary,
    surface = SurfaceMin,
    onSurface = TextPrimary,
    surfaceVariant = BgElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderHair,
    error = Error,
    onError = BgBase
)

private fun Typography.withFamily(f: FontFamily): Typography = Typography(
    displayLarge   = displayLarge.copy(fontFamily = f),
    displayMedium  = displayMedium.copy(fontFamily = f),
    headlineLarge  = headlineLarge.copy(fontFamily = f),
    headlineMedium = headlineMedium.copy(fontFamily = f),
    titleLarge     = titleLarge.copy(fontFamily = f),
    titleMedium    = titleMedium.copy(fontFamily = f),
    bodyLarge      = bodyLarge.copy(fontFamily = f),
    bodyMedium     = bodyMedium.copy(fontFamily = f),
    labelLarge     = labelLarge.copy(fontFamily = f),
    labelSmall     = labelSmall.copy(fontFamily = f)
)

@Composable
fun NumDozTheme(content: @Composable () -> Unit) {
    val family = vazirFamily()
    MaterialTheme(
        colorScheme = NumDozDarkScheme,
        typography = NumDozTypographyBase.withFamily(family),
        content = content
    )
}
