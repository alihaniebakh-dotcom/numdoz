package ir.numdoz.ads

import android.app.Activity
import ir.numdoz.NumDozApp
import ir.tapsell.mediation.Tapsell
import ir.tapsell.mediation.ad.AdStateListener
import ir.tapsell.mediation.ad.request.RequestResultListener
import ir.tapsell.mediation.ad.show.AdShowCompletionState
import java.lang.ref.WeakReference

private var currentActivity: WeakReference<Activity>? = null

fun registerAdActivity(activity: Activity) {
    currentActivity = WeakReference(activity)
}

class TapsellAdManager : AdManager {
    private var initCalled = false

    override fun initialize() {
        if (initCalled) return
        initCalled = true
        // Auto-init happens via ContentProvider; we just register listener.
        try {
            Tapsell.setInitializationListener {
                println("[NumDoz/ad] Mediation ready")
            }
            println("[NumDoz/ad] Mediation init listener registered")
        } catch (t: Throwable) {
            println("[NumDoz/ad] init listener failed: ${t.message}")
        }
    }

    override fun showRewarded(
        onRewarded: () -> Unit,
        onClosed: () -> Unit,
        onFailed: (String) -> Unit
    ) {
        val activity = currentActivity?.get()
        if (activity == null) {
            onFailed("no activity")
            return
        }
        initialize()

        println("[NumDoz/ad] requesting zone=${NumDozApp.REWARDED_ZONE_ID}")

        try {
            Tapsell.requestRewardedAd(
                NumDozApp.REWARDED_ZONE_ID,
                object : RequestResultListener {
                    override fun onSuccess(adId: String) {
                        println("[NumDoz/ad] ad loaded: $adId")
                        try {
                            Tapsell.showRewardedAd(
                                adId,
                                activity,
                                object : AdStateListener.Rewarded {
                                    override fun onAdImpression() {
                                        println("[NumDoz/ad] impression")
                                    }
                                    override fun onAdClicked() {
                                        println("[NumDoz/ad] clicked")
                                    }
                                    override fun onAdClosed(completionState: AdShowCompletionState) {
                                        println("[NumDoz/ad] closed: $completionState")
                                        onClosed()
                                    }
                                    override fun onRewarded() {
                                        println("[NumDoz/ad] ★ REWARDED")
                                        onRewarded()
                                    }
                                    override fun onAdFailed(message: String) {
                                        println("[NumDoz/ad] show failed: $message")
                                        onFailed(message)
                                    }
                                }
                            )
                        } catch (t: Throwable) {
                            println("[NumDoz/ad] show exception: ${t.message}")
                            onFailed(t.message ?: "show exception")
                        }
                    }

                    override fun onFailure(message: String) {
                        println("[NumDoz/ad] request failed: $message")
                        onFailed(message)
                    }
                }
            )
        } catch (t: Throwable) {
            println("[NumDoz/ad] request exception: ${t.message}")
            onFailed(t.message ?: "request exception")
        }
    }

    override fun showInterstitial(
        onClosed: () -> Unit,
        onFailed: (String) -> Unit
    ) {
        val activity = currentActivity?.get()
        if (activity == null) {
            onFailed("no activity")
            return
        }
        initialize()

        println("[NumDoz/ad] requesting interstitial zone=${NumDozApp.INTERSTITIAL_ZONE_ID}")

        try {
            Tapsell.requestInterstitialAd(
                NumDozApp.INTERSTITIAL_ZONE_ID,
                object : RequestResultListener {
                    override fun onSuccess(adId: String) {
                        println("[NumDoz/ad] interstitial loaded: $adId")
                        try {
                            Tapsell.showInterstitialAd(
                                adId,
                                activity,
                                object : AdStateListener.Interstitial {
                                    override fun onAdImpression() {
                                        println("[NumDoz/ad] interstitial impression")
                                    }
                                    override fun onAdClicked() {
                                        println("[NumDoz/ad] interstitial clicked")
                                    }
                                    override fun onAdClosed(completionState: AdShowCompletionState) {
                                        println("[NumDoz/ad] interstitial closed: $completionState")
                                        onClosed()
                                    }
                                    override fun onAdFailed(message: String) {
                                        println("[NumDoz/ad] interstitial show failed: $message")
                                        onFailed(message)
                                    }
                                }
                            )
                        } catch (t: Throwable) {
                            println("[NumDoz/ad] interstitial show exception: ${t.message}")
                            onFailed(t.message ?: "show exception")
                        }
                    }
                    override fun onFailure(message: String) {
                        println("[NumDoz/ad] interstitial request failed: $message")
                        onFailed(message)
                    }
                }
            )
        } catch (t: Throwable) {
            println("[NumDoz/ad] interstitial request exception: ${t.message}")
            onFailed(t.message ?: "request exception")
        }
    }
}

actual fun createAdManager(): AdManager = TapsellAdManager()
