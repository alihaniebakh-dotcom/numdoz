package ir.numdoz.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import ir.numdoz.ui.theme.NeonCyan
import kotlin.random.Random

private data class NumberParticle(
    val digit: Char,
    val x: Float,
    val y: Float,
    val speed: Float,
    val fontSizeSp: Int,
    val alpha: Float
)

@Composable
fun FloatingNumbers(
    modifier: Modifier = Modifier,
    count: Int = 32,
    color: Color = NeonCyan
) {
    val textMeasurer = rememberTextMeasurer()
    val digits = "0123456789"

    val particles = remember {
        List(count) {
            NumberParticle(
                digit = digits.random(),
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = 0.35f + Random.nextFloat() * 0.9f,
                fontSizeSp = 18 + Random.nextInt(0, 18),
                alpha = 0.20f + Random.nextFloat() * 0.28f
            )
        }
    }

    val layouts = remember(particles) {
        particles.map { p ->
            textMeasurer.measure(
                text = AnnotatedString(p.digit.toString()),
                style = TextStyle(
                    fontSize = p.fontSizeSp.sp,
                    fontWeight = FontWeight.Light
                )
            )
        }
    }

    val t = rememberInfiniteTransition(label = "numbers")
    val time by t.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(60000, easing = LinearEasing), RepeatMode.Restart),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEachIndexed { i, p ->
            val yFrac = ((p.y - time * p.speed) % 1f + 1f) % 1f
            val layout = layouts[i]
            val cx = p.x * size.width
            val cy = yFrac * size.height
            val topLeft = Offset(
                cx - layout.size.width / 2f,
                cy - layout.size.height / 2f
            )
            drawText(
                textLayoutResult = layout,
                color = color,
                topLeft = topLeft,
                alpha = p.alpha
            )
        }
    }
}
