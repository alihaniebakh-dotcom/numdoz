package ir.numdoz.ads

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MockAdManager : AdManager {
    override fun initialize() {
        println("[NumDoz/ad] mock initialized")
    }

    override fun showRewarded(onRewarded: () -> Unit, onClosed: () -> Unit, onFailed: (String) -> Unit) {
        println("[NumDoz/ad] mock rewarded (2.2s)")
        GlobalScope.launch {
            delay(1200)
            onRewarded()
            delay(1000)
            onClosed()
        }
    }

    override fun showInterstitial(onClosed: () -> Unit, onFailed: (String) -> Unit) {
        println("[NumDoz/ad] mock interstitial (2.5s)")
        GlobalScope.launch {
            delay(2500)
            onClosed()
        }
    }
}

actual fun createAdManager(): AdManager = MockAdManager()
