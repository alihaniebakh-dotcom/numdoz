package ir.numdoz.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class ApiException(
    val statusCode: Int,
    override val message: String
) : Exception(message)

class ApiClient(
    private val baseUrl: String = DEFAULT_BASE_URL,
    private val httpClient: HttpClient = buildHttpClient()
) {
    companion object {
        const val DEFAULT_BASE_URL = "https://numdoz-api.alihaniebakh.workers.dev"
    }

    suspend fun register(): RegisterResponse {
        val response = httpClient.post("$baseUrl/api/register")
        return decode<RegisterResponse>(response)
    }

    suspend fun me(secret: String): MeResponse {
        val response = httpClient.get("$baseUrl/api/me") {
            header("Authorization", "Bearer $secret")
        }
        return decode<MeResponse>(response)
    }

    suspend fun sync(secret: String, payload: SyncRequest): MeResponse {
        val response = httpClient.post("$baseUrl/api/sync") {
            header("Authorization", "Bearer $secret")
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
        return decode<MeResponse>(response)
    }

    suspend fun products(): ProductsResponse {
        val response = httpClient.get("$baseUrl/api/products")
        return decode<ProductsResponse>(response)
    }

    suspend fun purchaseVerify(
        secret: String,
        productId: String,
        purchaseToken: String,
        orderId: String? = null
    ): PurchaseResponse {
        val response = httpClient.post("$baseUrl/api/purchase/verify") {
            header("Authorization", "Bearer $secret")
            contentType(ContentType.Application.Json)
            setBody(PurchaseRequest(productId, purchaseToken, orderId))
        }
        return decode<PurchaseResponse>(response)
    }

    private suspend inline fun <reified T> decode(response: HttpResponse): T {
        if (!response.status.isSuccess()) {
            val errorBody = runCatching { response.bodyAsText() }.getOrNull() ?: ""
            throw ApiException(
                response.status.value,
                "HTTP ${response.status.value}: ${errorBody.take(300)}"
            )
        }
        return response.body()
    }
}