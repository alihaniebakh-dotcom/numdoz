package ir.numdoz.data.local

import ir.numdoz.data.model.Chapter
import ir.numdoz.data.model.ChapterStats
import ir.numdoz.data.model.Level
import ir.numdoz.data.model.LevelProgress
import ir.numdoz.data.model.Section
import ir.numdoz.data.model.SectionStats
import ir.numdoz.data.model.UserSettings
import ir.numdoz.data.model.UserWallet
import ir.numdoz.data.model.Year

interface DataStore {
    suspend fun ensureSchema()
    suspend fun seedIfEmpty()

    suspend fun getAllYears(): List<Year>
    suspend fun getAllChapters(): List<Chapter>
    suspend fun getChaptersByYear(yearId: String): List<Chapter>
    suspend fun getSections(chapterId: String): List<Section>
    suspend fun getLevels(sectionId: String): List<Level>
    suspend fun getLevel(levelId: String): Level?

    suspend fun upsertLevelProgress(progress: LevelProgress)
    suspend fun getLevelProgress(levelId: String): LevelProgress?
    suspend fun getAllLevelProgress(): List<LevelProgress>

    suspend fun getStatsForChapter(chapterId: String): ChapterStats
    suspend fun getStatsForSection(sectionId: String): SectionStats

    suspend fun getWallet(): UserWallet
    suspend fun updateWallet(wallet: UserWallet)

    suspend fun getSettings(): UserSettings
    suspend fun updateSettings(settings: UserSettings)

    suspend fun isIntroSeen(key: String): Boolean
    suspend fun markIntroSeen(key: String)
}

expect fun createDataStore(): DataStore
