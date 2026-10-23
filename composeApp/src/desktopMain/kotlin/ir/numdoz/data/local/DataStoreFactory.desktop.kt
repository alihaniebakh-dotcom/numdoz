package ir.numdoz.data.local

import java.io.File

actual fun createDataStore(): DataStore {
    val home = System.getProperty("user.home") ?: "."
    val dir = File(home, "NumDoz")
    if (!dir.exists()) dir.mkdirs()
    val dbFile = File(dir, "numdoz.db")
    return SqliteDataStore(dbFile.absolutePath)
}
