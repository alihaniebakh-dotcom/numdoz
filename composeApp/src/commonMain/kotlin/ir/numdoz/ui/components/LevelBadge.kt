package ir.numdoz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgAlpha = when (state) {
        LevelBadgeState.COMPLETED -> 0.20f
        LevelBadgeState.AVAILABLE -> 0.08f
        LevelBadgeState.LOCKED    -> 0.04f
    }
    val borderAlpha = when (state) {
        LevelBadgeState.COMPLETED -> 0.75f
        LevelBadgeState.AVAILABLE -> 0.50f
        LevelBadgeState.LOCKED    -> 0.15f
    }
    val fg = when (state) {
        LevelBadgeState.COMPLETED -> accent
        LevelBadgeState.AVAILABLE -> accent.copy(alpha = 0.92f)
        LevelBadgeState.LOCKED    -> TextMuted
    }

    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = bgAlpha))
            .border(1.dp, accent.copy(alpha = borderAlpha), RoundedCornerShape(12.dp))
            .clickable(enabled = state != LevelBadgeState.LOCKED, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            LevelBadgeState.LOCKED -> Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(18.dp)
            )
            LevelBadgeState.COMPLETED -> Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(24.dp)
            )
            LevelBadgeState.AVAILABLE -> Text(
                text = "$number",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = fg
            )
        }
    }
}
