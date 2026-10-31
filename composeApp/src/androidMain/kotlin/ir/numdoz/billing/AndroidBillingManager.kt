package ir.numdoz.billing

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultRegistry
import ir.cafebazaar.poolakey.Connection
import ir.cafebazaar.poolakey.Payment
import ir.cafebazaar.poolakey.config.PaymentConfiguration
import ir.cafebazaar.poolakey.config.SecurityCheck
import ir.cafebazaar.poolakey.entity.PurchaseInfo
import ir.cafebazaar.poolakey.entity.PurchaseState
import ir.cafebazaar.poolakey.request.PurchaseRequest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AndroidBillingManager(
    appContext: Context,
    private val registry: ActivityResultRegistry,
    rsaPublicKey: String,
) : BillingManager {

    private val tag = "NumDoz/Billing"

    private val securityCheck: SecurityCheck =
        if (rsaPublicKey.isNotBlank()) SecurityCheck.Enable(rsaPublicKey)
        else SecurityCheck.Disable

    private val payment = Payment(
        appContext,
        PaymentConfiguration(localSecurityCheck = securityCheck),
    )

    @Volatile private var connection: Connection? = null
    @Volatile private var connected: Boolean = false
    private val connectMutex = Mutex()

    override fun isConnected(): Boolean = connected

    override suspend fun connect(): Result<Unit> = connectMutex.withLock {
        if (connected) return@withLock Result.success(Unit)

        val deferred = CompletableDeferred<Unit>()
        try {
            connection = payment.connect {
                connectionSucceed {
                    connected = true
                    Log.i(tag, "connect: success")
                    if (deferred.isActive) deferred.complete(Unit)
                }
                connectionFailed { throwable ->
                    connected = false
                    Log.e(tag, "connect: failed ${throwable.message}", throwable)
                    if (deferred.isActive) deferred.completeExceptionally(throwable)
                }
                disconnected {
                    connected = false
                    Log.w(tag, "connect: disconnected")
                }
            }
        } catch (t: Throwable) {
            connected = false
            Log.e(tag, "connect: exception ${t.message}", t)
            return@withLock Result.failure(t)
        }

        runCatching { deferred.await() }
    }

    override suspend fun queryProducts(productIds: List<String>): Result<List<BillingProduct>> = runCatching {
        ensureConnected()

        val deferred = CompletableDeferred<List<BillingProduct>>()
        payment.getInAppSkuDetails(productIds) {
            getSkuDetailsSucceed { skuDetails ->
                Log.i(tag, "query: ${skuDetails.size} products")
                if (deferred.isActive) {
                    deferred.complete(
                        skuDetails.map { s ->
                            BillingProduct(
                                id = s.sku,
                                title = s.title,
                                description = s.description,
                                price = s.price,
                            )
                        }
                    )
                }
            }
            getSkuDetailsFailed { throwable ->
                Log.e(tag, "query: failed ${throwable.message}", throwable)
                if (deferred.isActive) deferred.completeExceptionally(throwable)
            }
        }
        deferred.await()
    }

    override suspend fun purchase(productId: String, payload: String): Result<BillingPurchase> = runCatching {
        ensureConnected()

        val deferred = CompletableDeferred<BillingPurchase>()
        val request = PurchaseRequest(productId = productId, payload = payload)

        payment.purchaseProduct(registry, request) {
            purchaseFlowBegan {
                Log.i(tag, "purchase: flow began for $productId")
            }
            failedToBeginFlow { throwable ->
                Log.e(tag, "purchase: failedToBeginFlow ${throwable.message}", throwable)
                if (deferred.isActive) deferred.completeExceptionally(throwable)
            }
            purchaseCanceled {
                Log.w(tag, "purchase: canceled by user")
                if (deferred.isActive) {
                    deferred.completeExceptionally(IllegalStateException("purchase canceled"))
                }
            }
            purchaseFailed { throwable ->
                Log.e(tag, "purchase: failed ${throwable.message}", throwable)
                if (deferred.isActive) deferred.completeExceptionally(throwable)
            }
            purchaseSucceed { info: PurchaseInfo ->
                if (info.purchaseState != PurchaseState.PURCHASED) {
                    Log.w(tag, "purchase: state=${info.purchaseState}, rejecting")
                    if (deferred.isActive) {
                        deferred.completeExceptionally(
                            IllegalStateException("purchase state not PURCHASED: ${info.purchaseState}")
                        )
                    }
                    return@purchaseSucceed
                }
                Log.i(tag, "purchase: success productId=${info.productId}")
                if (deferred.isActive) {
                    deferred.complete(
                        BillingPurchase(
                            productId = info.productId,
                            purchaseToken = info.purchaseToken,
                            payload = info.payload ?: "",
                            signature = info.dataSignature ?: "",
                            orderId = info.orderId ?: "",
                            originalJson = info.originalJson ?: "",
                        )
                    )
                }
            }
        }
        deferred.await()
    }

    override suspend fun consume(purchaseToken: String): Result<Unit> = runCatching {
        val deferred = CompletableDeferred<Unit>()
        payment.consumeProduct(purchaseToken) {
            consumeSucceed {
                Log.i(tag, "consume: success")
                if (deferred.isActive) deferred.complete(Unit)
            }
            consumeFailed { throwable ->
                Log.e(tag, "consume: failed ${throwable.message}", throwable)
                if (deferred.isActive) deferred.completeExceptionally(throwable)
            }
        }
        deferred.await()
    }

    override fun disconnect() {
        connection?.disconnect()
        connection = null
        connected = false
    }

    private fun ensureConnected() {
        check(connected) { "Billing not connected. Call connect() first." }
    }
}