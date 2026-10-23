package ir.numdoz.data.local

import android.content.Context

private var appContext: Context? = null

fun initAndroidDataStore(context: Context) {
    appContext = context.applicationContext
}

actual fun createDataStore(): DataStore {
    val ctx = appContext
        ?: error("Call initAndroidDataStore(context) in MainActivity.onCreate before using DataStore")
    return AndroidDataStore(ctx)
}
