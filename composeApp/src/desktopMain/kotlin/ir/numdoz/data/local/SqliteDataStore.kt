package ir.numdoz.data.local

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
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

class SqliteDataStore(private val dbPath: String) : DataStore {

    init {
        File(dbPath).parentFile?.mkdirs()
        println("[NumDoz/db] path = $dbPath")
    }

    private fun connect(): Connection =
        DriverManager.getConnection("jdbc:sqlite:$dbPath").apply {
            createStatement().use { it.execute("PRAGMA foreign_keys = ON") }
        }

    override suspend fun ensureSchema() = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.createStatement().use { st ->
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS years (
                        id TEXT PRIMARY KEY, order_index INTEGER NOT NULL,
                        title_fa TEXT NOT NULL, title_en TEXT NOT NULL,
                        description_fa TEXT NOT NULL DEFAULT '', description_en TEXT NOT NULL DEFAULT '',
                        accent_color_hex TEXT NOT NULL DEFAULT '#4FE3E0',
                        logo_name TEXT NOT NULL DEFAULT 'star',
                        is_locked INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS chapters (
                        id TEXT PRIMARY KEY, year_id TEXT NOT NULL DEFAULT 'arka',
                        order_index INTEGER NOT NULL,
                        title_fa TEXT NOT NULL, title_en TEXT NOT NULL,
                        description_fa TEXT NOT NULL DEFAULT '', description_en TEXT NOT NULL DEFAULT '',
                        accent_color_hex TEXT NOT NULL DEFAULT '#4FE3E0',
                        logo_name TEXT NOT NULL DEFAULT 'sun',
                        is_locked INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS sections (
                        id TEXT PRIMARY KEY, chapter_id TEXT NOT NULL,
                        order_index INTEGER NOT NULL,
                        title_fa TEXT NOT NULL, title_en TEXT NOT NULL,
                        month_number INTEGER NOT NULL DEFAULT 0,
                        description_fa TEXT NOT NULL DEFAULT '', description_en TEXT NOT NULL DEFAULT '',
                        logo_name TEXT NOT NULL DEFAULT 'moon',
                        accent_color_hex TEXT NOT NULL DEFAULT '#4FE3E0'
                    )
                """.trimIndent())
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS levels (
                        id TEXT PRIMARY KEY, section_id TEXT NOT NULL,
                        order_index INTEGER NOT NULL,
                        title_fa TEXT NOT NULL DEFAULT '', title_en TEXT NOT NULL DEFAULT '',
                        puzzle_type TEXT NOT NULL,
                        difficulty INTEGER NOT NULL DEFAULT 1,
                        time_limit_seconds INTEGER NOT NULL DEFAULT 180,
                        sequence TEXT NOT NULL, correct_answer TEXT NOT NULL,
                        hint1_fa TEXT NOT NULL DEFAULT '', hint1_en TEXT NOT NULL DEFAULT '',
                        hint2_fa TEXT NOT NULL DEFAULT '', hint2_en TEXT NOT NULL DEFAULT '',
                        explanation_fa TEXT NOT NULL DEFAULT '', explanation_en TEXT NOT NULL DEFAULT '',
                        day_number INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS level_progress (
                        level_id TEXT PRIMARY KEY,
                        completed INTEGER NOT NULL DEFAULT 0,
                        best_time_seconds INTEGER NOT NULL DEFAULT 0,
                        stars INTEGER NOT NULL DEFAULT 0,
                        hints_used INTEGER NOT NULL DEFAULT 0,
                        time_extensions_used INTEGER NOT NULL DEFAULT 0,
                        last_played_at INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS user_wallet (
                        id INTEGER PRIMARY KEY CHECK (id = 1),
                        free_hints INTEGER NOT NULL DEFAULT 3,
                        free_time_extensions INTEGER NOT NULL DEFAULT 1,
                        total_ads_watched INTEGER NOT NULL DEFAULT 0,
                        coins INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS user_settings (
                        key TEXT PRIMARY KEY, value TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }
        println("[NumDoz/db] schema ensured")
    }

    override suspend fun seedIfEmpty() = withContext(Dispatchers.IO) {
        connect().use { c ->
            val count = c.createStatement().use { st ->
                st.executeQuery("SELECT COUNT(*) FROM years").use { rs -> rs.next(); rs.getInt(1) }
            }
            if (count > 0) {
                println("[NumDoz/db] already seeded ($count years)")
                return@withContext
            }
            c.autoCommit = false
            try {
                c.createStatement().use { st ->
                    st.executeUpdate("DELETE FROM level_progress")
                    st.executeUpdate("DELETE FROM levels")
                    st.executeUpdate("DELETE FROM sections")
                    st.executeUpdate("DELETE FROM chapters")
                    st.executeUpdate("DELETE FROM years")
                }

                val iy = c.prepareStatement(
                    "INSERT INTO years (id,order_index,title_fa,title_en,description_fa,description_en,accent_color_hex,logo_name,is_locked) VALUES (?,?,?,?,?,?,?,?,?)"
                )
                for (y in SeedData.years) {
                    iy.setString(1, y.id); iy.setInt(2, y.orderIndex)
                    iy.setString(3, y.titleFa); iy.setString(4, y.titleEn)
                    iy.setString(5, y.descriptionFa); iy.setString(6, y.descriptionEn)
                    iy.setString(7, y.accentColorHex); iy.setString(8, y.logoName)
                    iy.setInt(9, if (y.isLocked) 1 else 0)
                    iy.executeUpdate()
                }

                val ic = c.prepareStatement(
                    "INSERT INTO chapters (id,year_id,order_index,title_fa,title_en,description_fa,description_en,accent_color_hex,logo_name,is_locked) VALUES (?,?,?,?,?,?,?,?,?,?)"
                )
                for (ch in SeedData.chapters) {
                    ic.setString(1, ch.id); ic.setString(2, ch.yearId)
                    ic.setInt(3, ch.orderIndex)
                    ic.setString(4, ch.titleFa); ic.setString(5, ch.titleEn)
                    ic.setString(6, ch.descriptionFa); ic.setString(7, ch.descriptionEn)
                    ic.setString(8, ch.accentColorHex); ic.setString(9, ch.logoName)
                    ic.setInt(10, if (ch.isLocked) 1 else 0)
                    ic.executeUpdate()
                }

                val isc = c.prepareStatement(
                    "INSERT INTO sections (id,chapter_id,order_index,title_fa,title_en,month_number,description_fa,description_en,logo_name,accent_color_hex) VALUES (?,?,?,?,?,?,?,?,?,?)"
                )
                for (s in SeedData.sections) {
                    isc.setString(1, s.id); isc.setString(2, s.chapterId)
                    isc.setInt(3, s.orderIndex)
                    isc.setString(4, s.titleFa); isc.setString(5, s.titleEn)
                    isc.setInt(6, s.monthNumber)
                    isc.setString(7, s.descriptionFa); isc.setString(8, s.descriptionEn)
                    isc.setString(9, s.logoName); isc.setString(10, s.accentColorHex)
                    isc.executeUpdate()
                }

                val il = c.prepareStatement(
                    "INSERT INTO levels (id,section_id,order_index,title_fa,title_en,puzzle_type,difficulty,time_limit_seconds,sequence,correct_answer,hint1_fa,hint1_en,hint2_fa,hint2_en,explanation_fa,explanation_en,day_number) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                )
                for (l in SeedData.levels) {
                    il.setString(1, l.id); il.setString(2, l.sectionId)
                    il.setInt(3, l.orderIndex)
                    il.setString(4, l.titleFa); il.setString(5, l.titleEn)
                    il.setString(6, l.puzzleType.name); il.setInt(7, l.difficulty)
                    il.setInt(8, l.timeLimitSeconds)
                    il.setString(9, l.sequence); il.setString(10, l.correctAnswer)
                    il.setString(11, l.hint1Fa); il.setString(12, l.hint1En)
                    il.setString(13, l.hint2Fa); il.setString(14, l.hint2En)
                    il.setString(15, l.explanationFa); il.setString(16, l.explanationEn)
                    il.setInt(17, l.dayNumber)
                    il.executeUpdate()
                }

                c.createStatement().use { it.executeUpdate("INSERT OR IGNORE INTO user_wallet (id) VALUES (1)") }
                c.commit()
                println("[NumDoz/db] SEEDED: ${SeedData.years.size} year, ${SeedData.chapters.size} seasons, ${SeedData.sections.size} months, ${SeedData.levels.size} levels")
            } catch (t: Throwable) {
                c.rollback(); println("[NumDoz/db] seed FAILED: ${t.message}"); throw t
            } finally { c.autoCommit = true }
        }
    }

    override suspend fun getAllYears(): List<Year> = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.createStatement().use { st ->
                st.executeQuery("SELECT * FROM years ORDER BY order_index").use { rs ->
                    buildList {
                        while (rs.next()) add(Year(
                            rs.getString("id"), rs.getInt("order_index"),
                            rs.getString("title_fa"), rs.getString("title_en"),
                            rs.getString("description_fa") ?: "", rs.getString("description_en") ?: "",
                            rs.getString("accent_color_hex") ?: "#4FE3E0",
                            rs.getString("logo_name") ?: "star",
                            rs.getInt("is_locked") != 0
                        ))
                    }
                }
            }
        }
    }

    override suspend fun getAllChapters(): List<Chapter> = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.createStatement().use { st ->
                st.executeQuery("SELECT * FROM chapters ORDER BY order_index").use { rs ->
                    buildList { while (rs.next()) add(rs.toChapter()) }
                }
            }
        }
    }

    override suspend fun getChaptersByYear(yearId: String): List<Chapter> = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.prepareStatement("SELECT * FROM chapters WHERE year_id = ? ORDER BY order_index").use { ps ->
                ps.setString(1, yearId)
                ps.executeQuery().use { rs ->
                    buildList { while (rs.next()) add(rs.toChapter()) }
                }
            }
        }
    }

    override suspend fun getSections(chapterId: String): List<Section> = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.prepareStatement("SELECT * FROM sections WHERE chapter_id = ? ORDER BY order_index").use { ps ->
                ps.setString(1, chapterId)
                ps.executeQuery().use { rs ->
                    buildList {
                        while (rs.next()) add(Section(
                            rs.getString("id"), rs.getString("chapter_id"),
                            rs.getInt("order_index"),
                            rs.getString("title_fa"), rs.getString("title_en"),
                            rs.getInt("month_number"),
                            rs.getString("description_fa") ?: "", rs.getString("description_en") ?: "",
                            rs.getString("logo_name") ?: "moon",
                            rs.getString("accent_color_hex") ?: "#4FE3E0"
                        ))
                    }
                }
            }
        }
    }

    override suspend fun getLevels(sectionId: String): List<Level> = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.prepareStatement("SELECT * FROM levels WHERE section_id = ? ORDER BY order_index").use { ps ->
                ps.setString(1, sectionId)
                ps.executeQuery().use { rs ->
                    buildList { while (rs.next()) add(rs.toLevel()) }
                }
            }
        }
    }

    override suspend fun getLevel(levelId: String): Level? = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.prepareStatement("SELECT * FROM levels WHERE id = ?").use { ps ->
                ps.setString(1, levelId)
                ps.executeQuery().use { rs -> if (rs.next()) rs.toLevel() else null }
            }
        }
    }

    override suspend fun upsertLevelProgress(progress: LevelProgress) = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.prepareStatement("""
                INSERT INTO level_progress (level_id,completed,best_time_seconds,stars,hints_used,time_extensions_used,last_played_at)
                VALUES (?,?,?,?,?,?,?)
                ON CONFLICT(level_id) DO UPDATE SET
                    completed = MAX(completed, excluded.completed),
                    best_time_seconds = CASE WHEN best_time_seconds = 0 OR excluded.best_time_seconds < best_time_seconds
                        THEN excluded.best_time_seconds ELSE best_time_seconds END,
                    stars = MAX(stars, excluded.stars),
                    hints_used = excluded.hints_used,
                    time_extensions_used = excluded.time_extensions_used,
                    last_played_at = excluded.last_played_at
            """.trimIndent()).use { ps ->
                ps.setString(1, progress.levelId)
                ps.setInt(2, if (progress.completed) 1 else 0)
                ps.setInt(3, progress.bestTimeSeconds)
                ps.setInt(4, progress.stars)
                ps.setInt(5, progress.hintsUsed)
                ps.setInt(6, progress.timeExtensionsUsed)
                ps.setLong(7, progress.lastPlayedAt)
                ps.executeUpdate()
                Unit
            }
        }
    }

    override suspend fun getLevelProgress(levelId: String): LevelProgress? = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.prepareStatement("SELECT * FROM level_progress WHERE level_id = ?").use { ps ->
                ps.setString(1, levelId)
                ps.executeQuery().use { rs -> if (rs.next()) rs.toProgress() else null }
            }
        }
    }

    override suspend fun getAllLevelProgress(): List<LevelProgress> = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.createStatement().use { st ->
                st.executeQuery("SELECT * FROM level_progress").use { rs ->
                    buildList { while (rs.next()) add(rs.toProgress()) }
                }
            }
        }
    }

    override suspend fun getStatsForChapter(chapterId: String): ChapterStats = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.prepareStatement("""
                SELECT
                    (SELECT COUNT(*) FROM sections WHERE chapter_id = ?) AS sc,
                    (SELECT COUNT(*) FROM levels l JOIN sections s ON l.section_id = s.id WHERE s.chapter_id = ?) AS lc,
                    (SELECT COUNT(*) FROM levels l JOIN sections s ON l.section_id = s.id
                        JOIN level_progress lp ON l.id = lp.level_id
                     WHERE s.chapter_id = ? AND lp.completed = 1) AS cc
            """.trimIndent()).use { ps ->
                ps.setString(1, chapterId); ps.setString(2, chapterId); ps.setString(3, chapterId)
                ps.executeQuery().use { rs ->
                    if (rs.next()) ChapterStats(rs.getInt("sc"), rs.getInt("lc"), rs.getInt("cc"))
                    else ChapterStats(0, 0, 0)
                }
            }
        }
    }

    override suspend fun getStatsForSection(sectionId: String): SectionStats = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.prepareStatement("""
                SELECT
                    (SELECT COUNT(*) FROM levels WHERE section_id = ?) AS tc,
                    (SELECT COUNT(*) FROM levels l JOIN level_progress lp ON l.id = lp.level_id
                     WHERE l.section_id = ? AND lp.completed = 1) AS cc
            """.trimIndent()).use { ps ->
                ps.setString(1, sectionId); ps.setString(2, sectionId)
                ps.executeQuery().use { rs ->
                    if (rs.next()) SectionStats(rs.getInt("tc"), rs.getInt("cc"))
                    else SectionStats(0, 0)
                }
            }
        }
    }

    override suspend fun getWallet(): UserWallet = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.createStatement().use { st ->
                st.executeQuery("SELECT * FROM user_wallet WHERE id = 1").use { rs ->
                    if (rs.next()) UserWallet(
                        rs.getInt("free_hints"), rs.getInt("free_time_extensions"),
                        rs.getInt("total_ads_watched"), rs.getInt("coins")
                    ) else UserWallet()
                }
            }
        }
    }

    override suspend fun updateWallet(wallet: UserWallet) = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.prepareStatement("""
                INSERT INTO user_wallet (id,free_hints,free_time_extensions,total_ads_watched,coins)
                VALUES (1,?,?,?,?)
                ON CONFLICT(id) DO UPDATE SET
                    free_hints = excluded.free_hints,
                    free_time_extensions = excluded.free_time_extensions,
                    total_ads_watched = excluded.total_ads_watched,
                    coins = excluded.coins
            """.trimIndent()).use { ps ->
                ps.setInt(1, wallet.freeHints); ps.setInt(2, wallet.freeTimeExtensions)
                ps.setInt(3, wallet.totalAdsWatched); ps.setInt(4, wallet.coins)
                ps.executeUpdate()
                Unit
            }
        }
    }

    override suspend fun getSettings(): UserSettings = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.createStatement().use { st ->
                st.executeQuery("SELECT key, value FROM user_settings").use { rs ->
                    val m = mutableMapOf<String, String>()
                    while (rs.next()) m[rs.getString("key")] = rs.getString("value")
                    UserSettings(
                        language = m["language"] ?: "fa",
                        soundEnabled = (m["sound_enabled"] ?: "1") == "1",
                        hapticsEnabled = (m["haptics_enabled"] ?: "1") == "1"
                    )
                }
            }
        }
    }

    override suspend fun updateSettings(settings: UserSettings) = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.autoCommit = false
            try {
                c.prepareStatement("INSERT INTO user_settings (key,value) VALUES (?,?) ON CONFLICT(key) DO UPDATE SET value = excluded.value").use { ps ->
                    ps.setString(1, "language"); ps.setString(2, settings.language); ps.executeUpdate()
                    ps.setString(1, "sound_enabled"); ps.setString(2, if (settings.soundEnabled) "1" else "0"); ps.executeUpdate()
                    ps.setString(1, "haptics_enabled"); ps.setString(2, if (settings.hapticsEnabled) "1" else "0"); ps.executeUpdate()
                }
                c.commit()
            } catch (t: Throwable) { c.rollback(); throw t } finally { c.autoCommit = true }
        }
    }

    override suspend fun isIntroSeen(key: String): Boolean = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.prepareStatement("SELECT value FROM user_settings WHERE key = ?").use { ps ->
                ps.setString(1, "intro_$key")
                ps.executeQuery().use { rs -> rs.next() && rs.getString("value") == "1" }
            }
        }
    }

    override suspend fun markIntroSeen(key: String) = withContext(Dispatchers.IO) {
        connect().use { c ->
            c.prepareStatement("INSERT INTO user_settings (key,value) VALUES (?,?) ON CONFLICT(key) DO UPDATE SET value = excluded.value").use { ps ->
                ps.setString(1, "intro_$key"); ps.setString(2, "1")
                ps.executeUpdate()
                Unit
            }
        }
    }

    private fun java.sql.ResultSet.toChapter(): Chapter = Chapter(
        id = getString("id"),
        yearId = getString("year_id") ?: "arka",
        orderIndex = getInt("order_index"),
        titleFa = getString("title_fa"), titleEn = getString("title_en"),
        descriptionFa = getString("description_fa") ?: "",
        descriptionEn = getString("description_en") ?: "",
        accentColorHex = getString("accent_color_hex") ?: "#4FE3E0",
        logoName = getString("logo_name") ?: "sun",
        isLocked = getInt("is_locked") != 0
    )

    private fun java.sql.ResultSet.toLevel(): Level = Level(
        id = getString("id"), sectionId = getString("section_id"),
        orderIndex = getInt("order_index"),
        titleFa = getString("title_fa") ?: "", titleEn = getString("title_en") ?: "",
        puzzleType = runCatching { PuzzleType.valueOf(getString("puzzle_type")) }.getOrDefault(PuzzleType.NUMBER),
        difficulty = getInt("difficulty"), timeLimitSeconds = getInt("time_limit_seconds"),
        sequence = getString("sequence"), correctAnswer = getString("correct_answer"),
        hint1Fa = getString("hint1_fa") ?: "", hint1En = getString("hint1_en") ?: "",
        hint2Fa = getString("hint2_fa") ?: "", hint2En = getString("hint2_en") ?: "",
        explanationFa = getString("explanation_fa") ?: "", explanationEn = getString("explanation_en") ?: "",
        dayNumber = getInt("day_number")
    )

    private fun java.sql.ResultSet.toProgress(): LevelProgress = LevelProgress(
        levelId = getString("level_id"),
        completed = getInt("completed") != 0,
        bestTimeSeconds = getInt("best_time_seconds"),
        stars = getInt("stars"),
        hintsUsed = getInt("hints_used"),
        timeExtensionsUsed = getInt("time_extensions_used"),
        lastPlayedAt = getLong("last_played_at")
    )
}
