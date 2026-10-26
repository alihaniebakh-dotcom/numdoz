package ir.numdoz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.numdoz.ui.theme.SurfaceMin
import ir.numdoz.ui.theme.TextMuted
import ir.numdoz.ui.theme.TextPrimary

@Composable
fun ChapterRow(
    title: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier,
    logoName: String = "sun",
    locked: Boolean = false,
    onClick: () -> Unit = {}
) {
    val shape = shapeForChapter(logoName)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                if (!locked) {
                    val r = size.height * 0.9f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(accent.copy(alpha = 0.10f), Color.Transparent),
                            center = Offset(size.width * 0.15f, size.height / 2f),
                            radius = r
                        ),
                        radius = r, center = Offset(size.width * 0.15f, size.height / 2f)
                    )
                }
            }
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        accent.copy(alpha = if (locked) 0.04f else 0.10f),
                        SurfaceMin.copy(alpha = 0.55f),
                        SurfaceMin.copy(alpha = 0.35f)
                    )
                )
            )
            .clickable(enabled = !locked, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ShapeLogo(
            logoName = logoName,
            shape = shape,
            accent = accent,
            size = 44.dp,
            dim = locked
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (locked) TextMuted else TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (locked) {
            Icon(Icons.Filled.Lock, null, tint = TextMuted, modifier = Modifier.size(20.dp))
        } else {
            Icon(Icons.Filled.ChevronRight, null, tint = TextMuted, modifier = Modifier.size(20.dp))
        }
    }
}
