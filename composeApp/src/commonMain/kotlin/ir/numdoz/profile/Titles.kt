package ir.numdoz.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Waves
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ir.numdoz.i18n.Lang

data class TitleInfo(
    val key: String,
    val nameFa: String,
    val nameEn: String,
    val icon: ImageVector,
    val minLevels: Int,
    val color: Color
)

object Titles {

    private val all: List<TitleInfo> = listOf(
        TitleInfo("spark",    "جرقه",       "Spark",       Icons.Filled.PlayArrow,            0,   Color(0xFF4FE3E0)),
        TitleInfo("ember",    "اخگر",       "Ember",       Icons.Filled.LocalFireDepartment,  10,  Color(0xFFF59E0B)),
        TitleInfo("wanderer", "رهرو",       "Wanderer",    Icons.Filled.ChevronRight,         30,  Color(0xFF9D7BFF)),
        TitleInfo("tracker",  "ردیاب",      "Tracker",     Icons.Filled.Check,                60,  Color(0xFF0EA5E9)),
        TitleInfo("sage",     "حکیم",        "Sage",        Icons.Filled.Lightbulb,            100, Color(0xFFEF4444)),
        TitleInfo("wizard",   "جادوگر",      "Wizard",      Icons.Filled.AcUnit,               150, Color(0xFFA855F7)),
        TitleInfo("myth",     "اسطوره‌ساز",  "Myth-Maker",  Icons.Filled.Waves,                200, Color(0xFF34D399)),
        TitleInfo("legend",   "افسانه",      "Legend",      Icons.Filled.Star,                 300, Color(0xFFFFC947))
    )

    fun current(levelsCompleted: Int): TitleInfo =
        all.last { levelsCompleted >= it.minLevels }

    fun next(levelsCompleted: Int): TitleInfo? =
        all.firstOrNull { levelsCompleted < it.minLevels }

    fun nameFor(t: TitleInfo, lang: Lang): String =
        if (lang == Lang.FA) t.nameFa else t.nameEn

    fun progressToNext(levelsCompleted: Int): Float {
        val cur = current(levelsCompleted)
        val nxt = next(levelsCompleted) ?: return 1f
        val span = (nxt.minLevels - cur.minLevels).coerceAtLeast(1)
        return ((levelsCompleted - cur.minLevels).toFloat() / span).coerceIn(0f, 1f)
    }
}
