package ir.numdoz.data.session

/**
 * Persistent storage for the anonymous session issued by /api/register.
 * Never store the sessionSecret in logs or share it across users.
 */
interface SessionStore {
    suspend fun getSecret(): String?
    suspend fun getUserId(): String?
    suspend fun save(secret: String, userId: String)
    suspend fun clear()
    suspend fun isRegistered(): Boolean = getSecret() != null
}

expect fun createSessionStore(): SessionStore