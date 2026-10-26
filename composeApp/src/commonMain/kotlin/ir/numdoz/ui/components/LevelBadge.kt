package ir.numdoz.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.numdoz.ui.theme.TextMuted

enum class LevelBadgeState { COMPLETED, AVAILABLE, LOCKED }

@Composable
fun LevelBadge(
    number: Int,
    state: LevelBadgeState,
    accent: Color,
    shape: LogoShape,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    logoName: String? = null,
    stars: Int = 0
) {
    val fillAlpha = when (state) {
        LevelBadgeState.COMPLETED -> 0.30f
        LevelBadgeState.AVAILABLE -> 0.14f
        LevelBadgeState.LOCKED -> 0.05f
    }
    val strokeAlpha = when (state) {
        LevelBadgeState.COMPLETED -> 0.90f
        LevelBadgeState.AVAILABLE -> 0.65f
        LevelBadgeState.LOCKED -> 0.18f
    }
    val fg = when (state) {
        LevelBadgeState.COMPLETED -> accent
        LevelBadgeState.AVAILABLE -> accent
        LevelBadgeState.LOCKED -> TextMuted
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable(enabled = state != LevelBadgeState.LOCKED, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val s = this.size.minDimension
                val c = Offset(this.size.width / 2f, this.size.height / 2f)
                val r = s * 0.42f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = fillAlpha * 0.5f), Color.Transparent),
                        center = c, radius = r * 1.5f
                    ),
                    radius = r * 1.5f, center = c
                )
                drawShapeFilled(
                    shape = shape, center = c, radius = r, color = accent,
                    fillAlpha = fillAlpha, strokeAlpha = strokeAlpha,
                    strokeWidth = (r * 0.10f).coerceAtLeast(1f)
                )
            }
            when (state) {
                LevelBadgeState.LOCKED -> Icon(Icons.Filled.Lock, null, tint = fg, modifier = Modifier.size(14.dp))
                LevelBadgeState.COMPLETED -> Icon(Icons.Filled.Check, null, tint = fg, modifier = Modifier.size(20.dp))
                LevelBadgeState.AVAILABLE -> Text("$number", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = fg)
            }
            if (logoName != null) {
                Box(modifier = Modifier.align(Alignment.BottomEnd).padding(2.dp).size(9.dp)) {
                    Icon(iconForMonthLogo(logoName), null, tint = fg.copy(alpha = 0.6f), modifier = Modifier.fillMaxSize())
                }
            }
        }

        // ★ 3 stars: filled = earned, faded = lost (or pending)
        Row(
            modifier = Modifier.padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { i ->
                val earned = i < stars
                val showDim = state == LevelBadgeState.COMPLETED
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = when {
                        earned -> accent
                        showDim -> accent.copy(alpha = 0.15f)
                        else -> accent.copy(alpha = 0.12f)
                    },
                    modifier = Modifier.size(6.dp)
                )
            }
        }
    }
}
