package ir.numdoz

import android.app.Application
import ir.tapsell.mediation.Tapsell

class NumDozApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Tapsell auto-initializes via ContentProvider using
        // the manifest placeholder TapsellMediationAppKey.
        // We just register a listener to know when ready.
        try {
            Tapsell.setInitializationListener {
                println("[NumDoz/ad] Mediation initialized (auto)")
            }
        } catch (t: Throwable) {
            println("[NumDoz/ad] setInitListener failed: ${t.message}")
        }
    }

    companion object {
        const val REWARDED_ZONE_ID = "6adcfe0ea43f8d7d0179b662"
        const val INTERSTITIAL_ZONE_ID = ""
    }
}
