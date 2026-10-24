package ir.numdoz.ads

import androidx.compose.runtime.staticCompositionLocalOf

interface AdManager {
    fun initialize()
    fun showRewarded(
        onRewarded: () -> Unit,
        onClosed: () -> Unit,
        onFailed: (String) -> Unit
    )
}

val LocalAdManager = staticCompositionLocalOf<AdManager> {
    error("AdManager not provided")
}

expect fun createAdManager(): AdManager
