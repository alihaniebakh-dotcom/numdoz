package ir.numdoz.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Cloud
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun iconForMonthLogo(name: String): ImageVector = when (name) {
    "sun", "sunrise" -> Icons.Filled.WbSunny
    "brightness" -> Icons.Filled.Brightness5
    "wind" -> Icons.Filled.Air
    "wave" -> Icons.Filled.Waves
    "water" -> Icons.Filled.Opacity
    "cloud" -> Icons.Filled.Cloud
    "ember" -> Icons.Filled.LocalFireDepartment
    "flame" -> Icons.Filled.Whatshot
    "sunset" -> Icons.Filled.WbTwilight
    "snow" -> Icons.Filled.AcUnit
    "moon" -> Icons.Filled.Nightlight
    "star" -> Icons.Filled.Star
    else -> Icons.Filled.Star
}

@Composable
fun ShapeLogo(
    logoName: String,
    shape: LogoShape,
    accent: Color,
    size: Dp = 64.dp,
    dim: Boolean = false
) {
    val a = if (dim) 0.45f else 1f
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val s = this.size.minDimension
            val c = Offset(this.size.width / 2f, this.size.height / 2f)
            val r = s * 0.44f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(accent.copy(alpha = 0.28f * a), Color.Transparent),
                    center = c, radius = r * 1.5f
                ),
                radius = r * 1.5f, center = c
            )

            drawShapeFilled(
                shape = shape,
                center = c,
                radius = r,
                color = accent,
                fillAlpha = 0.16f * a,
                strokeAlpha = 0.80f * a,
                strokeWidth = (r * 0.08f).coerceAtLeast(1.2f)
            )
        }

        Icon(
            imageVector = iconForMonthLogo(logoName),
            contentDescription = null,
            tint = accent.copy(alpha = a),
            modifier = Modifier.size(size * 0.38f)
        )
    }
}
