package ir.numdoz.data.session

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.prefs.Preferences

private const val NODE = "ir/numdoz/session"
private const val KEY_SECRET = "session_secret"
private const val KEY_USER_ID = "user_id"

private fun prefs(): Preferences = Preferences.userRoot().node(NODE)

private class DesktopSessionStore : SessionStore {
    override suspend fun getSecret(): String? = withContext(Dispatchers.IO) {
        prefs().get(KEY_SECRET, null)?.takeIf { it.isNotBlank() }
    }

    override suspend fun getUserId(): String? = withContext(Dispatchers.IO) {
        prefs().get(KEY_USER_ID, null)?.takeIf { it.isNotBlank() }
    }

    override suspend fun save(secret: String, userId: String) = withContext(Dispatchers.IO) {
        prefs().put(KEY_SECRET, secret)
        prefs().put(KEY_USER_ID, userId)
        prefs().flush()
        Unit
    }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        prefs().remove(KEY_SECRET)
        prefs().remove(KEY_USER_ID)
        prefs().flush()
        Unit
    }
}

actual fun createSessionStore(): SessionStore = DesktopSessionStore()