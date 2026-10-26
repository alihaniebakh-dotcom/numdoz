package ir.numdoz.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.numdoz.ui.theme.SurfaceMin
import ir.numdoz.ui.theme.TextMuted
import ir.numdoz.ui.theme.TextPrimary

@Composable
fun YearCard(
    title: String,
    titleEn: String,
    accent: Color,
    logoName: String,
    locked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    comingSoonLabel: String = "Coming soon"
) {
    val glow = rememberInfiniteTransition(label = "yearGlow")
    val pulse by glow.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val intensity = if (locked) 0.3f else pulse

    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                if (!locked) {
                    val r = size.height * 1.3f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(accent.copy(alpha = 0.18f * pulse), Color.Transparent),
                            center = Offset(size.width / 2f, size.height / 2f),
                            radius = r
                        ),
                        radius = r, center = Offset(size.width / 2f, size.height / 2f)
                    )
                }
            }
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.10f * intensity),
                        SurfaceMin.copy(alpha = 0.65f),
                        SurfaceMin.copy(alpha = 0.40f)
                    ),
                    center = Offset.Unspecified
                )
            )
            .clickable(enabled = !locked, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 26.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ShapeLogo(
                logoName = logoName,
                shape = shapeForYear(logoName),
                accent = accent,
                size = 92.dp,
                dim = locked
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = if (locked) TextMuted else TextPrimary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = titleEn,
                style = MaterialTheme.typography.labelLarge,
                color = accent.copy(alpha = if (locked) 0.45f else 0.9f)
            )
            if (locked) {
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(accent.copy(alpha = 0.12f))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = comingSoonLabel,
                        color = accent.copy(alpha = 0.95f),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
