package ir.numdoz.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val NumDozTypographyBase = Typography(
    displayLarge    = TextStyle(fontWeight = FontWeight.Light,    fontSize = 36.sp),
    displayMedium   = TextStyle(fontWeight = FontWeight.Light,    fontSize = 28.sp),
    headlineLarge   = TextStyle(fontWeight = FontWeight.Light,    fontSize = 24.sp),
    headlineMedium  = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 18.sp),
    titleLarge      = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 16.sp),
    titleMedium     = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 15.sp),
    bodyLarge       = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 14.sp),
    bodyMedium      = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 13.sp),
    labelLarge      = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 12.sp),
    labelSmall      = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 10.sp)
)
