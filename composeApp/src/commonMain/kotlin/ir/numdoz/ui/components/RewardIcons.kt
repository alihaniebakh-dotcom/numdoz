package ir.numdoz.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Pulsing golden star with sparkles — for "star reward" button. */
@Composable
fun StarRewardIcon(modifier: Modifier = Modifier, tint: Color = Color(0xFFFFC947), animate: Boolean = true) {
    val pulse: Float = if (animate) {
        val t = rememberInfiniteTransition(label = "starPulse")
        val v by t.animateFloat(
            initialValue = 0.85f, targetValue = 1.15f,
            animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "p"
        )
        v
    } else 1f

    Canvas(modifier = modifier) {
        val w = size.width; val h = size.height
        val c = Offset(w / 2f, h / 2f)
        val r = (minOf(w, h) / 2f) * 0.82f * pulse
        val innerR = r * 0.42f
        val star = Path()
        for (i in 0 until 10) {
            val rr = if (i % 2 == 0) r else innerR
            val a = -PI.toFloat() / 2 + PI.toFloat() * i / 5
            val x = c.x + rr * cos(a); val y = c.y + rr * sin(a)
            if (i == 0) star.moveTo(x, y) else star.lineTo(x, y)
        }
        star.close()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(tint.copy(alpha = 0.35f * pulse), Color.Transparent),
                center = c, radius = r * 1.4f
            ), radius = r * 1.4f, center = c
        )
        drawPath(star, tint)
        drawPath(star, Color.White.copy(alpha = 0.5f), style = Stroke(1.2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(Color.White.copy(alpha = 0.6f), radius = r * 0.10f, center = c)
    }
}

/** Gift box with ribbon bow — for "daily reward" chip. */
private fun Float.dpPx(w: Float): Float = w * 0.055f
