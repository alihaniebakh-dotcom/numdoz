package ir.numdoz.ads

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MockAdManager : AdManager {
    override fun initialize() {
        println("[NumDoz/ad] mock initialized")
    }

    override fun showRewarded(
        onRewarded: () -> Unit,
        onClosed: () -> Unit,
        onFailed: (String) -> Unit
    ) {
        println("[NumDoz/ad] mock: simulating 2.2s ad")
        GlobalScope.launch {
            delay(1200)       // fake loading
            onRewarded()      // award
            delay(1000)       // fake watching
            onClosed()        // close
        }
    }
}

actual fun createAdManager(): AdManager = MockAdManager()
