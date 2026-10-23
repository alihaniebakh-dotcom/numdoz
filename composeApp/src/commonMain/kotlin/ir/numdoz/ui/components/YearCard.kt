package ir.numdoz.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import ir.numdoz.ui.theme.BorderHair
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
    modifier: Modifier = Modifier
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
    val borderAlpha = if (locked) 0.15f else pulse

    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                if (!locked) {
                    val r = size.height * 1.2f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.16f * pulse),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2f, size.height / 2f),
                            radius = r
                        ),
                        radius = r,
                        center = Offset(size.width / 2f, size.height / 2f)
                    )
                }
            }
            .clip(RoundedCornerShape(24.dp))
            // ★ semi-transparent gradient — floating numbers show through
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SurfaceMin.copy(alpha = 0.62f),
                        SurfaceMin.copy(alpha = 0.42f)
                    )
                )
            )
            .border(
                width = if (locked) 1.dp else 1.2.dp,
                color = if (locked) BorderHair else accent.copy(alpha = borderAlpha),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(enabled = !locked, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LogoCircle(logoName = logoName, accent = accent, size = 88.dp, dim = locked)
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
                    .background(accent.copy(alpha = 0.15f))
                    .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "به زودی",
                    color = accent.copy(alpha = 0.95f),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
