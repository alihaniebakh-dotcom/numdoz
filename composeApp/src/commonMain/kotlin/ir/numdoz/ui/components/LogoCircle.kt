package ir.numdoz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LogoCircle(
    logoName: String,
    accent: Color,
    size: Dp = 48.dp,
    dim: Boolean = false
) {
    val a = if (dim) 0.45f else 1f
    val icon = iconForLogo(logoName)
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(accent.copy(alpha = 0.15f * a))
            .border(1.dp, accent.copy(alpha = 0.55f * a), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent.copy(alpha = a),
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

private fun iconForLogo(name: String): ImageVector = when (name) {
    "sun" -> Icons.Filled.WbSunny
    "sunrise" -> Icons.Filled.WbSunny
    "brightness" -> Icons.Filled.Brightness5
    "wind" -> Icons.Filled.Air
    "wave" -> Icons.Filled.Waves
    "water" -> Icons.Filled.InvertColors
    "cloud" -> Icons.Filled.Cloud
    "ember" -> Icons.Filled.LocalFireDepartment
    "flame" -> Icons.Filled.Whatshot
    "sunset" -> Icons.Filled.WbTwilight
    "snow" -> Icons.Filled.AcUnit
    "moon" -> Icons.Filled.Nightlight
    "star" -> Icons.Filled.Star
    "drop" -> Icons.Filled.Opacity
    else -> Icons.Filled.Star
}
