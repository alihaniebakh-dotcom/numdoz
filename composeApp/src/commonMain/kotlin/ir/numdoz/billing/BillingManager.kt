package ir.numdoz.billing

/** SKU details returned by queryProducts. */
data class BillingProduct(
    val id: String,
    val title: String,
    val description: String,
    val price: String
)

/** Result of a successful purchase, to be verified on the server. */
data class BillingPurchase(
    val productId: String,
    val purchaseToken: String,
    val payload: String,
    val signature: String,
    val orderId: String,
    val originalJson: String
)

/**
 * Cross-platform billing abstraction.
 * Android uses Poolakey (Bazaar IAB); other targets fall back to Noop.
 */
interface BillingManager {
    suspend fun connect(): Result<Unit>
    fun isConnected(): Boolean
    suspend fun queryProducts(productIds: List<String>): Result<List<BillingProduct>>
    suspend fun purchase(productId: String, payload: String): Result<BillingPurchase>
    suspend fun consume(purchaseToken: String): Result<Unit>
    fun disconnect()
}

/** Safe fallback used by desktop and before Android billing is initialized. */
class NoopBillingManager : BillingManager {
    private val err = UnsupportedOperationException("Billing not available on this platform")
    override suspend fun connect(): Result<Unit> = Result.failure(err)
    override fun isConnected(): Boolean = false
    override suspend fun queryProducts(productIds: List<String>): Result<List<BillingProduct>> = Result.failure(err)
    override suspend fun purchase(productId: String, payload: String): Result<BillingPurchase> = Result.failure(err)
    override suspend fun consume(purchaseToken: String): Result<Unit> = Result.failure(err)
    override fun disconnect() {}
}

/**
 * Global install point so any screen can obtain the current BillingManager
 * without threading it through every composable parameter.
 */
object BillingRegistry {
    @Volatile private var instance: BillingManager? = null
    fun install(manager: BillingManager) { instance = manager }
    fun get(): BillingManager = instance ?: NoopBillingManager()
}