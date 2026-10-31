package ir.numdoz.billing

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

object AndroidBillingInstaller {
    private const val tag = "NumDoz/Billing"

    @Volatile private var manager: AndroidBillingManager? = null
    @Volatile private var attached: ComponentActivity? = null

    fun install(activity: ComponentActivity) {
        if (attached === activity) return
        val m = AndroidBillingManager(
            appContext = activity.applicationContext,
            registry = activity.activityResultRegistry,
            rsaPublicKey = BillingConfig.BAZAAR_RSA_KEY,
        )
        manager = m
        attached = activity
        BillingRegistry.install(m)
        activity.lifecycleScope.launch {
            m.connect().onFailure { e ->
                Log.w(tag, "billing connect failed: ${e.message}", e)
            }
        }
        Log.i(tag, "installed for ${activity.javaClass.simpleName}")
    }

    fun disposeIfMatches(activity: ComponentActivity) {
        if (attached === activity) {
            manager?.disconnect()
            manager = null
            attached = null
            BillingRegistry.install(NoopBillingManager())
            Log.i(tag, "disposed")
        }
    }
}