package ir.numdoz.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import ir.numdoz.generated.resources.Res
import ir.numdoz.generated.resources.vazirmatn_bold
import ir.numdoz.generated.resources.vazirmatn_light
import ir.numdoz.generated.resources.vazirmatn_medium
import ir.numdoz.generated.resources.vazirmatn_regular
import ir.numdoz.generated.resources.vazirmatn_semi_bold
import org.jetbrains.compose.resources.Font

@Composable
fun vazirFamily(): FontFamily = FontFamily(
    Font(Res.font.vazirmatn_light, FontWeight.Light),
    Font(Res.font.vazirmatn_regular, FontWeight.Normal),
    Font(Res.font.vazirmatn_medium, FontWeight.Medium),
    Font(Res.font.vazirmatn_semi_bold, FontWeight.SemiBold),
    Font(Res.font.vazirmatn_bold, FontWeight.Bold)
)
