package ir.numdoz.data.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val PREFS_NAME = "numdoz_session"
private const val KEY_SECRET = "session_secret"
private const val KEY_USER_ID = "user_id"

private var appContext: Context? = null

fun initAndroidSessionStore(context: Context) {
    appContext = context.applicationContext
}

private fun prefs(): SharedPreferences {
    val ctx = appContext
        ?: error("Call initAndroidSessionStore(context) in MainActivity.onCreate before using SessionStore")
    return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}

private class AndroidSessionStore : SessionStore {
    override suspend fun getSecret(): String? = withContext(Dispatchers.IO) {
        prefs().getString(KEY_SECRET, null)?.takeIf { it.isNotBlank() }
    }

    override suspend fun getUserId(): String? = withContext(Dispatchers.IO) {
        prefs().getString(KEY_USER_ID, null)?.takeIf { it.isNotBlank() }
    }

    override suspend fun save(secret: String, userId: String) = withContext(Dispatchers.IO) {
        prefs().edit {
            putString(KEY_SECRET, secret)
            putString(KEY_USER_ID, userId)
        }
        Unit
    }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        prefs().edit { clear() }
        Unit
    }
}

actual fun createSessionStore(): SessionStore = AndroidSessionStore()