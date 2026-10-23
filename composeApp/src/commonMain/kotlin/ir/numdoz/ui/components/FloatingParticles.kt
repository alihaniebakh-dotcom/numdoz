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
import androidx.compose.ui.unit.dp
import ir.numdoz.ui.theme.NeonCyan
import kotlin.random.Random

private data class Particle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val sizeDp: Float,
    val alpha: Float
)

@Composable
fun FloatingParticles(
    modifier: Modifier = Modifier,
    count: Int = 20
) {
    val particles = remember {
        List(count) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = 0.35f + Random.nextFloat() * 0.9f,
                sizeDp = 1.2f + Random.nextFloat() * 1.8f,
                alpha = 0.10f + Random.nextFloat() * 0.25f
            )
        }
    }

    val t = rememberInfiniteTransition(label = "particles")
    val time by t.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(60000, easing = LinearEasing), RepeatMode.Restart),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            val y = ((p.y - time * p.speed) % 1f + 1f) % 1f
            drawCircle(
                color = NeonCyan.copy(alpha = p.alpha),
                radius = p.sizeDp.dp.toPx(),
                center = Offset(p.x * size.width, y * size.height)
            )
        }
    }
}
