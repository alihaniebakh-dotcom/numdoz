package ir.numdoz.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalletDto(
    @SerialName("user_id") val userId: String? = null,
    val coins: Int = 0,
    val hints: Int = 0,
    val capsules: Int = 0,
    @SerialName("updated_at") val updatedAt: Long = 0L
)

@Serializable
data class UserDto(
    val id: String,
    val username: String = "",
    @SerialName("avatar_id") val avatarId: Int = 0,
    @SerialName("created_at") val createdAt: Long = 0L,
    @SerialName("updated_at") val updatedAt: Long = 0L
)

@Serializable
data class ProgressDto(
    @SerialName("user_id") val userId: String = "",
    @SerialName("level_id") val levelId: String,
    val completed: Int = 0,
    val stars: Int = 0,
    @SerialName("best_time") val bestTime: Int = 0,
    @SerialName("updated_at") val updatedAt: Long = 0L
)

@Serializable
data class RegisterResponse(
    val id: String,
    val sessionSecret: String,
    val wallet: WalletDto = WalletDto()
)

@Serializable
data class MeResponse(
    val user: UserDto? = null,
    val progress: List<ProgressDto> = emptyList(),
    val wallet: WalletDto? = null
)

@Serializable
data class WalletRequest(val coins: Int, val hints: Int, val capsules: Int)

@Serializable
data class ProgressRequest(
    val levelId: String,
    val completed: Boolean = false,
    val stars: Int = 0,
    val bestTime: Int = 0
)

@Serializable
data class SyncRequest(
    val username: String? = null,
    val avatarId: Int? = null,
    val wallet: WalletRequest? = null,
    val progress: List<ProgressRequest>? = null
)

@Serializable
data class ProductDto(val id: String, val coins: Int, val title: String)

@Serializable
data class ProductsResponse(val products: List<ProductDto>, val mock: Boolean = false)

@Serializable
data class PurchaseRequest(
    val productId: String,
    val purchaseToken: String,
    val orderId: String? = null
)

@Serializable
data class PurchaseResponse(
    val ok: Boolean = true,
    val alreadyOwned: Boolean = false,
    val coinsGranted: Int = 0,
    val wallet: WalletDto? = null
)

@Serializable
data class ErrorResponse(val error: String = "unknown")