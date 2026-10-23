package ir.numdoz.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import ir.numdoz.data.model.Chapter
import ir.numdoz.data.model.ChapterStats
import ir.numdoz.data.model.Level
import ir.numdoz.data.model.LevelProgress
import ir.numdoz.data.model.PuzzleType
import ir.numdoz.data.model.Section
import ir.numdoz.data.model.SectionStats
import ir.numdoz.data.model.UserSettings
import ir.numdoz.data.model.UserWallet
import ir.numdoz.data.model.Year
import ir.numdoz.data.seed.SeedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val DB_NAME = "numdoz.db"
private const val DB_VERSION = 1
private const val TAG = "NumDoz/db"

class AndroidDataStore(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION), DataStore {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "onCreate — creating tables")
        db.execSQL("""
            CREATE TABLE years (
                id TEXT PRIMARY KEY,
                order_index INTEGER NOT NULL,
                title_fa TEXT NOT NULL,
                title_en TEXT NOT NULL,
                description_fa TEXT NOT NULL DEFAULT '',
                description_en TEXT NOT NULL DEFAULT '',
                accent_color_hex TEXT NOT NULL DEFAULT '#4FE3E0',
                logo_name TEXT NOT NULL DEFAULT 'star',
                is_locked INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE chapters (
                id TEXT PRIMARY KEY,
                year_id TEXT NOT NULL DEFAULT 'arka',
                order_index INTEGER NOT NULL,
                title_fa TEXT NOT NULL,
                title_en TEXT NOT NULL,
                description_fa TEXT NOT NULL DEFAULT '',
                description_en TEXT NOT NULL DEFAULT '',
                accent_color_hex TEXT NOT NULL DEFAULT '#4FE3E0',
                logo_name TEXT NOT NULL DEFAULT 'sun',
                is_locked INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE sections (
                id TEXT PRIMARY KEY,
                chapter_id TEXT NOT NULL,
                order_index INTEGER NOT NULL,
                title_fa TEXT NOT NULL,
                title_en TEXT NOT NULL,
                month_number INTEGER NOT NULL DEFAULT 0,
                description_fa TEXT NOT NULL DEFAULT '',
                description_en TEXT NOT NULL DEFAULT '',
                logo_name TEXT NOT NULL DEFAULT 'moon',
                accent_color_hex TEXT NOT NULL DEFAULT '#4FE3E0'
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE levels (
                id TEXT PRIMARY KEY,
                section_id TEXT NOT NULL,
                order_index INTEGER NOT NULL,
                title_fa TEXT NOT NULL DEFAULT '',
                title_en TEXT NOT NULL DEFAULT '',
                puzzle_type TEXT NOT NULL,
                difficulty INTEGER NOT NULL DEFAULT 1,
                time_limit_seconds INTEGER NOT NULL DEFAULT 180,
                sequence TEXT NOT NULL,
                correct_answer TEXT NOT NULL,
                hint1_fa TEXT NOT NULL DEFAULT '',
                hint1_en TEXT NOT NULL DEFAULT '',
                hint2_fa TEXT NOT NULL DEFAULT '',
                hint2_en TEXT NOT NULL DEFAULT '',
                explanation_fa TEXT NOT NULL DEFAULT '',
                explanation_en TEXT NOT NULL DEFAULT '',
                day_number INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE level_progress (
                level_id TEXT PRIMARY KEY,
                completed INTEGER NOT NULL DEFAULT 0,
                best_time_seconds INTEGER NOT NULL DEFAULT 0,
                stars INTEGER NOT NULL DEFAULT 0,
                hints_used INTEGER NOT NULL DEFAULT 0,
                time_extensions_used INTEGER NOT NULL DEFAULT 0,
                last_played_at INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE user_wallet (
                id INTEGER PRIMARY KEY CHECK (id = 1),
                free_hints INTEGER NOT NULL DEFAULT 3,
                free_time_extensions INTEGER NOT NULL DEFAULT 1,
                total_ads_watched INTEGER NOT NULL DEFAULT 0,
                coins INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE user_settings (
                key TEXT PRIMARY KEY,
                value TEXT NOT NULL
            )
        """.trimIndent())
        db.execSQL("INSERT OR IGNORE INTO user_wallet (id) VALUES (1)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade $oldVersion → $newVersion")
        // No migrations yet
    }

    override suspend fun ensureSchema() = withContext(Dispatchers.IO) {
        writableDatabase // triggers onCreate if first time
        Log.d(TAG, "schema ensured")
    }

    override suspend fun seedIfEmpty() = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val count = db.rawQuery("SELECT COUNT(*) FROM years", null).use { c ->
            if (c.moveToFirst()) c.getInt(0) else 0
        }
        if (count >= SeedData.years.size) {
            Log.d(TAG, "already seeded ($count years)")
            return@withContext
        }

        Log.d(TAG, "seeding (current=$count)")
        db.beginTransaction()
        try {
            db.delete("level_progress", null, null)
            db.delete("levels", null, null)
            db.delete("sections", null, null)
            db.delete("chapters", null, null)
            db.delete("years", null, null)

            SeedData.years.forEach { y ->
                val cv = ContentValues().apply {
                    put("id", y.id); put("order_index", y.orderIndex)
                    put("title_fa", y.titleFa); put("title_en", y.titleEn)
                    put("description_fa", y.descriptionFa); put("description_en", y.descriptionEn)
                    put("accent_color_hex", y.accentColorHex); put("logo_name", y.logoName)
                    put("is_locked", if (y.isLocked) 1 else 0)
                }
                db.insert("years", null, cv)
            }
            SeedData.chapters.forEach { c ->
                val cv = ContentValues().apply {
                    put("id", c.id); put("year_id", c.yearId); put("order_index", c.orderIndex)
                    put("title_fa", c.titleFa); put("title_en", c.titleEn)
                    put("description_fa", c.descriptionFa); put("description_en", c.descriptionEn)
                    put("accent_color_hex", c.accentColorHex); put("logo_name", c.logoName)
                    put("is_locked", if (c.isLocked) 1 else 0)
                }
                db.insert("chapters", null, cv)
            }
            SeedData.sections.forEach { s ->
                val cv = ContentValues().apply {
                    put("id", s.id); put("chapter_id", s.chapterId); put("order_index", s.orderIndex)
                    put("title_fa", s.titleFa); put("title_en", s.titleEn)
                    put("month_number", s.monthNumber)
                    put("description_fa", s.descriptionFa); put("description_en", s.descriptionEn)
                    put("logo_name", s.logoName); put("accent_color_hex", s.accentColorHex)
                }
                db.insert("sections", null, cv)
            }
            SeedData.levels.forEach { l ->
                val cv = ContentValues().apply {
                    put("id", l.id); put("section_id", l.sectionId); put("order_index", l.orderIndex)
                    put("title_fa", l.titleFa); put("title_en", l.titleEn)
                    put("puzzle_type", l.puzzleType.name); put("difficulty", l.difficulty)
                    put("time_limit_seconds", l.timeLimitSeconds)
                    put("sequence", l.sequence); put("correct_answer", l.correctAnswer)
                    put("hint1_fa", l.hint1Fa); put("hint1_en", l.hint1En)
                    put("hint2_fa", l.hint2Fa); put("hint2_en", l.hint2En)
                    put("explanation_fa", l.explanationFa); put("explanation_en", l.explanationEn)
                    put("day_number", l.dayNumber)
                }
                db.insert("levels", null, cv)
            }
            db.setTransactionSuccessful()
            Log.d(TAG, "SEEDED: ${SeedData.years.size} years, ${SeedData.chapters.size} seasons, ${SeedData.sections.size} months, ${SeedData.levels.size} levels")
        } catch (t: Throwable) {
            Log.e(TAG, "seed failed: ${t.message}", t)
            throw t
        } finally {
            db.endTransaction()
        }
    }

    override suspend fun getAllYears(): List<Year> = withContext(Dispatchers.IO) {
        readableDatabase.rawQuery("SELECT * FROM years ORDER BY order_index", null).use { c ->
            buildList {
                while (c.moveToNext()) add(Year(
                    id = c.s("id"),
                    orderIndex = c.i("order_index"),
                    titleFa = c.s("title_fa"),
                    titleEn = c.s("title_en"),
                    descriptionFa = c.s("description_fa"),
                    descriptionEn = c.s("description_en"),
                    accentColorHex = c.s("accent_color_hex"),
                    logoName = c.s("logo_name"),
                    isLocked = c.i("is_locked") != 0
                ))
            }
        }
    }

    override suspend fun getAllChapters(): List<Chapter> = withContext(Dispatchers.IO) {
        readableDatabase.rawQuery("SELECT * FROM chapters ORDER BY order_index", null).use { c ->
            buildList { while (c.moveToNext()) add(c.toChapter()) }
        }
    }

    override suspend fun getChaptersByYear(yearId: String): List<Chapter> = withContext(Dispatchers.IO) {
        readableDatabase.rawQuery(
            "SELECT * FROM chapters WHERE year_id = ? ORDER BY order_index",
            arrayOf(yearId)
        ).use { c -> buildList { while (c.moveToNext()) add(c.toChapter()) } }
    }

    override suspend fun getSections(chapterId: String): List<Section> = withContext(Dispatchers.IO) {
        readableDatabase.rawQuery(
            "SELECT * FROM sections WHERE chapter_id = ? ORDER BY order_index",
            arrayOf(chapterId)
        ).use { c ->
            buildList {
                while (c.moveToNext()) add(Section(
                    id = c.s("id"), chapterId = c.s("chapter_id"),
                    orderIndex = c.i("order_index"),
                    titleFa = c.s("title_fa"), titleEn = c.s("title_en"),
                    monthNumber = c.i("month_number"),
                    descriptionFa = c.s("description_fa"),
                    descriptionEn = c.s("description_en"),
                    logoName = c.s("logo_name"),
                    accentColorHex = c.s("accent_color_hex")
                ))
            }
        }
    }

    override suspend fun getLevels(sectionId: String): List<Level> = withContext(Dispatchers.IO) {
        readableDatabase.rawQuery(
            "SELECT * FROM levels WHERE section_id = ? ORDER BY order_index",
            arrayOf(sectionId)
        ).use { c -> buildList { while (c.moveToNext()) add(c.toLevel()) } }
    }

    override suspend fun getLevel(levelId: String): Level? = withContext(Dispatchers.IO) {
        readableDatabase.rawQuery(
            "SELECT * FROM levels WHERE id = ?", arrayOf(levelId)
        ).use { c -> if (c.moveToFirst()) c.toLevel() else null }
    }

    override suspend fun upsertLevelProgress(progress: LevelProgress) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val existing = db.rawQuery(
            "SELECT * FROM level_progress WHERE level_id = ?", arrayOf(progress.levelId)
        ).use { c -> if (c.moveToFirst()) c.toProgress() else null }

        val merged = if (existing == null) progress else LevelProgress(
            levelId = progress.levelId,
            completed = existing.completed || progress.completed,
            bestTimeSeconds = when {
                existing.bestTimeSeconds == 0 -> progress.bestTimeSeconds
                progress.bestTimeSeconds == 0 -> existing.bestTimeSeconds
                else -> minOf(existing.bestTimeSeconds, progress.bestTimeSeconds)
            },
            stars = maxOf(existing.stars, progress.stars),
            hintsUsed = progress.hintsUsed,
            timeExtensionsUsed = progress.timeExtensionsUsed,
            lastPlayedAt = progress.lastPlayedAt
        )

        val cv = ContentValues().apply {
            put("level_id", merged.levelId)
            put("completed", if (merged.completed) 1 else 0)
            put("best_time_seconds", merged.bestTimeSeconds)
            put("stars", merged.stars)
            put("hints_used", merged.hintsUsed)
            put("time_extensions_used", merged.timeExtensionsUsed)
            put("last_played_at", merged.lastPlayedAt)
        }
        db.insertWithOnConflict("level_progress", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        Unit
    }

    override suspend fun getLevelProgress(levelId: String): LevelProgress? = withContext(Dispatchers.IO) {
        readableDatabase.rawQuery(
            "SELECT * FROM level_progress WHERE level_id = ?", arrayOf(levelId)
        ).use { c -> if (c.moveToFirst()) c.toProgress() else null }
    }

    override suspend fun getAllLevelProgress(): List<LevelProgress> = withContext(Dispatchers.IO) {
        readableDatabase.rawQuery("SELECT * FROM level_progress", null).use { c ->
            buildList { while (c.moveToNext()) add(c.toProgress()) }
        }
    }

    override suspend fun getStatsForChapter(chapterId: String): ChapterStats = withContext(Dispatchers.IO) {
        val sections = getSections(chapterId)
        var total = 0
        var completed = 0
        for (s in sections) {
            val st = getStatsForSection(s.id)
            total += st.totalLevels
            completed += st.completedLevels
        }
        ChapterStats(sections.size, total, completed)
    }

    override suspend fun getStatsForSection(sectionId: String): SectionStats = withContext(Dispatchers.IO) {
        val db = readableDatabase
        val total = db.rawQuery(
            "SELECT COUNT(*) FROM levels WHERE section_id = ?", arrayOf(sectionId)
        ).use { c -> if (c.moveToFirst()) c.getInt(0) else 0 }
        val completed = db.rawQuery(
            "SELECT COUNT(*) FROM levels l JOIN level_progress lp ON l.id = lp.level_id WHERE l.section_id = ? AND lp.completed = 1",
            arrayOf(sectionId)
        ).use { c -> if (c.moveToFirst()) c.getInt(0) else 0 }
        SectionStats(total, completed)
    }

    override suspend fun getWallet(): UserWallet = withContext(Dispatchers.IO) {
        readableDatabase.rawQuery("SELECT * FROM user_wallet WHERE id = 1", null).use { c ->
            if (c.moveToFirst()) UserWallet(
                freeHints = c.i("free_hints"),
                freeTimeExtensions = c.i("free_time_extensions"),
                totalAdsWatched = c.i("total_ads_watched"),
                coins = c.i("coins")
            ) else UserWallet()
        }
    }

    override suspend fun updateWallet(wallet: UserWallet) = withContext(Dispatchers.IO) {
        val cv = ContentValues().apply {
            put("id", 1)
            put("free_hints", wallet.freeHints)
            put("free_time_extensions", wallet.freeTimeExtensions)
            put("total_ads_watched", wallet.totalAdsWatched)
            put("coins", wallet.coins)
        }
        writableDatabase.insertWithOnConflict("user_wallet", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        Unit
    }

    override suspend fun getSettings(): UserSettings = withContext(Dispatchers.IO) {
        readableDatabase.rawQuery("SELECT key, value FROM user_settings", null).use { c ->
            val m = mutableMapOf<String, String>()
            while (c.moveToNext()) m[c.s("key")] = c.s("value")
            UserSettings(
                language = m["language"] ?: "fa",
                soundEnabled = (m["sound_enabled"] ?: "1") == "1",
                hapticsEnabled = (m["haptics_enabled"] ?: "1") == "1"
            )
        }
    }

    override suspend fun updateSettings(settings: UserSettings) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            putSetting(db, "language", settings.language)
            putSetting(db, "sound_enabled", if (settings.soundEnabled) "1" else "0")
            putSetting(db, "haptics_enabled", if (settings.hapticsEnabled) "1" else "0")
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        Unit
    }

    private fun putSetting(db: SQLiteDatabase, key: String, value: String) {
        val cv = ContentValues().apply {
            put("key", key)
            put("value", value)
        }
        db.insertWithOnConflict("user_settings", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    override suspend fun isIntroSeen(key: String): Boolean = withContext(Dispatchers.IO) {
        readableDatabase.rawQuery(
            "SELECT value FROM user_settings WHERE key = ?", arrayOf("intro_$key")
        ).use { c -> c.moveToFirst() && c.getString(0) == "1" }
    }

    override suspend fun markIntroSeen(key: String) = withContext(Dispatchers.IO) {
        putSetting(writableDatabase, "intro_$key", "1")
        Unit
    }

    // ── Cursor helpers ──

    private fun Cursor.s(col: String): String = getString(getColumnIndexOrThrow(col)) ?: ""
    private fun Cursor.i(col: String): Int = getInt(getColumnIndexOrThrow(col))
    private fun Cursor.l(col: String): Long = getLong(getColumnIndexOrThrow(col))

    private fun Cursor.toChapter(): Chapter = Chapter(
        id = s("id"),
        yearId = s("year_id").ifEmpty { "arka" },
        orderIndex = i("order_index"),
        titleFa = s("title_fa"),
        titleEn = s("title_en"),
        descriptionFa = s("description_fa"),
        descriptionEn = s("description_en"),
        accentColorHex = s("accent_color_hex").ifEmpty { "#4FE3E0" },
        logoName = s("logo_name").ifEmpty { "sun" },
        isLocked = i("is_locked") != 0
    )

    private fun Cursor.toLevel(): Level = Level(
        id = s("id"),
        sectionId = s("section_id"),
        orderIndex = i("order_index"),
        titleFa = s("title_fa"),
        titleEn = s("title_en"),
        puzzleType = runCatching { PuzzleType.valueOf(s("puzzle_type")) }.getOrDefault(PuzzleType.NUMBER),
        difficulty = i("difficulty"),
        timeLimitSeconds = i("time_limit_seconds"),
        sequence = s("sequence"),
        correctAnswer = s("correct_answer"),
        hint1Fa = s("hint1_fa"),
        hint1En = s("hint1_en"),
        hint2Fa = s("hint2_fa"),
        hint2En = s("hint2_en"),
        explanationFa = s("explanation_fa"),
        explanationEn = s("explanation_en"),
        dayNumber = i("day_number")
    )

    private fun Cursor.toProgress(): LevelProgress = LevelProgress(
        levelId = s("level_id"),
        completed = i("completed") != 0,
        bestTimeSeconds = i("best_time_seconds"),
        stars = i("stars"),
        hintsUsed = i("hints_used"),
        timeExtensionsUsed = i("time_extensions_used"),
        lastPlayedAt = l("last_played_at")
    )
}
