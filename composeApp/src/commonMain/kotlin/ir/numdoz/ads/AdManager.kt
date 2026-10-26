package ir.numdoz.ads

import androidx.compose.runtime.staticCompositionLocalOf

interface AdManager {
    fun initialize()
    fun showRewarded(onRewarded: () -> Unit, onClosed: () -> Unit, onFailed: (String) -> Unit)
    fun showInterstitial(onClosed: () -> Unit, onFailed: (String) -> Unit)
}

object SessionTracker {
    var levelsCompletedThisSession: Int = 0
    fun reset() { levelsCompletedThisSession = 0 }
}

val LocalAdManager = staticCompositionLocalOf<AdManager> {
    error("AdManager not provided")
}

expect fun createAdManager(): AdManager
