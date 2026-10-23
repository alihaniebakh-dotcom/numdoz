package ir.numdoz.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import ir.numdoz.ui.theme.BgBase
import ir.numdoz.ui.theme.LocalAccent
import ir.numdoz.ui.theme.LocalAccentAlt

@Composable
fun MinimalBackground(
    modifier: Modifier = Modifier,
    overrideAccent: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val topTarget = overrideAccent ?: LocalAccent.current
    val bottomTarget = overrideAccent ?: LocalAccentAlt.current

    val topColor by animateColorAsState(
        targetValue = topTarget,
        animationSpec = tween(600),
        label = "bgTop"
    )
    val bottomColor by animateColorAsState(
        targetValue = bottomTarget,
        animationSpec = tween(600),
        label = "bgBottom"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgBase)
            .drawBehind {
                // Top glow
                val topRadius = size.width * 1.0f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(topColor.copy(alpha = 0.10f), Color.Transparent),
                        center = Offset(size.width / 2f, 0f),
                        radius = topRadius
                    ),
                    radius = topRadius,
                    center = Offset(size.width / 2f, 0f)
                )
                // Bottom glow
                val botRadius = size.width * 0.95f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(bottomColor.copy(alpha = 0.08f), Color.Transparent),
                        center = Offset(size.width / 2f, size.height),
                        radius = botRadius
                    ),
                    radius = botRadius,
                    center = Offset(size.width / 2f, size.height)
                )
                // Left glow
                val leftRadius = size.width * 0.6f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(bottomColor.copy(alpha = 0.05f), Color.Transparent),
                        center = Offset(0f, size.height * 0.5f),
                        radius = leftRadius
                    ),
                    radius = leftRadius,
                    center = Offset(0f, size.height * 0.5f)
                )
                // Right glow
                val rightRadius = size.width * 0.6f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(topColor.copy(alpha = 0.05f), Color.Transparent),
                        center = Offset(size.width, size.height * 0.5f),
                        radius = rightRadius
                    ),
                    radius = rightRadius,
                    center = Offset(size.width, size.height * 0.5f)
                )
            }
    ) {
        content()
    }
}
