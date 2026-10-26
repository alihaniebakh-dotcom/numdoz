package ir.numdoz.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAccent = staticCompositionLocalOf { NeonCyan }
val LocalAccentAlt = staticCompositionLocalOf { NeonPurple }
val LocalAccentTertiary = staticCompositionLocalOf { NeonPink }

fun spectrumColor(hue: Float, saturation: Float = 0.85f, lightness: Float = 0.62f): Color {
    val h = ((hue % 360f) + 360f) % 360f
    return Color.hsl(h, saturation, lightness)
}

@Composable
fun rememberSpectrumHue(cycleSeconds: Int = 120, phaseOffset: Float = 0f): Float {
    val transition = rememberInfiniteTransition(label = "spectrum")
    val hue by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(cycleSeconds * 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hue"
    )
    return (hue + phaseOffset) % 360f
}
