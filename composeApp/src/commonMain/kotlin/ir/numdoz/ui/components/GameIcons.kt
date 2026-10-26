package ir.numdoz.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ═══════════════════════════════════════════════════════════════════════
// 1. COIN — "N" in a double-ringed coin (NumDoz currency)
// ═══════════════════════════════════════════════════════════════════════
@Composable
fun CoinIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFFFC947),
    animate: Boolean = true
) {
    val measurer = rememberTextMeasurer()
    val spin by if (animate) {
        val t = rememberInfiniteTransition(label = "coinSpin")
        t.animateFloat(
            initialValue = 0f, targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
            label = "sp"
        )
    } else androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0f) }

    Canvas(modifier = modifier) {
        val w = size.width; val h = size.height
        val c = Offset(w / 2f, h / 2f)
        val rOuter = minOf(w, h) * 0.46f
        val rInner = rOuter * 0.82f
        val stroke = (rOuter * 0.09f).coerceAtLeast(1f)

        // Radial glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(tint.copy(alpha = 0.30f), Color.Transparent),
                center = c, radius = rOuter * 1.5f
            ), radius = rOuter * 1.5f, center = c
        )

        // Outer coin fill
        drawCircle(tint.copy(alpha = 0.22f), rOuter, c)

        // Rotating dashed outer ring
        val segs = 14
        for (i in 0 until segs) {
            val a = spin + i * 360f / segs
            drawArc(
                color = tint.copy(alpha = 0.9f),
                startAngle = a, sweepAngle = 360f / segs * 0.5f,
                useCenter = false,
                topLeft = Offset(c.x - rOuter, c.y - rOuter),
                size = Size(rOuter * 2, rOuter * 2),
                style = Stroke(width = stroke * 0.7f, cap = StrokeCap.Round)
            )
        }

        // Inner solid ring
        drawCircle(tint, rInner, c, style = Stroke(width = stroke))

        // "N" letter centered
        val textLayout = measurer.measure(
            text = AnnotatedString("N"),
            style = TextStyle(
                fontSize = (rInner * 1.1f).toSp(),
                fontWeight = FontWeight.Bold,
                color = tint
            )
        )
        drawText(
            textLayoutResult = textLayout,
            topLeft = Offset(c.x - textLayout.size.width / 2f, c.y - textLayout.size.height / 2f)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════
// 2. HINT — "?" in a puzzle card (matches a sequence fragment "3 ? 5")
// ═══════════════════════════════════════════════════════════════════════
@Composable
fun HintIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF4FE3E0),
    animate: Boolean = true
) {
    val measurer = rememberTextMeasurer()
    val pulse by if (animate) {
        val t = rememberInfiniteTransition(label = "hintPulse")
        t.animateFloat(
            initialValue = 0.55f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "hp"
        )
    } else androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(1f) }

    Canvas(modifier = modifier) {
        val w = size.width; val h = size.height
        val cx = w / 2f; val cy = h / 2f
        val cardSide = minOf(w, h) * 0.78f
        val half = cardSide / 2f
        val corner = cardSide * 0.20f
        val stroke = (cardSide * 0.09f).coerceAtLeast(1f)

        // Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(tint.copy(alpha = 0.30f * pulse), Color.Transparent),
                center = Offset(cx, cy), radius = half * 1.6f
            ), radius = half * 1.6f, center = Offset(cx, cy)
        )

        // Rounded-square puzzle card
        val card = Path().apply {
            val x0 = cx - half; val y0 = cy - half
            val x1 = cx + half; val y1 = cy + half
            moveTo(x0 + corner, y0)
            lineTo(x1 - corner, y0)
            quadraticBezierTo(x1, y0, x1, y0 + corner)
            lineTo(x1, y1 - corner)
            quadraticBezierTo(x1, y1, x1 - corner, y1)
            lineTo(x0 + corner, y1)
            quadraticBezierTo(x0, y1, x0, y1 - corner)
            lineTo(x0, y0 + corner)
            quadraticBezierTo(x0, y0, x0 + corner, y0)
            close()
        }
        drawPath(card, tint.copy(alpha = 0.15f + 0.10f * pulse))
        drawPath(card, tint, style = Stroke(width = stroke, join = StrokeJoin.Round))

        // "?" in the center
        val qLayout = measurer.measure(
            text = AnnotatedString("?"),
            style = TextStyle(
                fontSize = (cardSide * 0.85f).toSp(),
                fontWeight = FontWeight.Bold,
                color = tint
            )
        )
        drawText(
            textLayoutResult = qLayout,
            topLeft = Offset(cx - qLayout.size.width / 2f, cy - qLayout.size.height / 2f)
        )

        // Orbiting bright dot
        val orbitR = half * 1.10f
        val ang = (pulse * PI.toFloat() * 2)
        val dotX = cx + orbitR * cos(ang)
        val dotY = cy + orbitR * sin(ang)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(tint, Color.Transparent),
                center = Offset(dotX, dotY), radius = cardSide * 0.12f
            ),
            radius = cardSide * 0.12f, center = Offset(dotX, dotY)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════
