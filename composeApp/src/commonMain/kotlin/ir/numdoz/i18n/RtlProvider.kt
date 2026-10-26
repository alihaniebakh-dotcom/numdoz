package ir.numdoz.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/** فارسی → RTL، انگلیسی → LTR */
@Composable
fun RtlProvider(content: @Composable () -> Unit) {
    val isFa = LocalLang.current == Lang.FA
    CompositionLocalProvider(
        LocalLayoutDirection provides (if (isFa) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        content()
    }
}
