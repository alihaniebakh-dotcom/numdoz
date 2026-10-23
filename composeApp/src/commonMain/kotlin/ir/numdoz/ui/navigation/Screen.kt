package ir.numdoz.ui.navigation

sealed class Screen {
    data object Splash : Screen()
    data object YearList : Screen()
    data class Chapters(val yearId: String) : Screen()
    data class ChapterMonths(val yearId: String, val chapterId: String) : Screen()
    data class MonthDays(val yearId: String, val chapterId: String, val sectionId: String) : Screen()
    data class LevelPlay(
        val levelId: String,
        val chapterId: String,
        val sectionId: String,
        val yearId: String
    ) : Screen()
}