// 3. TIME BOOST — "+30" inside a rotating-clock circle
// ═══════════════════════════════════════════════════════════════════════
@Composable
fun TimeBoostIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF22D3EE),
    animate: Boolean = true
) {
    val measurer = rememberTextMeasurer()
    val spin by if (animate) {
        val t = rememberInfiniteTransition(label = "timeSpin")
        t.animateFloat(
            initialValue = 0f, targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
            label = "sp"
        )
    } else androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0f) }

    Canvas(modifier = modifier) {
        val w = size.width; val h = size.height
        val c = Offset(w / 2f, h / 2f)
        val r = minOf(w, h) * 0.42f
        val stroke = (r * 0.12f).coerceAtLeast(1f)

        // Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(tint.copy(alpha = 0.30f), Color.Transparent),
                center = c, radius = r * 1.5f
            ), radius = r * 1.5f, center = c
        )

        // Base circle
        drawCircle(tint.copy(alpha = 0.15f), r, c)
        drawCircle(tint, r, c, style = Stroke(width = stroke))

        // Rotating arc (clock tick)
        drawArc(
            color = tint,
            startAngle = spin,
            sweepAngle = 70f,
            useCenter = false,
            topLeft = Offset(c.x - r * 1.18f, c.y - r * 1.18f),
            size = Size(r * 2.36f, r * 2.36f),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        // Tick marks (like clock at 12, 3, 6, 9)
        val tickR = r * 0.92f
        for (i in 0 until 4) {
            val a = i * PI.toFloat() / 2 - PI.toFloat() / 2
            val x1 = c.x + tickR * 0.82f * cos(a)
            val y1 = c.y + tickR * 0.82f * sin(a)
            val x2 = c.x + tickR * cos(a)
            val y2 = c.y + tickR * sin(a)
            drawLine(tint.copy(alpha = 0.7f), Offset(x1, y1), Offset(x2, y2), strokeWidth = stroke * 0.6f, cap = StrokeCap.Round)
        }

        // "+30" text
        val layout = measurer.measure(
            text = AnnotatedString("+30"),
            style = TextStyle(
                fontSize = (r * 0.68f).toSp(),
                fontWeight = FontWeight.Bold,
                color = tint
            )
        )
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(c.x - layout.size.width / 2f, c.y - layout.size.height / 2f)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════
// 4. DAILY REWARD — Star burst with "day" streak number
// ═══════════════════════════════════════════════════════════════════════
@Composable
fun DailyIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFF472B6),
    animate: Boolean = true
) {
    val measurer = rememberTextMeasurer()
    val pulse by if (animate) {
        val t = rememberInfiniteTransition(label = "dailyPulse")
        t.animateFloat(
            initialValue = 0.85f, targetValue = 1.15f,
            animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "dp"
        )
    } else androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(1f) }

    Canvas(modifier = modifier) {
        val w = size.width; val h = size.height
        val cx = w / 2f; val cy = h / 2f
        val r = minOf(w, h) * 0.40f * pulse

        // Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(tint.copy(alpha = 0.35f * pulse), Color.Transparent),
                center = Offset(cx, cy), radius = r * 1.7f
            ), radius = r * 1.7f, center = Offset(cx, cy)
        )

        // Star
        val starPath = Path()
        val points = 5
        for (i in 0 until points * 2) {
            val rr = if (i % 2 == 0) r else r * 0.45f
            val ang = -PI.toFloat() / 2 + PI.toFloat() * i / points
            val x = cx + rr * cos(ang)
            val y = cy + rr * sin(ang)
            if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
        }
        starPath.close()
        drawPath(starPath, tint.copy(alpha = 0.28f))
        drawPath(starPath, tint, style = Stroke((r * 0.14f).coerceAtLeast(1f), join = StrokeJoin.Round))

        // Small 4-point sparkles around
        val sparkOffsets = listOf(
            0f to -r * 1.30f,   // top
            r * 1.25f to 0f,    // right
            0f to r * 1.30f,    // bottom
            -r * 1.25f to 0f    // left
        )
        sparkOffsets.forEach { (dx, dy) ->
            val sx = cx + dx; val sy = cy + dy
            val s = r * 0.14f * pulse
            drawLine(tint.copy(alpha = 0.8f), Offset(sx - s, sy), Offset(sx + s, sy), strokeWidth = 1.5f, cap = StrokeCap.Round)
            drawLine(tint.copy(alpha = 0.8f), Offset(sx, sy - s), Offset(sx, sy + s), strokeWidth = 1.5f, cap = StrokeCap.Round)
        }

        // "روز" tiny text under star
        val layout = measurer.measure(
            text = AnnotatedString("1×"),
            style = TextStyle(
                fontSize = (r * 0.55f).toSp(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF08080E)
            )
        )
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(cx - layout.size.width / 2f, cy - layout.size.height / 2f + r * 0.05f)
        )
    }
}

private fun Float.toSp(): androidx.compose.ui.unit.TextUnit = this.sp
