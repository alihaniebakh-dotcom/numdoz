package ir.numdoz.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ir.numdoz.ui.theme.LocalAccent
import ir.numdoz.ui.theme.LocalAccentAlt

@Composable
fun AnimatedLogo(
    modifier: Modifier = Modifier,
    size: Dp = 128.dp
) {
    val accent = LocalAccent.current
    val accentAlt = LocalAccentAlt.current

    val t = rememberInfiniteTransition(label = "logo")

    val rotation by t.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Restart),
        label = "rot"
    )
    val wave by t.animateFloat(
        0f, 4f,
        infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Restart),
        label = "wave"
    )
    val pulse by t.animateFloat(
        0.25f, 1f,
        infiniteRepeatable(tween(1900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f

        val ringR = s * 0.46f
        val segCount = 24
        for (i in 0 until segCount) {
            val a = i * 360f / segCount + rotation
            drawArc(
                color = accent.copy(alpha = 0.10f + 0.10f * pulse),
                startAngle = a,
                sweepAngle = 360f / segCount * 0.35f,
                useCenter = false,
                topLeft = Offset(cx - ringR, cy - ringR),
                size = Size(ringR * 2, ringR * 2),
                style = Stroke(width = 0.8.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        val lineLen = s * 0.55f
        val x0 = cx - lineLen * 0.5f
        val y0 = cy + lineLen * 0.5f
        val dx = lineLen / 4f
        val dy = -lineLen / 4f
        val positions = List(5) { i -> Offset(x0 + dx * i, y0 + dy * i) }

        for (i in 0..3) {
            drawLine(
                color = accent.copy(alpha = 0.22f),
                start = positions[i],
                end = positions[i + 1],
                strokeWidth = 1.dp.toPx()
            )
        }

        val seg = wave.toInt().coerceIn(0, 3)
        val localT = (wave - seg).coerceIn(0f, 1f)
        val pulsePos = Offset(
            positions[seg].x + (positions[seg + 1].x - positions[seg].x) * localT,
            positions[seg].y + (positions[seg + 1].y - positions[seg].y) * localT
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accent.copy(alpha = 0.85f), Color.Transparent),
                center = pulsePos,
                radius = s * 0.07f
            ),
            radius = s * 0.07f,
            center = pulsePos
        )

        for (i in 0..3) {
            val r = s * (0.028f + i * 0.004f)
            drawCircle(color = accent, radius = r, center = positions[i])
        }

        val target = positions[4]
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentAlt.copy(alpha = 0.45f * pulse),
                    Color.Transparent
                ),
                center = target,
                radius = s * 0.16f
            ),
            radius = s * 0.16f,
            center = target
        )
        drawCircle(
            color = accent.copy(alpha = 0.6f + 0.4f * pulse),
            radius = s * 0.055f,
            center = target,
            style = Stroke(width = 1.5.dp.toPx())
        )
        drawCircle(
            color = accent.copy(alpha = pulse),
            radius = s * 0.018f,
            center = target
        )
    }
}
