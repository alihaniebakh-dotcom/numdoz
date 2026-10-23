package ir.numdoz.data.model

data class Year(
    val id: String,
    val orderIndex: Int,
    val titleFa: String,
    val titleEn: String,
    val descriptionFa: String = "",
    val descriptionEn: String = "",
    val accentColorHex: String = "#4FE3E0",
    val logoName: String = "star",
    val isLocked: Boolean = false
)

data class Chapter(
    val id: String,
    val yearId: String,
    val orderIndex: Int,
    val titleFa: String,
    val titleEn: String,
    val descriptionFa: String = "",
    val descriptionEn: String = "",
    val accentColorHex: String = "#4FE3E0",
    val logoName: String = "sun",
    val isLocked: Boolean = false
)

data class Section(
    val id: String,
    val chapterId: String,
    val orderIndex: Int,
    val titleFa: String,
    val titleEn: String,
    val monthNumber: Int = 0,
    val descriptionFa: String = "",
    val descriptionEn: String = "",
    val logoName: String = "moon",
    val accentColorHex: String = "#4FE3E0"
)

enum class PuzzleType { NUMBER, LETTER, SHAPE, MIXED }

data class Level(
    val id: String,
    val sectionId: String,
    val orderIndex: Int,
    val titleFa: String = "",
    val titleEn: String = "",
    val puzzleType: PuzzleType,
    val difficulty: Int = 1,
    val timeLimitSeconds: Int = 180,
    val sequence: String,
    val correctAnswer: String,
    val hint1Fa: String = "",
    val hint1En: String = "",
    val hint2Fa: String = "",
    val hint2En: String = "",
    val explanationFa: String = "",
    val explanationEn: String = "",
    val dayNumber: Int = 0
)

data class LevelProgress(
    val levelId: String,
    val completed: Boolean = false,
    val bestTimeSeconds: Int = 0,
    val stars: Int = 0,
    val hintsUsed: Int = 0,
    val timeExtensionsUsed: Int = 0,
    val lastPlayedAt: Long = 0L
)

data class UserWallet(
    val freeHints: Int = 3,
    val freeTimeExtensions: Int = 1,
    val totalAdsWatched: Int = 0,
    val coins: Int = 0
)

data class UserSettings(
    val language: String = "fa",
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
)

data class ChapterStats(
    val sectionCount: Int,
    val totalLevels: Int,
    val completedLevels: Int
)

data class SectionStats(
    val totalLevels: Int,
    val completedLevels: Int
)

data class ChapterWithStats(
    val chapter: Chapter,
    val stats: ChapterStats
)
